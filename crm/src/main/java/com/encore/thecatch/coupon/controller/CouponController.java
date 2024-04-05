package com.encore.thecatch.coupon.controller;

import com.encore.thecatch.common.DefaultResponse;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.ResponseDto;
import com.encore.thecatch.coupon.domain.Coupon;
import com.encore.thecatch.coupon.dto.CouponReceiveDto;
import com.encore.thecatch.coupon.dto.CouponReqDto;
import com.encore.thecatch.coupon.dto.CouponResDto;
import com.encore.thecatch.coupon.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseDto couponCreate(@RequestBody CouponReqDto couponReqDto) throws Exception{
        System.out.println(couponReqDto.getName());
        System.out.println(couponReqDto.getStartDate());
        Coupon coupon = couponService.create(couponReqDto);
        return new ResponseDto(HttpStatus.CREATED, ResponseCode.SUCCESS_CREATE_COUPON, new DefaultResponse<Long>(coupon.getId()));
    }

    @PatchMapping("/publish/{id}")
    public ResponseDto couponPublish(@PathVariable Long id){
        Coupon coupon = couponService.publish(id);
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_PUBLISH_COUPON, new DefaultResponse<Long>(coupon.getId()));
    }

    @PatchMapping("/receive")
    public ResponseDto couponReceive(@RequestBody CouponReceiveDto couponReceiveDto){
        Coupon coupon = couponService.receive(couponReceiveDto);
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_RECEIVE_COUPON, new DefaultResponse<Long>(coupon.getId()));
    }

    @GetMapping("/list")
    public ResponseDto findAll() {
        List<CouponResDto> couponResDtos = couponService.findAll();
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, new DefaultResponse.ListResponse<CouponResDto>(couponResDtos));
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
