package com.encore.thecatch.User.service;

import com.encore.thecatch.common.Dto.ResponseDto;
import com.encore.thecatch.common.Jwt.JwtTokenProvider;
import com.encore.thecatch.common.Jwt.RefreshToken.RefreshToken;
import com.encore.thecatch.User.domain.User;
import com.encore.thecatch.User.dto.request.UserLoginDto;
import com.encore.thecatch.User.dto.request.UserSignUpDto;
import com.encore.thecatch.User.dto.response.UserInfoDto;
import com.encore.thecatch.common.Jwt.RefreshToken.RefreshTokenRepository;
import com.encore.thecatch.User.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
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


    public ResponseDto doLogin(UserLoginDto userLoginDto) {
        User user = userRepository.findByEmail(userLoginDto.getEmail()).orElseThrow(() -> new IllegalArgumentException("이메일이 일치하지 않습니다."));
        if(!passwordEncoder.matches(userLoginDto.getPassword(),user.getPassword())){
            throw new IllegalArgumentException("패스워드가 일치하지 않습니다.");
        }
        String accessToken = jwtTokenProvider.createAccessToken(String.format("%s", user.getEmail())); // 토큰 생성
        String refreshToken = jwtTokenProvider.createRefreshToken(); // 리프레시 토큰 생성
        // 리프레시 토큰이 이미 있으면 토큰을 갱신하고 없으면 토큰을 추가한다.
        refreshTokenRepository.findById(user.getId())
                .ifPresentOrElse(
                        it -> it.updateRefreshToken(refreshToken),
                        () -> refreshTokenRepository.save(new RefreshToken(user, refreshToken))
                );
        Map<String, String> result = new HashMap<>();
        result.put("access_token", accessToken);
        result.put("refresh_token", refreshToken);
        return new ResponseDto(HttpStatus.OK, "JWT token is created", result);
    }
}
