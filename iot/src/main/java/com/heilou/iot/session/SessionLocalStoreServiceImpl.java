package com.heilou.iot.session;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话存储服务
 */
@Service
public class SessionLocalStoreServiceImpl implements SessionStoreService {

	private Map<String, SessionContext> sessionCache = new ConcurrentHashMap<String, SessionContext>();

	@Override
	public void put(String clientId, SessionContext sessionStore) {
		sessionCache.put(clientId, sessionStore);
	}

	@Override
	public SessionContext get(String clientId) {
		return sessionCache.get(clientId);
	}

	@Override
	public boolean containsKey(String clientId) {
		return sessionCache.containsKey(clientId);
	}

	@Override
	public void remove(String clientId) {
		sessionCache.remove(clientId);
	}
}
