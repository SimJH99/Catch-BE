package com.encore.thecatch.publishcoupon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KafkaLimitedCoupon {
    private Long userId;
    private Long couponId;
}
