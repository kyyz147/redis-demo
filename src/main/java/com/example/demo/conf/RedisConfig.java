package com.example.demo.conf;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig extends CachingConfigurerSupport{
			@Autowired
		    private RedisConnectionFactory redisConnectionFactory;
			 @Bean
			 @SuppressWarnings("all")
			public RedisTemplate<String, Object> redisTemplate() {
				 /*RedisConnectionFactory factory*/
		        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
		
		        template.setConnectionFactory(redisConnectionFactory);
		
		        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
		
		        ObjectMapper om = new ObjectMapper();
		
		        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		
		        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		
		        jackson2JsonRedisSerializer.setObjectMapper(om);
		
		        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
		
		        // key采用String的序列化方式
		
		        template.setKeySerializer(stringRedisSerializer);
		
		        // hash的key也采用String的序列化方式
		
		        template.setHashKeySerializer(stringRedisSerializer);
		
		        // value序列化方式采用jackson
		
		        template.setValueSerializer(jackson2JsonRedisSerializer);
		
		        // hash的value序列化方式采用jackson
		
		        template.setHashValueSerializer(jackson2JsonRedisSerializer);
		
		        template.afterPropertiesSet();
		
		        return template;
		
			    }
		 	/*@Bean
		    @Override
		    public CacheManager cacheManager() {
		        // 初始化缓存管理器，在这里我们可以缓存的整体过期时间什么的，我这里默认没有配置
		        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager
		                .RedisCacheManagerBuilder
		                .fromConnectionFactory(redisConnectionFactory);
		        return builder.build();
		    }*/
		 	/*@Bean
		    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory){
		        return RedisCacheManager.create(connectionFactory);
		 	}*/
			 @Bean
			    CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
			        /* 默认配置， 默认超时时间为30s */
			        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration
			                .ofSeconds(30L)).disableCachingNullValues();
			        RedisCacheManager cacheManager = RedisCacheManager.builder(RedisCacheWriter.lockingRedisCacheWriter
			                (connectionFactory)).cacheDefaults(defaultCacheConfig).transactionAware().build();
			        return cacheManager;
			    }
}
@ConfigurationProperties
class DataJedisProperties{
    @Value("${spring.redis.host}")
    private  String host;
    @Value("${spring.redis.password}")
    private  String password;
    @Value("${spring.redis.port}")
    private  int port;
    @Value("${spring.redis.timeout}")
    private  int timeout;
    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle;
    @Value("${spring.redis.jedis.pool.max-wait}")
    private long maxWaitMillis;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
    	RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setPassword(password);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
        /*JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(host);
        factory.setPort(port);
        factory.setTimeout(timeout);
        factory.setPassword(password);
        return factory;*/
    }
    @Bean
    public JedisPool redisPoolFactory() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);

        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
        return jedisPool;
    }
	}
