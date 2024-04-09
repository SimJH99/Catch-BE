package com.encore.thecatch.coupon.dto;

import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.coupon.domain.Coupon;

import com.encore.thecatch.coupon.domain.CouponStatus;
<<<<<<< HEAD
import com.encore.thecatch.publishcoupon.domain.PublishCoupon;
=======
import com.encore.thecatch.publish_coupon.domain.PublishCoupon;
import com.querydsl.core.annotations.QueryProjection;
>>>>>>> origin/dev
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CouponResDto {
    private Long id;
    private String name;
    private String code;
    private CouponStatus status;
    private int quantity;
    private String startDate;
    private String endDate;
    private Company company;

    public static CouponResDto toCouponResDto(Coupon coupon){
        CouponResDtoBuilder builder = CouponResDto.builder();
        builder.id(coupon.getId());
        builder.name(coupon.getName());
        builder.code(coupon.getCode());
        builder.status(coupon.getCouponStatus());
        builder.quantity(coupon.getQuantity());
        builder.startDate(String.valueOf(coupon.getStartDate()));
        builder.endDate(String.valueOf(coupon.getEndDate()));
        builder.company(coupon.getCompanyId());
        return builder.build();
    }

    public static CouponResDto publishToCouponDto(PublishCoupon publishCoupon){
        Coupon coupon = publishCoupon.getCoupon();
        return CouponResDto.toCouponResDto(coupon);
    }



}
