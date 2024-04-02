package com.encore.thecatch.mail.controller;

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
    public ResponseDto mailSend(@RequestBody EmailReqDto emailReqDto){
        System.out.println("이메일 인증 이메일 :"+emailReqDto.getEmail());
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, emailSendService.createEmailAuthNumber(emailReqDto));
    }

    @PostMapping("/groupMailSend")
    public ResponseDto mailSend(@RequestBody GroupEmailReqDto groupEmailReqDto){
        String result = emailSendService.createGroupEmail(groupEmailReqDto);
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, result);
    }
    @PostMapping("/mailAuthCheck")
    public String AuthCheck(@RequestBody EmailCheckDto emailCheckDto){
        Boolean Checked=emailSendService.checkAuthNum(emailCheckDto.getEmail(),emailCheckDto.getAuthNum());
        if(Checked){
            return "ok";
        }
        else{
            throw new NullPointerException("뭔가 잘못!");
        }
    }
}
