package com.heilou.iot.packet;

import com.heilou.iot.support.ChannelAttributeHelper;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author heilou
 * @version 1.0
 * @description: publish
 * @date 2021/4/2 10:09
 */
@Slf4j
@Component
public class PublishPacket {

    public void processPublish(Channel channel, MqttPublishMessage msg) {
        byte[] messageBytes = new byte[msg.payload().readableBytes()];
        msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
        log.info("publish msg deviceId：{}, msg: {}", ChannelAttributeHelper.getClientId(channel), new String(messageBytes));
        if(msg.fixedHeader().qosLevel() == MqttQoS.AT_LEAST_ONCE){
            this.sendPubAckMessage(channel, msg.variableHeader().packetId());
        }
        if (msg.fixedHeader().qosLevel() == MqttQoS.EXACTLY_ONCE) {
            this.sendPubRecMessage(channel, msg.variableHeader().packetId());
        }
        //        // QoS=0
//        if (msg.fixedHeader().qosLevel() == MqttQoS.AT_MOST_ONCE) {
//            byte[] messageBytes = new byte[msg.payload().readableBytes()];
//            msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
//            InternalMessage internalMessage = new InternalMessage()
//                    .setTopic(msg.variableHeader().topicName())
//                    .setMqttQoS(msg.fixedHeader().qosLevel().value())
//                    .setMessageBytes(messageBytes)
//                    .setDup(false)
//                    .setRetain(false)
//                    .setClientId(clientId);
//            KafkaService.send(internalMessage);
//            this.sendPublishMessage(msg.variableHeader().topicName(), msg.fixedHeader().qosLevel(), messageBytes, false, false);
//        }
//        // QoS=1
//        if (msg.fixedHeader().qosLevel() == MqttQoS.AT_LEAST_ONCE) {
//            byte[] messageBytes = new byte[msg.payload().readableBytes()];
//            msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
//            InternalMessage internalMessage = new InternalMessage()
//                    .setTopic(msg.variableHeader().topicName())
//                    .setMqttQoS(msg.fixedHeader().qosLevel().value())
//                    .setMessageBytes(messageBytes)
//                    .setDup(false)
//                    .setRetain(false)
//                    .setClientId(clientId);
//            KafkaService.send(internalMessage);
//            this.sendPublishMessage(msg.variableHeader().topicName(), msg.fixedHeader().qosLevel(), messageBytes, false, false);
//            this.sendPubAckMessage(channel, msg.variableHeader().packetId());
//        }
//        // QoS=2
//        if (msg.fixedHeader().qosLevel() == MqttQoS.EXACTLY_ONCE) {
//            byte[] messageBytes = new byte[msg.payload().readableBytes()];
//            msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
//            InternalMessage internalMessage = new InternalMessage()
//                    .setTopic(msg.variableHeader().topicName())
//                    .setMqttQoS(msg.fixedHeader().qosLevel().value())
//                    .setMessageBytes(messageBytes)
//                    .setDup(false)
//                    .setRetain(false)
//                    .setClientId(clientId);
//            KafkaService.send(internalMessage);
//            this.sendPublishMessage(msg.variableHeader().topicName(), msg.fixedHeader().qosLevel(), messageBytes, false, false);
//            this.sendPubRecMessage(channel, msg.variableHeader().packetId());
//        }
//        // retain=1, 保留消息
//        if (msg.fixedHeader().isRetain()) {
//            byte[] messageBytes = new byte[msg.payload().readableBytes()];
//            msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
//            if (messageBytes.length == 0) {
//                RetainMessageStoreService.remove(msg.variableHeader().topicName());
//            } else {
//                RetainMessageStore retainMessageStore = new RetainMessageStore().setTopic(msg.variableHeader().topicName()).setMqttQoS(msg.fixedHeader().qosLevel().value())
//                        .setMessageBytes(messageBytes);
//                RetainMessageStoreService.put(msg.variableHeader().topicName(), retainMessageStore);
//            }
//        }
    }

    private void sendPublishMessage(String topic, MqttQoS mqttQoS, byte[] messageBytes, boolean retain, boolean dup) {
//        List<SubscribeStore> subscribeStores = SubscribeStoreService.search(topic);
//        for (SubscribeStore subscribeStore : subscribeStores) {
//            if (SessionStoreService.containsKey(subscribeStore.getClientId())) {
//                // 订阅者收到MQTT消息的QoS级别, 最终取决于发布消息的QoS和主题订阅的QoS
//                MqttQoS respQoS = mqttQoS.value() > subscribeStore.getMqttQoS() ? MqttQoS.valueOf(subscribeStore.getMqttQoS()) : mqttQoS;
//                if (respQoS == MqttQoS.AT_MOST_ONCE) {
//                    MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
//                            new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
//                            new MqttPublishVariableHeader(topic, 0),
//                            Unpooled.buffer().writeBytes(messageBytes));
//                    log.info("PUBLISH - clientId: {}, topic: {}, Qos: {}", subscribeStore.getClientId(), topic, respQoS.value());
//                    SessionStoreService.get(subscribeStore.getClientId()).getChannel().writeAndFlush(publishMessage);
//                }
//                if (respQoS == MqttQoS.AT_LEAST_ONCE) {
//                    int messageId = MessageIdService.getNextMessageId();
//                    MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
//                            new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
//                            new MqttPublishVariableHeader(topic, messageId), Unpooled.buffer().writeBytes(messageBytes));
//                    log.info("PUBLISH - clientId: {}, topic: {}, Qos: {}, messageId: {}", subscribeStore.getClientId(), topic, respQoS.value(), messageId);
//                    DupPublishMessageStore dupPublishMessageStore = new DupPublishMessageStore().setClientId(subscribeStore.getClientId())
//                            .setTopic(topic).setMqttQoS(respQoS.value()).setMessageBytes(messageBytes).setMessageId(messageId);
//                    DupPublishMessageStoreService.put(subscribeStore.getClientId(), dupPublishMessageStore);
//                    SessionStoreService.get(subscribeStore.getClientId()).getChannel().writeAndFlush(publishMessage);
//                }
//                if (respQoS == MqttQoS.EXACTLY_ONCE) {
//                    int messageId = MessageIdService.getNextMessageId() + 1;
//                    MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
//                            new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
//                            new MqttPublishVariableHeader(topic, messageId), Unpooled.buffer().writeBytes(messageBytes));
//                    log.info("PUBLISH - clientId: {}, topic: {}, Qos: {}, messageId: {}", subscribeStore.getClientId(), topic, respQoS.value(), messageId);
//                    DupPublishMessageStore dupPublishMessageStore = new DupPublishMessageStore().setClientId(subscribeStore.getClientId())
//                            .setTopic(topic).setMqttQoS(respQoS.value()).setMessageBytes(messageBytes).setMessageId(messageId);
//                    DupPublishMessageStoreService.put(subscribeStore.getClientId(), dupPublishMessageStore);
//                    SessionStoreService.get(subscribeStore.getClientId()).getChannel().writeAndFlush(publishMessage);
//                }
//            }
//        }
    }

    private void sendPubAckMessage(Channel channel, int messageId) {
        MqttPubAckMessage pubAckMessage = (MqttPubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId),
                null);
        channel.writeAndFlush(pubAckMessage);
    }

    private void sendPubRecMessage(Channel channel, int messageId) {
        MqttMessage pubRecMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBREC, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId),
                null);
        channel.writeAndFlush(pubRecMessage);
    }

}
