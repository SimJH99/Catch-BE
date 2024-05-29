package com.encore.event.coupon.adapter.out.redis.operation;

import com.encore.event.config.RedisOperation;
import com.encore.event.coupon.application.port.in.ApplyForLimitedCouponIssueCommend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class LimitedCouponIssueOperation implements RedisOperation<ApplyForLimitedCouponIssueCommend> {

    public Long count(RedisOperations<String, Object> operations, ApplyForLimitedCouponIssueCommend commend) {
        //key 가져와서
        String key = "limited:coupon:"+commend.getCouponId()+":userIssue";
        //key의 카운트를 셈
        Long size = operations.opsForSet().size(key);

        log.info("[LimitedCouponIssueOperation] [count] key ::: {}, size ::: {}", key, size);
        return size;
    }


    public Long add(RedisOperations<String, Object> operations, ApplyForLimitedCouponIssueCommend commend) {
        String key = "limited:coupon:"+commend.getCouponId()+":userIssue";
        String value = this.generateValue(commend);
        Long result = operations.opsForSet().add(key, value);
        log.info(
                "[LimitedCouponIssueOperation] [add] key ::: {}, value ::: {}, result ::: {}", key, value, result);
        return result;
    }

    public Long remove(RedisOperations<String, Object> operations, ApplyForLimitedCouponIssueCommend commend) {
        String key = "limited:coupon:"+commend.getCouponId()+":userIssue";
        String value = this.generateValue(commend);
        Long result = operations.opsForSet().remove(key, value);
        log.info(
                "[LimitedCouponIssueOperation] [remove] key ::: {}, value ::: {}, result ::: {}",
                key,
                value,
                result);
        return result;
    }

    public Boolean delete(RedisOperations<String, Object> operations, ApplyForLimitedCouponIssueCommend commend) {
        String key = "limited:coupon:"+commend.getCouponId()+":userIssue";
        Boolean result = operations.delete(key);
        log.info("[LimitedCouponIssueOperation] [delete] key ::: {}, result ::: {}", key, result);
        return result;
    }

    public Boolean expire(RedisOperations<String, Object> operations, ApplyForLimitedCouponIssueCommend commend, Duration duration) {
        String key = "limited:coupon:"+commend.getCouponId()+":userIssue";
        Boolean result = operations.expire(key, duration);
        log.info(
                "[LimitedCouponIssueOperation] [expire] key ::: {}, expire ::: {}, result ::: {}",
                key,
                duration,
                result);
        return result;
    }


    public String generateValue(ApplyForLimitedCouponIssueCommend commend) {
        return String.valueOf(commend.getUserId());
    }

    public void execute(RedisOperations<String, Object> operations, ApplyForLimitedCouponIssueCommend commend) {

        this.count(operations, commend);
        this.add(operations, commend);

    }
}
