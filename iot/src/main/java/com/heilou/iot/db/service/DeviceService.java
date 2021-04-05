package com.heilou.iot.db.service;

import com.heilou.common.base.service.BaseService;
import com.heilou.iot.db.model.Device;

public interface DeviceService extends BaseService {

    Device getDeviceByMacOrId(String macOrId);

    void updateOnlineStatus(String id, String status);
}
