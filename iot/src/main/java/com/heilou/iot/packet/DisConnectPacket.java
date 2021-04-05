package com.heilou.iot.packet;

import com.heilou.common.constant.DeviceConstant;
import com.heilou.iot.db.model.Device;
import com.heilou.iot.db.service.DeviceService;
import com.heilou.iot.session.SessionContext;
import com.heilou.iot.session.SessionStoreService;
import com.heilou.iot.support.ChannelAttributeHelper;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description:  断开报文处理
 * @author heilou
 * @date 2021/4/2 10:07
 * @version 1.0
 */
@Slf4j
@Component
public class DisConnectPacket {

    @Autowired
    private SessionStoreService sessionStoreService;
    @Autowired
    private DeviceService deviceService;

    public void processDisConnect(Channel channel,MqttMessage msg){
        String clientId = ChannelAttributeHelper.getClientId(channel);
        log.info("DISCONNECT - clientId: {}", clientId);
        SessionContext sessionContext = sessionStoreService.get(clientId);
        log.info("DISCONNECT - clientId: {}, cleanSession: {}", clientId, sessionContext.isCleanSession());
        sessionStoreService.remove(clientId);
        //更新device离线状态
        deviceService.updateOnlineStatus(clientId, DeviceConstant.DEVICE_OFFLINE);
        channel.close();
    }

}
