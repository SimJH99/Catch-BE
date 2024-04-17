package com.encore.batch.entity;

public enum CouponStatus {
    ISSUANCE, //발급
    DELETE, // 삭제
    PUBLISH, // 발행
    EXPIRATION, //만료
    RECEIVE,  //수령
    USED; //사용된
}
