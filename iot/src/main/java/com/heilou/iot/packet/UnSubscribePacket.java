package com.heilou.iot.packet;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @description: UnSubscribe
 * @author heilou
 * @date 2021/4/2 10:12
 * @version 1.0
 */
@Slf4j
@Component
public class UnSubscribePacket {

    public void processUnSubscribe(Channel channel, MqttUnsubscribeMessage msg) {

    }
}
