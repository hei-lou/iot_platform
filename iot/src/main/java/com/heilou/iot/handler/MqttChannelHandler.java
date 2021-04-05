package com.heilou.iot.handler;

import cn.hutool.core.util.IdUtil;
import com.heilou.common.constant.Constants;
import com.heilou.common.constant.DeviceConstant;
import com.heilou.iot.db.service.DeviceService;
import com.heilou.iot.packet.*;
import com.heilou.iot.session.SessionStoreService;
import com.heilou.iot.support.ChannelAttributeHelper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static io.netty.handler.codec.mqtt.MqttMessageType.CONNACK;
import static io.netty.handler.codec.mqtt.MqttMessageType.CONNECT;


/**
 * @description: mqtt handler
 * @author heilou
 * @date 2021/4/1 14:55
 * @version 1.0
 */
@ChannelHandler.Sharable
@Slf4j
@Component
public class MqttChannelHandler extends SimpleChannelInboundHandler<MqttMessage> {

    @Autowired
    private ConnectPacket connectPacket;
    @Autowired
    private DisConnectPacket disConnectPacket;
    @Autowired
    private PingReqPacket pingReqPacket;
    @Autowired
    private PubAckPacket pubAckPacket;
    @Autowired
    private PubCompPacket pubCompPacket;
    @Autowired
    private PublishPacket publishPacket;
    @Autowired
    private PubRecPacket pubRecPacket;
    @Autowired
    private PubRelPacket pubRelPacket;
    @Autowired
    private SubscribePacket subscribePacket;
    @Autowired
    private UnSubscribePacket unSubscribePacket;
    @Autowired
    private SessionStoreService sessionStoreService;
    @Autowired
    private DeviceService deviceService;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MqttMessage mqttMessage) throws Exception {
        if (mqttMessage.decoderResult().isFailure()) {
            Throwable cause = mqttMessage.decoderResult().cause();
            if (cause instanceof MqttUnacceptableProtocolVersionException) {
                channelHandlerContext.writeAndFlush(MqttMessageFactory.newMessage(
                        new MqttFixedHeader(CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION, false),
                        null));
            } else if (cause instanceof MqttIdentifierRejectedException) {
                channelHandlerContext.writeAndFlush(MqttMessageFactory.newMessage(
                        new MqttFixedHeader(CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false),
                        null));
            }
            channelHandlerContext.close();
            return;
        }
        // 设置traceId
        MDC.put(Constants.TRACE_ID, IdUtil.objectId());

        MqttMessageType mqttMessageType = mqttMessage.fixedHeader().messageType();

        //校验是否连接过
        if(!CONNECT.equals(mqttMessageType) && StringUtils.isEmpty(ChannelAttributeHelper.getClientId(channelHandlerContext.channel()))){
            channelHandlerContext.writeAndFlush(MqttMessageFactory.newMessage(
                    new MqttFixedHeader(CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                    new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false),
                    null));
            channelHandlerContext.close();
        }

        // 分发处理协议包
        switch (mqttMessageType) {
            case CONNECT:
                connectPacket.processConnect(channelHandlerContext.channel(), (MqttConnectMessage) mqttMessage);
                break;
            case PUBLISH:
                publishPacket.processPublish(channelHandlerContext.channel(), (MqttPublishMessage) mqttMessage);
                break;
            case PUBACK:
                pubAckPacket.processPubAck(channelHandlerContext.channel(), (MqttMessageIdVariableHeader) mqttMessage.variableHeader());
                break;
            case PUBREC:
                pubRecPacket.processPubRec(channelHandlerContext.channel(), (MqttMessageIdVariableHeader) mqttMessage.variableHeader());
                break;
            case PUBREL:
                pubRelPacket.processPubRel(channelHandlerContext.channel(), (MqttMessageIdVariableHeader) mqttMessage.variableHeader());
                break;
            case PUBCOMP:
                pubCompPacket.processPubComp(channelHandlerContext.channel(), (MqttMessageIdVariableHeader) mqttMessage.variableHeader());
                break;
            case SUBSCRIBE:
                subscribePacket.processSubscribe(channelHandlerContext.channel(), (MqttSubscribeMessage) mqttMessage);
                break;
            case UNSUBSCRIBE:
                unSubscribePacket.processUnSubscribe(channelHandlerContext.channel(), (MqttUnsubscribeMessage) mqttMessage);
                break;
            case PINGREQ:
                pingReqPacket.processPingReq(channelHandlerContext.channel(), mqttMessage);
                break;
            case DISCONNECT:
                disConnectPacket.processDisConnect(channelHandlerContext.channel(), mqttMessage);
                break;
            default:
                break;
        }
    }

    /** 
     * @description: 设备直接断开
     * @param: ctx 
     * @return: void 
     * @author heilou
     * @date: 2021/4/4 10:44
     */ 
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        // 清除本地缓存
        String clientId = ChannelAttributeHelper.getClientId(ctx.channel());
        if(StringUtils.isNotEmpty(clientId)){
            sessionStoreService.remove(clientId);
        }
        //更新device离线状态
        deviceService.updateOnlineStatus(clientId, DeviceConstant.DEVICE_OFFLINE);
    }

    /**
     * @description: 出现异常
     * @param: channelHandlerContext,cause 
     * @return: void 
     * @author heilou
     * @date: 2021/4/1 15:38
     */ 
    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
        log.error("出现异常: ", cause);
        if (cause instanceof IOException) {
            // 远程主机强迫关闭了一个现有的连接的异常
            channelHandlerContext.close();
        } else {
            super.exceptionCaught(channelHandlerContext, cause);
        }
    }
}
