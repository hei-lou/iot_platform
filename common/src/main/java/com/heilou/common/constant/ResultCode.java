package com.heilou.common.constant;

/**
 * 响应信息
 */
public enum ResultCode {

    //设备端错误码
    DEVICE_IS_OFFLINE("50001", "设备已经离线"),
    DEVICE_SEND_COMMAND_FAIL("50002", "设备命令下发失败"),
    PARAMETER_ILLEGAL("40000", "参数非法");

    private String code;
    private String desc;

    ResultCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
