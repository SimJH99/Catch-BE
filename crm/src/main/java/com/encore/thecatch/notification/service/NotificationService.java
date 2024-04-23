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

import com.encore.thecatch.coupon.domain.Coupon;
import com.encore.thecatch.notification.domain.Notification;
import com.encore.thecatch.notification.repository.NotificationRepository;
import com.encore.thecatch.user.domain.User;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
//@RequiredArgsConstructor
//@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
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
