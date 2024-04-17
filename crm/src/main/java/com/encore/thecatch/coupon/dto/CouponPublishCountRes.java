package com.encore.thecatch.coupon.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CouponPublishCountRes {
    private Long count;

    @QueryProjection
    public CouponPublishCountRes(Long count){
        this.count = count;
    }
}
