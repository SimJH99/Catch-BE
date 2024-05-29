package com.encore.thecatch.coupon.dto;

import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.coupon.domain.Coupon;
import com.encore.thecatch.receivecoupon.domain.ReceiveCoupon;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CouponResDto {
    private Long id;
    private String name;
    private String code;
    private String status;
    private int quantity;
    private Long price;
    private String startDate;
    private String endDate;
    private Company company;

    public static CouponResDto toCouponResDto(Coupon coupon){
        CouponResDto couponResDto = CouponResDto.builder()
                .id(coupon.getId())
                .name(coupon.getName())
                .code(coupon.getCode())
                .status(coupon.getCouponStatus().getValue())
                .quantity(coupon.getQuantity())
                .price(coupon.getPrice())
                .startDate(String.valueOf(coupon.getStartDate()))
                .endDate(String.valueOf(coupon.getEndDate()))
                .company(coupon.getCompanyId())
                .build();
        return couponResDto;

    }

    public static CouponResDto publishToCouponDto(ReceiveCoupon receiveCoupon){
        Coupon coupon = receiveCoupon.getCoupon();
        return CouponResDto.toCouponResDto(coupon);
    }



}
