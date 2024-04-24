package com.encore.thecatch.event.service;

import com.encore.thecatch.admin.domain.Admin;
import com.encore.thecatch.admin.repository.AdminRepository;
import com.encore.thecatch.common.CatchException;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.util.AesUtil;
import com.encore.thecatch.event.domain.Event;
import com.encore.thecatch.event.dto.request.EventCreateDto;
import com.encore.thecatch.event.repository.EventRepository;
import com.encore.thecatch.log.domain.AdminLog;
import com.encore.thecatch.log.domain.LogType;
import com.encore.thecatch.log.repository.AdminLogRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final AdminRepository adminRepository;
    private final AdminLogRepository adminLogRepository;
    private final AesUtil aesUtil;

    public EventService(EventRepository eventRepository,
                        AdminRepository adminRepository,
                        AdminLogRepository adminLogRepository,
                        AesUtil aesUtil) {
        this.eventRepository = eventRepository;
        this.adminRepository = adminRepository;
        this.adminLogRepository = adminLogRepository;
        this.aesUtil = aesUtil;
    }


    @PreAuthorize("hasAnyAuthority('MARKETER','ADMIN')")
    @Transactional
    public Event eventCreate(EventCreateDto eventCreateDto, String ip) throws Exception {
        String employeeNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin marketer = adminRepository.findByEmployeeNumber(employeeNumber).orElseThrow(
                () -> new CatchException(ResponseCode.ADMIN_NOT_FOUND)
        );

        Event event = Event.builder()
                .name(eventCreateDto.getName())
                .contents(eventCreateDto.getContents())
                .startDate(LocalDate.parse(eventCreateDto.getStartDate()))
                .endDate(LocalDate.parse(eventCreateDto.getEndDate()))
                .companyId(marketer.getCompany())
                .build();

        eventRepository.save(event);

        AdminLog adminLog = AdminLog.builder()
                .type(LogType.CREATE_EVENT)
                .ip(ip)
                .employeeNumber(aesUtil.aesCBCDecode(marketer.getEmployeeNumber()))
                .method("POST")
                .data("Create event :" + event.getName())
                .build();

        adminLogRepository.save(adminLog);

        return event;
    }
}
