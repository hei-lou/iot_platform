package com.heilou.iot.packet;

import com.heilou.iot.support.ChannelAttributeHelper;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @description: PUBACK连接处理
 * @author heilou
 * @date 2021/4/2 10:08
 * @version 1.0
 */
@Slf4j
@Component
public class PubAckPacket {

    public void processPubAck(Channel channel, MqttMessageIdVariableHeader variableHeader){
        int messageId = variableHeader.messageId();
        log.info("PUBACK - clientId: {}, messageId: {}", ChannelAttributeHelper.getClientId(channel), messageId);
//        DupPublishMessageStoreService.remove(ChannelAttributeHelper.getClientId(channel), messageId);

    }


}
