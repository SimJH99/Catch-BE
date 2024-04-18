//package com.encore.thecatch.notification.service;
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
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class NotificationService {
//
//    private final NotificationRepository notificationRepository;
//    private final UserRepository userRepository;
//    private final AdminRepository adminRepository;
//
//    @Transactional
//    public void saveNotification(String token){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Admin admin = adminRepository.findByEmployeeNumber(authentication.getName()).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND));
//
////        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND));
//        System.out.println("로그인 후 알람 세이브");
//        Notification notification = Notification.builder()
//                .token(token)
//                .build();
//
//        notification.confirmUser(admin);
//        notificationRepository.save(notification);
//
//    }
//
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
//}
