package com.encore.thecatch.coupon.dto;

import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.coupon.domain.Coupon;
import com.encore.thecatch.publishcoupon.domain.PublishCoupon;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CouponResDto {
    private String name;
    private String code;
    private int quantity;
    private String startDate;
    private String endDate;
    private Company company;

    public static CouponResDto toCouponResDto(Coupon coupon){
        CouponResDtoBuilder builder = CouponResDto.builder();
        builder.name(coupon.getName());
        builder.code(coupon.getCode());
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
