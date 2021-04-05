package com.heilou.iot.session;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: session
 * @author heilou
 * @date 2021/4/2 10:25
 * @version 1.0
 */
@Data
@AllArgsConstructor
public class SessionContext implements Serializable {

	private static final long serialVersionUID = 5209539791996944490L;

	private String clientId;

	private Channel channel;

	private boolean cleanSession;

	private MqttPublishMessage willMessage;
}
