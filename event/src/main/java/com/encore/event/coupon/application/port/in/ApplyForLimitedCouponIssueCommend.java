package com.encore.event.coupon.application.port.in;

import lombok.*;


@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = false) //자신 클래스의 필드 값만 고려한다.
public class ApplyForLimitedCouponIssueCommend {
    private Long userId;
    private String key;

    @Override
    public String toString() {
        return "{" +
                "userId:" + userId +
                ", key:'" + key + '\'' +
                '}';
    }
}
