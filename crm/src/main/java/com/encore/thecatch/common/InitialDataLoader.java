package com.encore.thecatch.common;

import com.encore.thecatch.admin.domain.Admin;
import com.encore.thecatch.admin.dto.request.AdminSignUpDto;
import com.encore.thecatch.admin.repository.AdminRepository;
import com.encore.thecatch.common.dto.Role;
import com.encore.thecatch.common.util.AesUtil;
import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.company.repository.CompanyRepository;
import com.encore.thecatch.complaint.entity.Complaint;
import com.encore.thecatch.complaint.repository.ComplaintRepository;
import com.encore.thecatch.log.domain.LogType;
import com.encore.thecatch.log.domain.UserLog;
import com.encore.thecatch.log.repository.UserLogRepository;
import com.encore.thecatch.user.domain.Gender;
import com.encore.thecatch.user.domain.Grade;
import com.encore.thecatch.user.domain.TotalAddress;
import com.encore.thecatch.user.domain.User;
import com.encore.thecatch.user.dto.request.UserLoginDto;
import com.encore.thecatch.user.dto.request.UserSignUpDto;
import com.encore.thecatch.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class InitialDataLoader implements CommandLineRunner {
    //CommandLineRunner를 통해 스프링빈으로 등록되는 시점에 run메서드 실행

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompanyRepository companyRepository;
    private final AdminRepository adminRepository;
    private final UserLogRepository userLogRepository;
    private final ComplaintRepository complaintRepository;

    private final AesUtil aesUtil;
    static Boolean randomEmployeeNumber = true;


    public InitialDataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder, CompanyRepository companyRepository, AdminRepository adminRepository, UserLogRepository userLogRepository, ComplaintRepository complaintRepository, AesUtil aesUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.companyRepository = companyRepository;
        this.adminRepository = adminRepository;
        this.userLogRepository = userLogRepository;
        this.complaintRepository = complaintRepository;
        this.aesUtil = aesUtil;
    }

    @Override
    public void run(String... args) throws Exception {
        Random random = new Random();

        // 랜덤한 한글 이름 생성
        String[] surnames = {"김", "이", "박", "최", "정", "강", "조", "윤", "장", "임"};
        String[] givenNames = {"민준", "서연", "하준", "지우", "지후", "서준", "서현", "지민", "수빈", "지유", "주원", "지호", "지훈", "예은", "수현", "지원", "다은", "은지", "윤서", "현우"};


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
                String surname = surnames[random.nextInt(surnames.length)];
                String givenName = givenNames[random.nextInt(givenNames.length)];
                String name = surname + givenName;
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

            LocalDate birthDate = LocalDate.of(2000,12,13);
            for (int i = 0; i < 300; i++) {

                if(i % 2 == 0) {
                    birthDate = birthDate.minusDays(3);
                }

                if(i % 5 == 0) {
                    birthDate = birthDate.minusMonths(1);
                }

                if(i % 10 == 0) {
                    birthDate = birthDate.minusYears(1);
                }

                String surname = surnames[random.nextInt(surnames.length)];
                String givenName = givenNames[random.nextInt(givenNames.length)];

                String name = surname + givenName;

                Gender[] genders = {Gender.MALE, Gender.FEMALE};
                String[] grades = {"SILVER", "GOLD", "VIP", "VVIP"};
                String grade = grades[random.nextInt(grades.length)];
                Gender gender = genders[random.nextInt(genders.length)];

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
                        .gender(gender)
                        .consentReceiveMarketing(true)
                        .companyId(1L)
                        .build();
                User user = User.toEntity(userSignUpDto, company);
                user.passwordEncode(passwordEncoder);
                toEncodeAES(user);
                user.userUpdate("", grade);
                testUsers.add(user);
            }

            List<User> users = userRepository.saveAll(testUsers);


            String[] categoryList = {"DELIVERY", "ORDER", "CANCEL/EXCHANGE/REFUND", "MYINFO", "CONFIRMATION", "SERVICE"};
            LocalDateTime createdTime = LocalDateTime.now();

            List<Complaint> complaints = new ArrayList<>();
            List<UserLog> userLogList = new ArrayList<>();

            for(int i = 0; i < users.size(); i++) {

                if(i%3 == 0) {
                    createdTime = createdTime.minusMonths(1);
                }
                if(i%2 == 0) {
                    createdTime = createdTime.minusDays(1);

                }

                users.get(i).setCreatedTime(createdTime);

                UserLog userLoginLog = UserLog.builder()
                        .type(LogType.USER_LOGIN) // DB로 나눠 관리하지 않고 LogType으로 구별
                        .ip("192.168.0.216")
                        .email(aesUtil.aesCBCDecode(users.get(i).getEmail()))
                        .method("POST")
                        .data("user login")
                        .build();
                userLogList.add(userLoginLog);

                String category = categoryList[random.nextInt(categoryList.length)];
                Complaint complaint = Complaint.builder()
                        .title("고객 문의 "+i)
                        .category(category)
                        .user(users.get(i))
                        .contents("문의 드립니다. 상품을 교환하고 싶습니다.")
                        .build();
                complaints.add(complaint);
            }


            userRepository.saveAll(users);
            complaintRepository.saveAll(complaints);
            userLogRepository.saveAll(userLogList);

            List<Complaint> complaintList = new ArrayList<>();
            List<UserLog> userLogUpdateList = new ArrayList<>();
            complaintList = complaintRepository.findAll();
            userLogUpdateList = userLogRepository.findAll();
            LocalDateTime updateCeatedTime = LocalDateTime.of(2024,5,8,0,0);

            for(int i = 0; i < complaintList.size(); i++) {

                if(i % 10 == 0) {
                    updateCeatedTime = updateCeatedTime.minusDays(1);
                }

                if(i % 3 ==0) {
                    updateCeatedTime = updateCeatedTime.minusHours(3);
                }


                complaintList.get(i).setCreatedTime(updateCeatedTime);
                userLogUpdateList.get(i).setCreatedTime(updateCeatedTime);
            }


            userLogRepository.saveAll(userLogUpdateList);
            complaintRepository.saveAll(complaintList);

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

