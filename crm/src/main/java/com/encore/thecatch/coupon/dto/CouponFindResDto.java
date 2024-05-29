package com.encore.thecatch.coupon.dto;

import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.coupon.domain.Coupon;
import com.encore.thecatch.coupon.domain.CouponStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class CouponFindResDto {
    private Long id;
    private String name;
    private String code;
    private CouponStatus status;
    private int quantity;
    private LocalDate startDate;
    private LocalDate endDate;
    private Company company;


    @QueryProjection
    public CouponFindResDto(
            Long id,
            String name,
            String code,
            CouponStatus status,
            int quantity,
            LocalDate startDate,
            LocalDate endDate,
            Company company){
        this.id = id;
        this.name = name;
        this.code = code;
        this.status = status;
        this.quantity = quantity;
        this.startDate =startDate;
        this.endDate = endDate;
        this.company = company;
    }

}
