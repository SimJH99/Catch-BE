package com.encore.thecatch.event.service;

import com.encore.thecatch.admin.domain.Admin;
import com.encore.thecatch.admin.repository.AdminRepository;
import com.encore.thecatch.common.CatchException;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.redis.RedisService;
import com.encore.thecatch.common.util.AesUtil;
import com.encore.thecatch.event.domain.Event;
import com.encore.thecatch.event.domain.EventStatus;
import com.encore.thecatch.event.dto.request.EventCreateDto;
import com.encore.thecatch.event.dto.request.EventUpdateDto;
import com.encore.thecatch.event.dto.response.EventContentsDto;
import com.encore.thecatch.event.dto.response.EventDetailDto;
import com.encore.thecatch.event.dto.response.EventInfoDto;
import com.encore.thecatch.event.dto.response.EventSearchDto;
import com.encore.thecatch.event.repository.EventQueryRepository;
import com.encore.thecatch.event.repository.EventRepository;
import com.encore.thecatch.log.domain.AdminLog;
import com.encore.thecatch.log.domain.LogType;
import com.encore.thecatch.log.repository.AdminLogRepository;
import com.encore.thecatch.mail.service.EmailSendService;
import com.encore.thecatch.notification.dto.EventNotificationReqDto;
import com.encore.thecatch.notification.service.NotificationService;
import com.encore.thecatch.user.domain.User;
import com.encore.thecatch.user.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final AdminRepository adminRepository;
    private final AdminLogRepository adminLogRepository;
    private final AesUtil aesUtil;
    private final EventQueryRepository eventQueryRepository;

    private final UserRepository userRepository;
    public final EmailSendService emailSendService;

    private final RedisService redisService;
    public final FirebaseMessaging firebaseMessaging;
    public final NotificationService notificationService;

    public EventService(EventRepository eventRepository,
                        AdminRepository adminRepository,
                        AdminLogRepository adminLogRepository,
                        AesUtil aesUtil,
                        EventQueryRepository eventQueryRepository,
                        UserRepository userRepository,
                        EmailSendService emailSendService,
                        RedisService redisService,
                        FirebaseMessaging firebaseMessaging,
                        NotificationService notificationService) {
        this.eventRepository = eventRepository;
        this.adminRepository = adminRepository;
        this.adminLogRepository = adminLogRepository;
        this.aesUtil = aesUtil;
        this.eventQueryRepository = eventQueryRepository;
        this.userRepository = userRepository;
        this.emailSendService = emailSendService;
        this.redisService = redisService;
        this.firebaseMessaging = firebaseMessaging;
        this.notificationService = notificationService;
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
                .eventStatus(EventStatus.ISSUANCE)
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

    @PreAuthorize("hasAnyAuthority('MARKETER','ADMIN')")
    public Page<EventInfoDto> searchEvent(EventSearchDto eventSearchDto, Pageable pageable) throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Admin marketer = adminRepository.findByEmployeeNumber(authentication.getName()).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND));
        return eventQueryRepository.findEventList(eventSearchDto, marketer.getCompany(), pageable);
    }

    @PreAuthorize("hasAnyAuthority('MARKETER','ADMIN')")
    @Transactional
    public void eventDelete(Long id){
        String employeeNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin marketer = adminRepository.findByEmployeeNumber(employeeNumber).orElseThrow(
                () -> new CatchException(ResponseCode.ADMIN_NOT_FOUND)
        );
        Event event = eventRepository.findById(id).orElseThrow(
                () -> new CatchException(ResponseCode.EVENT_NOT_FOUND)
        );
        if (event.getEventStatus().equals(EventStatus.ISSUANCE)){
            eventRepository.delete(event);
        }else{
            throw new CatchException(ResponseCode.EVENT_CAN_NOT_DELETE);
        }

    }

    @PreAuthorize("hasAnyAuthority('MARKETER','ADMIN')")
    public EventDetailDto eventDetail(Long id, String ip) throws Exception {
        String employeeNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin marketer = adminRepository.findByEmployeeNumber(employeeNumber).orElseThrow(
                () -> new CatchException(ResponseCode.ADMIN_NOT_FOUND)
        );

        Event event = eventRepository.findById(id).orElseThrow(
                () -> new CatchException(ResponseCode.EVENT_NOT_FOUND)
        );

        EventDetailDto eventDetailDto = EventDetailDto.builder()
                .name(event.getName())
                .contents(event.getContents())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .build();

        AdminLog adminLog = AdminLog.builder()
                .type(LogType.EVENT_DETAIL_VIEW)
                .ip(ip)
                .employeeNumber(aesUtil.aesCBCDecode(marketer.getEmployeeNumber()))
                .method("GET")
                .data("view at marketer:" + marketer.getId() + " detail")
                .build();

        adminLogRepository.save(adminLog);

        return eventDetailDto;
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public String createEventNotification(Long id, EventNotificationReqDto eventNotificationReqDto) throws Exception {

        List<Long> userIds = eventNotificationReqDto.getUserIds();

        Event event = eventRepository.findById(id).orElseThrow(
                () -> new CatchException(ResponseCode.EVENT_NOT_FOUND)
        );
        List<User> users = new ArrayList<>();
        for (Long userId : userIds) {
            users.add(userRepository.findById(userId).orElseThrow(() -> new CatchException(ResponseCode.USER_NOT_FOUND)));
        }
        for (User user : users) {
            String fcmToken = redisService.getValues("PushToken" + user.getId());
            System.out.println(fcmToken);
            if (fcmToken.equals("false")) {
                boolean confirm = false;
                notificationService.saveEventNotification(user, confirm, event);
            } else {
                boolean confirm = true;
                notificationService.saveEventNotification(user, confirm, event);
                Message message = Message.builder()
                        .setToken(fcmToken)
                        .setNotification(Notification.builder()
                                .setTitle(event.getName())
                                .setBody("이벤트를 확인하세요!")
                                .build())
                        .build();
                try {
                    String response = firebaseMessaging.send(message);
                    System.out.println("Successfully sent message: " + response);
                } catch (FirebaseMessagingException e) {
                    System.err.println("Error sending message: " + e.getMessage());
                }
            }
        }
        return "전송 완료";
    }
    public EventContentsDto eventContents(Long id){
        Event event = eventRepository.findById(id).orElseThrow(
                () -> new CatchException(ResponseCode.EVENT_NOT_FOUND)
        );

        return EventContentsDto.builder()
                .contents(event.getContents())
                .build();
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','CS','MARKETER')")
    public Event eventPublish(Long id) {
        String employeeNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin admin = adminRepository.findByEmployeeNumber(employeeNumber).orElseThrow(()-> new CatchException(ResponseCode.ADMIN_NOT_FOUND));
        Event event = eventRepository.findById(id).orElseThrow(()->new CatchException(ResponseCode.EVENT_NOT_FOUND));
        if(event.getEventStatus().equals(EventStatus.ISSUANCE)&& event.getCompanyId() == admin.getCompany()){
            event.publishEvent();
        }else{
            throw new CatchException(ResponseCode.EVENT_CAN_NOT_PUBlISH);
        }
        return event;
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MARKETER')")
    public Event eventUpdate(Long id, EventUpdateDto eventUpdateDto) {
        String employeeNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin marketer = adminRepository.findByEmployeeNumber(employeeNumber).orElseThrow(
                () -> new CatchException(ResponseCode.ADMIN_NOT_FOUND)
        );
        Event event = eventRepository.findById(id).orElseThrow(
                () -> new CatchException(ResponseCode.EVENT_NOT_FOUND)
        );
        event.eventUpdate(eventUpdateDto);

        return event;
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','CS','MARKETER')")
    public Long issuanceEventCount() {
        return eventQueryRepository.issuanceEventCount();
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','CS','MARKETER')")
    public Long publishEventCount() {
        return eventQueryRepository.publishEventCount();
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','CS','MARKETER')")
    public Long expirationEventCount() {
        return eventQueryRepository.expirationEventCount();
    }
}
