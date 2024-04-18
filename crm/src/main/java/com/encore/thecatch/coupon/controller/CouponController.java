package com.encore.thecatch.coupon.controller;

import com.encore.thecatch.common.DefaultResponse;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.ResponseDto;
import com.encore.thecatch.coupon.domain.Coupon;
import com.encore.thecatch.coupon.dto.*;
import com.encore.thecatch.coupon.service.CouponService;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupon")
public class CouponController {


    private final CouponService couponService;

    @Autowired
    public CouponController(CouponService couponService){
        this.couponService = couponService;
    }

    @PostMapping("/create")
    public ResponseDto couponCreate(@RequestBody CouponReqDto couponReqDto){
        Coupon coupon = couponService.createCoupon(couponReqDto);
        return new ResponseDto(HttpStatus.CREATED, ResponseCode.SUCCESS_CREATE_COUPON, new DefaultResponse<Long>(coupon.getId()));
    }

    @PatchMapping("/{id}/publish")
    public ResponseDto couponPublish(@PathVariable Long id) throws Exception {
        Coupon coupon = couponService.publish(id);
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_PUBLISH_COUPON, new DefaultResponse<Long>(coupon.getId()));
    }

    @PatchMapping("/receive")
    public ResponseDto couponReceive(@RequestBody CouponReceiveDto couponReceiveDto){
        Coupon coupon = couponService.receive(couponReceiveDto);
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_RECEIVE_COUPON, new DefaultResponse<Long>(coupon.getId()));
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/list")
    public ResponseDto findAll(Pageable pageable) {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, new DefaultResponse.PagedResponse<CouponResDto>(couponService.findAll(pageable)));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/search")
    public ResponseDto searchCoupon(@RequestBody SearchCouponCondition searchCouponCondition, Pageable pageable)throws Exception{
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, new DefaultResponse.PagedResponse<CouponFindResDto>(couponService.searchCoupon(searchCouponCondition, pageable)));
    }
    @GetMapping("/myList")
    public ResponseDto findMyAll() {
        List<CouponResDto> couponResDtos = couponService.findMyAll();
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, new DefaultResponse.ListResponse<CouponResDto>(couponResDtos));
    }

    @GetMapping("/{id}")
    public ResponseDto couponRead(@PathVariable Long id){
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, new DefaultResponse<CouponResDto>(couponService.findById(id)));
    }


    @PatchMapping("/{id}/update")
    public ResponseDto couponUpdate(@PathVariable Long id, @RequestBody CouponReqDto couponReqDto){
        Coupon coupon = couponService.couponUpdate(id, couponReqDto);
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS , new DefaultResponse<Long>(coupon.getId()));
    }

    @PatchMapping("/{id}/delete")
    public ResponseDto couponDelete(@PathVariable Long id){
        couponService.couponDelete(id);
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_DELETE_COUPON, new DefaultResponse<Long>(id));
    }

}
