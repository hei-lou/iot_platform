package com.heilou.iot.rpc;

import com.heilou.api.iot.DeviceCommandService;
import com.heilou.common.constant.ResultCode;
import com.heilou.common.constant.ReturnData;
import com.heilou.iot.session.SessionContext;
import com.heilou.iot.session.SessionStoreService;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.mqtt.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import static com.heilou.common.constant.MqttTopic.PUB_TOPIC;

/**
 * @author heilou
 * @version 1.0
 * @description:
 * @date 2021/4/4 17:00
 */
@DubboService
@Service
public class DeviceCommandServiceImpl implements DeviceCommandService {

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);

    @Autowired
    private SessionStoreService sessionStoreService;

    /**
     * @description: 命令下发接口
     * @param: command
     * @return: com.heilou.common.constant.ReturnData
     * @author heilou
     * @date: 2021/4/4 17:00
     */
    @Override
    public ReturnData sendCommand(String deviceId, String command) {
        if (StringUtils.isEmpty(deviceId) || StringUtils.isEmpty(command)) {
            return new ReturnData(ResultCode.PARAMETER_ILLEGAL);
        }
        SessionContext sessionContext = sessionStoreService.get(deviceId);
        if (sessionContext == null) {
            return new ReturnData(ResultCode.DEVICE_IS_OFFLINE);
        }
        MqttPublishMessage mqttPublishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.AT_MOST_ONCE, false, 0),
                new MqttPublishVariableHeader(PUB_TOPIC, ATOMIC_INTEGER.getAndIncrement()),
                Unpooled.buffer().writeBytes(command.getBytes(StandardCharsets.UTF_8)));
        ChannelFuture channelFuture = sessionContext.getChannel().writeAndFlush(mqttPublishMessage);
        if (channelFuture.isDone() && channelFuture.isSuccess()) {
            return new ReturnData();
        } else {
            return new ReturnData(ResultCode.DEVICE_SEND_COMMAND_FAIL);
        }

    }
}
