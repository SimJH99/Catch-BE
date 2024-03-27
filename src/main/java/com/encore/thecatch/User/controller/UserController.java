package com.encore.thecatch.User.controller;

import com.encore.thecatch.common.Dto.ResponseDto;
import com.encore.thecatch.User.domain.User;
import com.encore.thecatch.User.dto.request.UserLoginDto;
import com.encore.thecatch.User.dto.request.UserSignUpDto;
import com.encore.thecatch.User.dto.response.HttpDto;
import com.encore.thecatch.User.dto.response.UserInfoDto;
import com.encore.thecatch.User.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user/signUp")
    public ResponseEntity<HttpDto> userSignUp(@ModelAttribute UserSignUpDto userSignUpDto){
        User user = userService.signUp(userSignUpDto);
        return new ResponseEntity<>(new HttpDto(HttpStatus.CREATED, "Create", user.getId()), HttpStatus.CREATED);
    }

    @GetMapping("/user/{id}/detail")
    public UserInfoDto userDetail(@PathVariable Long id){
        UserInfoDto userInfoDto = userService.userDetail(id);
        return userInfoDto;
    }

    @PostMapping("/user/doLogin")
    public ResponseEntity<ResponseDto> userLogin(UserLoginDto userLoginDto){
        ResponseDto responseDto = userService.doLogin(userLoginDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
