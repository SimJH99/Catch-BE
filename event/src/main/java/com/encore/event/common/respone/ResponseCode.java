package com.encore.event.common.respone;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum ResponseCode {

    SUCCESS("SUCCESS", "성공"),
    SUCCESS_LIMITED_COUPON("SUCCESS_LIMITED_COUPON", "선착순 쿠폰이 발급되었습니다."),
    FAIL_LIMITED_COUPON("FAIL_LIMITED_COUPON", "선착순 쿠폰 발급이 마감되었습니다."),
    ;


    private final String code;
    private final String label;

    ResponseCode(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
