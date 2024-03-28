package com.encore.thecatch.coupon.controller;

import com.encore.thecatch.common.CommonResponse;
import com.encore.thecatch.coupon.domain.Coupon;
import com.encore.thecatch.coupon.dto.CouponReceiveDto;
import com.encore.thecatch.coupon.dto.CouponReqDto;
import com.encore.thecatch.coupon.dto.CouponResDto;
import com.encore.thecatch.coupon.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<CommonResponse> couponCreate(@RequestBody CouponReqDto couponReqDto){
        Coupon coupon = couponService.create(couponReqDto);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.CREATED, "coupon success created", coupon.getId()), HttpStatus.CREATED);
    }

    @PatchMapping("/publish/{id}")
//    public ResponseEntity<CommonResponse> couponPublish(@RequestBody List<MemberReqDto> memberReqDtos)
    public ResponseEntity<CommonResponse> couponPublish(@PathVariable Long id){
        Coupon coupon = couponService.publish(id);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK, "coupon success publish", coupon.getId()), HttpStatus.OK);
    }

    @PatchMapping("/receive")
    public ResponseEntity<CommonResponse> couponReceive(@RequestBody CouponReceiveDto couponReceiveDto){
        Coupon coupon = couponService.receive(couponReceiveDto);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK, "coupon success receive", coupon.getName()),HttpStatus.OK);
    }

    @GetMapping("/list/{companyId}")
    public ResponseEntity<CommonResponse> findAll(@PathVariable Long companyId) {
        List<CouponResDto> couponResDtos = couponService.findAll(companyId);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK, "success check", couponResDtos), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse> couponRead(@PathVariable Long id){
        Coupon coupon = couponService.findById(id);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK, "coupon success read", CouponResDto.toCouponResDto(coupon)), HttpStatus.OK);
    }


    @PatchMapping("/{id}/update")
    public ResponseEntity<CommonResponse> couponUpdate(@PathVariable Long id, @RequestBody CouponReqDto couponReqDto){
        Coupon coupon = couponService.couponUpdate(id, couponReqDto);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK, "coupon success uppdate", coupon.getId()), HttpStatus.OK);
    }

    @PatchMapping("/{id}/delete")
    public ResponseEntity<CommonResponse> couponDelete(@PathVariable Long id){
        couponService.couponDelete(id);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK, "coupon success delete", null), HttpStatus.OK);
    }



}
