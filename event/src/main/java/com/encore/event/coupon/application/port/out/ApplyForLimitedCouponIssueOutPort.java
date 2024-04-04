package com.encore.event.coupon.application.port.out;

import com.encore.event.coupon.application.port.in.ApplyForLimitedCouponIssueCommend;

public interface ApplyForLimitedCouponIssueOutPort {
    public Boolean apply(ApplyForLimitedCouponIssueCommend commend);
}
