package com.encore.thecatch.coupon.repository;


import com.encore.thecatch.coupon.domain.CouponStatus;
import com.encore.thecatch.coupon.domain.QCoupon;
import com.encore.thecatch.coupon.dto.CouponFindResDto;
import com.encore.thecatch.coupon.dto.QCouponFindResDto;
import com.encore.thecatch.coupon.dto.SearchCouponCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class CouponQueryRepository {

    QCoupon coupon = QCoupon.coupon;

    private final JPAQueryFactory queryFactory;

    public List<CouponFindResDto> findCouponList(SearchCouponCondition searchCouponCondition) throws Exception {
        return queryFactory
                .select(new QCouponFindResDto(
                        coupon.id,
                        coupon.name,
                        coupon.code,
                        coupon.couponStatus,
                        coupon.quantity,
                        coupon.startDate,
                        coupon.endDate,
                        coupon.companyId))
                .from(coupon)
                .where(
                        eqName(searchCouponCondition.getName()),
                        eqCode(searchCouponCondition.getCode()),
                        eqStartDate(LocalDateTime.parse(searchCouponCondition.getStartDate())),
                        eqEndDate(LocalDateTime.parse(searchCouponCondition.getEndDate())),
                        eqStatus(searchCouponCondition.getCouponStatus()))
                .fetch();
    }
        private BooleanExpression eqName(String name) throws Exception {
        return hasText(name) ? coupon.name.eq(name) : null;
    }

    private BooleanExpression eqCode(String code) throws Exception {
        return hasText(code) ? coupon.name.eq(code) : null;
    }

    private BooleanExpression eqStartDate(LocalDateTime startDate) {
        return startDate != null ? coupon.startDate.eq(startDate) : null;
    }
    private BooleanExpression eqEndDate(LocalDateTime endDate) {
        return endDate != null ? coupon.endDate.eq(endDate) : null;
    }
    private BooleanExpression eqStatus(CouponStatus couponStatus) {
        return couponStatus != null ? coupon.couponStatus.eq(couponStatus) : null;
    }
}
