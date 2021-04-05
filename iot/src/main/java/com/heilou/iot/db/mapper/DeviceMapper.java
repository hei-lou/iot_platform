package com.heilou.iot.db.mapper;

import com.heilou.common.base.mapper.BaseMapper;
import com.heilou.iot.db.model.Device;

public interface DeviceMapper extends BaseMapper {

    Device getDeviceByMacOrId(String macOrId);
}