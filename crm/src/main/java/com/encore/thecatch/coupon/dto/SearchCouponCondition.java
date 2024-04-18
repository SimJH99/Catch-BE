package com.encore.thecatch.coupon.dto;

import com.encore.thecatch.coupon.domain.CouponStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
public class SearchCouponCondition {
    private String name;
    private String code;
    private String startDate;
    private String endDate;
    private String couponStatus;
}
