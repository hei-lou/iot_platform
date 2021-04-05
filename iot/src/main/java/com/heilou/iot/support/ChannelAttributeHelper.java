package com.heilou.iot.support;

import com.heilou.iot.session.SessionContext;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * @author heilou
 * @version 1.0
 * @description: 缓存
 * @date 2021/4/1 18:19
 */
public class ChannelAttributeHelper {

    private static final String CONN_CLIENTID = "conn_clientId";

    private static final String CONN_SESSION = "conn_session";

    // attribute key that be used for saving clientId
    public static AttributeKey<String> CONN_CLIENT_ID = AttributeKey
            .valueOf(CONN_CLIENTID);

    // attribute key that be used for saving session
    public static AttributeKey<SessionContext> CONN_SESSION_KEY = AttributeKey
            .valueOf(CONN_SESSION);

    // put clientId into channel context
    public static void setClientId(Channel channel, String clientId) {
        channel.attr(CONN_CLIENT_ID).set(clientId);
    }

    // get ClientId from channel
    public static String getClientId(Channel channel) {
        return channel.attr(CONN_CLIENT_ID).get();
    }

    public static SessionContext getSession(Channel channel) {
        return channel.attr(CONN_SESSION_KEY).get();
    }

    // put session into channel context
    public static void setSession(Channel channel, SessionContext session) {
        channel.attr(CONN_SESSION_KEY).set(session);
    }

}
