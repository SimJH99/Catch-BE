package com.encore.event.coupon.adapter.out.kafka;

import com.encore.event.coupon.application.port.in.ApplyForLimitedCouponIssueCommend;
import com.encore.event.coupon.application.port.out.ApplyForLimitedCouponIssueOutPort;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaCouponProducerAdapter implements ApplyForLimitedCouponIssueOutPort {

    private final KafkaTemplate<String,String> kafkaTemplate;

    @Override
    public Boolean limitedCouponIssue(ApplyForLimitedCouponIssueCommend commend) {
        ProducerRecord<String,String> record = new ProducerRecord<>("limited-coupon-apply","coupon", commend.toString());
        kafkaTemplate.send(record);
        System.out.println("[KAFKA] couponId: "+record.key()+" topic: "+record.topic()+" value: "+record.value());
        return true;
    }

}
