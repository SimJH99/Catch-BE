package com.encore.thecatch.common;

import com.encore.thecatch.admin.domain.Admin;
import com.encore.thecatch.admin.dto.request.AdminSignUpDto;
import com.encore.thecatch.admin.repository.AdminRepository;
import com.encore.thecatch.common.dto.Role;
import com.encore.thecatch.common.util.AesUtil;
import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.company.repository.CompanyRepository;
import com.encore.thecatch.user.domain.Gender;
import com.encore.thecatch.user.domain.TotalAddress;
import com.encore.thecatch.user.domain.User;
import com.encore.thecatch.user.dto.request.UserSignUpDto;
import com.encore.thecatch.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class InitialDataLoader implements CommandLineRunner {
    //CommandLineRunner를 통해 스프링빈으로 등록되는 시점에 run메서드 실행

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompanyRepository companyRepository;
    private final AdminRepository adminRepository;
    private final AesUtil aesUtil;
    static Boolean randomEmployeeNumber = true;


    public InitialDataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder, CompanyRepository companyRepository, AdminRepository adminRepository, AesUtil aesUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.companyRepository = companyRepository;
        this.adminRepository = adminRepository;
        this.aesUtil = aesUtil;
    }

    @Override
    public void run(String... args) throws Exception {
        Random random = new Random();

        // 랜덤한 한글 이름 생성
        String[] surnames = {"김", "이", "박", "최", "정", "강", "조", "윤", "장", "임"};
        String[] givenNames = {"민준", "서연", "하준", "지우", "지후", "서준", "서현", "지민", "수빈", "지유", "주원", "지호", "지훈", "예은", "수현", "지원", "다은", "은지", "윤서", "현우"};
        String surname = surnames[random.nextInt(surnames.length)];
        String givenName = givenNames[random.nextInt(givenNames.length)];

        String name = surname + givenName;

        if(companyRepository.count() < 1) {
            Company company = Company.builder()
                    .name("캐치")
                    .build();
            companyRepository.save(company);
        }

        if(adminRepository.count() < 1){
            List<Admin> testAdmins = new ArrayList<>();
            Company company = companyRepository.findById(1L).orElseThrow();
            Set<String> usedEmployeeNumbers = new HashSet<>(); // 중복된 직원 번호를 체크하기 위한 Set

            for (int i = 0; i < 300; i++) {
                String employeeNumber;
                // 직원 번호 생성
                do {
                    if (randomEmployeeNumber) {
                        // 랜덤한 직원 번호 생성
                        StringBuilder sb = new StringBuilder();
                        sb.append("B");
                        for (int j = 0; j < 5; j++) {
                            // 랜덤한 숫자를 문자열로 추가합니다.
                            sb.append(random.nextInt(10));
                        }
                        employeeNumber = sb.toString();
                    } else {
                        // 일련번호 형식의 직원 번호 생성
                        employeeNumber = "B" + String.format("%05d", i + 1);
                    }
                } while (!usedEmployeeNumbers.add(employeeNumber)); // 중복된 번호가 발생하면 다시 번호 생성


                AdminSignUpDto adminSignUpDto = new AdminSignUpDto();
                adminSignUpDto.setEmployeeNumber(employeeNumber);
                adminSignUpDto.setName(name);
                adminSignUpDto.setEmail("admin" + i + "@test.com");
                adminSignUpDto.setPassword("1234");
                // 랜덤으로 role 선택
                String role = (random.nextBoolean()) ? "MARKETER" : "CS";
                adminSignUpDto.setRole(Role.valueOf(role));

                Admin admin = Admin.toEntity(adminSignUpDto, company);

                // 필요한 경우 데이터 암호화
                toEncodeAES(admin);

                // 비밀번호 인코딩
                admin.passwordEncoder(passwordEncoder);

                // Admin 저장
                testAdmins.add(admin);
            }

            adminRepository.saveAll(testAdmins);
        }

        if(userRepository.count() < 1) {
            List<User> testUsers = new ArrayList<>();
            Company company = companyRepository.findById(1L).orElseThrow();

            LocalDate birthDate = LocalDate.of(1990,1,1);

            for (int i = 0; i < 300; i++) {

                UserSignUpDto userSignUpDto = UserSignUpDto.builder()
                        .name(name)
                        .email("user"+i+"@test.com")
                        .password("1234")
                        .birthDate(birthDate)
                        .address("동작구")
                        .detailAddress("보라매")
                        .zipcode("12345")
                        .phoneNumber("010-1234-5678")
                        .role(Role.USER)
                        .gender(Gender.FEMALE)
                        .consentReceiveMarketing(true)
                        .companyId(1L)
                        .build();
                User user = User.toEntity(userSignUpDto, company);
                user.passwordEncode(passwordEncoder);
                toEncodeAES(user);
                testUsers.add(user);
            }

            userRepository.saveAll(testUsers);
        }


    }

    private void toEncodeAES(Admin admin) throws Exception {
        String name = aesUtil.aesCBCEncode(admin.getName());
        String employeeNumber = aesUtil.aesCBCEncode(admin.getEmployeeNumber());
        String email = aesUtil.aesCBCEncode(admin.getEmail());

        admin.dataEncode(name, employeeNumber, email);
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


}

