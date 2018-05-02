package com.hzz.queue;

import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Created by hongshuiqiao on 2017/10/13.
 */
public class StringRedisMqCreator implements MessageQueueCreator {
    private StringRedisTemplate redisTemplate;

    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public MessageQueue createMq(String name) {
        StringRedisMessageQueue queue = new StringRedisMessageQueue();
        queue.setQueueName(name);
        queue.setRedisTemplate(redisTemplate);
        return queue;
    }
}
