package com.encore.event.coupon.application.port.out;

import com.encore.event.coupon.application.port.in.ApplyForLimitedCouponIssueCommend;
import org.springframework.data.redis.core.RedisOperations;

public interface RedisCouponOutPort {

    Boolean limitedCouponIssue(ApplyForLimitedCouponIssueCommend commend);
}
