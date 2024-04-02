package com.encore.thecatch.admin.controller;

import com.encore.thecatch.admin.dto.request.AdminLoginDto;
import com.encore.thecatch.admin.dto.request.AdminSignUpDto;
import com.encore.thecatch.admin.service.AdminService;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.ResponseDto;
import com.encore.thecatch.common.util.IPUtil;
import org.springframework.http.HttpStatus;
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
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_CREATE_MEMBER, adminService.totalAdminSignUp(adminSignUpDto));
    }
    @PostMapping("/admin/signUp")
    public ResponseDto adminSingUp(@RequestBody AdminSignUpDto adminSignUpDto) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_CREATE_MEMBER, adminService.adminSignUp(adminSignUpDto));
    }

    @PostMapping("/admin/doLogin")
    public ResponseDto adminDoLogin(@RequestBody AdminLoginDto adminLoginDto, HttpServletRequest request) throws Exception {
        return adminService.doLogin(adminLoginDto, IPUtil.getClientIP(request));
    }
}
