package com.encore.thecatch.user.controller;

import com.encore.thecatch.common.DefaultResponse;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.ResponseDto;
import com.encore.thecatch.common.util.IPUtil;
import com.encore.thecatch.coupon.dto.CouponFindResDto;
import com.encore.thecatch.coupon.dto.CouponResDto;
import com.encore.thecatch.coupon.dto.SearchCouponCondition;
import com.encore.thecatch.user.domain.User;
import com.encore.thecatch.user.dto.request.UserLoginDto;
import com.encore.thecatch.user.dto.request.UserSignUpDto;
import com.encore.thecatch.user.dto.response.ChartAgeRes;
import com.encore.thecatch.user.dto.response.ChartGenderRes;
import com.encore.thecatch.user.dto.response.ChartGradeRes;
import com.encore.thecatch.user.dto.response.UserInfoDto;
import com.encore.thecatch.user.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user/signUp")
    public ResponseDto userSignUp(@RequestBody UserSignUpDto userSignUpDto) throws Exception {
        User user = userService.signUp(userSignUpDto);
        return new ResponseDto(HttpStatus.CREATED, ResponseCode.SUCCESS_CREATE_MEMBER, new DefaultResponse<Long>(user.getId()));
    }

    @GetMapping("/user/{id}/detail")
    public ResponseDto userDetail(@PathVariable Long id) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, new DefaultResponse<UserInfoDto>(userService.userDetail(id)));
    }

    @PostMapping("/user/disable")
    public ResponseDto userDisable() {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, new DefaultResponse<>(userService.userDisable()));
    }

    @PostMapping("/user/doLogin")
    public ResponseDto userLogin(@RequestBody UserLoginDto userLoginDto, HttpServletRequest request) throws Exception {
        ResponseDto responseDto = userService.doLogin(userLoginDto, IPUtil.getClientIP(request));
        return responseDto;
    }

    @PostMapping("/user/doLogout")
    public ResponseDto userLogin(){
        return new ResponseDto(HttpStatus.OK,ResponseCode.SUCCESS,new DefaultResponse<>(userService.doLogout()));
    }

    @GetMapping("/user/grade")
    public ResponseDto chartGrade(){
        return new ResponseDto(HttpStatus.OK,
                ResponseCode.SUCCESS,
                new DefaultResponse<List<ChartGradeRes>>(userService.chartGrade()));
    }
    @GetMapping("/user/gender")
    public ResponseDto chartGender(){
        return new ResponseDto(HttpStatus.OK,
                ResponseCode.SUCCESS,
                new DefaultResponse<List<ChartGenderRes>>(userService.chartGender()));
    }

    @GetMapping("/user/age")
    public ResponseDto chartAge(){
        return new ResponseDto(HttpStatus.OK,
                ResponseCode.SUCCESS,
                new DefaultResponse<List<ChartAgeRes>>(userService.chartAge()));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/user/list")
    public ResponseDto findAll(Pageable pageable) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, new DefaultResponse.PagedResponse<UserInfoDto>(userService.findAll(pageable)));
    }

}
