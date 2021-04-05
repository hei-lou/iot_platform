package com.heilou.iot.command;

import com.heilou.iot.session.SessionContext;
import io.netty.handler.codec.mqtt.MqttMessage;

/**
 * 消息处理器
 * 
 * @author heilou
 *
 */
public interface CommandProcesser {

	/**
	 * 消息处理函数
	 */
	void process(SessionContext ctx, MqttMessage msg);
}
