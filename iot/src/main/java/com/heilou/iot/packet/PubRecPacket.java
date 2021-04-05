package com.heilou.iot.packet;

import com.heilou.iot.support.ChannelAttributeHelper;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @description: pub rec
 * @author heilou
 * @date 2021/4/2 10:10
 * @version 1.0
 */
@Slf4j
@Component
public class PubRecPacket {

    public void processPubRec(Channel channel, MqttMessageIdVariableHeader variableHeader) {
        MqttMessage pubRelMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBREL, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(variableHeader.messageId()),
                null);
        log.info("PUBREC - clientId: {}, messageId: {}", ChannelAttributeHelper.getClientId(channel), variableHeader.messageId());
//        DupPublishMessageStoreService.remove(ChannelAttributeHelper.getClientId(channel), variableHeader.messageId());
//        DupPubRelMessageStore dupPubRelMessageStore = new DupPubRelMessageStore().setClientId(ChannelAttributeHelper.getClientId(channel))
//                .setMessageId(variableHeader.messageId());
//        DupPubRelMessageStoreService.put(ChannelAttributeHelper.getClientId(channel), dupPubRelMessageStore);
        channel.writeAndFlush(pubRelMessage);
    }
}
