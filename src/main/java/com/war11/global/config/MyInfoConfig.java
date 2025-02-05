package com.war11.global.config;


import com.war11.global.util.YamlPropertySourceFactory;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Configuration
@PropertySource(value = "classpath:myInfo.yml", factory = YamlPropertySourceFactory.class)
public class MyInfoConfig {

    @Value("${db.user}")
    private String user;

    @Value("${db.url")
    private String dbUrl;

    @Value("${db.port")
    private String dbPort;

    @Value("${db.name}")
    private String name;

    @Value("${db.pw}")
    private String pw;

}
