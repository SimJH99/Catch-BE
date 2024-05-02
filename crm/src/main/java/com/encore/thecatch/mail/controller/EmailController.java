package com.encore.thecatch.mail.controller;

import com.encore.thecatch.admin.dto.request.AdminLoginDto;
import com.encore.thecatch.common.DefaultResponse;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.ResponseDto;
import com.encore.thecatch.mail.dto.EmailCheckDto;
import com.encore.thecatch.mail.dto.EmailReqDto;
import com.encore.thecatch.mail.dto.EventEmailReqDto;
import com.encore.thecatch.mail.dto.GroupEmailReqDto;
import com.encore.thecatch.mail.service.EmailSendService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class EmailController {
    private final EmailSendService emailSendService;

    public EmailController(EmailSendService emailSendService) {
        this.emailSendService = emailSendService;
    }

    @PostMapping("/mailSend")
    public ResponseDto mailSend(@RequestBody AdminLoginDto adminLoginDto) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, emailSendService.createEmailAuthNumber(adminLoginDto));
    }

    @PostMapping("/event/{id}/mailSend")
    public ResponseDto mailSend(@PathVariable Long id, @RequestBody EventEmailReqDto eventEmailReqDto) {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, emailSendService.createEventEmail(id, eventEmailReqDto));
    }

    @PostMapping("/tracking_pixel/{to_email}/{event_id}")
    public ResponseDto trackEmail(@PathVariable("to_email") String toEmail, @PathVariable("event_id") Long eventId) {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, emailSendService.trackingPixel(toEmail, eventId));
    }
}