package com.heilou.iot.server;

import com.heilou.common.utils.SystemUtil;
import com.heilou.iot.handler.HeartbeatCheckHandler;
import com.heilou.iot.handler.MqttChannelHandler;
import com.heilou.iot.handler.OutBoundMsgHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.internal.SystemPropertyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.security.KeyStore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author heilou
 * @version 1.0
 * @description: netty服务器启动类
 * @date 2021/4/1 12:37
 */
@Component
@ConfigurationProperties(prefix = "netty")
@Slf4j
public class NettyBootstrapServer {

    @Value("${netty.host}")
    private String host;
    @Value("${netty.port}")
    private Integer port;
    @Value("${netty.heart}")
    private Integer heart;
    @Value("${netty.leak_detector_level}")
    private String leakDetectorLevel;
    @Value("${netty.boss_group_thread_count}")
    private Integer bossGroupThreadCount;
    @Value("${netty.worker_group_thread_count}")
    private Integer workerGroupThreadCount;
    @Value("${netty.max_payload_size}")
    private Integer maxPayloadSize;
    @Value("${netty.ssl.enable}")
    private Boolean ssl;
    @Value("${netty.ssl.jksFile}")
    private String jksFile;
    @Value("${netty.ssl.jksStorePassword}")
    private String jksStorePassword;
    @Value("${netty.ssl.jksCertificatePassword}")
    private String jksCertificatePassword;

    @Autowired
    private MqttChannelHandler mqttChannelHandler;
    @Autowired
    private HeartbeatCheckHandler heartbeatCheckHandler;
    @Autowired
    private OutBoundMsgHandler outBoundMsgHandler;


    private Channel serverChannel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private String PROTOCOL = "TLS";

    @PostConstruct
    public void init() throws Exception {
        log.info("Setting resource leak detector level to {}", leakDetectorLevel);
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.valueOf(leakDetectorLevel.toUpperCase()));

        log.info("Starting MQTT transport server");
        initEventPool();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        initHandler(socketChannel.pipeline());
                    }
                })
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        serverChannel = serverBootstrap.bind(host, port).sync().channel();
        log.info("Mqtt transport started!");
    }

    /**
     * init handler
     */
    protected void initHandler(ChannelPipeline channelPipeline) {
        if (ssl) {
            //开启ssl
            initSslHandler(channelPipeline);
        }
        //编解码
        channelPipeline.addLast("decoder", new MqttDecoder(maxPayloadSize));
        channelPipeline.addLast("encoder", MqttEncoder.INSTANCE);
        //心跳
        channelPipeline.addLast(new IdleStateHandler(Math.round(heart * 1.1f), 0, 0));
        // 处理handler
        channelPipeline.addLast(heartbeatCheckHandler);
        channelPipeline.addLast(mqttChannelHandler);
        channelPipeline.addLast(outBoundMsgHandler);

    }

    //ssl初始化
    private void initSslHandler(ChannelPipeline channelPipeline) {
        if (!ObjectUtils.allNotNull(jksCertificatePassword, jksFile, jksStorePassword)) {
            throw new NullPointerException("SSL file and password is null");
        }
        String algorithm = SystemPropertyUtil.get("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX509";
        }
        SSLContext serverContext;
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(NettyBootstrapServer.class.getResourceAsStream(jksFile),
                    jksStorePassword.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
            kmf.init(ks, jksCertificatePassword.toCharArray());
            serverContext = SSLContext.getInstance(PROTOCOL);
            serverContext.init(kmf.getKeyManagers(), null, null);
        } catch (Exception e) {
            throw new Error("Failed to initialize the server-side SSLContext", e);
        }
        SSLEngine engine = serverContext.createSSLEngine();
        engine.setUseClientMode(false);
        channelPipeline.addLast("ssl", new SslHandler(engine));
    }

    /**
     * 初始化EventPool 参数
     */
    private void initEventPool() {
        // 是否具备epoll
        if (useEpoll()) {
            bossGroup = new EpollEventLoopGroup(bossGroupThreadCount, new ThreadFactory() {
                private AtomicInteger index = new AtomicInteger(0);

                public Thread newThread(Runnable r) {
                    return new Thread(r, "EPOLL_BOSS_" + index.incrementAndGet());
                }
            });
            workerGroup = new EpollEventLoopGroup(workerGroupThreadCount, new ThreadFactory() {
                private AtomicInteger index = new AtomicInteger(0);

                public Thread newThread(Runnable r) {
                    return new Thread(r, "EPOLL_WORKER_" + index.incrementAndGet());
                }
            });

        } else {
            bossGroup = new NioEventLoopGroup(bossGroupThreadCount, new ThreadFactory() {
                private AtomicInteger index = new AtomicInteger(0);

                public Thread newThread(Runnable r) {
                    return new Thread(r, "BOSS_" + index.incrementAndGet());
                }
            });
            workerGroup = new NioEventLoopGroup(workerGroupThreadCount, new ThreadFactory() {
                private AtomicInteger index = new AtomicInteger(0);

                public Thread newThread(Runnable r) {
                    return new Thread(r, "WORKER_" + index.incrementAndGet());
                }
            });
        }
    }

    private boolean useEpoll() {
        return SystemUtil.isLinux()
                && Epoll.isAvailable();
    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        log.info("Stopping MQTT transport!");
        try {
            serverChannel.close().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
        log.info("MQTT transport stopped!");
    }
}

