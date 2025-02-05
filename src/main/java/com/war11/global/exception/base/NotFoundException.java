package com.war11.global.exception.base;

import com.war11.global.exception.enums.ErrorCode;

public class NotFoundException extends BusinessException {

  public NotFoundException(ErrorCode errorCode) {
    super(errorCode);
  }
}
