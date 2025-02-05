package com.war11.global.exception.base;

import com.war11.global.exception.enums.ErrorCode;

public class AuthException extends BusinessException {

  public AuthException(ErrorCode errorCode) {
    super(errorCode);
  }
}
