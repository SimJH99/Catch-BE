package com.encore.thecatch.user.service;

import com.encore.thecatch.common.CatchException;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.ResponseDto;
import com.encore.thecatch.common.jwt.JwtTokenProvider;
import com.encore.thecatch.common.jwt.RefreshToken.RefreshToken;
import com.encore.thecatch.common.jwt.RefreshToken.RefreshTokenRepository;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.company.repository.CompanyRepository;
import com.encore.thecatch.common.util.AesUtil;
import com.encore.thecatch.log.domain.Log;
import com.encore.thecatch.log.domain.LogType;
import com.encore.thecatch.log.repository.LogRepository;
import com.encore.thecatch.user.domain.TotalAddress;
import com.encore.thecatch.user.domain.User;
import com.encore.thecatch.user.dto.request.UserLoginDto;
import com.encore.thecatch.user.dto.request.UserSignUpDto;
import com.encore.thecatch.user.dto.response.UserInfoDto;
import com.encore.thecatch.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final LogRepository logRepository;

    private final AesUtil aesUtil;

    private final CompanyRepository companyRepository;

    public UserService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       LogRepository logRepository,
                       CompanyRepository companyRepository,
                       AesUtil aesUtil
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.logRepository = logRepository;
        this.companyRepository = companyRepository;
        this.aesUtil = aesUtil;
    }

    @Transactional
    public User signUp(UserSignUpDto userSignUpDto) throws Exception {
        if (userRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
            throw new CatchException(ResponseCode.EXISTING_EMAIL);
        }
        Company company = companyRepository.findById(userSignUpDto.getCompanyId()).orElseThrow(()-> new CatchException(ResponseCode.COMPANY_NOT_FOUND));
        System.out.println(userSignUpDto.getPassword());
        User user = User.toEntity(userSignUpDto, company);

        user.passwordEncode(passwordEncoder);
        toEncodeAES(user);

        return userRepository.save(user);
    }

    private void toEncodeAES(User user) throws Exception {
        String name = aesUtil.aesCBCEncode(user.getName());
        String email = aesUtil.aesCBCEncode(user.getEmail());
        String phoneNumber = aesUtil.aesCBCEncode(user.getPhoneNumber());
        String address = aesUtil.aesCBCEncode(user.getTotalAddress().getAddress());
        String detailAddress = aesUtil.aesCBCEncode(user.getTotalAddress().getDetailAddress());
        String zipcode = aesUtil.aesCBCEncode(String.valueOf(user.getTotalAddress().getZipcode()));

        TotalAddress totalAddress = TotalAddress.builder()
                .address(address)
                .detailAddress(detailAddress)
                .zipcode(zipcode)
                .build();

        user.dataEncode(name, email, phoneNumber, totalAddress);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public UserInfoDto userDetail(Long id) throws Exception {
        User user = userRepository.findById(id).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND));
        decodeToUser(user);

        UserInfoDto userInfoDto = UserInfoDto.toUserInfoDto(user);
        return userInfoDto;
    }

    private void decodeToUser(User user) throws Exception {
        String name = aesUtil.aesCBCDecode(user.getName());
        String email = aesUtil.aesCBCDecode(user.getEmail());
        String phoneNumber = aesUtil.aesCBCDecode(user.getPhoneNumber());
        String address = aesUtil.aesCBCDecode(user.getTotalAddress().getAddress());
        String detailAddress = aesUtil.aesCBCDecode(user.getTotalAddress().getDetailAddress());
        String zipcode = aesUtil.aesCBCDecode(String.valueOf(user.getTotalAddress().getZipcode()));

        TotalAddress totalAddress = TotalAddress.builder()
                .address(address)
                .detailAddress(detailAddress)
                .zipcode(zipcode)
                .build();
        user.dataDecode(name, email, phoneNumber, totalAddress);
    }

    @Transactional
    public ResponseDto doLogin(UserLoginDto userLoginDto, String ip) throws Exception {
        String email = aesUtil.aesCBCEncode(userLoginDto.getEmail());

        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("이메일이 일치하지 않습니다."));
        if(!passwordEncoder.matches(userLoginDto.getPassword(),user.getPassword())){
            throw new IllegalArgumentException("패스워드가 일치하지 않습니다.");
        }
        String accessToken = jwtTokenProvider.createAccessToken(String.format("%s:%s", user.getEmail(), user.getRole())); // 토큰 생성
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId()); // 리프레시 토큰 생성
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
                .email(aesUtil.aesCBCDecode(user.getEmail()))
                .method("POST")
                .data("user login")
                .build();

        logRepository.save(loginLog);

        return new ResponseDto(HttpStatus.OK, "JWT token is created", result);
    }

    public ResponseDto userDisable(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new CatchException(ResponseCode.USER_NOT_FOUND)
        );
        user.userActiveToDisable();
        return new ResponseDto(HttpStatus.OK, "user Disable",null);
    }
}
