package com.encore.event.coupon.application.service;

import com.encore.event.common.UseCase;
import com.encore.event.common.respone.ResponseCode;
import com.encore.event.common.respone.ResponseDto;
import com.encore.event.coupon.application.port.in.ApplyForLimitedCouponIssueCommend;
import com.encore.event.coupon.application.port.in.ApplyForLimitedCouponIssueUseCase;
import com.encore.event.coupon.application.port.out.ApplyForLimitedCouponIssueOutPort;
import com.encore.event.coupon.application.port.out.RedisCouponOutPort;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class ApplyForLimitedCouponIssue implements ApplyForLimitedCouponIssueUseCase {

    private final ApplyForLimitedCouponIssueOutPort applyForLimitedCouponIssueOutPort;
    private final RedisCouponOutPort redisCouponOutPort;

    @Override
    @Synchronized
    public ResponseDto applyForLimitedCouponIssue(ApplyForLimitedCouponIssueCommend commend) {

        Boolean res = redisCouponOutPort.limitedCouponIssue(commend);
        if(res) {
            applyForLimitedCouponIssueOutPort.limitedCouponIssue(commend);
            return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_LIMITED_COUPON,null);
        }

        return new ResponseDto(HttpStatus.OK, ResponseCode.FAIL_LIMITED_COUPON,null);
    }


}
