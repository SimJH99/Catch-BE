package com.encore.thecatch.event.controller;

import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.ResponseDto;
import com.encore.thecatch.common.util.IPUtil;
import com.encore.thecatch.event.dto.request.EventCreateDto;
import com.encore.thecatch.event.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/event/create")
    public ResponseDto eventCreate(@RequestBody EventCreateDto eventCreateDto, HttpServletRequest request) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_EVENT_CREATE, eventService.eventCreate(eventCreateDto, IPUtil.getClientIP(request)));
    }
}
