package com.heilou.iot.packet;

import com.heilou.iot.support.ChannelAttributeHelper;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @description: ping req报文
 * @author heilou
 * @date 2021/4/2 10:08
 * @version 1.0
 */
@Slf4j
@Component
public class PingReqPacket {

    public void processPingReq(Channel channel, MqttMessage msg){
        log.info("PINGREQ - clientId: {}", ChannelAttributeHelper.getClientId(channel));
        MqttMessage pingRespMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0),
                null,
                null);
        channel.writeAndFlush(pingRespMessage);
    }
}
