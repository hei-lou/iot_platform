package com.heilou.iot.db.service.impl;

import com.heilou.common.base.mapper.BaseMapper;
import com.heilou.common.base.service.impl.BaseServiceImpl;
import com.heilou.iot.db.mapper.DeviceStatusDataMapper;
import com.heilou.iot.db.service.DeviceStatusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceStatusDataImpl extends BaseServiceImpl implements DeviceStatusDataService {
    @Autowired
    private DeviceStatusDataMapper deviceStatusDataMapper;


    @Override
    public BaseMapper getMapper() {
        return this.deviceStatusDataMapper;
    }


}
