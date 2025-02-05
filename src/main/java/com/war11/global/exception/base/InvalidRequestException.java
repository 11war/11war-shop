package com.war11.global.exception.base;

import com.war11.global.exception.enums.ErrorCode;

public class InvalidRequestException extends BusinessException {

  public InvalidRequestException(ErrorCode errorCode) {
    super(errorCode);
  }
}
