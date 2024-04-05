package com.encore.event.coupon.adapter.out.redis;

import com.encore.event.coupon.application.port.in.ApplyForLimitedCouponIssueCommend;
import com.encore.event.coupon.application.port.out.RedisCouponOutPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisCouponAdapter implements RedisCouponOutPort {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Long count(ApplyForLimitedCouponIssueCommend command) {
        return null;
    }
}
