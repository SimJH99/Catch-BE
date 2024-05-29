package com.encore.thecatch.notification.controller;

//import com.encore.thecatch.notification.service.NotificationService;

import com.encore.thecatch.common.DefaultResponse;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.ResponseDto;
import com.encore.thecatch.notification.domain.Notification;
import com.encore.thecatch.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/nonReceive")
    public ResponseDto findNonReceive() {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, new DefaultResponse.ListResponse<Notification>(notificationService.findNonReceive()));
    }

    @GetMapping("/eventList")
    public ResponseDto findUserEventList(Pageable pageable) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, new DefaultResponse.PagedResponse<String>(notificationService.findUserEvent(pageable)));
    }

    @PatchMapping("/{id}/notificationRead")
    public ResponseDto notificationRead(@PathVariable Long id) throws Exception {
        notificationService.notificationRead(id);
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, new DefaultResponse<Long>(id));
    }
}
