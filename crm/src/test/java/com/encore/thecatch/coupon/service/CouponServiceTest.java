//package com.encore.thecatch.coupon.service;
//
//
//import com.encore.thecatch.coupon.domain.Coupon;
//import com.encore.thecatch.coupon.dto.CouponReqDto;
//import com.encore.thecatch.coupon.dto.CouponResDto;
//import com.encore.thecatch.coupon.repository.CouponRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import javax.transaction.Transactional;
//
//import static org.assertj.core.api.Assertions.*;
//
//@SpringBootTest
//public class CouponServiceTest {
//    @Autowired
//    private CouponService couponService;
//
//    @Test
//    @Transactional
//    void 쿠폰_생성(){
//        CouponReqDto couponReqDto = CouponReqDto.builder()
//                .name("Test1")
//                .quantity(10)
//                .startDate("2024-04-01T00:00:00")
//                .endDate("2024-04-03T00:00:00")
//                .build();
//        Coupon coupon = couponService.create(couponReqDto);
//        CouponResDto savedCoupon = couponService.findById(coupon.getId());
//        assertThat(savedCoupon.getName()).isEqualTo(couponReqDto.getName());
//        assertThat(savedCoupon.getQuantity()).isEqualTo(couponReqDto.getQuantity());
//        assertThat(savedCoupon.getStartDate()).isEqualTo(couponReqDto.getStartDate());
//        assertThat(savedCoupon.getEndDate()).isEqualTo(couponReqDto.getEndDate());
//    }
//
////    void 쿠폰_찾기(){
////        CouponReqDto couponReqDto = CouponReqDto.builder()
////                .name("Test1")
////                .quantity(10)
////                .startDate("2024-04-01T00:00:00")
////                .endDate("2024-04-03T00:00:00")
////                .build();
////        Coupon coupon = couponService.create(couponReqDto);
////
////    }
//}
