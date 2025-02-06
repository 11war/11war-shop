package com.war11.global.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
  @Value("${spring.data.redis.host}")
  private String host;

  @Value("${spring.data.redis.port}")
  private int port;

  @Value("${spring.data.redis.database}")
  private int database;

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
    redisStandaloneConfiguration.setHostName(host);
    redisStandaloneConfiguration.setPort(port);
    redisStandaloneConfiguration.setDatabase(database);

    LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
    connectionFactory.setShareNativeConnection(false);
    return connectionFactory;
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory());
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

    return redisTemplate;
  }

  @Bean
  public RedissonClient redissonClient() {
    Config config = new Config();
    config.useSingleServer()
        .setAddress("redis://" + host + ":" + port)
        .setDatabase(database)
        .setConnectionMinimumIdleSize(10)
        .setConnectionPoolSize(64)
        .setConnectTimeout(3000)
        .setRetryAttempts(3)
        .setRetryInterval(1500);
    return Redisson.create(config);
  }
}
