package com.encore.event.coupon.application.port.out;

import com.encore.event.coupon.application.port.in.ApplyForLimitedCouponIssueCommend;

public interface RedisCouponOutPort {
    Long count(ApplyForLimitedCouponIssueCommend command);
}
