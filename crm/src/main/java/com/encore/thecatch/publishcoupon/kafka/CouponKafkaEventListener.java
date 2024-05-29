package com.encore.thecatch.publishcoupon.kafka;

import com.encore.thecatch.receivecoupon.service.ReceiveCouponService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class CouponKafkaEventListener {
//
//    private final PublishCouponService service;
//
//
//    @KafkaListener(topics = "limited-coupon-apply")
//    public void listen(@Payload String data) throws JsonProcessingException {
//        log.info("received data : {}", data);
//
//        service.limitedCouponReceive(data);
//    }
//}
