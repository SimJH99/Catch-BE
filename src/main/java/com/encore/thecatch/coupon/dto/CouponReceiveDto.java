package com.encore.thecatch.coupon.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponReceiveDto {
    private String code;

//    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
//    public CouponReceiveDto(String code) {
//        this.code = code;
//    }
}