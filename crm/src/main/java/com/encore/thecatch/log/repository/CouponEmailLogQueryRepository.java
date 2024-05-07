package com.encore.thecatch.log.repository;

import com.encore.thecatch.common.querydsl.Querydsl4RepositorySupport;
import com.encore.thecatch.coupon.domain.Coupon;
import com.encore.thecatch.log.domain.CouponEmailLog;
import com.encore.thecatch.log.domain.LogType;
import com.encore.thecatch.log.domain.QCouponEmailLog;
import org.springframework.stereotype.Repository;

@Repository
public class CouponEmailLogQueryRepository extends Querydsl4RepositorySupport {

QCouponEmailLog couponEmailLog = QCouponEmailLog.couponEmailLog;

    public CouponEmailLogQueryRepository (){
        super(CouponEmailLog.class);
    }

    public Long couponSendCount(Coupon coupon) {
        return select(couponEmailLog.count())
                .from(couponEmailLog)
                .where(
                        couponEmailLog.type.eq(LogType.COUPON_EMAIL_SEND),
                        couponEmailLog.coupon.eq(coupon))
                .fetchCount();
    }
}
