package com.heilou.iot.packet;

import cn.hutool.core.util.StrUtil;
import com.heilou.common.constant.DeviceConstant;
import com.heilou.iot.db.model.Device;
import com.heilou.iot.db.service.DeviceService;
import com.heilou.iot.service.AuthService;
import com.heilou.iot.session.SessionContext;
import com.heilou.iot.session.SessionStoreService;
import com.heilou.iot.support.ChannelAttributeHelper;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author heilou
 * @version 1.0
 * @description: 连接报文处理
 * @date 2021/4/2 10:05
 */
@Slf4j
@Component
public class ConnectPacket {

    @Autowired
    private AuthService authService;
    @Autowired
    private SessionStoreService sessionStoreService;
    @Autowired
    private DeviceService deviceService;

    public void processConnect(Channel channel, MqttConnectMessage msg) {
        log.info("device CONNECT###");
        // 消息解码器出现异常
        if (msg.decoderResult().isFailure()) {
            Throwable cause = msg.decoderResult().cause();
            if (cause instanceof MqttUnacceptableProtocolVersionException) {
                // 不支持的协议版本
                MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION, false), null);
                channel.writeAndFlush(connAckMessage);
                channel.close();
                log.warn("协议版本不支持");
                return;
            } else if (cause instanceof MqttIdentifierRejectedException) {
                // 不合格的clientId
                MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false), null);
                channel.writeAndFlush(connAckMessage);
                channel.close();
                log.warn("不合格的clientId");
                return;
            }
            channel.close();
            return;
        }
        String clientId = msg.payload().clientIdentifier();
        // clientId为空或null的情况, 这里要求客户端必须提供clientId
        if (StrUtil.isBlank(clientId)) {
            MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                    new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false), null);
            channel.writeAndFlush(connAckMessage);
            channel.close();
            return;
        }
        // 用户名和密码验证
        String username = msg.payload().userName();
        String password = msg.payload().passwordInBytes() == null ? null : new String(msg.payload().passwordInBytes(), CharsetUtil.UTF_8);
        if (!authService.checkValid(username, password)) {
            MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                    new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, false), null);
            channel.writeAndFlush(connAckMessage);
            channel.close();
            return;
        }
        // 如果会话中已存储这个新连接的clientId, 就关闭之前该clientId的连接
        if (sessionStoreService.containsKey(clientId)) {
            SessionContext sessionContext = sessionStoreService.get(msg.payload().clientIdentifier());
            Channel previous = sessionContext.getChannel();
            Boolean cleanSession = sessionContext.isCleanSession();
            if (cleanSession) {
                sessionStoreService.remove(msg.payload().clientIdentifier());
            }
            previous.close();
        }

        //存储会话消息及返回接受客户端连接
        SessionContext sessionStore = new SessionContext(clientId, channel, msg.variableHeader().isCleanSession(), null);
        sessionStoreService.put(clientId, sessionStore);
        //将clientId存储到channel的map中
        ChannelAttributeHelper.setClientId(channel, clientId);
        Boolean sessionPresent = sessionStoreService.containsKey(msg.payload().clientIdentifier()) && !msg.variableHeader().isCleanSession();
        MqttConnAckMessage okResp = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, sessionPresent),
                null
        );
        log.info("CONNECT - clientId: {}, cleanSession: {}", clientId, msg.variableHeader().isCleanSession());

        //更新db在线状态
        deviceService.updateOnlineStatus(clientId, DeviceConstant.DEVICE_ONLINE);
        channel.writeAndFlush(okResp);

        //处理遗嘱信息
        if (msg.variableHeader().isWillFlag()) {
            MqttPublishMessage willMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.valueOf(msg.variableHeader().willQos()), msg.variableHeader().isWillRetain(), 0),
                    new MqttPublishVariableHeader(msg.payload().willTopic(), 0),
                    Unpooled.buffer().writeBytes(msg.payload().willMessageInBytes())
            );
            sessionStore.setWillMessage(willMessage);
        }
        // 如果cleanSession为0, 需要重发同一clientId存储的未完成的QoS1和QoS2的DUP消息
//        if (!msg.variableHeader().isCleanSession()) {
//            List<DupPublishMessageStore> dupPublishMessageStoreList = DupPublishMessageStoreService.get(msg.payload().clientIdentifier());
//            List<DupPubRelMessageStore> dupPubRelMessageStoreList = DupPubRelMessageStoreService.get(msg.payload().clientIdentifier());
//            dupPublishMessageStoreList.forEach(dupPublishMessageStore -> {
//                MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
//                        new MqttFixedHeader(MqttMessageType.PUBLISH, true, MqttQoS.valueOf(dupPublishMessageStore.getMqttQoS()), false, 0),
//                        new MqttPublishVariableHeader(dupPublishMessageStore.getTopic(), dupPublishMessageStore.getMessageId()),
//                        Unpooled.buffer().writeBytes(dupPublishMessageStore.getMessageBytes())
//                );
//                channel.writeAndFlush(publishMessage);
//            });
//            dupPubRelMessageStoreList.forEach(dupPubRelMessageStore -> {
//                MqttMessage pubRelMessage = MqttMessageFactory.newMessage(
//                        new MqttFixedHeader(MqttMessageType.PUBREL, true, MqttQoS.AT_MOST_ONCE, false, 0),
//                        MqttMessageIdVariableHeader.from(dupPubRelMessageStore.getMessageId()),
//                        null
//                );
//                channel.writeAndFlush(pubRelMessage);
//            });
//        }
    }
}
