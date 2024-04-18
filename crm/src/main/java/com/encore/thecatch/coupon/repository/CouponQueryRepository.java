package com.encore.thecatch.coupon.repository;


import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.company.domain.QCompany;
import com.encore.thecatch.coupon.domain.Coupon;
import com.encore.thecatch.coupon.domain.CouponStatus;
import com.encore.thecatch.coupon.domain.QCoupon;
import com.encore.thecatch.coupon.dto.CouponFindResDto;
import com.encore.thecatch.coupon.dto.QCouponFindResDto;
import com.encore.thecatch.coupon.dto.QCouponPublishCountRes;
import com.encore.thecatch.coupon.dto.SearchCouponCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;


import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class CouponQueryRepository {

    QCoupon coupon = QCoupon.coupon;

    private final JPAQueryFactory queryFactory;

    public Page<CouponFindResDto> findCouponList(SearchCouponCondition searchCouponCondition, Company company, Pageable pageable) throws Exception {
        List<CouponFindResDto> content = queryFactory
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
                        eqStartDate(searchCouponCondition.getStartDate()),
                        eqEndDate(searchCouponCondition.getEndDate()),
                        eqStatus(searchCouponCondition.getCouponStatus()),
                        coupon.companyId.eq(company))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Coupon> countQuery = queryFactory
                .selectFrom(coupon)
                .where(
                        eqName(searchCouponCondition.getName()),
                        eqCode(searchCouponCondition.getCode()),
                        eqStartDate(searchCouponCondition.getStartDate()),
                        eqEndDate(searchCouponCondition.getEndDate()),
                        eqStatus(searchCouponCondition.getCouponStatus()),
                        coupon.companyId.eq(company)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
    }

    public Long couponPublishCount() {
        return queryFactory
                .select(new QCouponPublishCountRes(
                        coupon.count().as("count")
                ))
                .from(coupon)
                .where(coupon.couponStatus.eq(CouponStatus.PUBLISH))
                .fetchCount();
    }

        private BooleanExpression eqName(String name) throws Exception {
        return hasText(name) ? coupon.name.eq(name) : null;
    }

    private BooleanExpression eqCode(String code) throws Exception {
        return hasText(code) ? coupon.name.eq(code) : null;
    }

    private BooleanExpression eqStartDate(String startDate) {
        return hasText(startDate) ? coupon.startDate.eq(LocalDate.parse(startDate)) : null;
    }
    private BooleanExpression eqEndDate(String endDate) {
        return hasText(endDate) ? coupon.startDate.eq(LocalDate.parse(endDate)) : null;
    }
    private BooleanExpression eqStatus(String couponStatus) {
        return hasText(couponStatus) ? coupon.couponStatus.eq(CouponStatus.fromValue(couponStatus)) : null;
    }
}
