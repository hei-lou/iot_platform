package com.heilou.iot.db.service.impl;

import com.heilou.common.base.mapper.BaseMapper;
import com.heilou.common.base.service.impl.BaseServiceImpl;
import com.heilou.iot.db.mapper.DeviceMapper;
import com.heilou.iot.db.model.Device;
import com.heilou.iot.db.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceServiceImpl extends BaseServiceImpl implements DeviceService {
    @Autowired
    private DeviceMapper deviceMapper;

    @Override
    public BaseMapper getMapper() {
        return this.deviceMapper;
    }
    
    /** 
     * @description: 根据设备id或者mac获取设备 
     * @param: macOrId 
     * @return: com.heilou.iot.db.model.Device
     * @author heilou
     * @date: 2021/4/4 11:44
     */ 
    @Override
    public Device getDeviceByMacOrId(String macOrId) {
        return deviceMapper.getDeviceByMacOrId(macOrId);
    }

    /**
     * @description: 更新设备在线状态
     * @param:
     * @return: void
     * @author heilou
     * @date: 2021/4/4 12:03
     */
    public void updateOnlineStatus(String id, String status){
        Device device = new Device();
        device.setId(id);
        device.setOnline(status);
        this.updateByPrimaryKeySelective(device);
    }

}
