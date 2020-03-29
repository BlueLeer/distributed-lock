package com.lee.distributed.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;

/**
 * @author lee
 * @date 2020/3/29 11:42
 */
@Component
public class RedisLock {

    // if not exist
    public static final String NX = "nx";
    // 超时时间单位:秒
    public static final String EX = "ex";
    // 超时时间单位:毫秒
    public static final String PX = "px";
    public static final String LOCK_SUCCESS = "OK";
    public static final Long UNLOCK_SUCCESS = 1L;

    @Autowired
    private JedisPool jedisPool;

    public boolean tryLock(String lockKey, String lockId, int expireSeconds) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String result = jedis.set(lockKey, lockId, NX, EX, expireSeconds);
            if (LOCK_SUCCESS.equals(result)) {
                return true;
            }
            return false;
        } finally {
            jedis.close();
        }
    }

    /**
     * 解锁的时候,为了避免解了别的线程加的锁需要判断一下,通常这个lockId可以用ThreadID来代替,判断和解锁不是原子性的,因此需要用lua脚本来实现
     *
     * @param lockKey
     * @param lockId
     * @return
     */
    public boolean unlock(String lockKey, String lockId) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String lua_script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
            Object eval = jedis.eval(lua_script, Collections.singletonList(lockKey), Collections.singletonList(lockId));
            if (UNLOCK_SUCCESS.equals(eval)) {
                return true;
            }
            return false;
        } finally {
            jedis.close();
        }
    }
}
