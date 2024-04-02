package com.encore.thecatch.admin.service;

import com.encore.thecatch.admin.domain.Admin;
import com.encore.thecatch.admin.dto.request.AdminLoginDto;
import com.encore.thecatch.admin.dto.request.AdminSignUpDto;
import com.encore.thecatch.admin.repository.AdminRepository;
import com.encore.thecatch.common.CatchException;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.ResponseDto;
import com.encore.thecatch.common.dto.Role;
import com.encore.thecatch.common.jwt.JwtTokenProvider;
import com.encore.thecatch.common.jwt.RefreshToken.RefreshToken;
import com.encore.thecatch.common.jwt.RefreshToken.RefreshTokenRepository;
import com.encore.thecatch.common.util.AesUtil;
import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.company.repository.CompanyRepository;
import com.encore.thecatch.log.domain.AdminLog;
import com.encore.thecatch.log.domain.LogType;
import com.encore.thecatch.log.repository.AdminLogRepository;
import com.encore.thecatch.user.domain.User;
import com.encore.thecatch.user.dto.request.UserLoginDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    private final AdminLogRepository adminLogRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AesUtil aesUtil;

    public AdminService(
            AdminRepository adminRepository,
            CompanyRepository companyRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            AdminLogRepository adminLogRepository,
            RefreshTokenRepository refreshTokenRepository,
            AesUtil aesUtil
    ) {
        this.adminRepository = adminRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.adminLogRepository = adminLogRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.aesUtil = aesUtil;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public Admin adminSignUp(AdminSignUpDto adminSignUpDto) throws Exception {
        if (adminRepository.findByEmployeeNumber(aesUtil.aesCBCEncode(adminSignUpDto.getEmployeeNumber())).isPresent()) {
            throw new CatchException(ResponseCode.EXISTING_EMPLOYEE_NUMBER);
        }
        Company company = companyRepository.findById(adminSignUpDto.getCompanyId()).orElseThrow(
                ()-> new CatchException(ResponseCode.COMPANY_NOT_FOUND));

        Admin admin = Admin.toEntity(adminSignUpDto, company);
        toEncodeAES(admin);
        admin.passwordEncoder(passwordEncoder);

        return adminRepository.save(admin);
    }

    @Transactional
    public Admin totalAdminSignUp(AdminSignUpDto adminSignUpDto) throws Exception {
        if (adminRepository.findByEmployeeNumber(aesUtil.aesCBCEncode(adminSignUpDto.getEmployeeNumber())).isPresent()) {
            throw new CatchException(ResponseCode.EXISTING_EMPLOYEE_NUMBER);
        }
        Company company = companyRepository.findById(adminSignUpDto.getCompanyId()).orElseThrow(
                ()-> new CatchException(ResponseCode.COMPANY_NOT_FOUND));

        Admin admin = Admin.toEntity(adminSignUpDto, company);
        toEncodeAES(admin);
        admin.passwordEncoder(passwordEncoder);

        return adminRepository.save(admin);
    }

    @Transactional
    public ResponseDto doLogin(AdminLoginDto adminLoginDto, String ip) throws Exception {
        String employeeNum = aesUtil.aesCBCEncode(adminLoginDto.getEmployeeNumber());

        Admin admin = adminRepository.findByEmployeeNumber(employeeNum).orElseThrow(
                () -> new CatchException(ResponseCode.USER_NOT_FOUND));
        if(!passwordEncoder.matches(adminLoginDto.getPassword(),admin.getPassword())){
            throw new CatchException(ResponseCode.USER_NOT_FOUND);
        }
        String accessToken = jwtTokenProvider.createAccessToken(String.format("%s:%s", adminLoginDto.getEmployeeNumber(), admin.getRole())); // 토큰 생성
        String refreshToken = jwtTokenProvider.createRefreshToken(admin.getId()); // 리프레시 토큰 생성
        // 리프레시 토큰이 이미 있으면 토큰을 갱신하고 없으면 토큰을 추가한다.
        refreshTokenRepository.findById(admin.getId())
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
        return new ResponseDto(HttpStatus.OK, "JWT token is created", result);
    }

    private void toEncodeAES(Admin admin) throws Exception {
        String name = aesUtil.aesCBCEncode(admin.getName());
        String employeeNumber = aesUtil.aesCBCEncode(admin.getEmployeeNumber());

        admin.dataEncode(name, employeeNumber);
    }

    private void toDecodeAES(Admin admin) throws Exception {
        String name = aesUtil.aesCBCDecode(admin.getName());
        String employeeNumber = aesUtil.aesCBCDecode(admin.getEmployeeNumber());

        admin.dataDecode(name, employeeNumber);
    }
}
