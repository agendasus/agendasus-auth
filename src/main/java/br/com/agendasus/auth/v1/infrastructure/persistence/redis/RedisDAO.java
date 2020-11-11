package br.com.agendasus.auth.v1.infrastructure.persistence.redis;

import br.com.agendasus.auth.v1.infrastructure.system.SystemProperties;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.Set;

@Repository
public class RedisDAO {

    @Autowired
    private SystemProperties properties;

    protected static JedisPool jedisPool;
    static final Gson gson = new Gson();

    @Autowired
    public void init() {
        final JedisPoolConfig poolConfig = buildPoolConfig();
        jedisPool = new JedisPool(poolConfig, properties.getRedisHost(), properties.getRedisPort(), 2000, properties.getRedisPassword());
    }

    private JedisPoolConfig buildPoolConfig() {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(3);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
        poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }

    public void del(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }

    public void expire(String key, int i) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.expire(key, i);
        }
    }

    public Set<String> keys(String pattern) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.keys(pattern);
        }
    }

    public void set(String key, String value){
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value);
        }
    }

    public String get(String key){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    public void increment(String key){
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.incr(key);
        }
    }

    public void decrement(String key){
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.decr(key);
        }
    }

}
