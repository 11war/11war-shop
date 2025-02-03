package com.war11.domain.product.entity.enums;

import com.war11.global.exception.BusinessException;
import com.war11.global.exception.enums.ErrorCode;

public enum ProductStatus  {
    AVAILABLE, SOLD_OUT, DISCONTINUED;

    public static ProductStatus from(String status){
        for(ProductStatus productStatus : ProductStatus.values()){
            if(status.equalsIgnoreCase(productStatus.name())){
                return ProductStatus.valueOf(status.toUpperCase());
            }
        }
        throw new BusinessException(ErrorCode.INVALID_PRODUCT_STATUS_VALUE);
    }
}
