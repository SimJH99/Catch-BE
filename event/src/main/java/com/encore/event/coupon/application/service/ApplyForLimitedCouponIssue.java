package com.encore.event.coupon.application.service;

import com.encore.event.common.UseCase;
import com.encore.event.coupon.application.port.in.ApplyForLimitedCouponIssueCommend;
import com.encore.event.coupon.application.port.in.ApplyForLimitedCouponIssueUseCase;
import com.encore.event.coupon.application.port.out.ApplyForLimitedCouponIssueOutPort;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class ApplyForLimitedCouponIssue implements ApplyForLimitedCouponIssueUseCase {

    private final ApplyForLimitedCouponIssueOutPort applyForLimitedCouponIssueOutPort;
    @Override
    public void applyForLimitedCouponIssue(ApplyForLimitedCouponIssueCommend commend) {
        applyForLimitedCouponIssueOutPort.apply(commend);
    }


}
