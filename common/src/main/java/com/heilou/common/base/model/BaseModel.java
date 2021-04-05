package com.heilou.common.base.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/** 基类
 * @author heilou
 * @version 1.0
 * @description:
 * @date 2021/3/29 17:32
 */
@ApiModel("基类")
@Data
public class BaseModel {
    @ApiModelProperty("主键")
    private String id;
    @ApiModelProperty("创建时间")
    private Date createTime;
    @ApiModelProperty("更新时间")
    private Date updateTime;
}
