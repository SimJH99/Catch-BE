package com.encore.thecatch.user.service;

import com.encore.thecatch.admin.domain.Admin;
import com.encore.thecatch.admin.repository.AdminRepository;
import com.encore.thecatch.common.CatchException;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.ResponseDto;
import com.encore.thecatch.common.dto.Role;
import com.encore.thecatch.common.jwt.JwtTokenProvider;
import com.encore.thecatch.common.jwt.RefreshToken.RefreshToken;
import com.encore.thecatch.common.jwt.RefreshToken.RefreshTokenRepository;
import com.encore.thecatch.common.redis.RedisService;
import com.encore.thecatch.common.util.AesUtil;
import com.encore.thecatch.common.util.MaskingUtil;
import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.company.repository.CompanyRepository;
import com.encore.thecatch.log.domain.AdminLog;
import com.encore.thecatch.log.domain.LogType;
import com.encore.thecatch.log.domain.UserLog;
import com.encore.thecatch.log.repository.AdminLogRepository;
import com.encore.thecatch.log.repository.UserLogRepository;
import com.encore.thecatch.user.domain.Gender;
import com.encore.thecatch.user.domain.Grade;
import com.encore.thecatch.user.domain.TotalAddress;
import com.encore.thecatch.user.domain.User;
import com.encore.thecatch.user.dto.request.UserLoginDto;
import com.encore.thecatch.user.dto.request.UserSearchDto;
import com.encore.thecatch.user.dto.request.UserSignUpDto;
import com.encore.thecatch.user.dto.request.UserUpdateDto;
import com.encore.thecatch.user.dto.response.*;
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
import java.util.*;


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
    private final AdminLogRepository adminLogRepository;
    private final MaskingUtil maskingUtil;

    public UserService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       UserLogRepository userLogRepository,
                       CompanyRepository companyRepository,
                       AesUtil aesUtil,
                       RedisService redisService,
                       UserQueryRepository userQueryRepository,
                       AdminRepository adminRepository,
                       AdminLogRepository adminLogRepository,
                       MaskingUtil maskingUtil
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
        this.adminLogRepository = adminLogRepository;
        this.maskingUtil = maskingUtil;
    }

    @Transactional
    public User signUp(UserSignUpDto userSignUpDto) throws Exception {
        if (userRepository.findByEmail(aesUtil.aesCBCEncode(userSignUpDto.getEmail())).isPresent()) {
            throw new CatchException(ResponseCode.EXISTING_EMAIL);
        }
        Company company = companyRepository.findById(userSignUpDto.getCompanyId()).orElseThrow(
                () -> new CatchException(ResponseCode.COMPANY_NOT_FOUND));

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

    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','CS','MARKETER')")
    public UserDetailDto userDetail(Long id, String ip) throws Exception {
        String employeeNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin systemAdmin = adminRepository.findByEmployeeNumber(employeeNumber).orElseThrow(
                () -> new CatchException(ResponseCode.ADMIN_NOT_FOUND)
        );
        User user = userRepository.findById(id).orElseThrow(
                () -> new CatchException(ResponseCode.USER_NOT_FOUND)
        );

        UserDetailDto userDetailDto = toDetailDto(user);

        AdminLog adminLog = AdminLog.builder()
                .type(LogType.USER_DETAIL_VIEW) // DB로 나눠 관리하지 않고 LogType으로 구별
                .ip(ip)
                .employeeNumber(aesUtil.aesCBCDecode(systemAdmin.getEmployeeNumber()))
                .method("GET")
                .data("view at adminId:" + user.getId() + " detail")
                .build();

        adminLogRepository.save(adminLog);
        return userDetailDto;
    }

    private UserDetailDto toDetailDto(User user) throws Exception {
        return UserDetailDto.builder()
                .name(aesUtil.aesCBCDecode(user.getName()))
                .email(aesUtil.aesCBCDecode(user.getEmail()))
                .birthDate(user.getBirthDate())
                .address(aesUtil.aesCBCDecode(user.getTotalAddress().getAddress()))
                .detailAddress(aesUtil.aesCBCDecode(user.getTotalAddress().getDetailAddress()))
                .zipcode(aesUtil.aesCBCDecode(user.getTotalAddress().getZipcode()))
                .consentReceiveMarketing(user.isConsentReceiveMarketing())
                .gender(user.getGender())
                .phoneNumber(aesUtil.aesCBCDecode(user.getPhoneNumber()))
                .grade(user.getGrade())
                .active(user.isActive())
                .userNotice(user.getUserNotice())
                .build();
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
        if (!passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword())) {
            throw new CatchException(ResponseCode.USER_NOT_FOUND);
        }
        String accessToken = jwtTokenProvider.createAccessToken(String.format("%s:%s", user.getEmail(), user.getRole())); // 토큰 생성
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getRole(), user.getId()); // 리프레시 토큰 생성
        // 리프레시 토큰이 이미 있으면 토큰을 갱신하고 없으면 토큰을 추가한다.
        refreshTokenRepository.findByUserEmail(user.getEmail())
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


