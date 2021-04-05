package com.heilou.iot.handler;

import com.heilou.iot.support.ChannelAttributeHelper;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 服务器响应信息打印
 *
 * @author heilou
 */
@Sharable
@Service
@Slf4j
public class OutBoundMsgHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
        //信息为异常的时候直接发送。
        if (msg instanceof Throwable) {
            log.error("服务器响应异常");
            return;
        }
//        MqttMessage mqtt = (MqttMessage) msg;
//        Object payload = mqtt.payload();
//        String payloadStr = payload == null ? "null" : payload.toString();
//        log.info("服务器响应clientId: {}, msgType:{}, playload: {}", ChannelAttributeHelper.getClientId(ctx.channel()), mqtt.fixedHeader().messageType(), payloadStr);
    }


}
