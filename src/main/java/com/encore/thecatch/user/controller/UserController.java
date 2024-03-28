package com.encore.thecatch.user.controller;

import com.encore.thecatch.common.DefaultResponse;
import com.encore.thecatch.common.dto.ResponseDto;
import com.encore.thecatch.common.util.IPUtil;
import com.encore.thecatch.user.domain.User;
import com.encore.thecatch.user.dto.request.UserLoginDto;
import com.encore.thecatch.user.dto.request.UserSignUpDto;
import com.encore.thecatch.user.dto.response.UserInfoDto;
import com.encore.thecatch.user.service.UserService;
import com.encore.thecatch.common.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user/signUp")
    public ResponseDto userSignUp(@RequestBody UserSignUpDto userSignUpDto) throws Exception {
        User user =userService.signUp(userSignUpDto);
        return new ResponseDto(HttpStatus.CREATED, ResponseCode.SUCCESS_CREATE_MEMBER, new DefaultResponse<Long>(user.getId()));
    }

    @GetMapping("/user/{id}/detail")
    public ResponseDto userDetail(@PathVariable Long id) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, new DefaultResponse<UserInfoDto>(userService.userDetail(id)));
    }

    @PostMapping("/user/doLogin")
    public ResponseDto userLogin(@RequestBody UserLoginDto userLoginDto, HttpServletRequest request) throws Exception {
        ResponseDto responseDto = userService.doLogin(userLoginDto, IPUtil.getClientIP(request));
        return responseDto;
    }
}
