package com.ll.TeamProject.global.redis

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RedisService(
    private val redisTemplate: StringRedisTemplate
) {

    fun setValue(key: String, value: String, timeoutSeconds: Long) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(timeoutSeconds))
    }

    fun getValue(key: String): String? {
        return redisTemplate.opsForValue().get(key)
    }

    fun deleteValue(key: String) {
        redisTemplate.delete(key)
    }
}
