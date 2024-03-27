package com.encore.thecatch.user.service;

import com.encore.thecatch.common.CatchException;
import com.encore.thecatch.common.Dto.ResponseDto;
import com.encore.thecatch.common.Jwt.JwtTokenProvider;
import com.encore.thecatch.common.Jwt.RefreshToken.RefreshToken;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.log.domain.Log;
import com.encore.thecatch.log.domain.LogType;
import com.encore.thecatch.log.repository.LogRepository;
import com.encore.thecatch.user.domain.User;
import com.encore.thecatch.user.dto.request.UserLoginDto;
import com.encore.thecatch.user.dto.request.UserSignUpDto;
import com.encore.thecatch.user.dto.response.UserInfoDto;
import com.encore.thecatch.common.Jwt.RefreshToken.RefreshTokenRepository;
import com.encore.thecatch.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AesBytesEncryptor encryptor;
    private final JwtTokenProvider jwtTokenProvider;
    private final LogRepository logRepository;

    public UserService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, PasswordEncoder passwordEncoder, AesBytesEncryptor encryptor, JwtTokenProvider jwtTokenProvider, LogRepository logRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.encryptor = encryptor;
        this.jwtTokenProvider = jwtTokenProvider;
        this.logRepository = logRepository;
    }

    @Transactional
    public User signUp(UserSignUpDto userSignUpDto){
        if (userRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
            throw new CatchException(ResponseCode.EXISTING_EMAIL);
        }
        System.out.println(userSignUpDto.getPassword());
        User user = User.toEntity(userSignUpDto);

        user.passwordEncode(passwordEncoder);

        String name = encrypt(user.getName());
        String email = encrypt(user.getEmail());
        String phoneNumber = encrypt(user.getPhoneNumber());
        user.dataEncode(name, email, phoneNumber);

        return userRepository.save(user);
    }

    public UserInfoDto userDetail(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND));
        String name = decrypt(user.getName());
        String email = decrypt(user.getEmail());
        String phoneNumber = decrypt(user.getPhoneNumber());
        user.dataDecode(name, email, phoneNumber);

        UserInfoDto userInfoDto = UserInfoDto.toUserInfoDto(user);
        return userInfoDto;
    }


    public ResponseDto doLogin(UserLoginDto userLoginDto, String ip) {

        String email = encrypt(userLoginDto.getEmail());
        System.out.println("##### "+email);
        System.out.println(userRepository.findById(4L).get().getEmail());

        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("이메일이 일치하지 않습니다."));
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


        Log loginLog = Log.builder()
                .type(LogType.LOGIN) // DB로 나눠 관리하지 않고 LogType으로 구별
                .ip(ip)
                .email(user.getEmail())
                .method("POST")
                .data("user login")
                .build();

        logRepository.save(loginLog);

        return new ResponseDto(HttpStatus.OK, "JWT token is created", result);
    }

    // 암호화
    public String encrypt(String data) {
        byte[] encrypt = encryptor.encrypt(data.getBytes(StandardCharsets.UTF_8));
        return byteArrayToString(encrypt);
    }

    // 복호화
    public String decrypt(String data) {
        byte[] decryptBytes = stringToByteArray(data);
        byte[] decrypt = encryptor.decrypt(decryptBytes);
        return new String(decrypt, StandardCharsets.UTF_8);
    }

    // byte -> String
    public String byteArrayToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte abyte : bytes) {
            sb.append(abyte);
            sb.append(" ");
        }
        return sb.toString();
    }

    // String -> byte
    public byte[] stringToByteArray(String byteString) {
        String[] split = byteString.split("\\s");
        ByteBuffer buffer = ByteBuffer.allocate(split.length);
        for (String s : split) {
            buffer.put((byte) Integer.parseInt(s));
        }
        return buffer.array();
    }


}
