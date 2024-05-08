package com.encore.thecatch.coupon.controller;

import com.encore.thecatch.common.DefaultResponse;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.ResponseDto;
import com.encore.thecatch.coupon.domain.Coupon;
import com.encore.thecatch.coupon.dto.*;
import com.encore.thecatch.coupon.service.CouponService;
import com.encore.thecatch.user.dto.request.PublishUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupon")
public class CouponController {


    private final CouponService couponService;

    @Autowired
    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping("/create")
    public ResponseDto couponCreate(@RequestBody CouponReqDto couponReqDto){
        Coupon coupon = couponService.createCoupon(couponReqDto);
        return new ResponseDto(HttpStatus.CREATED, ResponseCode.SUCCESS_CREATE_COUPON, new DefaultResponse<Long>(coupon.getId()));
    }

    @PatchMapping("/{id}/couponNotificationSend")
    public ResponseDto couponNotificationSend(@PathVariable Long id, @RequestBody PublishUserDto publishUserDto) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_PUBLISH_COUPON, couponService.createCouponNotification(id, publishUserDto));
    }


    @PostMapping("/receive")
    public ResponseDto couponReceive(@RequestBody CouponReceiveDto couponReceiveDto) {
        Coupon coupon = couponService.receive(couponReceiveDto);
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_RECEIVE_COUPON, new DefaultResponse<Long>(coupon.getId()));
    }

    @GetMapping("/list")
    public ResponseDto findAll(Pageable pageable) {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, new DefaultResponse.PagedResponse<CouponResDto>(couponService.findAll(pageable)));
    }

    @PostMapping("/search")
    public ResponseDto searchCoupon(@RequestBody SearchCouponCondition searchCouponCondition, Pageable pageable)throws Exception{
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, new DefaultResponse<Page<CouponFindResDto>>(couponService.searchCoupon(searchCouponCondition, pageable)));
    }

    @GetMapping("/myList")
    public ResponseDto findMyAll(Pageable pageable) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, new DefaultResponse.PagedResponse<CouponResDto>(couponService.findMyAll(pageable)));
    }
    @GetMapping("/myCouponCount")
    public ResponseDto findMyCouponCount() throws Exception{
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, new DefaultResponse<Integer>(couponService.findMyCouponCount()));
    }

    @GetMapping("/receivable")
    public ResponseDto findReceivable() throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, new DefaultResponse.ListResponse<CouponResDto>(couponService.findReceivable()));
    }

    @GetMapping("/{id}")
    public ResponseDto couponRead(@PathVariable Long id) {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, new DefaultResponse<CouponResDto>(couponService.findById(id)));
    }


    @PatchMapping("/{id}/update")
    public ResponseDto couponUpdate(@PathVariable Long id, @RequestBody CouponReqDto couponReqDto) {
        Coupon coupon = couponService.couponUpdate(id, couponReqDto);
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, new DefaultResponse<Long>(coupon.getId()));
    }

    @PostMapping("/{id}/delete")
    public ResponseDto couponDelete(@PathVariable Long id) {
        couponService.couponDelete(id);
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_DELETE_COUPON, "SUCCESS");
    }

    @GetMapping("/publish/count")
    public ResponseDto couponPublishCount() {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, new DefaultResponse<Long>(couponService.couponPublishCount()));
    }

    @GetMapping("/issuance/count")
    public ResponseDto couponIssuanceCount() {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, new DefaultResponse<Long>(couponService.couponIssuanceCount()));
    }

    //오늘 만료될 쿠폰
    @GetMapping("/expiration/count")
    public ResponseDto couponExpirationCount() {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, new DefaultResponse<Long>(couponService.couponExpirationCount()));
    }

    @PatchMapping("/{id}/publish")
    public ResponseDto couponPublish(@PathVariable Long id) throws Exception {
        couponService.couponPublish(id);
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, new DefaultResponse<Long>(id));
    }
}
