package com.encore.thecatch.notification.controller;

//import com.encore.thecatch.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationApiController {
//    private final NotificationService notificationService;
//
//    @PostMapping("/new")
//    public void saveNotification(@RequestBody String token){
//        System.out.println("hi");
//        System.out.println(token);
//        notificationService.saveNotification(token);
//    }
}
