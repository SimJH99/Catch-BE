package com.encore.thecatch.notification.controller;

//import com.encore.thecatch.notification.service.NotificationService;

import com.encore.thecatch.common.DefaultResponse;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.ResponseDto;
import com.encore.thecatch.notification.dto.NotificationResDto;
import com.encore.thecatch.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/nonReceive")
    public ResponseDto findNonReceive(Pageable pageable) {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, new DefaultResponse.PagedResponse<NotificationResDto>(notificationService.findNonReceive(pageable)));
    }

}
