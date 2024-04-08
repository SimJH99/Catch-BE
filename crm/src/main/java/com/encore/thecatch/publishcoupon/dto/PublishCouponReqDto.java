package com.encore.thecatch.publishcoupon.dto;

import com.encore.thecatch.coupon.domain.CouponStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublishCouponReqDto {
    private Long memberId;
    private Long couponId;
    private CouponStatus status;
}
