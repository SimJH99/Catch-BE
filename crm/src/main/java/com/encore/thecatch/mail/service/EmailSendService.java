package com.encore.thecatch.mail.service;

import com.encore.thecatch.admin.domain.Admin;
import com.encore.thecatch.admin.dto.request.AdminLoginDto;
import com.encore.thecatch.admin.repository.AdminRepository;
import com.encore.thecatch.common.CatchException;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.RsData;
import com.encore.thecatch.common.redis.RedisService;
import com.encore.thecatch.common.util.AesUtil;
import com.encore.thecatch.log.domain.EmailLog;
import com.encore.thecatch.log.repository.EmailLogRepository;
import com.encore.thecatch.mail.Entity.EmailTask;
import com.encore.thecatch.mail.dto.GroupEmailReqDto;
import com.encore.thecatch.mail.repository.EmailTaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class EmailSendService {
    private final JavaMailSender javaMailSender;
    private final String username;
    private final RedisService redisService;
    private final EmailLogRepository emailLogRepository;
    private final EmailTaskRepository emailTaskRepository;
    private final AdminRepository adminRepository;
    private final AesUtil aesUtil;
    private int authNumber;

    public EmailSendService(
            JavaMailSender javaMailSender,
            @Value("${spring.mail.username}")
            String username,
            RedisService redisService,
            EmailLogRepository emailLogRepository,
            EmailTaskRepository emailTaskRepository,
            AdminRepository adminRepository,
            AesUtil aesUtil
    ) {
        this.javaMailSender = javaMailSender;
        this.username = username;
        this.redisService = redisService;
        this.emailLogRepository = emailLogRepository;
        this.emailTaskRepository = emailTaskRepository;
        this.adminRepository = adminRepository;
        this.aesUtil = aesUtil;
    }

    public boolean checkAuthNum(String email, String authNum) {
        if (redisService.getValues(authNum) == null){
            return false;
        }
        else if (redisService.getValues(authNum).equals(email)) {
            return true;
        } else {
            return false;
        }
    }

    //임의의 6자리 양수를 반환합니다.
    public void makeRandomNumber() {
        Random r = new Random();
        StringBuilder randomNumber = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            randomNumber.append(r.nextInt(10));
        }

        authNumber = Integer.parseInt(randomNumber.toString());
    }

    @Async
    public void createEmailAuthNumber(AdminLoginDto adminLoginDto) throws Exception {
        Admin admin = adminRepository.findByEmployeeNumber(aesUtil.aesCBCEncode(adminLoginDto.getEmployeeNumber()))
                .orElseThrow(() -> new CatchException(ResponseCode.USER_NOT_FOUND));

        makeRandomNumber();
        String toMail = aesUtil.aesCBCDecode(admin.getEmail()); // 넘겨받은 보낼 메일 주소
        String title = "Catch 로그인 인증 메일 입니다."; // 이메일 제목
        String content =
                "<div style='font-family: Arial, sans-serif; color: #333333; border-top: 2px solid #CCCCCC; padding-top: 20px;'>" +
                "<h2 style='margin-bottom: 20px;'>Catch 로그인 인증 메일입니다.</h2>" +
                "<p style='margin-bottom: 10px;'>인증 번호는 <strong>" + authNumber + "</strong>입니다.</p>" +
                "<p>3분 내로 인증 번호를 제대로 입력해주세요.</p>" +
                "</div>" +
                "<div style='border-bottom: 2px solid #CCCCCC; padding-bottom: 20px;'></div>";

        mailSend(username, toMail, title, content);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CS','MARKETER')")
    public String createGroupEmail(GroupEmailReqDto groupEmailReqDto) {
        List<String> emailList = groupEmailReqDto.getEmailList();

        EmailTask task = EmailTask.builder()
                .title(groupEmailReqDto.getTitle())
                .build();
        emailTaskRepository.save(task);

        for (String email : emailList) {
            String title = groupEmailReqDto.getTitle(); // 이메일 제목
            String content = groupEmailReqDto.getContents(); // 이메일 내용
            GroupSend(task, username, email, title, content);
        }
        return "전송 완료";
    }

    //이메일을 전송합니다.
    @Async
    public void mailSend(String setFrom, String toMail, String title, String content) {
        MimeMessage message = javaMailSender.createMimeMessage();//JavaMailSender 객체를 사용하여 MimeMessage 객체를 생성
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");//이메일 메시지와 관련된 설정을 수행합니다.
            // true를 전달하여 multipart 형식의 메시지를 지원하고, "utf-8"을 전달하여 문자 인코딩을 설정
            helper.setFrom(setFrom);//이메일의 발신자 주소 설정
            helper.setTo(toMail);//이메일의 수신자 주소 설정
            helper.setSubject(title);//이메일의 제목을 설정
            helper.setText(content, true);//이메일의 내용 설정 두 번째 매개 변수에 true를 설정하여 html 설정으로한다.
            javaMailSender.send(message);
        } catch (MessagingException e) {//이메일 서버에 연결할 수 없거나, 잘못된 이메일 주소를 사용하거나, 인증 오류가 발생하는 등 오류
            // 이러한 경우 MessagingException이 발생

            log.error("error: " + e);
        }
        redisService.setValues(Integer.toString(authNumber),toMail, Duration.ofMinutes(3L)); // 유효기간 3분
    }


    @PreAuthorize("hasAuthority('MARKETER')")
    public void GroupSend(EmailTask task, String setFrom, String toMail, String title, String content) {
//        if (toMail.endsWith("@naver.com")) return CompletableFuture.completedFuture(RsData.of("S-2", "메일이 발송되었습니다."));
        CompletableFuture.supplyAsync(() -> {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            try {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
                helper.setFrom(setFrom);//이메일의 발신자 주소 설정
                helper.setTo(toMail);//이메일의 수신자 주소 설정
                helper.setSubject(title);//이메일의 제목을 설정
                helper.setText(content, true);//이메일의 내용 설정 두 번째 매개 변수에 true를 설정하여 html 설정으로한다.
                javaMailSender.send(mimeMessage);
                return RsData.of("S-1", "메일이 발송되었습니다.", toMail);
            } catch (MessagingException e) {
                return RsData.of("F-1", "메일이 발송되지 않았습니다.", toMail);
            }

        }).thenApply(result -> {
            // CompletableFuture가 완료된 후에 실행될 작업을 정의합니다.
            EmailLog log = EmailLog.builder()
                    .message(result.getMsg())
                    .CODE(result.getResultCode())
                    .toEmail(result.getData())
                    .emailTaskId(task.getId())
                    .build();
            emailLogRepository.save(log);
            return result;
        });
    }
}
