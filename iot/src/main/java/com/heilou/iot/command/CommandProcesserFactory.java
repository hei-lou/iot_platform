package com.heilou.iot.command;

import io.netty.handler.codec.mqtt.MqttMessageType;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理器工厂
 * 
 * @author
 *
 */
public class CommandProcesserFactory {

	/**
	 * 处理对象缓存
	 */
	private static Map<MqttMessageType, CommandProcesser> HANDLER_MAP = new HashMap<MqttMessageType, CommandProcesser>();

	static {
		HANDLER_MAP.put(MqttMessageType.CONNECT, null);
	}

	/**
	 * 获取处理器对象实例
	 * 
	 * @param type
	 *            见Constants
	 * @return
	 */
	public static CommandProcesser getInstance(MqttMessageType type) {
		return HANDLER_MAP.get(type);
	}
}
