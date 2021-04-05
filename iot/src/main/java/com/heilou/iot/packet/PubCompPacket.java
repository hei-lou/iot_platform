package com.heilou.iot.packet;

import com.heilou.iot.support.ChannelAttributeHelper;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @description: PUBCOMP连接处理
 * @author heilou
 * @date 2021/4/2 10:08
 * @version 1.0
 */
@Slf4j
@Component
public class PubCompPacket {

    public void processPubComp(Channel channel, MqttMessageIdVariableHeader variableHeader){
        int messageId = variableHeader.messageId();
        log.info("PUBCOMP - clientId: {}, messageId: {}", ChannelAttributeHelper.getClientId(channel), messageId);
//        DupPubRelMessageStoreService.remove((String)channel.attr(AttributeKey.valueOf("clientId")).get(), variableHeader.messageId());
    }
}
