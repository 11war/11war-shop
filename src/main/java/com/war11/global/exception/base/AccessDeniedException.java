package com.war11.global.exception.base;

import com.war11.global.exception.enums.ErrorCode;

public class AccessDeniedException extends BusinessException {

  public AccessDeniedException(ErrorCode errorCode) {
    super(errorCode);
  }
}
