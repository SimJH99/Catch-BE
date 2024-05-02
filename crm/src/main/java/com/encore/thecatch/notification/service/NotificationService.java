package com.encore.thecatch.notification.service;
//
//import com.encore.thecatch.admin.domain.Admin;
//import com.encore.thecatch.admin.repository.AdminRepository;
//import com.encore.thecatch.common.CatchException;
//import com.encore.thecatch.common.ResponseCode;
//import com.encore.thecatch.notification.domain.Notification;
//import com.encore.thecatch.notification.dto.NotificationRequestDto;
//import com.encore.thecatch.notification.repository.NotificationRepository;
//import com.encore.thecatch.user.domain.User;
//import com.encore.thecatch.user.repository.UserRepository;
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.Message;
//import com.google.firebase.messaging.WebpushConfig;
//import com.google.firebase.messaging.WebpushNotification;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//import javax.transaction.Transactional;
//import java.util.concurrent.ExecutionException;
//

import com.encore.thecatch.common.CatchException;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.util.AesUtil;
import com.encore.thecatch.coupon.domain.Coupon;
import com.encore.thecatch.log.domain.EmailLog;
import com.encore.thecatch.log.repository.EmailLogRepository;
import com.encore.thecatch.notification.domain.Notification;
import com.encore.thecatch.notification.dto.NotificationResDto;
import com.encore.thecatch.notification.repository.NotificationRepository;
import com.encore.thecatch.user.domain.User;
import com.encore.thecatch.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Service
//@RequiredArgsConstructor
//@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailLogRepository emailLogRepository;
    private final UserRepository userRepository;
    private final AesUtil aesUtil;

    public NotificationService(NotificationRepository notificationRepository,
                               EmailLogRepository emailLogRepository,
                               UserRepository userRepository,
                               AesUtil aesUtil) {
        this.notificationRepository = notificationRepository;
        this.emailLogRepository = emailLogRepository;
        this.userRepository = userRepository;
        this.aesUtil = aesUtil;
    }

    @Transactional
    public void saveNotification(User user, Boolean confirm, Coupon coupon){
        Notification notification = Notification.builder()
                .user(user)
                .notificationTitle(coupon.getName())
                .notificationContent(coupon.getCode())
                .confirm(confirm)
                .build();
        notificationRepository.save(notification);
    }

    public Page<NotificationResDto> findNonReceive(Pageable pageable){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND));
        Page<Notification> notifications = notificationRepository.findByUserIdAndConfirm(user.getId(), false , pageable);
        return notifications.map(NotificationResDto::toNotificationResDto);
    }

    @PreAuthorize("hasAuthority('USER')")
    public Page<String> findUserEvent(Pageable pageable) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND));
        Page<Notification> notifications = notificationRepository.findByUserIdAndConfirm(user.getId(), true , pageable);
        List<EmailLog> emailLogs = emailLogRepository.findByToEmail(aesUtil.aesCBCDecode(user.getEmail()));

        HashSet<String> set = new HashSet<>();
        for (Notification notification : notifications) {
            set.add(notification.getNotificationContent());
        }
        for (EmailLog emailLog : emailLogs) {
            set.add(emailLog.getEvent().getContents());
        }

        List<String> list = new ArrayList<>(set);

        return new PageImpl<String>(list, pageable, set.size());
    }
//    public String getNotificationToken() {
////        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
////        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND));
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Admin admin = adminRepository.findByEmployeeNumber(authentication.getName()).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND));
//
//        Notification notification = notificationRepository.findByAdmin(admin)
//                .orElseThrow(() -> new CatchException(ResponseCode.USER_NOT_FOUND));
//
//        return notification.getToken();
//    }
//
//    public void sendNotification(NotificationRequestDto notificationRequestDto) throws ExecutionException, InterruptedException {
//        Message message = Message.builder()
//                .setWebpushConfig(WebpushConfig.builder()
//                        .setNotification(WebpushNotification.builder()
//                                .setTitle(notificationRequestDto.getTitle())
//                                .setBody(notificationRequestDto.getMessage())
//                                .build())
//                        .build())
//                .setToken(notificationRequestDto.getToken())
//                .build();
//        String response = FirebaseMessaging.getInstance().sendAsync(message).get();
//        log.info(">>>>Send message : " + response);
//    }
//
//    public void deleteNotification() {
////        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
////        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND));
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Admin admin = adminRepository.findByEmployeeNumber(authentication.getName()).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND));
//
//        Notification notification = notificationRepository.findByAdmin(admin)
//                .orElseThrow(() -> new CatchException(ResponseCode.USER_NOT_FOUND));
//
//        notificationRepository.delete(notification);
//    }
}