//    @Transactional
//    public String userDisable() {
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByEmail(email).orElseThrow(
//                () -> new CatchException(ResponseCode.USER_NOT_FOUND));
//        user.userActiveToDisable();
//        return "user Disable";
//    }

    @Transactional
    public String doLogout() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new CatchException(ResponseCode.USER_NOT_FOUND));
        redisService.deleteValues(String.valueOf(user.getId()));
        redisService.deleteValues("PushToken:" + user.getEmail());

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

        user.masking(maskingName, maskingEmail, maskingPhoneNumber);
        // maskingBirthDate,maskingTotalAddress, maskingAddress, maskingDetailAddress, maskingZipcode
    }

    public Page<UserListRes> searchUser(UserSearchDto userSearchDto, Pageable pageable) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Admin admin = adminRepository.findByEmployeeNumber(authentication.getName()).orElseThrow(() -> new CatchException(ResponseCode.ADMIN_NOT_FOUND));
        List<UserListRes> userListRes = userQueryRepository.UserList(userSearchDto, admin.getCompany());
        List<UserListRes> userListRes1 = new ArrayList<>();
        for(UserListRes user : userListRes){
            String name = aesUtil.aesCBCDecode(user.getName());
            String email = aesUtil.aesCBCDecode(user.getEmail());
            String phoneNumber = aesUtil.aesCBCDecode(user.getPhoneNumber());

            user = UserListRes.builder()
                    .id(user.getId())
                    .name(maskingUtil.nameMasking(name))
                    .email(maskingUtil.emailMasking(email))
                    .birthDate(user.getBirthDate())
                    .phoneNumber(maskingUtil.phoneMasking(phoneNumber))
                    .gender(user.getGender())
                    .grade(user.getGrade())
                    .build();
            userListRes1.add(user);
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), userListRes1.size());

        return new PageImpl<>(userListRes1.subList(start, end), pageable, userListRes1.size());
    }

    public UserProfileDto userProfile() throws Exception {
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new CatchException(ResponseCode.USER_NOT_FOUND)
        );
        return new UserProfileDto(aesUtil.aesCBCDecode(user.getName()));
    }

    @Transactional
    public void userLogout(String ip) throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new CatchException(ResponseCode.USER_NOT_FOUND)
        );

        RefreshToken refreshToken = refreshTokenRepository.findByUserEmail(user.getEmail()).orElseThrow(
                () -> new CatchException(ResponseCode.REFRESH_TOKEN_NOT_FOUND)
        );
        refreshTokenRepository.delete(refreshToken);
        redisService.deleteValues(user.getRole() + "" + user.getId());
        redisService.deleteValues("PushToken" + user.getId());
        UserLog userLogoutLog = UserLog.builder()
                .type(LogType.USER_LOGOUT) // DB로 나눠 관리하지 않고 LogType으로 구별
                .ip(ip)
                .email(aesUtil.aesCBCDecode(user.getEmail()))
                .method("POST")
                .data("user logout")
                .build();

        userLogRepository.save(userLogoutLog);
    }

    public List<User> createTestUsers(int count) throws Exception {
        List<User> testUsers = new ArrayList<>();
        Random random = new Random(); // 랜덤 객체 생성

        for (int i = 0; i < count; i++) {
            // 랜덤 한국 이름 생성
            String[] surnames = {"김", "이", "박", "최", "정", "강", "조", "윤", "장", "임"};
            String[] givenNames = {"민준", "서연", "하준", "지우", "지후", "서준", "서현", "지민", "수빈", "지유", "주원", "지호", "지훈", "예은", "수현", "지원", "다은", "은지", "윤서", "현우"};
            String surname = surnames[random.nextInt(surnames.length)];
            String givenName = givenNames[random.nextInt(givenNames.length)];
            String name = surname + givenName;

            // 랜덤 이메일 생성
            String email = "test" + i + "@test.com";

            // 랜덤 패스워드 생성
            String password = "1234";

            // 랜덤 생일 생성 (20세~60세)
            String birthDate = String.format("%04d-%02d-%02d", 1964 + random.nextInt(40), random.nextInt(12) + 1, random.nextInt(28) + 1);

            // 랜덤 주소 생성
            String[] cities = {"서울", "부산", "대구", "인천", "광주", "대전", "울산", "세종", "경기", "강원", "충청", "전라", "경상", "제주"};
            String city = cities[random.nextInt(cities.length)];
            String[] districts = {"강남구", "서초구", "송파구", "마포구", "강서구", "영등포구", "종로구", "중구", "동구", "서구", "남구", "북구"};
            String district = districts[random.nextInt(districts.length)];
            String[] neighborhoods = {"역삼동", "논현동", "청담동", "삼성동", "신사동", "서초동", "잠실동", "신천동", "망원동", "합정동", "서교동", "연남동"};
            String neighborhood = neighborhoods[random.nextInt(neighborhoods.length)];
            String address = city + " " + district + " " + neighborhood;

            String[] grades = {"SILVER", "GOLD", "VIP", "VVIP"};
            String grade = grades[random.nextInt(grades.length)];

            // 랜덤 상세 주소 생성
            String apartment = "아파트" + (random.nextInt(20) + 1) + "동";
            String building = "건물" + (random.nextInt(10) + 1);
            String floor = (random.nextInt(20) + 1) + "층";
            String unit = (random.nextInt(10) + 1) + "호";
            String detailAddress = apartment + " " + building + " " + floor + " " + unit;

            // 랜덤 우편번호 생성
            int zipcode = 10000 + random.nextInt(90000);

            // 랜덤 핸드폰 번호 생성
            String phoneNumber = "010-" + String.format("%04d-%04d", random.nextInt(10000), random.nextInt(10000));

            // 랜덤 성별 생성
            String gender = (random.nextBoolean()) ? "MALE" : "FEMALE";

            // 랜덤 마케팅 수신 여부 생성
            boolean consentReceiveMarketing = random.nextBoolean();

            // User 객체 생성 및 추가
            UserSignUpDto build = UserSignUpDto.builder()
                    .name(name)
                    .email(email)
                    .password(password)
                    .birthDate(LocalDate.parse(birthDate))
                    .address(address)
                    .detailAddress(detailAddress)
                    .zipcode(String.valueOf(zipcode))
                    .phoneNumber(phoneNumber)
                    .role(Role.USER)
                    .gender(Gender.valueOf(gender))
                    .grade(Grade.valueOf(grade))
                    .consentReceiveMarketing(consentReceiveMarketing)
                    .build();
            Company company = companyRepository.findById(1L).orElseThrow(
                    () -> new CatchException(ResponseCode.COMPANY_NOT_FOUND));
            User user = User.toEntity(build, company);
            user.passwordEncode(passwordEncoder);
            toEncodeAES(user);

            userRepository.save(user);
        }

        return testUsers;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CS','MARKETER')")
    @Transactional
    public UserDetailDto userUpdate(Long id, UserUpdateDto userUpdateDto, String ip) throws Exception {
        String employeeNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin systemAdmin = adminRepository.findByEmployeeNumber(employeeNumber).orElseThrow(
                () -> new CatchException(ResponseCode.ADMIN_NOT_FOUND)
        );

        User user = userRepository.findById(id).orElseThrow(
                () -> new CatchException(ResponseCode.USER_NOT_FOUND)
        );

        String userNotice = userUpdateDto.getUserNotice();

        user.userUpdate(userNotice);

        AdminLog adminLog = AdminLog.builder()
                .type(LogType.USER_UPDATE) // DB로 나눠 관리하지 않고 LogType으로 구별
                .ip(ip)
                .employeeNumber(aesUtil.aesCBCDecode(systemAdmin.getEmployeeNumber()))
                .method("PATCH")
                .data("update at userId:" + user.getId())
                .build();

        adminLogRepository.save(adminLog);

        return toDetailDto(user);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CS','MARKETER')")
    @Transactional
    public String userDisabled(Long id, String ip) throws Exception {
        String employeeNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin systemAdmin = adminRepository.findByEmployeeNumber(employeeNumber).orElseThrow(
                () -> new CatchException(ResponseCode.ADMIN_NOT_FOUND)
        );

        User user = userRepository.findById(id).orElseThrow(
                () -> new CatchException(ResponseCode.USER_NOT_FOUND)
        );

        user.userActiveToDisable();

        AdminLog adminLog = AdminLog.builder()
                .type(LogType.USER_DISABLED) // DB로 나눠 관리하지 않고 LogType으로 구별
                .ip(ip)
                .employeeNumber(aesUtil.aesCBCDecode(systemAdmin.getEmployeeNumber()))
                .method("PATCH")
                .data("disabled at userId:" + user.getId())
                .build();

        adminLogRepository.save(adminLog);

        return "SUCCESS";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CS','MARKETER')")
    @Transactional
    public String userActivation(Long id, String ip) throws Exception {
        String employeeNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin systemAdmin = adminRepository.findByEmployeeNumber(employeeNumber).orElseThrow(
                () -> new CatchException(ResponseCode.ADMIN_NOT_FOUND)
        );

        User user = userRepository.findById(id).orElseThrow(
                () -> new CatchException(ResponseCode.USER_NOT_FOUND)
        );

        user.userActiveToActivation();

        AdminLog adminLog = AdminLog.builder()
                .type(LogType.USER_ACTIVATION) // DB로 나눠 관리하지 않고 LogType으로 구별
                .ip(ip)
                .employeeNumber(aesUtil.aesCBCDecode(systemAdmin.getEmployeeNumber()))
                .method("PATCH")
                .data("activation at userId:" + user.getId())
                .build();

        adminLogRepository.save(adminLog);

        return "SUCCESS";
    }
    //webPush Test
    public ResponseDto savePushToken(String email, String pushToken) throws Exception {
        User user = userRepository.findByEmail(aesUtil.aesCBCEncode(email))
                .orElseThrow(() -> new CatchException(ResponseCode.USER_NOT_FOUND));
        if(user.isConsentReceiveMarketing()){
            redisService.setValues("PushToken"+user.getId(), pushToken);
            Map<String, String> result = new HashMap<>();
            result.put("pushToken", pushToken);
            return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, result);
        }else{
            return new ResponseDto(HttpStatus.OK, ResponseCode.NOT_RECEIVE_MARKETING_USER, null);
        }

    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<UserInfoDto> findMarketing(Pageable pageable) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Admin admin = adminRepository.findByEmployeeNumber(authentication.getName()).orElseThrow(()-> new CatchException(ResponseCode.ADMIN_NOT_FOUND));
        Company company = admin.getCompany();
        Page<User> users = userRepository.findByCompanyAndConsentReceiveMarketing(company, pageable, true);
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

}
