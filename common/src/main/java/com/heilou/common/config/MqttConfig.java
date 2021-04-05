package com.heilou.common.config;

/**
 * @author heilou
 * @version 1.0
 * @description:
 * @date 2021/4/2 11:14
 */
public class MqttConfig {

    // 心跳时间 秒
    public static int keepAlive = 180;
    // host地址
    public static String mqttHost = "api.heilou.com.cn";
    // 端口号
    public static String mqttPort = "1883";
    // mqtt版本
    public static String mqttVersion = "MQTT3.1.1";
}
