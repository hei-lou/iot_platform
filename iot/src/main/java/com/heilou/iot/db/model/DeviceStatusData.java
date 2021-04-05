package com.heilou.iot.db.model;

import com.heilou.common.base.model.BaseModel;
import lombok.Data;

@Data
public class DeviceStatusData extends BaseModel {
    private String deviceId;

    private String open;

    private String pattern;

    private String wind;

    private Integer meshCycle;

    private String pm25;

    private String tvoc;

    private String co2;

    private String childLock;

    private String temperature;

    private String humidity;

}