package com.encore.thecatch.coupon.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CouponReqDto {
    private String name;
    private int quantity;
    private String startDate;
    private String endDate;
    private Long companyId;
}
