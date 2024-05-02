package com.encore.thecatch.user.controller;

import com.encore.thecatch.common.DefaultResponse;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.ResponseDto;
import com.encore.thecatch.common.util.IPUtil;
import com.encore.thecatch.notification.dto.PushTokenDto;
import com.encore.thecatch.user.domain.User;
import com.encore.thecatch.user.dto.request.UserLoginDto;
import com.encore.thecatch.user.dto.request.UserSearchDto;
import com.encore.thecatch.user.dto.request.UserSignUpDto;
import com.encore.thecatch.user.dto.request.UserUpdateDto;
import com.encore.thecatch.user.dto.response.*;
import com.encore.thecatch.user.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
    @GetMapping("/user/profile")
    public ResponseDto userProfile() throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_LOGIN_USER_PROFILE, userService.userProfile());
    }

    @PostMapping("/user/doLogin")
    public ResponseDto userLogin(@RequestBody UserLoginDto userLoginDto, HttpServletRequest request) throws Exception {
        ResponseDto responseDto = userService.doLogin(userLoginDto, IPUtil.getClientIP(request));
        return responseDto;
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

    @PostMapping("/user/search")
    public ResponseDto searchUser(@RequestBody UserSearchDto userSearchDto, Pageable pageable) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS,
                new DefaultResponse.PagedResponse<UserListRes>(userService.searchUser(userSearchDto, pageable)));
    }
    @PostMapping("/user/doLogout")
    public ResponseDto userLogout(HttpServletRequest request) throws Exception {
        userService.userLogout(IPUtil.getClientIP(request));
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_LOGOUT,"success");
    }
    @GetMapping("/user/{id}/detail")
    public ResponseDto userDetail(@PathVariable Long id,HttpServletRequest request) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_USER_DETAIL, userService.userDetail(id,IPUtil.getClientIP(request)));
    }

    @PostMapping("/user/random/create")
    public ResponseDto randomAdminCreate() throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_CREATE_MEMBER, userService.createTestUsers(555));
    }

    @PatchMapping("/user/{id}/update")
    public ResponseDto adminUpdate(@PathVariable Long id, @RequestBody UserUpdateDto userUpdateDto, HttpServletRequest request) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_USER_UPDATE, userService.userUpdate(id, userUpdateDto, IPUtil.getClientIP(request)));
    }

    @PatchMapping("/user/{id}/disabled")
    public ResponseDto adminDisabled(@PathVariable Long id, HttpServletRequest request) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_USER_DISABLED, userService.userDisabled(id, IPUtil.getClientIP(request)));
    }

    @PatchMapping("/user/{id}/activation")
    public ResponseDto adminActivation(@PathVariable Long id, HttpServletRequest request) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_USER_ACTIVATION, userService.userActivation(id, IPUtil.getClientIP(request)));
    }

    @GetMapping("/user/marketing")
    public ResponseDto findMarketing(Pageable pageable) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, new DefaultResponse.PagedResponse<UserInfoDto>(userService.findMarketing(pageable)));
    }


    @PostMapping("/user/pushToken")
    public ResponseDto savePushToken(@RequestBody PushTokenDto pushTokenDto) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, userService.savePushToken(pushTokenDto.getEmail() ,pushTokenDto.getPushToken()));
    }

}
