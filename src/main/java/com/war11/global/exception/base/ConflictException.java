package com.war11.global.exception.base;

import com.war11.global.exception.BusinessException;
import com.war11.global.exception.enums.ErrorCode;

public class ConflictException extends BusinessException {

  public ConflictException(ErrorCode errorCode) {
    super(errorCode);
  }
}
