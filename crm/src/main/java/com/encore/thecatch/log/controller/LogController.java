package com.encore.thecatch.log.controller;

import com.encore.thecatch.common.DefaultResponse;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.ResponseDto;
import com.encore.thecatch.log.dto.DayOfWeekLogin;
import com.encore.thecatch.log.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/log")
public class LogController {

    private final LogService logService;

    //고객 총 방문 수
    @GetMapping("/visit/total")
    public ResponseDto visitTotalUser(){
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS
                , new DefaultResponse<Long>(logService.visitTotalUser()));
    }

    //고객 오늘 총 방문 수
    @GetMapping("/visit/today")
    public ResponseDto visitToday(){
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS
                , new DefaultResponse<Long>(logService.visitToday()));
    }

    //고객 오늘 실 방문수
    @GetMapping("/visit/today/user")
    public ResponseDto visitTodayUser(){
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS
                , new DefaultResponse<Long>(logService.visitTodayUserCount()));
    }

    //고객 지난주 요일별 방문수
    @GetMapping("/visit/week/user")
    public ResponseDto dayOfWeekLogin(){
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS
                , new DefaultResponse<List<DayOfWeekLogin>>(logService.dayOfWeekLogin()));
    }

    //이메일 총 발송 건수
    @GetMapping("/email/total")
    public ResponseDto totalEmail(){
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS
                , new DefaultResponse<Long>(logService.totalEmail()));
    }

    @GetMapping("/coupon/{id}/send/count")
    public ResponseDto couponSendCount(@PathVariable Long id){
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS
                , new DefaultResponse<Long>(logService.couponSendCount(id)));
    }

    @GetMapping("/coupon/{id}/receive/count")
    public ResponseDto couponReceiveCount(@PathVariable Long id){
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS
                , new DefaultResponse<Long>(logService.couponReceiveCount(id)));
    }

    @GetMapping("/event/{id}/send/count")
    public ResponseDto eventSendCount(@PathVariable Long id){
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS
                , new DefaultResponse<Long>(logService.eventSendCount(id)));
    }

    @GetMapping("/event/{id}/receive/count")
    public ResponseDto eventReceiveCount(@PathVariable Long id){
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS
                , new DefaultResponse<Long>(logService.eventReceiveCount(id)));
    }


}
