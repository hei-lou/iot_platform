package com.heilou.iot.db.model;

import com.heilou.common.base.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("设备")
public class Device extends BaseModel {
    @ApiModelProperty("mac")
    private String mac;
    @ApiModelProperty("固件版本")
    private String version;
    @ApiModelProperty("是否在线 00-离线 10-在线")
    private String online;
    @ApiModelProperty("是否开机 00-关机 10-开机")
    private String open;
    @ApiModelProperty("设备型号")
    private String model;
    @ApiModelProperty("使用说明")
    private String instruction;
}