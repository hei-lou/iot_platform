package com.heilou.iot.packet;

import com.heilou.iot.support.ChannelAttributeHelper;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @description: PUBREL连接处理
 * @author heilou
 * @date 2021/4/2 10:11
 * @version 1.0
 */
@Slf4j
@Component
public class PubRelPacket {

    public void processPubRel(Channel channel, MqttMessageIdVariableHeader variableHeader) {
        MqttMessage pubCompMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBCOMP, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(variableHeader.messageId()),
                null);
        log.info("PUBREL - clientId: {}, messageId: {}", ChannelAttributeHelper.getClientId(channel), variableHeader.messageId());
        channel.writeAndFlush(pubCompMessage);
    }
}
