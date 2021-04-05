package com.heilou.common.constant;

public interface RedisKey {

    // 手机发送验证码缓存前缀
    String SMS_CODE_PREFIX = "SMS_CODE_";

    // 手机发送验证码时效缓存前缀
    String TEL_SEND_SMS_TIME_PREFIX = "TEL_SMS_";
}
