package com.war11.global.config;

import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;


@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "My API",
                version = "1.0",
                description = "이 API는 쿠폰 관련 기능을 제공합니다."
        )
)
public class SwaggerConfig {
}
