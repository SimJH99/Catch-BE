package com.encore.thecatch.User.service;

import com.encore.thecatch.User.domain.User;
import com.encore.thecatch.User.dto.request.UserSignUpDto;
import com.encore.thecatch.User.dto.response.UserInfoDto;
import com.encore.thecatch.User.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User signUp(UserSignUpDto userSignUpDto){
        if (userRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 있는 이메일입니다.");
        }
        User user = User.toEntity(userSignUpDto);

        user.passwordEncode(passwordEncoder);

        return userRepository.save(user);
    }

    public UserInfoDto userDetail(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("해당 유저가 없습니다"));
        UserInfoDto userInfoDto = UserInfoDto.toUserInfoDto(user);
        return userInfoDto;
    }


//    public UserLoginDto doLogin(UserLoginDto userLoginDto) {
//        userRepository.findByEmail(userLoginDto.getEmail())
//                .filter(inDB -> passwordEncoder.matches(userLoginDto.getPassword(),inDB.getPassword()))
//                .orElseThrow(()-> new IllegalArgumentException("이메일 또는 패스워드가 일치하지 않습니다."));
//
//
//
//    }
}
