package com.vttp.ssf.assessment.config;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    Logger logger = Logger.getLogger(RedisConfig.class.getName());

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    private String apiKey;

    @PostConstruct
    private void init() {
        apiKey = System.getenv("REDIS_API_KEY");
    }

    @Bean
    @Scope("singleton")
    public RedisTemplate<String, Object> redisTemplate() {
        logger.log(Level.INFO, "Creating new redisconfig");
        final RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();

        config.setHostName(redisHost);
        config.setPort(redisPort);
        config.setPassword(apiKey);
        logger.log(Level.INFO, "Successfully set host " + redisHost + " and port " + redisPort);

        // connect config to Jedis driver
        final JedisClientConfiguration jedisClient = JedisClientConfiguration
                .builder()
                .build();
        final JedisConnectionFactory jedisFac = new JedisConnectionFactory(config, jedisClient);
        jedisFac.afterPropertiesSet();
        logger.log(Level.INFO, "jedisFac configured");

        // allow app to communicate with RedisTemplate
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(jedisFac);
        logger.log(Level.INFO, "template configured");

        // convert strings to UTF-8
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        RedisSerializer<Object> serializer = new JdkSerializationRedisSerializer(getClass().getClassLoader());

        template.setValueSerializer(serializer);
        logger.log(Level.INFO, "serializers configured");

        return template;
    }
}
