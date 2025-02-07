package com.war11.global.util;

import com.war11.global.exception.BusinessException;
import com.war11.global.exception.enums.ErrorCode;

public class AdminUtil {

    public static void AdminIdCheck(String loginId) {
        String checkid = loginId.toLowerCase();
        if(!checkid.equals("admin")){
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ID);
        }
    }

}
