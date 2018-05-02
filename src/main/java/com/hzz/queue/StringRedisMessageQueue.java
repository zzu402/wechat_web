package com.hzz.queue;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Created by hongshuiqiao on 2017/10/13.
 */
public class StringRedisMessageQueue extends AbstractMessageQueue {
    private StringRedisTemplate redisTemplate;

    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void push(String message) throws InterruptedException {
        redisTemplate.opsForList().leftPush(getQueueName(), message);
    }

    @Override
    public String pop() throws InterruptedException {
        return redisTemplate.opsForList().rightPop(getQueueName());
    }

    @Override
    public String pop(long timeout) throws InterruptedException {
        return redisTemplate.opsForList().rightPop(getQueueName(),timeout, TimeUnit.MILLISECONDS);
    }
}
