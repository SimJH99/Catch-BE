package com.encore.thecatch.admin.controller;

import com.encore.thecatch.admin.dto.request.AdminLoginDto;
import com.encore.thecatch.admin.dto.request.AdminSignUpDto;
import com.encore.thecatch.admin.dto.response.AdminSearchDto;
import com.encore.thecatch.admin.service.AdminService;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.ResponseDto;
import com.encore.thecatch.common.util.IPUtil;
import com.encore.thecatch.mail.dto.EmailCheckDto;
import com.encore.thecatch.notification.dto.PushTokenDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/system/admin/signUp")
    public ResponseDto totalAdminSingUp(@RequestBody AdminSignUpDto adminSignUpDto) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_CREATE_MEMBER, adminService.systemAdminSignUp(adminSignUpDto));
    }

    @PostMapping("/admin/signUp")
    public ResponseDto adminSingUp(@RequestBody AdminSignUpDto adminSignUpDto) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_CREATE_MEMBER, adminService.adminSignUp(adminSignUpDto));
    }

    @PostMapping("/admin/doLogin")
    public ResponseDto adminDoLogin(@RequestBody AdminLoginDto adminLoginDto) throws Exception {
        return adminService.doLogin(adminLoginDto);
    }

    @PostMapping("/admin/mailAuthCheck")
    public ResponseDto verifyAuthNumber(@RequestBody EmailCheckDto emailCheckDto, HttpServletRequest request) {
        try {
            // 인증 번호 검증을 AdminService에 전달하고 응답을 반환합니다.
            return adminService.validateAuthNumber(emailCheckDto.getEmployeeNumber(), emailCheckDto.getAuthNumber(), IPUtil.getClientIP(request));
        } catch (Exception e) {
            // 인증 번호 검증 요청 처리 중에 예외가 발생하면 500 Internal Server Error를 반환합니다.
            return new ResponseDto(HttpStatus.EXPECTATION_FAILED, ResponseCode.EMAIL_CHECK_FAIL, "EMAIL_CHECK_FAIL");
        }
    }

    @GetMapping("/admin/all")
    public Page<AdminSearchDto> allNonAdmin(@PageableDefault(size = 10, sort = "createdTime", direction = Sort.Direction.DESC) Pageable pageable) throws Exception {
        return adminService.allNonAdmin(pageable);
    }

    @PostMapping("/admin/random/create")
    public ResponseDto randomAdminCreate() throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_CREATE_MEMBER, adminService.createTestAdmins(150, true));
    }

    @GetMapping("/admin/test")
    public ResponseDto test(){
        return new ResponseDto(HttpStatus.OK, "OK", "OK");
    }


    @PostMapping("/admin/pushToken")
    public ResponseDto savePushToken(@RequestBody PushTokenDto pushTokenDto) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, adminService.savePushToken(pushTokenDto.getEmployeeNumber() ,pushTokenDto.getPushToken()));
    }
}
