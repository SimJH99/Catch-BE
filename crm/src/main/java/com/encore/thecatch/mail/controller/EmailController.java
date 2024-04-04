package com.encore.thecatch.mail.controller;

import com.encore.thecatch.admin.dto.request.AdminLoginDto;
import com.encore.thecatch.common.DefaultResponse;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.ResponseDto;
import com.encore.thecatch.mail.dto.EmailCheckDto;
import com.encore.thecatch.mail.dto.EmailReqDto;
import com.encore.thecatch.mail.dto.GroupEmailReqDto;
import com.encore.thecatch.mail.service.EmailSendService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {
    private final EmailSendService emailSendService;

    public EmailController(EmailSendService emailSendService) {
        this.emailSendService = emailSendService;
    }

    @PostMapping("/mailSend")
    public ResponseDto mailSend(@RequestBody AdminLoginDto adminLoginDto) throws Exception {
        emailSendService.createEmailAuthNumber(adminLoginDto);
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, null);
    }

    @PostMapping("/groupMailSend")
    public ResponseDto mailSend(@RequestBody GroupEmailReqDto groupEmailReqDto){
        String result = emailSendService.createGroupEmail(groupEmailReqDto);
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, result);
    }

}
