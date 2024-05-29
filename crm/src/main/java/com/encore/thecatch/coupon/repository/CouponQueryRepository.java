package com.encore.thecatch.coupon.repository;


import com.encore.thecatch.common.querydsl.Querydsl4RepositorySupport;
import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.coupon.domain.Coupon;
import com.encore.thecatch.coupon.domain.CouponStatus;
import com.encore.thecatch.coupon.domain.QCoupon;
import com.encore.thecatch.coupon.dto.CouponFindResDto;
import com.encore.thecatch.coupon.dto.QCouponFindResDto;
import com.encore.thecatch.coupon.dto.QCouponPublishCountRes;
import com.encore.thecatch.coupon.dto.SearchCouponCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

import static org.springframework.util.StringUtils.hasText;

@Repository
public class CouponQueryRepository extends Querydsl4RepositorySupport {

    QCoupon coupon = QCoupon.coupon;

    public CouponQueryRepository() {
        super(Coupon.class);
    }

    public Page<CouponFindResDto> findCouponList(SearchCouponCondition searchCouponCondition, Company company, Pageable pageable) {
        return applyPagination(
                pageable,
                query -> query
                        .select(
                                new QCouponFindResDto(
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
                                eqStartDate(searchCouponCondition.getStartDate()),
                                eqEndDate(searchCouponCondition.getEndDate()),
                                eqStatus(searchCouponCondition.getCouponStatus()),
                                coupon.couponStatus.notIn(CouponStatus.DELETE),
                                coupon.companyId.eq(company))
                        .orderBy(coupon.createdTime.desc()),

                countQuery -> countQuery
                        .selectFrom(coupon)
                        .where(
                                eqName(searchCouponCondition.getName()),
                                eqCode(searchCouponCondition.getCode()),
                                eqStartDate(searchCouponCondition.getStartDate()),
                                eqEndDate(searchCouponCondition.getEndDate()),
                                eqStatus(searchCouponCondition.getCouponStatus()),
                                coupon.companyId.eq(company)));
    }


    public Long couponPublishCount() {
        return select(new QCouponPublishCountRes(
                        coupon.count().as("count")
                ))
                .from(coupon)
                .where(coupon.couponStatus.eq(CouponStatus.PUBLISH))
                .fetchCount();
    }

    public Long couponIssuanceCount() {
        return select(coupon.count())
                .from(coupon)
                .where(coupon.couponStatus.eq(CouponStatus.ISSUANCE))
                .fetchCount();
    }

    public Long couponExpirationCount() {
        return select(coupon.count())
                .from(coupon)
                .where(coupon.endDate.eq(LocalDate.now()))
                .fetchCount();
    }


    private BooleanExpression eqName(String name) {
        return hasText(name) ? coupon.name.eq(name) : null;
    }

    private BooleanExpression eqCode(String code) {
        return hasText(code) ? coupon.code.eq(code) : null;
    }

    private BooleanExpression eqStartDate(String startDate) {
        return hasText(startDate) ? coupon.startDate.eq(LocalDate.from(LocalDate.parse(startDate).atStartOfDay())) : null;

    }

    private BooleanExpression eqEndDate(String endDate) {
        return hasText(endDate) ? coupon.endDate.eq(LocalDate.from(LocalDate.parse(endDate).atStartOfDay())) : null;
    }

    private BooleanExpression eqStatus(String couponStatus) {
        return hasText(couponStatus) ? coupon.couponStatus.eq(CouponStatus.fromValue(couponStatus)) : null;
    }
}
