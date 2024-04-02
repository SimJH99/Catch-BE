package com.encore.thecatch.mail.service;

import com.encore.thecatch.common.redis.RedisService;
import com.encore.thecatch.log.domain.EmailLog;
import com.encore.thecatch.log.domain.LogType;
import com.encore.thecatch.log.repository.EmailLogRepository;
import com.encore.thecatch.mail.Entity.EmailTask;
import com.encore.thecatch.mail.dto.EmailReqDto;
import com.encore.thecatch.mail.dto.GroupEmailReqDto;
import com.encore.thecatch.mail.repository.EmailTaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailSendService {
    private final JavaMailSender javaMailSender;
    private final String username;
    private final RedisService redisService;
    private final EmailLogRepository emailLogRepository;

    private final EmailTaskRepository emailTaskRepository;
    private int authNumber;

    public EmailSendService(
            JavaMailSender javaMailSender,
            @Value("${spring.mail.username}")
            String username,
            RedisService redisService,
            EmailLogRepository emailLogRepository,
            EmailTaskRepository emailTaskRepository
    ) {
        this.javaMailSender = javaMailSender;
        this.username = username;
        this.redisService = redisService;
        this.emailLogRepository = emailLogRepository;
        this.emailTaskRepository = emailTaskRepository;
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
    public String createEmailAuthNumber(EmailReqDto emailReqDto) {
        makeRandomNumber();
        String toMail = emailReqDto.getEmail(); // 넘겨받은 보낼 메일 주소
        String title = "Catch 로그인 인증 메일 입니다."; // 이메일 제목
        String content =
                "Catch 로그인 인증 메일 입니다." +    //html 형식으로 작성 !
                        "<br><br>" +
                        "인증 번호는 " + authNumber + "입니다." +
                        "<br>" +
                        "3분내로 인증번호를 제대로 입력해주세요"; //이메일 내용 삽입
        mailSend(username, toMail, title, content);
        return Integer.toString(authNumber);
    }

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
            e.printStackTrace();//e.printStackTrace()는 예외를 기본 오류 스트림에 출력하는 메서드
        }
        redisService.setValues(Integer.toString(authNumber),toMail, Duration.ofMinutes(3L)); // 유효기간 3분
    }


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
                    .type(LogType.EMAIL)
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
