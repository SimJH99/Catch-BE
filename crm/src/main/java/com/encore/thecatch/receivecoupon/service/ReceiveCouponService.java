package com.encore.thecatch.receivecoupon.service;

import com.encore.thecatch.common.CatchException;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.coupon.domain.Coupon;
import com.encore.thecatch.coupon.domain.CouponStatus;
import com.encore.thecatch.coupon.repository.CouponRepository;
import com.encore.thecatch.receivecoupon.domain.ReceiveCoupon;
import com.encore.thecatch.receivecoupon.dto.KafkaLimitedCoupon;
import com.encore.thecatch.receivecoupon.repository.ReceiveCouponRepository;
import com.encore.thecatch.user.domain.User;
import com.encore.thecatch.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReceiveCouponService {

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final ReceiveCouponRepository receiveCouponRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public Coupon limitedCouponReceive(String data) throws JsonProcessingException {

        KafkaLimitedCoupon limitedCoupon = objectMapper.readValue(data, KafkaLimitedCoupon.class);

        User user = userRepository.findById(limitedCoupon.getUserId()).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND));
        Coupon coupon = couponRepository.findById(limitedCoupon.getCouponId()).orElseThrow(()->new CatchException(ResponseCode.COUPON_NOT_FOUND));

        List<ReceiveCoupon> receiveCouponList = receiveCouponRepository.findByCouponIdAndUserId(coupon.getId(),user.getId());

        if(receiveCouponList.isEmpty()) {
            ReceiveCoupon receiveCoupon = ReceiveCoupon.builder().coupon(coupon).user(user).couponStatus(CouponStatus.ISSUANCE).build();
            receiveCouponRepository.save(receiveCoupon);
        }

        return coupon;
    }


}
