package com.huawei.springbootweb.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 基于注解加载配置文件
 */
@Configuration
@PropertySource("classpath:config/redis.properties")
@Slf4j
public class RedisConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    /**
     *
     */
    @Value("${redis.host}")
    private String redisHost;

    /**
     *
     */
    @Value("${redis.port}")
    private Integer redisPort;

    @Value("${redis.maxIdle}")
    private Integer maxIdle;

    @Value("${redis.maxWait}")
    private Integer timeout;

    @Value("${redis.password}")
    private String password;

    /**
     * Jedis 连接工厂.
     *
     * @return 配置好的Jedis连接工厂
     */
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration configuration =
                new RedisStandaloneConfiguration(redisHost, redisPort);
        return new JedisConnectionFactory(configuration);
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        /*
         * Redis 序列化器.
         *
         * RedisTemplate 默认的系列化类是 JdkSerializationRedisSerializer,用JdkSerializationRedisSerializer序列化的话,
         * 被序列化的对象必须实现Serializable接口。在存储内容时，除了属性的内容外还存了其它内容在里面，总长度长，且不容易阅读。
         *
         * Jackson2JsonRedisSerializer 和 GenericJackson2JsonRedisSerializer，两者都能序列化成 json，
         * 但是后者会在 json 中加入 @class 属性，类的全路径包名，方便反系列化。前者如果存放了 List 则在反系列化的时候如果没指定
         * TypeReference 则会报错 java.util.LinkedHashMap cannot be cast to
         */
        RedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        RedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // 定义RedisTemplate，并设置连接工程
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();

        // key 的序列化采用 StringRedisSerializer
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        // value 值的序列化采用 GenericJackson2JsonRedisSerializer
        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);
        // 设置连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    public CacheManager initRedisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager
                .RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory);
        return builder.build();
    }

    @Bean
    public JedisPool redisPoolFactory() {
        log.info("JedisPool注入成功！！");
        log.info("redis地址：" + redisHost + ":" + redisPort);
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        //jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);

        JedisPool jedisPool = new JedisPool(jedisPoolConfig, redisHost, redisPort, timeout, password);
        return jedisPool;
    }

//    @Bean
//    public RedissonClient redissonClient() {
//        Config config = new Config();
//        config.useSingleServer().setAddress("127.0.0.1:6379");
//        RedissonClient redisson = Redisson.create(config);
//
//        RMap<String, String> test = redisson.getMap("test");
//        String value = test.get("key");
//        return redisson;
//    }
//
//    @Bean
//    public RedissonClient redisson() throws IOException {
//        Config config = Config.fromYAML(new ClassPathResource("redisson.yml").getInputStream());
//        RedissonClient redisson = Redisson.create(config);
//        return redisson;
//    }
//
//    @Bean
//    public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redisson) {
//        return new RedissonConnectionFactory(redisson);
//    }

    @Bean("redisTemplate")
    public RedisTemplate getRedisTemplate(RedisConnectionFactory redissonConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redissonConnectionFactory);
        redisTemplate.setValueSerializer(valueSerializer());
        redisTemplate.setKeySerializer(keySerializer());
        redisTemplate.setHashKeySerializer(keySerializer());
        redisTemplate.setHashValueSerializer(valueSerializer());
        return redisTemplate;
    }

    @Bean
    public RedisSerializer keySerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    public RedisSerializer valueSerializer() {
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        return jackson2JsonRedisSerializer;
    }
}
