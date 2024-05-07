package com.encore.thecatch.receivecoupon.repository;

import com.encore.thecatch.common.querydsl.Querydsl4RepositorySupport;
import com.encore.thecatch.coupon.domain.Coupon;
import com.encore.thecatch.coupon.domain.CouponStatus;
import com.encore.thecatch.receivecoupon.domain.QReceiveCoupon;
import com.encore.thecatch.receivecoupon.domain.ReceiveCoupon;
import org.springframework.stereotype.Repository;

@Repository
public class ReceiveCouponQueryRepository extends Querydsl4RepositorySupport {
    QReceiveCoupon receiveCoupon = QReceiveCoupon.receiveCoupon;

    public ReceiveCouponQueryRepository (){
        super(ReceiveCoupon.class);
    }

    public Long couponReceiveCount(Coupon coupon) {
        return select(receiveCoupon.count())
                .distinct()
                .from(receiveCoupon)
                .where(
                        receiveCoupon.coupon.eq(coupon),
                        receiveCoupon.couponStatus.eq(CouponStatus.RECEIVE))
                .fetchCount();
    }
}
