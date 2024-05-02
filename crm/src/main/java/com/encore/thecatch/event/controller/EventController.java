package com.encore.thecatch.event.controller;

import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.ResponseDto;
import com.encore.thecatch.common.util.IPUtil;
import com.encore.thecatch.event.dto.request.EventCreateDto;
import com.encore.thecatch.event.dto.response.EventSearchDto;
import com.encore.thecatch.event.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;


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

    @PostMapping("/eventList")
    public ResponseDto eventSearch(@RequestBody EventSearchDto eventSearchDto, Pageable pageable) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS, eventService.searchEvent(eventSearchDto, pageable));
    }

    @GetMapping("/event/{id}/detail")
    public ResponseDto adminDetail(@PathVariable Long id, HttpServletRequest request) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_EVENT_DETAIL, eventService.eventDetail(id,IPUtil.getClientIP(request)));
    }

    @GetMapping("/user/event/{id}")
    public ResponseDto userDetail(@PathVariable Long id, HttpServletRequest request) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_EVENT_DETAIL, eventService.eventContents(id));
    }

}
