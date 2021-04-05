package com.heilou.api.iot;

import com.heilou.common.constant.ReturnData;

public interface DeviceCommandService {

    ReturnData sendCommand(String deviceId, String command);

}
