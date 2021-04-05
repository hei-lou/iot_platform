package com.heilou.iot.handler;

import com.heilou.common.constant.DeviceConstant;
import com.heilou.iot.db.service.DeviceService;
import com.heilou.iot.packet.PublishPacket;
import com.heilou.iot.session.SessionContext;
import com.heilou.iot.session.SessionStoreService;
import com.heilou.iot.support.ChannelAttributeHelper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 心跳检测处理
 * 
 * @author
 *
 */
@Sharable
@Component
@Slf4j
public class HeartbeatCheckHandler extends ChannelDuplexHandler {

	@Autowired
	private SessionStoreService sessionStoreService;
	@Autowired
	private PublishPacket publishPacket;
	@Autowired
	private DeviceService deviceService;

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			// 超过一段时间没有收到心跳了
			if (e.state() == IdleState.READER_IDLE) {
				String clientId = ChannelAttributeHelper.getClientId(ctx.channel());

				SessionContext sessionContext;
				if ((sessionContext = sessionStoreService.get(clientId)) != null) {
					// 发送遗嘱消息
					if (sessionContext.getWillMessage() != null) {
						publishPacket.processPublish(ctx.channel(), sessionContext.getWillMessage());
					}
					// 清除本地缓存
					sessionStoreService.remove(clientId);
					log.warn("device : {}心跳未收到, 也未下发命令, 断开连接, 设置离线状态, ctx: {}", clientId, ctx);
				}
				deviceService.updateOnlineStatus(clientId, DeviceConstant.DEVICE_OFFLINE);
				// 关闭连接
				ctx.close();
			}
		}
		super.userEventTriggered(ctx, evt);
	}

}
