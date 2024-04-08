package com.encore.thecatch.coupon.controller;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class LimitedCouponApplyConsumer {

    @KafkaListener(topics = "limited-coupon-apply", groupId = "catchevent")
    public void listener(String body){
        System.out.println("body === " + body);

    }


}
