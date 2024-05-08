package com.encore.thecatch.log.service;

//import com.encore.thecatch.log.repository.EmailLogQueryRepository;

import com.encore.thecatch.common.CatchException;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.coupon.domain.Coupon;
import com.encore.thecatch.coupon.repository.CouponRepository;
import com.encore.thecatch.event.domain.Event;
import com.encore.thecatch.event.repository.EventRepository;
import com.encore.thecatch.log.dto.DayOfWeekLogin;
import com.encore.thecatch.log.dto.VisitTodayUserRes;
import com.encore.thecatch.log.repository.CouponEmailLogQueryRepository;
import com.encore.thecatch.log.repository.EmailLogQueryRepository;
import com.encore.thecatch.log.repository.UserLogQueryRepository;
import com.encore.thecatch.receivecoupon.repository.ReceiveCouponQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LogService {
    private final UserLogQueryRepository userLogQueryRepository;
    private final EmailLogQueryRepository emailLogQueryRepository;
    private final CouponEmailLogQueryRepository couponEmailLogQueryRepository;
    private final CouponRepository couponRepository;
    private final ReceiveCouponQueryRepository receiveCouponQueryRepository;
    private final EventRepository eventRepository;


    public Long visitTotalUser() {
        return userLogQueryRepository.visitTotalUser();
    }

    public Long visitToday() {
        return userLogQueryRepository.visitToday();
    }

    public Long visitTodayUserCount() {
        List<VisitTodayUserRes> list = userLogQueryRepository.visitTodayUser();
        return (long) list.size();
    }

    public List<DayOfWeekLogin> dayOfWeekLogin() {
        return userLogQueryRepository.dayOfWeekLogin();
    }

    public Long totalEmail() {
        return emailLogQueryRepository.totalEmail();
    }

    public Long couponSendCount(Long id) {
        Coupon coupon = couponRepository.findById(id).orElseThrow(() -> new CatchException(ResponseCode.COUPON_NOT_FOUND));
        return  couponEmailLogQueryRepository.couponSendCount(coupon);
    }

    public Long couponReceiveCount(Long id) {
        Coupon coupon = couponRepository.findById(id).orElseThrow(() -> new CatchException(ResponseCode.COUPON_NOT_FOUND));
        return receiveCouponQueryRepository.couponReceiveCount(coupon);
    }

    public Long eventSendCount(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new CatchException(ResponseCode.EVENT_NOT_FOUND));
        return emailLogQueryRepository.eventSendCount(event);
    }

    public Long eventReceiveCount(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new CatchException(ResponseCode.EVENT_NOT_FOUND));
        return emailLogQueryRepository.eventReceiveCount(event);
    }
}
