package com.encore.thecatch.coupon.domain;

import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public enum CouponStatus {
        ISSUANCE("생성"),
        DELETE("삭제"),
        PUBLISH("배포"),
        EXPIRATION("만료"),
        RECEIVE("수령"),
        USED("사용된");
        private final String value;

        CouponStatus(String value){
                this.value = value;
        }

        public static CouponStatus fromValue(String value) {
                if (StringUtils.hasText(value)) {
                        for (CouponStatus status : CouponStatus.values()) {
                                if (status.getValue().equals(value)) {
                                        return status;
                                }
                        }
                }
                throw new IllegalArgumentException("No enum constant for value: " + value);
        }

        public static String toValue(CouponStatus status) {
                return status.getValue();
        }

}