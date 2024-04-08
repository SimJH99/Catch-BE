package com.encore.event.coupon.application.port.in;

import lombok.*;


@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = false) //자신 클래스의 필드 값만 고려한다.
public class ApplyForLimitedCouponIssueCommend {
    private String userId;
    private String key;
    private String status;

    @Override
    public String toString() {
        return "{" +
                "memberId='" + userId + '\'' +
                ", couponId='" + key + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
