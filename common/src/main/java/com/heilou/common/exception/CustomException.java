package com.heilou.common.exception;

import com.heilou.common.constant.HttpCode;
import com.heilou.common.constant.ResultCode;
import lombok.Data;

/**
 *  自定义异常
 */
@Data
public class CustomException extends RuntimeException {

  private static final long serialVersionUID = 5234579791996945984L;

  private String msg;
  private String code;
  private Throwable t;

  public CustomException(HttpCode httpCode){
    this.code = httpCode.getCode();
    this.msg = httpCode.getDesc();
  }

  public CustomException(ResultCode resultCode){
    this.code = resultCode.getCode();
    this.msg = resultCode.getDesc();
  }

}
