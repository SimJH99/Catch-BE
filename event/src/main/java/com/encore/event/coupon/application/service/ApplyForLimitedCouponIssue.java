package com.encore.event.coupon.application.service;

import com.encore.event.common.UseCase;
import com.encore.event.coupon.application.port.in.ApplyForLimitedCouponIssueCommend;
import com.encore.event.coupon.application.port.in.ApplyForLimitedCouponIssueUseCase;
import com.encore.event.coupon.application.port.out.ApplyForLimitedCouponIssueOutPort;
import com.encore.event.coupon.application.port.out.RedisCouponOutPort;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class ApplyForLimitedCouponIssue implements ApplyForLimitedCouponIssueUseCase {

    private final ApplyForLimitedCouponIssueOutPort applyForLimitedCouponIssueOutPort;
    private final RedisCouponOutPort redisCouponOutPort;

    @Override
    @Synchronized
    public void applyForLimitedCouponIssue(ApplyForLimitedCouponIssueCommend commend) {



        Long issuedCouponCount = redisCouponOutPort.count(commend);


//        Long count = couponCountOutPort.increment(commend);
//
//        if(count > 100)
//        {
//            return;
//        }

        //MULTI - 트랜젝션 시작
        //set 자료형을 활용한 중복지급 문제 해결
        //key coupon:limited:{쿠폰정책ID}:issued:users
        //value userId

        //SCARD coupon:limited:{쿠폰정책ID}:issued:users

        //EXEC - 트랜젝션 종료

        applyForLimitedCouponIssueOutPort.limitedCouponIssue(commend);
    }


}
