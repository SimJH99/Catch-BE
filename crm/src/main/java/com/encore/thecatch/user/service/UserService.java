package com.encore.thecatch.user.service;

import com.encore.thecatch.admin.domain.Admin;
import com.encore.thecatch.admin.dto.response.AdminSearchDto;
import com.encore.thecatch.admin.repository.AdminRepository;
import com.encore.thecatch.common.CatchException;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.ResponseDto;
import com.encore.thecatch.common.jwt.JwtTokenProvider;
import com.encore.thecatch.common.jwt.RefreshToken.RefreshToken;
import com.encore.thecatch.common.jwt.RefreshToken.RefreshTokenRepository;
import com.encore.thecatch.common.redis.RedisService;
import com.encore.thecatch.common.util.AesUtil;
import com.encore.thecatch.common.util.MaskingUtil;
import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.company.repository.CompanyRepository;
import com.encore.thecatch.coupon.domain.Coupon;
import com.encore.thecatch.coupon.dto.CouponResDto;
import com.encore.thecatch.log.domain.LogType;
import com.encore.thecatch.log.domain.UserLog;
import com.encore.thecatch.log.repository.UserLogRepository;
import com.encore.thecatch.user.domain.TotalAddress;
import com.encore.thecatch.user.domain.User;
import com.encore.thecatch.user.dto.request.UserLoginDto;
import com.encore.thecatch.user.dto.request.UserSignUpDto;
import com.encore.thecatch.user.dto.response.ChartAgeRes;
import com.encore.thecatch.user.dto.response.ChartGenderRes;
import com.encore.thecatch.user.dto.response.ChartGradeRes;
import com.encore.thecatch.user.dto.response.UserInfoDto;
import com.encore.thecatch.user.repository.UserQueryRepository;
import com.encore.thecatch.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserLogRepository userLogRepository;
    private final AesUtil aesUtil;
    private final CompanyRepository companyRepository;
    private final RedisService redisService;
    private final UserQueryRepository userQueryRepository;
    private final AdminRepository adminRepository;
    private final MaskingUtil maskingUtil;

    public UserService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       UserLogRepository userLogRepository,
                       CompanyRepository companyRepository,
                       AesUtil aesUtil,
                       RedisService redisService, UserQueryRepository userQueryRepository, AdminRepository adminRepository, MaskingUtil maskingUtil
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userLogRepository = userLogRepository;
        this.companyRepository = companyRepository;
        this.aesUtil = aesUtil;
        this.redisService = redisService;
        this.userQueryRepository = userQueryRepository;
        this.adminRepository = adminRepository;
        this.maskingUtil = maskingUtil;
    }

    @Transactional
    public User signUp(UserSignUpDto userSignUpDto) throws Exception {
        if (userRepository.findByEmail(aesUtil.aesCBCEncode(userSignUpDto.getEmail())).isPresent()) {
            throw new CatchException(ResponseCode.EXISTING_EMAIL);
        }
        Company company = companyRepository.findById(userSignUpDto.getCompanyId()).orElseThrow(
                ()-> new CatchException(ResponseCode.COMPANY_NOT_FOUND));

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
        User user = userRepository.findById(id).orElseThrow(
                ()-> new CatchException(ResponseCode.USER_NOT_FOUND));
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

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new CatchException(ResponseCode.USER_NOT_FOUND));
        if(!passwordEncoder.matches(userLoginDto.getPassword(),user.getPassword())){
            throw new CatchException(ResponseCode.USER_NOT_FOUND);
        }
        String accessToken = jwtTokenProvider.createAccessToken(String.format("%s:%s", user.getEmail(), user.getRole())); // 토큰 생성
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getRole(), user.getId()); // 리프레시 토큰 생성
        // 리프레시 토큰이 이미 있으면 토큰을 갱신하고 없으면 토큰을 추가한다.
        refreshTokenRepository.findById(user.getId())
                .ifPresentOrElse(
                        it -> it.updateRefreshToken(refreshToken),
                        () -> refreshTokenRepository.save(new RefreshToken(user, refreshToken))
                );
        Map<String, String> result = new HashMap<>();
        result.put("access_token", accessToken);
        result.put("refresh_token", refreshToken);


        UserLog userLoginLog = UserLog.builder()
                .type(LogType.USER_LOGIN) // DB로 나눠 관리하지 않고 LogType으로 구별
                .ip(ip)
                .email(aesUtil.aesCBCDecode(user.getEmail()))
                .method("POST")
                .data("user login")
                .build();


        userLogRepository.save(userLoginLog);
        return new ResponseDto(HttpStatus.OK, "JWT token is created", result);
    }

    @Transactional
    public String userDisable() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new CatchException(ResponseCode.USER_NOT_FOUND));
        user.userActiveToDisable();
        return "user Disable";
    }

    @Transactional
    public String doLogout() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new CatchException(ResponseCode.USER_NOT_FOUND));
        redisService.deleteValues(String.valueOf(user.getId()));

        return "delete refresh token";
    }

    public List<ChartGradeRes> chartGrade() {
        return userQueryRepository.countGrade();
    }


    public List<ChartGenderRes> chartGender() {
        return userQueryRepository.countGender();
    }

    public List<ChartAgeRes> chartAge() {
        return userQueryRepository.countAge();
    }

    public Page<UserInfoDto> findAll(Pageable pageable) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Admin admin = adminRepository.findByEmployeeNumber(authentication.getName()).orElseThrow(()-> new CatchException(ResponseCode.ADMIN_NOT_FOUND));
        Company company = admin.getCompany();
        Page<User> users = userRepository.findByCompany(company, pageable);
        List<UserInfoDto> maskingUserList = new ArrayList<>();
        for (User nonUser : users) {
            decodeToUser(nonUser);
            toMasking(nonUser);
            UserInfoDto userInfoDto = UserInfoDto.toUserInfoDto(nonUser);
            maskingUserList.add(userInfoDto);
        }
        return new PageImpl<>(maskingUserList, pageable, users.getTotalElements());
//        return users.map(UserInfoDto::toUserInfoDto);
    }

    private void toMasking(User user) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String maskingName = maskingUtil.nameMasking(user.getName());
        String maskingEmail = maskingUtil.emailMasking(user.getEmail());
        String maskingPhoneNumber = maskingUtil.phoneMasking(user.getPhoneNumber());
//        String maskingBirthDate = maskingUtil.birthMasking(user.getBrithDate().format(formatter));
//        String maskingTotalAddress = maskingUtil.addressMasking(String.valueOf(user.getTotalAddress()));
//        String maskingAddress = maskingUtil.addressMasking(user.getTotalAddress().getAddress());
//        String maskingDetailAddress = maskingUtil.addressMasking(user.getTotalAddress().getDetailAddress());
//        String maskingZipcode = maskingUtil.addressMasking(user.getTotalAddress().getZipcode());

        user.masking(maskingName, maskingEmail,maskingPhoneNumber);
        // maskingBirthDate,maskingTotalAddress, maskingAddress, maskingDetailAddress, maskingZipcode
    }

}
