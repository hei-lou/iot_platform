# iot_platform
基于netty、mqtt实现的高并发MQTT服务broker

# mqtt简介
MQTT 协议是 IBM 开发的即时通讯协议，相对于 IM 的实际上的准标准协议 XMPP 来说，MQTT 更小，更快，更轻量。MQTT 适合于任何计算能力有限，工作在低带宽、不可靠的网络中的设备，包括手机，传感器等等。

# 功能
- 已实现：
- 发布订阅功能
- 遗言通知
- 会话session数据
- 发布保留消息
- 主题过滤
- 实现标准的 qos0 qos1 qos2消息确认机制
- ssl加密
- 集成spring容器

