package com.encore.thecatch.mail.service;

import com.encore.thecatch.admin.domain.Admin;
import com.encore.thecatch.admin.dto.request.AdminLoginDto;
import com.encore.thecatch.admin.repository.AdminRepository;
import com.encore.thecatch.common.CatchException;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.RsData;
import com.encore.thecatch.common.redis.RedisService;
import com.encore.thecatch.common.util.AesUtil;
import com.encore.thecatch.coupon.domain.Coupon;
import com.encore.thecatch.coupon.repository.CouponRepository;
import com.encore.thecatch.event.domain.Event;
import com.encore.thecatch.event.repository.EventRepository;
import com.encore.thecatch.log.domain.CouponEmailLog;
import com.encore.thecatch.log.domain.EmailLog;
import com.encore.thecatch.log.domain.LogType;
import com.encore.thecatch.log.repository.CouponEmailLogRepository;
import com.encore.thecatch.log.repository.EmailLogRepository;
import com.encore.thecatch.mail.dto.CommentsEmailDto;
import com.encore.thecatch.mail.dto.CouponEmailReqDto;
import com.encore.thecatch.mail.dto.EventEmailReqDto;
import com.encore.thecatch.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.time.Duration;
import java.util.ArrayList;
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
    private final AdminRepository adminRepository;
    private final AesUtil aesUtil;
    private final EventRepository eventRepository;
    private int authNumber;

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final CouponEmailLogRepository couponEmailLogRepository;

    public EmailSendService(
            JavaMailSender javaMailSender,
            @Value("${spring.mail.username}")
            String username,
            RedisService redisService,
            EmailLogRepository emailLogRepository,
            AdminRepository adminRepository,
            AesUtil aesUtil,
            EventRepository eventRepository,
            UserRepository userRepository, CouponRepository couponRepository,
            CouponEmailLogRepository couponEmailLogRepository
    ) {
        this.javaMailSender = javaMailSender;
        this.username = username;
        this.redisService = redisService;
        this.emailLogRepository = emailLogRepository;
        this.adminRepository = adminRepository;
        this.aesUtil = aesUtil;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.couponRepository = couponRepository;
        this.couponEmailLogRepository = couponEmailLogRepository;
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
    public String createEmailAuthNumber(AdminLoginDto adminLoginDto) throws Exception {
        Admin admin = adminRepository.findByEmployeeNumber(aesUtil.aesCBCEncode(adminLoginDto.getEmployeeNumber()))
                .orElseThrow(() -> new CatchException(ResponseCode.ADMIN_NOT_FOUND));

        makeRandomNumber();
        String toMail = aesUtil.aesCBCDecode(admin.getEmail()); // 보낼 이메일 주소
        String title = "Catch 로그인 인증 메일"; // 이메일 제목
        String content =
                "<!DOCTYPE html>" +
                        "<html lang=\"en\">" +
                        "<head>" +
                        "<meta charset=\"UTF-8\">" +
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                        "<title>Catch 로그인 인증 코드</title>" +
                        "</head>" +
                        "<body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 20px 0;\">" +
                        "<table align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"margin: 0 auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);\">" +
                        "<tr>" +
                        "<td style=\"padding: 20px;\">" +
                        "<h2 style=\"color: #f5a742; margin-bottom: 50px;\">Catch 로그인 인증 코드</h2>" +
                        "<p style=\"margin-bottom: 10px; font-size: 16px; color: #555555;\">안녕하세요, Catch에 로그인하려고 하시는군요!</p>" +
                        "<p style=\"margin-bottom: 10px; font-size: 16px; color: #555555;\">인증 번호: <span style=\"font-size: 24px; color: #f5a742;\">" + authNumber + "</span></p>" +
                        "<p style=\"margin-bottom: 0; font-size: 14px; color: #999999;\">이 인증 번호는 3분 동안 유효합니다. 로그인 화면에 입력해주세요.</p>" +
                        "</td>" +
                        "</tr>" +
                        "</table>" +
                        "</body>" +
                        "</html>";

        mailSend(username, toMail, title, content);

        return "success";
    }

    @Async
    public void createCommentsEmail(CommentsEmailDto commentsEmailDto) throws Exception {

        String setFrom = username;
        String toMail = commentsEmailDto.getUserEmail(); // 받는 이메일 주소
        String title = "[주)"+commentsEmailDto.getAdminCompany()+"] 1:1 상담에 대한 답변이 등록되었습니다."; // 이메일 제목
        String content = "<p style=\"font-size: 10pt; font-family: sans-serif; padding: 0px 0px 0px 10pt;\"><br></p>\n" +
                "<table align=\"center\" width=\"700\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border: 1px solid rgb(187, 192, 196);\">\n" +
                "    <tbody><tr><td style=\"padding: 24px 14px 0px;\">\n" +
                "                <table width=\"670\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                <tbody><tr><td>\n" +
                "                           <img src=\"http://m-img.cafe24.com/images/template/admin/ko_KR/img_visual_customer_20.jpg\">\n" +
                "                        </td></tr><tr><td style=\"padding: 50px 0px 0px 10px; font-size: 12px; font-family: Gulim; color: rgb(57, 57, 57); line-height: 19px;\">\n" +
                "                            <p><b>" + commentsEmailDto.getUserName() + "</b> 님께서 <b>" + commentsEmailDto.getComplaintCreatedTime() + "</b>에 문의하신 내용에 대한 답변입니다.<br>\n" +
                "                            답변을 받기까지 많이 기다리시진 않으셨는지요. <br>최대한 빠르고 정확한 답변을 드리기 위해 더욱 노력하겠습니다.</p>\n" +
                "                        </td></tr><tr><td>\n" +
                "                            \n" +
                "                            <table width=\"670\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size: 12px; font-family: Gulim; color: rgb(57, 57, 57); line-height: 19px;\">\n" +
                "                            <tbody><tr><td style=\"padding: 23px 0px 0px;\">\n" +
                "                                        <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin: 0px 0px 20px;\">\n" +
                "                                        <tbody><tr><td width=\"19\"><img src=\"http://m-img.cafe24.com/images/template/admin/ko_KR/ico_title.gif\"></td><td><strong style=\"font-size: 13px; font-family: Gulim; color: rgb(28, 28, 28);\">고객님께서 작성하신 내용입니다.</strong></td></tr></tbody>\n" +
                "                                        </table>\n" +
                "                                    </td></tr><tr><td align=\"left\" height=\"130\" style=\"padding: 14px; font-size: 12px; font-family: Gulim; border: 1px solid rgb(213, 213, 213);\">" + commentsEmailDto.getComplaintContents() + "\n" +
                "                                    </td></tr><tr><td height=\"40\">&nbsp;</td></tr><tr><td>\n" +
                "                                        <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin: 0px 0px 20px;\">\n" +
                "                                        <tbody><tr><td width=\"19\"><img src=\"http://m-img.cafe24.com/images/template/admin/ko_KR/ico_title.gif\"></td><td><strong style=\"font-size: 13px; font-family: Gulim; color: rgb(28, 28, 28);\">문의하신 내용에 대한 답변입니다.</strong></td></tr></tbody>\n" +
                "                                        </table>\n" +
                "                                    </td></tr><tr><td align=\"left\" height=\"130\" style=\"padding: 14px; font-size: 12px; font-family: Gulim; border: 1px solid rgb(213, 213, 213);\">\n" +
                "                                        <p>" + commentsEmailDto.getCommentContents() + "<br></p>\n" +
                "                                    </td></tr></tbody>\n" +
                "                            </table>\n" +
                "                            \n" +
                "                        </td></tr><tr><td style=\"padding: 30px 0px 60px 10px; font-size: 12px; font-family: Gulim; color: rgb(57, 57, 57); line-height: 19px;\">\n" +
                "                            <p>만족스러운 답변이 되셨기를 바랍니다. <br>앞으로도 저희 쇼핑몰의 많은 이용부탁드립니다. 감사합니다.</p>\n" +
                "                        </td></tr></tbody>\n" +
                "                </table>\n" +
                "            </td></tr><tr><td style=\"padding: 24px 34px; font-family: Gulim; font-size: 12px; line-height: 18px; background-color: rgb(202, 205, 212); color: rgb(255, 255, 255);\">\n" +
                "                <p style=\"\"><br></p>\n" +
                "            </td></tr></tbody>\n" +
                "</table>";

        // 메일 전송
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
        });
    }


    @Async
    public String createCouponEmail(Long id, CouponEmailReqDto couponEmailReqDto) throws Exception {

        List<Long> userIds = couponEmailReqDto.getUserIds();
        List<String> emailList = new ArrayList<>();
        for(Long userId : userIds){
            String email = aesUtil.aesCBCDecode(userRepository.findById(userId).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND)).getEmail());
            emailList.add(email);;
        }

        Coupon coupon = couponRepository.findById(id).orElseThrow(
                () -> new CatchException(ResponseCode.COUPON_NOT_FOUND)
        );
        String content =
                "<div></div><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" height=\"100%\" width=\"100%\" style=\"border-collapse: collapse; text-size-adjust: 100%; height: 100%; margin: 0px; padding: 0px; width: 100%; background-color: rgb(255, 255, 255);\">" +
                        "<tbody><tr><td align=\"center\" style=\"text-size-adjust: 100%; height: 100%; margin: 0px; padding: 0px; width: 100%; border-top: 0px;\">" +
                        "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"border-collapse: collapse; text-size-adjust: 100%;\">" +
                        "<tbody><tr><td align=\"center\" style=\"background: none center center / cover no-repeat rgb(255, 255, 255); text-size-adjust: 100%; border-top: 0px; border-bottom: 0px; padding-top: 0px; padding-bottom: 0px;\">" +
                        "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"border-collapse: collapse; text-size-adjust: 100%; max-width: 600px !important;\">" +
                        "<tbody><tr><td style=\"text-size-adjust: 100%;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"min-width: 100%; border-collapse: collapse; text-size-adjust: 100%;\">" +
                        "<tbody><tr><td style=\"padding-top: 9px; text-size-adjust: 100%;\"><table align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 100%; min-width: 100%; border-collapse: collapse; text-size-adjust: 100%;\">" +
                        "<tbody><tr><td style=\"padding: 0px 18px 9px; text-size-adjust: 100%; word-break: break-word; color: rgb(32, 32, 32); font-family: &quot;Helvetica Neue&quot;, Helvetica, Arial, Verdana, sans-serif; font-size: 24px; line-height: 100%; text-align: left;\">" +
                        "<h3 style=\"text-align: center; display: block; margin: 0px; padding: 0px; color: rgb(32, 32, 32); font-family: &quot;Helvetica Neue&quot;, Helvetica, Arial, Verdana, sans-serif; font-size: 30px; font-style: normal; font-weight: normal; line-height: 100%; letter-spacing: normal;\">" +
                        "<span style=\"font-family: &quot;helvetica neue&quot;, helvetica, arial, verdana, sans-serif;\"><span><span style=\"font-size: 32px; font-weight: bold; color: rgb(255, 152, 0);\">" + coupon.getName() + "</span></span></span></h3></td></tr></tbody></table></td></tr></tbody></table>" +
                        "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"min-width: 100%; border-collapse: collapse; text-size-adjust: 100%;\"><tbody><tr><td style=\"padding-top: 9px; text-size-adjust: 100%;\"><table align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 100%; min-width: 100%; border-collapse: collapse; text-size-adjust: 100%; width: 600px;\">" +
                        "<tbody><tr><td style=\"padding: 0px 18px 9px; line-height: 100%; text-size-adjust: 100%; word-break: break-word; text-align: left; width: 564px;\"><div style=\"\"><font style=\"\"><span style=\"font-size: 16px; white-space-collapse: preserve;\"><br></span></font></div><div style=\"\"><font style=\"\"><span style=\"font-size: 16px; white-space-collapse: preserve;\"> <img src=\"https://ditoday.com/wp-content/uploads/2023/10/top_bg@1x.429187b0.png\" width=\"617\" height=\"207\"></span></font></div>" +
                        "<div style=\"\"><font style=\"\"><span style=\"font-size: 16px; white-space-collapse: preserve;\"><br></span></font></div><p style=\"color: rgb(32, 32, 32); font-family: &quot;Helvetica Neue&quot;, Helvetica, Arial, Verdana, sans-serif; font-size: 24px; text-align: center; line-height: 100%; margin: 10px 0px; padding: 0px; text-size-adjust: 100%;\">" +
                        "원하는 상품을 더욱 <span style=\"color: rgb(255, 152, 0);\">저렴한 가격</span>에 만나보세요!</p><p style=\"color: rgb(32, 32, 32); font-family: &quot;Helvetica Neue&quot;, Helvetica, Arial, Verdana, sans-serif; font-size: 24px; text-align: center; line-height: 100%; margin: 10px 0px; padding: 0px; text-size-adjust: 100%;\"><span style=\"font-size: 16px;\">‣ 쿠폰 코드: " + coupon.getCode() + "</span></p>" +
                        "<p style=\"color: rgb(32, 32, 32); font-family: &quot;Helvetica Neue&quot;, Helvetica, Arial, Verdana, sans-serif; font-size: 24px; text-align: center; line-height: 100%; margin: 10px 0px; padding: 0px; text-size-adjust: 100%;\"><span style=\"font-size: 16px;\">마이페이지 쿠폰 등록에서 입력하여 적용 가능합니다.</span></p></td></tr></tbody></table></td></tr></tbody></table>" +
                        "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"min-width: 100%; border-collapse: collapse; text-size-adjust: 100%;\"><tbody><tr><td style=\"padding-top: 9px; text-size-adjust: 100%;\"><table align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 100%; min-width: 100%; border-collapse: collapse; text-size-adjust: 100%;\"><tbody><tr><td style=\"padding: 0px 18px 9px; color: rgb(73, 70, 70); text-size-adjust: 100%; word-break: break-word; font-family: &quot;Helvetica Neue&quot;, Helvetica, Arial, Verdana, sans-serif; font-size: 24px; line-height: 100%; text-align: left;\">" +
                        "<div style=\"margin: 0px auto; max-width: 500px; text-align: center;\"><span style=\"font-family: &quot;helvetica neue&quot;, helvetica, arial, verdana, sans-serif;\"><span style=\"color: rgb(34, 34, 34); display: inline-block; font-size: 12px; font-weight: 400; line-height: 19px;\">본 메일은 정보통신망법률 등 관련규정에 의거 회원님의 이메일 수신동의 여부를 확인 후 발송되었습니다.&nbsp;만약 메일 수신을 원치 않으시면&nbsp;<u><a href=\"https://heights-store.us12.list-manage.com/unsubscribe?u=65408ac755ac2505fdafa3f9e&amp;id=781fc25856&amp;e=137ceec6bc&amp;c=648c077731\" target=\"_blank\" style=\"text-size-adjust: 100%; color: rgb(0, 124, 137); font-weight: normal; text-decoration: underline;\">수신거부</a></u>를 클릭하세요. 혹은&nbsp;<u><a href=\"https://heights-store.us12.list-manage.com/profile?u=65408ac755ac2505fdafa3f9e&amp;id=781fc25856&amp;e=137ceec6bc&amp;c=648c077731\" target=\"_blank\" style=\"text-size-adjust: 100%; color: rgb(0, 124, 137); font-weight: normal; text-decoration: underline;\">이메일 변경</a></u>&nbsp;할 수 있습니다.<br></span></span></div></td></tr></tbody></table></td></tr></tbody></table>" +
                        "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"min-width: 100%; border-collapse: collapse; text-size-adjust: 100%; table-layout: fixed !important;\"><tbody><tr><td style=\"min-width: 100%; padding: 18px; text-size-adjust: 100%;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"min-width: 100%; border-top: 2px solid rgb(255, 255, 255); border-collapse: collapse; text-size-adjust: 100%;\"><tbody><tr><td style=\"text-size-adjust: 100%;\"><span></span></td></tr></tbody></table></td></tr></tbody></table>" +
                        "</td></tr></tbody></table></td></tr></tbody></table>";


        for (String toMail : emailList) {
            String title = coupon.getName(); // 이메일 제목
            couponGroupSend(coupon, username, toMail, title, content);
        }
        return "전송 완료";
    }


    @PreAuthorize("hasAnyAuthority('ADMIN','CS','MARKETER')")
    public String createEventEmail(Long id, EventEmailReqDto eventEmailReqDto) throws Exception {
        List<Long> userIds = eventEmailReqDto.getUserIds();
        List<String> emailList = new ArrayList<>();
        for(Long userId : userIds){
            String email = aesUtil.aesCBCDecode(userRepository.findById(userId).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND)).getEmail());
            emailList.add(email);;
        }

        Event event = eventRepository.findById(id).orElseThrow(
                () -> new CatchException(ResponseCode.EVENT_NOT_FOUND)
        );

        for (String toMail : emailList) {
            String title = event.getName(); // 이메일 제목
            String content = event.getContents(); // 이메일 내용
            Long eventId = event.getId();
            content = content.replace("{email}", toMail);
            content = content.replace("{eventId}", String.valueOf(eventId));

            GroupSend(event, username, toMail, title, content);
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



    @PreAuthorize("hasAnyAuthority('MARKETER','ADMIN')")
//    @PreAuthorize("hasAuthority('MARKETER')")
    public void GroupSend(Event event, String setFrom, String toMail, String title, String content) {
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
                    .event(event)
                    .toEmail(result.getData())
                    .type(LogType.EVENT_EMAIL_SEND)
                    .viewCount(0L)
                    .emailCheck(false)
                    .build();

            emailLogRepository.save(log);
            return result;
        });
    }

    @PreAuthorize("hasAuthority('MARKETER')")
    public void couponGroupSend(Coupon coupon, String setFrom, String toMail, String title, String content) {
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
            CouponEmailLog log = CouponEmailLog.builder()
                    .message(result.getMsg())
                    .CODE(result.getResultCode())
                    .coupon(coupon)
                    .toEmail(result.getData())
                    .type(LogType.COUPON_EMAIL_SEND)
                    .emailCheck(false)
                    .build();
            couponEmailLogRepository.save(log);
            return result;
        });
    }

    @Transactional
    public String trackingPixel(String email, Long id) {
        EmailLog emailLog = emailLogRepository.findByToEmailAndEventId(email,id).orElseThrow(
                () -> new CatchException(ResponseCode.TO_EMAIL_NOT_FOUND)
        );
        emailLog.check();

        return "success";
    }
}
