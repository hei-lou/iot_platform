package com.heilou.iot.command.impl;

import com.heilou.iot.command.CommandProcesser;
import com.heilou.iot.session.SessionContext;
import com.sun.prism.impl.BaseContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.channels.Channel;

/**
 * @description: 应答指令处理
 * @author heilou
 * @date 2021/4/1 18:06
 * @version 1.0
 */
@Service(value = "respCommandProcesser")
@Slf4j
public class RespCommandProcesser implements CommandProcesser {

	/**
	 * 处理函数
	 */
	public void process(Channel channel, MqttMessage msg) {

	}

	@Override
	public void process(SessionContext ctx, MqttMessage msg) {

	}
}
