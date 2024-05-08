package com.encore.thecatch.admin.service;

import com.encore.thecatch.admin.domain.Admin;
import com.encore.thecatch.admin.dto.request.AdminLoginDto;
import com.encore.thecatch.admin.dto.request.AdminSignUpDto;
import com.encore.thecatch.admin.dto.request.AdminUpdateDto;
import com.encore.thecatch.admin.dto.response.AdminDetailDto;
import com.encore.thecatch.admin.dto.response.AdminInfoDto;
import com.encore.thecatch.admin.dto.response.AdminProfileDto;
import com.encore.thecatch.admin.dto.response.AdminSearchDto;
import com.encore.thecatch.admin.repository.AdminQueryRepository;
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
import com.encore.thecatch.log.repository.AdminLogRepository;
import com.encore.thecatch.mail.service.EmailSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class AdminService {
    private final AdminRepository adminRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailSendService emailSendService;
    private final AdminLogRepository adminLogRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AesUtil aesUtil;
    private final MaskingUtil maskingUtil;
    private final AdminQueryRepository adminQueryRepository;
    private final RedisService redisService;

    public AdminService(
            AdminRepository adminRepository,
            CompanyRepository companyRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            EmailSendService emailSendService,
            AdminLogRepository adminLogRepository,
            RefreshTokenRepository refreshTokenRepository,
            AdminQueryRepository adminQueryRepository,
            AesUtil aesUtil,
            MaskingUtil maskingUtil,
            RedisService redisService
    ) {
        this.adminRepository = adminRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.emailSendService = emailSendService;
        this.adminLogRepository = adminLogRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.aesUtil = aesUtil;
        this.maskingUtil = maskingUtil;
        this.adminQueryRepository = adminQueryRepository;
        this.redisService = redisService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public Admin adminSignUp(AdminSignUpDto adminSignUpDto) throws Exception {
        String employeeNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        if (adminRepository.findByEmployeeNumber(aesUtil.aesCBCEncode(adminSignUpDto.getEmployeeNumber())).isPresent()) {
            throw new CatchException(ResponseCode.EXISTING_EMPLOYEE_NUMBER);
        }
        Admin systemAdmin = adminRepository.findByEmployeeNumber(employeeNumber).orElseThrow(
                () -> new CatchException(ResponseCode.USER_NOT_FOUND)
        );
        Company company = companyRepository.findById(systemAdmin.getCompany().getId()).orElseThrow(
                () -> new CatchException(ResponseCode.COMPANY_NOT_FOUND));

        Admin admin = Admin.toEntity(adminSignUpDto, company);
        toEncodeAES(admin);
        admin.passwordEncoder(passwordEncoder);

        return adminRepository.save(admin);
    }

    @Transactional
    public Admin systemAdminSignUp(AdminSignUpDto adminSignUpDto) throws Exception {
        if (adminRepository.findByEmployeeNumber(aesUtil.aesCBCEncode(adminSignUpDto.getEmployeeNumber())).isPresent()) {
            throw new CatchException(ResponseCode.EXISTING_EMPLOYEE_NUMBER);
        }
        Company company = companyRepository.findById(adminSignUpDto.getCompanyId()).orElseThrow(
                () -> new CatchException(ResponseCode.COMPANY_NOT_FOUND));

        Admin admin = Admin.toEntity(adminSignUpDto, company);
        toEncodeAES(admin);
        admin.passwordEncoder(passwordEncoder);

        return adminRepository.save(admin);
    }

    @Transactional
    public ResponseDto doLogin(AdminLoginDto adminLoginDto) throws Exception {
        String employeeNum = aesUtil.aesCBCEncode(adminLoginDto.getEmployeeNumber());

        Admin admin = adminRepository.findByEmployeeNumber(employeeNum).orElseThrow(
                () -> new CatchException(ResponseCode.ADMIN_NOT_FOUND));

        if (!admin.isActive()) {
            throw new CatchException(ResponseCode.DISABLED_ACCOUNT);
        }

        if (!passwordEncoder.matches(adminLoginDto.getPassword(), admin.getPassword())) {
            throw new CatchException(ResponseCode.FAIL_PASSWORD_CHECK);
        }

        return new ResponseDto(HttpStatus.OK, ResponseCode.CHECK_EMAIL, "CHECK_EMAIL");
    }

    public ResponseDto validateAuthNumber(String employeeNumber, String authNumber, String ip) {
        try {
            Admin admin = adminRepository.findByEmployeeNumber(aesUtil.aesCBCEncode(employeeNumber))
                    .orElseThrow(() -> new CatchException(ResponseCode.ADMIN_NOT_FOUND));

            // 입력된 인증 번호를 검증합니다.
            boolean isAuthValid = emailSendService.checkAuthNum(aesUtil.aesCBCDecode(admin.getEmail()), authNumber);

            // 인증 번호가 유효하면 로그인 성공
            if (isAuthValid) {
                String accessToken = jwtTokenProvider.createAccessToken(String.format("%s:%s", admin.getEmployeeNumber(), admin.getRole())); // 토큰 생성
                String refreshToken = jwtTokenProvider.createRefreshToken(admin.getRole(), admin.getId()); // 리프레시 토큰 생성

                // 리프레시 토큰이 이미 있으면 토큰을 갱신하고 없으면 토큰을 추가한다.
                refreshTokenRepository.findByAdminEmployeeNumber(admin.getEmployeeNumber())
                        .ifPresentOrElse(
                                it -> it.updateRefreshToken(refreshToken),
                                () -> refreshTokenRepository.save(new RefreshToken(admin, refreshToken))
                        );
                Map<String, String> result = new HashMap<>();
                result.put("access_token", accessToken);
                result.put("refresh_token", refreshToken);

                AdminLog adminLog = AdminLog.builder()
                        .type(LogType.ADMIN_LOGIN) // DB로 나눠 관리하지 않고 LogType으로 구별
                        .ip(ip)
                        .employeeNumber(aesUtil.aesCBCDecode(admin.getEmployeeNumber()))
                        .method("POST")
                        .data("admin login")
                        .build();

                adminLogRepository.save(adminLog);
                return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_LOGIN, result);
            } else {
                return new ResponseDto(HttpStatus.UNAUTHORIZED, ResponseCode.INVALID_VERIFICATION_CODE, null);
            }
        } catch (Exception e) {
            log.info(String.valueOf(e));
            return new ResponseDto(HttpStatus.INTERNAL_SERVER_ERROR, ResponseCode.INTERNAL_SERVER_ERROR, null);
        }
    }

    public ResponseDto superLogin(AdminLoginDto adminLoginDto) throws Exception {
        Admin admin = adminRepository.findByEmployeeNumber(aesUtil.aesCBCEncode(adminLoginDto.getEmployeeNumber()))
                .orElseThrow(() -> new CatchException(ResponseCode.ADMIN_NOT_FOUND));

        // 인증 번호가 유효하면 로그인 성공
        String accessToken = jwtTokenProvider.createAccessToken(String.format("%s:%s", admin.getEmployeeNumber(), admin.getRole())); // 토큰 생성
        String refreshToken = jwtTokenProvider.createRefreshToken(admin.getRole(), admin.getId()); // 리프레시 토큰 생성

        // 리프레시 토큰이 이미 있으면 토큰을 갱신하고 없으면 토큰을 추가한다.
        refreshTokenRepository.findByAdminEmployeeNumber(admin.getEmployeeNumber())
                .ifPresentOrElse(
                        it -> it.updateRefreshToken(refreshToken),
                        () -> refreshTokenRepository.save(new RefreshToken(admin, refreshToken))
                );

        Map<String, String> result = new HashMap<>();
        result.put("access_token", accessToken);
        result.put("refresh_token", refreshToken);

        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_LOGIN, result);
    }

    private void toMasking(Admin admin) {
        String maskingName = maskingUtil.nameMasking(admin.getName());
        String maskingEmployeeNumber = maskingUtil.employeeNumberMasking(admin.getEmployeeNumber());
        String maskingEmail = maskingUtil.emailMasking(admin.getEmail());

        admin.masking(maskingName, maskingEmployeeNumber, maskingEmail);
    }

    private void toEncodeAES(Admin admin) throws Exception {
        String name = aesUtil.aesCBCEncode(admin.getName());
        String employeeNumber = aesUtil.aesCBCEncode(admin.getEmployeeNumber());
        String email = aesUtil.aesCBCEncode(admin.getEmail());

        admin.dataEncode(name, employeeNumber, email);
    }

    private void toDecodeAES(Admin admin) throws Exception {
        String name = aesUtil.aesCBCDecode(admin.getName());
        String employeeNumber = aesUtil.aesCBCDecode(admin.getEmployeeNumber());
        String email = aesUtil.aesCBCDecode(admin.getEmail());

        admin.dataDecode(name, employeeNumber, email);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<AdminInfoDto> searchAdmin(AdminSearchDto adminSearchDto, Pageable pageable) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Admin admin = adminRepository.findByEmployeeNumber(authentication.getName()).orElseThrow(() -> new CatchException(ResponseCode.USER_NOT_FOUND));
        return adminQueryRepository.findAdminList(adminSearchDto, admin.getCompany(), pageable)
                .map(new Function<AdminInfoDto, AdminInfoDto>() {
                    @Override
                    public AdminInfoDto apply(AdminInfoDto adminInfoDto) {
                        try {
                            return AdminInfoDto.builder()
                                    .id(adminInfoDto.getId())
                                    .name(maskingUtil.nameMasking(aesUtil.aesCBCDecode(adminInfoDto.getName())))
                                    .employeeNumber(maskingUtil.employeeNumberMasking(aesUtil.aesCBCDecode(adminInfoDto.getEmployeeNumber())))
                                    .email(maskingUtil.emailMasking(aesUtil.aesCBCDecode(adminInfoDto.getEmail())))
                                    .role(adminInfoDto.getRole())
                                    .build();
                        } catch (Exception e) {
                            throw new CatchException(ResponseCode.AES_DECODE_FAIL);
                        }
                    }
                });

    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseCode emailCheck(AdminSignUpDto adminSignUpDto) throws Exception {
        if (adminRepository.findByEmail(aesUtil.aesCBCEncode(adminSignUpDto.getEmail())).isPresent()) {
            return ResponseCode.EXISTING_EMAIL;
        } else {
            return ResponseCode.SUCCESS;
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseCode employeeNumberCheck(AdminSignUpDto adminSignUpDto) throws Exception {
        if (adminRepository.findByEmployeeNumber(aesUtil.aesCBCEncode(adminSignUpDto.getEmployeeNumber())).isPresent()) {
            return ResponseCode.EXISTING_EMPLOYEE_NUMBER;
        } else {
            return ResponseCode.SUCCESS;
        }
    }


//    public List<Admin> createTestAdmins(int count, boolean randomEmployeeNumber) throws Exception {
//        List<Admin> testAdmins = new ArrayList<>();
//        Random random = new Random(); // 랜덤 객체 생성
//        Company company = companyRepository.findById(1L).orElseThrow();
//        Set<String> usedEmployeeNumbers = new HashSet<>(); // 중복된 직원 번호를 체크하기 위한 Set
//
//        for (int i = 0; i < count; i++) {
//            String employeeNumber;
//            // 직원 번호 생성
//            do {
//                if (randomEmployeeNumber) {
//                    // 랜덤한 직원 번호 생성
//                    StringBuilder sb = new StringBuilder();
//                    sb.append("B");
//                    for (int j = 0; j < 5; j++) {
//                        // 랜덤한 숫자를 문자열로 추가합니다.
//                        sb.append(random.nextInt(10));
//                    }
//                    employeeNumber = sb.toString();
//                } else {
//                    // 일련번호 형식의 직원 번호 생성
//                    employeeNumber = "B" + String.format("%05d", i + 1);
//                }
//            } while (!usedEmployeeNumbers.add(employeeNumber)); // 중복된 번호가 발생하면 다시 번호 생성
//
//            // 랜덤한 한글 이름 생성
//            String[] surnames = {"김", "이", "박", "최", "정", "강", "조", "윤", "장", "임"};
//            String[] givenNames = {"민준", "서연", "하준", "지우", "지후", "서준", "서현", "지민", "수빈", "지유", "주원", "지호", "지훈", "예은", "수현", "지원", "다은", "은지", "윤서", "현우"};
//            String surname = surnames[random.nextInt(surnames.length)];
//            String givenName = givenNames[random.nextInt(givenNames.length)];
//            String name = surname + givenName;
//
//            AdminSignUpDto adminSignUpDto = new AdminSignUpDto();
//            adminSignUpDto.setEmployeeNumber(employeeNumber);
//            adminSignUpDto.setName(name);
//            adminSignUpDto.setEmail("testt" + i + "@test.com");
//            adminSignUpDto.setPassword("1234");
//            // 랜덤으로 role 선택
//            String role = (random.nextBoolean()) ? "MARKETER" : "CS";
//            adminSignUpDto.setRole(Role.valueOf(role));
//
//            Admin admin = Admin.toEntity(adminSignUpDto, company);
//
//            // 필요한 경우 데이터 암호화
//            toEncodeAES(admin);
//
//            // 비밀번호 인코딩
//            admin.passwordEncoder(passwordEncoder);
//
//            // Admin 저장
//            testAdmins.add(adminRepository.save(admin));
//        }
//
//        return testAdmins;
//    }

    //webPush Test

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public AdminDetailDto adminDetail(Long id, String ip) throws Exception {
        String employeeNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin systemAdmin = adminRepository.findByEmployeeNumber(employeeNumber).orElseThrow(
                () -> new CatchException(ResponseCode.ADMIN_NOT_FOUND)
        );

        Admin admin = adminRepository.findById(id).orElseThrow(
                () -> new CatchException(ResponseCode.USER_NOT_FOUND)
        );

        AdminDetailDto adminDetailDto = AdminDetailDto.builder()
                .name(aesUtil.aesCBCDecode(admin.getName()))
                .email(aesUtil.aesCBCDecode(admin.getEmail()))
                .employeeNumber(aesUtil.aesCBCDecode(admin.getEmployeeNumber()))
                .role(admin.getRole())
                .active(admin.isActive())
                .build();

        AdminLog adminLog = AdminLog.builder()
                .type(LogType.ADMIN_DETAIL_VIEW) // DB로 나눠 관리하지 않고 LogType으로 구별
                .ip(ip)
                .employeeNumber(aesUtil.aesCBCDecode(systemAdmin.getEmployeeNumber()))
                .method("GET")
                .data("view at adminId:" + admin.getId() + " detail")
                .build();

        adminLogRepository.save(adminLog);
        return adminDetailDto;
    }

    public AdminProfileDto adminProfile() throws Exception {
        String employeeNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin admin = adminRepository.findByEmployeeNumber(employeeNumber).orElseThrow(
                () -> new CatchException(ResponseCode.ADMIN_NOT_FOUND)
        );
        return AdminProfileDto.builder()
                .name(aesUtil.aesCBCDecode(admin.getName()))
                .employeeNumber(aesUtil.aesCBCDecode(admin.getEmployeeNumber()))
                .role(admin.getRole())
                .build();
    }

    @Transactional
    public void adminLogout(String ip) throws Exception {
        String employeeNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin admin = adminRepository.findByEmployeeNumber(employeeNumber).orElseThrow(
                () -> new CatchException(ResponseCode.ADMIN_NOT_FOUND)
        );
        RefreshToken refreshToken = refreshTokenRepository.findByAdminEmployeeNumber(admin.getEmployeeNumber()).orElseThrow(
                () -> new CatchException(ResponseCode.REFRESH_TOKEN_NOT_FOUND)
        );
        refreshTokenRepository.delete(refreshToken);
        redisService.deleteValues(admin.getRole() + "" + admin.getId());

        AdminLog adminLog = AdminLog.builder()
                .type(LogType.ADMIN_LOGOUT) // DB로 나눠 관리하지 않고 LogType으로 구별
                .ip(ip)
                .employeeNumber(aesUtil.aesCBCDecode(admin.getEmployeeNumber()))
                .method("POST")
                .data("admin logout")
                .build();

        adminLogRepository.save(adminLog);
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public AdminDetailDto adminUpdate(Long id, AdminUpdateDto adminUpdateDto, String ip) throws Exception {
        String employeeNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin systemAdmin = adminRepository.findByEmployeeNumber(employeeNumber).orElseThrow(
                () -> new CatchException(ResponseCode.ADMIN_NOT_FOUND)
        );

        Admin admin = adminRepository.findById(id).orElseThrow(
                () -> new CatchException(ResponseCode.USER_NOT_FOUND)
        );
        String name = adminUpdateDto.getName();
        String email = adminUpdateDto.getEmail();
        Role role = adminUpdateDto.getRole();
        if (!email.equals(aesUtil.aesCBCDecode(admin.getEmail())) && adminRepository.findByEmail(aesUtil.aesCBCEncode(email)).isPresent()) {
            throw new CatchException(ResponseCode.EXISTING_EMAIL);
        }

        if (name.isBlank() || email.isBlank()) {
            throw new CatchException(ResponseCode.IS_BLANK);
        }
        admin.adminUpdate(aesUtil.aesCBCEncode(name), aesUtil.aesCBCEncode(email), role);

        AdminLog adminLog = AdminLog.builder()
                .type(LogType.ADMIN_UPDATE) // DB로 나눠 관리하지 않고 LogType으로 구별
                .ip(ip)
                .employeeNumber(aesUtil.aesCBCDecode(systemAdmin.getEmployeeNumber()))
                .method("PATCH")
                .data("update at adminId:" + admin.getId())
                .build();

        adminLogRepository.save(adminLog);

        return AdminDetailDto.builder()
                .name(aesUtil.aesCBCDecode(admin.getName()))
                .email(aesUtil.aesCBCDecode(admin.getEmail()))
                .employeeNumber(aesUtil.aesCBCDecode(admin.getEmployeeNumber()))
                .role(admin.getRole())
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public String adminDisabled(Long id, String ip) throws Exception {
        String employeeNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin systemAdmin = adminRepository.findByEmployeeNumber(employeeNumber).orElseThrow(
                () -> new CatchException(ResponseCode.ADMIN_NOT_FOUND)
        );

        Admin admin = adminRepository.findById(id).orElseThrow(
                () -> new CatchException(ResponseCode.USER_NOT_FOUND)
        );

        admin.adminActiveToDisable();

        AdminLog adminLog = AdminLog.builder()
                .type(LogType.ADMIN_DISABLED) // DB로 나눠 관리하지 않고 LogType으로 구별
                .ip(ip)
                .employeeNumber(aesUtil.aesCBCDecode(systemAdmin.getEmployeeNumber()))
                .method("PATCH")
                .data("disabled at adminId:" + admin.getId())
                .build();

        adminLogRepository.save(adminLog);

        return "SUCCESS_ADMIN_DISABLED";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public String adminActivation(Long id, String ip) throws Exception {
        String employeeNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin systemAdmin = adminRepository.findByEmployeeNumber(employeeNumber).orElseThrow(
                () -> new CatchException(ResponseCode.ADMIN_NOT_FOUND)
        );

        Admin admin = adminRepository.findById(id).orElseThrow(
                () -> new CatchException(ResponseCode.USER_NOT_FOUND)
        );

        admin.adminActiveToActivation();

        AdminLog adminLog = AdminLog.builder()
                .type(LogType.ADMIN_ACTIVATION) // DB로 나눠 관리하지 않고 LogType으로 구별
                .ip(ip)
                .employeeNumber(aesUtil.aesCBCDecode(systemAdmin.getEmployeeNumber()))
                .method("PATCH")
                .data("activation at adminId:" + admin.getId())
                .build();

        adminLogRepository.save(adminLog);

        return "SUCCESS_ADMIN_ACTIVATION";
    }
}
