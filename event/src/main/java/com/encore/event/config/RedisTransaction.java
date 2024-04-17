package com.encore.event.config;


import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisTransaction {

    public Object execute(
            RedisOperations<String, Object> redisTemplate, RedisOperation operation, Object vo) {

        return redisTemplate.execute(
                new SessionCallback<Object>() {
                    @Override
                    public Object execute(RedisOperations callbackOperations) throws DataAccessException {

                        // [1] REDIS 트랜잭션 Start
                        callbackOperations.multi();

                        // [2] Operation 실행
                        operation.execute(callbackOperations, vo);

                        // [3] REDIS 트랜잭션 End
                        return callbackOperations.exec();
                    }
                });
    }
}