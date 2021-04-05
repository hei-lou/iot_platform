package com.heilou.common.constant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("返回数据")
public class ReturnData<T> implements Serializable{
    private static final long serialVersionUID = 5234579791996944490L;
    @ApiModelProperty("响应编码")
    private String code = "200";
    @ApiModelProperty("提示消息")
    private String msg = "success";
    @ApiModelProperty("响应数据")
    private T data;
    @ApiModelProperty("响应时间戳")
    private long timestamp = System.currentTimeMillis();

    public ReturnData(T data){
        this.data = data;
    }
    public ReturnData(){
    }

    public ReturnData(ResultCode resultCode){
        this.code = resultCode.getCode();
        this.msg = resultCode.getDesc();
    }
}