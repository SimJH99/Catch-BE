package com.encore.event.coupon.application.port.in;

import com.encore.event.common.respone.ResponseDto;

public interface ApplyForLimitedCouponIssueUseCase {
    ResponseDto applyForLimitedCouponIssue(ApplyForLimitedCouponIssueCommend commend);
}
