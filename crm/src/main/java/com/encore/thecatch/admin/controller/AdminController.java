package com.encore.thecatch.admin.controller;

import com.encore.thecatch.admin.dto.request.AdminLoginDto;
import com.encore.thecatch.admin.dto.request.AdminSignUpDto;
import com.encore.thecatch.admin.dto.request.AdminUpdateDto;
import com.encore.thecatch.admin.dto.response.AdminInfoDto;
import com.encore.thecatch.admin.dto.response.AdminSearchDto;
import com.encore.thecatch.admin.service.AdminService;
import com.encore.thecatch.common.DefaultResponse;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.ResponseDto;
import com.encore.thecatch.common.util.IPUtil;
import com.encore.thecatch.mail.dto.EmailCheckDto;
import com.encore.thecatch.notification.dto.PushTokenDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/admin/superLogin")
    public ResponseDto adminSuperLogin(@RequestBody AdminLoginDto adminLoginDto) throws Exception {
        return adminService.superLogin(adminLoginDto);
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

    @PostMapping("/admin/all")
    public ResponseDto searchAdmin(@RequestBody AdminSearchDto adminSearchDto, Pageable pageable) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_ADMIN_LIST,
                new DefaultResponse<Page<AdminInfoDto>>(adminService.searchAdmin(adminSearchDto, pageable)));
    }

    @PostMapping("/admin/emailCheck")
    public ResponseDto adminEmailCheck(@RequestBody AdminSignUpDto adminSignUpDto) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.CHECK_EMAIL, adminService.emailCheck(adminSignUpDto));
    }

    @PostMapping("/admin/employeeNumberCheck")
    public ResponseDto employeeNumberCheck(@RequestBody AdminSignUpDto adminSignUpDto) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.CHECK_EMPLOYEE_NUMBER, adminService.employeeNumberCheck(adminSignUpDto));
    }

//    @PostMapping("/admin/random/create")
//    public ResponseDto randomAdminCreate() throws Exception {
//        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_CREATE_MEMBER, adminService.createTestAdmins(150, true));
//    }

    @PostMapping("/admin/searchList")
    public ResponseDto searchComplaint(@RequestBody AdminSearchDto adminSearchDto, Pageable pageable) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_ADMIN_LIST,
                new DefaultResponse<Page<AdminInfoDto>>(adminService.searchAdmin(adminSearchDto, pageable)));
    }

    @GetMapping("/admin/{id}/detail")
    public ResponseDto adminDetail(@PathVariable Long id,HttpServletRequest request) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_ADMIN_DETAIL, adminService.adminDetail(id,IPUtil.getClientIP(request)));
    }

    @GetMapping("/admin/profile")
    public ResponseDto adminProfile() throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_LOGIN_ADMIN_PROFILE, adminService.adminProfile());
    }

    @PostMapping("/admin/doLogout")
    public ResponseDto adminLogout(HttpServletRequest request) throws Exception {
        adminService.adminLogout(IPUtil.getClientIP(request));
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_LOGOUT,"success");
    }

    @PatchMapping("/admin/{id}/update")
    public ResponseDto adminUpdate(@PathVariable Long id, @RequestBody AdminUpdateDto adminUpdateDto, HttpServletRequest request) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_ADMIN_UPDATE, adminService.adminUpdate(id, adminUpdateDto, IPUtil.getClientIP(request)));
    }

    @PatchMapping("/admin/{id}/disabled")
    public ResponseDto adminDisabled(@PathVariable Long id, HttpServletRequest request) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_ADMIN_DISABLED, adminService.adminDisabled(id, IPUtil.getClientIP(request)));
    }

    @PatchMapping("/admin/{id}/activation")
    public ResponseDto adminActivation(@PathVariable Long id, HttpServletRequest request) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_ADMIN_ACTIVATION, adminService.adminActivation(id, IPUtil.getClientIP(request)));
    }

    @GetMapping("/admin/test")
    public ResponseDto test(){
        return new ResponseDto(HttpStatus.OK, "OK", "OK");
    }

}
