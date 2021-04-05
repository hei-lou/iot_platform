package com.heilou.iot.packet;

import com.heilou.iot.support.ChannelAttributeHelper;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * SUBSCRIBE连接处理
 */
@Slf4j
@Component
public class SubscribePacket {

    public void processSubscribe(Channel channel, MqttSubscribeMessage msg) {
        log.info("subscribe topic deviceId：{}", ChannelAttributeHelper.getClientId(channel));
        List<MqttTopicSubscription> mqttTopicSubscriptions = msg.payload().topicSubscriptions();
        for (MqttTopicSubscription mqttTopicSubscription : mqttTopicSubscriptions){
            MqttQoS mqttQoS = mqttTopicSubscription.qualityOfService();
            MqttSubAckMessage subAckMessage = (MqttSubAckMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.SUBACK, false, mqttQoS, false, 0),
                    MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()), MqttQoS.AT_MOST_ONCE);
            channel.writeAndFlush(subAckMessage);
        }

    }
}
