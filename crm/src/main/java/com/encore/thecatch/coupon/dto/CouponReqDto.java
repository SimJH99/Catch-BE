package com.encore.thecatch.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponReqDto {
    private String name;
    private int quantity;
    private Long price;
    private String startDate;
    private String endDate;
}
