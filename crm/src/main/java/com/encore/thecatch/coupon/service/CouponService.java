package com.encore.thecatch.coupon.service;

import com.encore.thecatch.admin.domain.Admin;
import com.encore.thecatch.admin.repository.AdminRepository;
import com.encore.thecatch.common.CatchException;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.redis.RedisService;
import com.encore.thecatch.common.util.AesUtil;
import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.company.repository.CompanyRepository;
import com.encore.thecatch.coupon.domain.Coupon;
import com.encore.thecatch.coupon.domain.CouponStatus;
import com.encore.thecatch.coupon.dto.*;
import com.encore.thecatch.coupon.repository.CouponQueryRepository;
import com.encore.thecatch.coupon.repository.CouponRepository;
import com.encore.thecatch.mail.service.EmailSendService;
import com.encore.thecatch.notification.service.NotificationService;
import com.encore.thecatch.receivecoupon.domain.ReceiveCoupon;
import com.encore.thecatch.receivecoupon.repository.ReceiveCouponRepository;
import com.encore.thecatch.user.domain.User;
import com.encore.thecatch.user.dto.request.PublishUserDto;
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
import java.util.UUID;

@Service
public class CouponService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final ReceiveCouponRepository receiveCouponRepository;
    private final CouponQueryRepository couponQueryRepository;
    private final NotificationService notificationService;
    private final RedisService redisService;
    private final AdminRepository adminRepository;
    public final FirebaseMessaging firebaseMessaging;
    public final EmailSendService emailSendService;
    private final AesUtil aesUtil;


    public CouponService(CompanyRepository companyRepository, UserRepository userRepository, CouponQueryRepository couponQueryRepository,CouponRepository couponRepository, ReceiveCouponRepository receiveCouponRepository, NotificationService notificationService, RedisService redisService, AdminRepository adminRepository, FirebaseMessaging firebaseMessaging, EmailSendService emailSendService, AesUtil aesUtil) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.couponRepository = couponRepository;
        this.receiveCouponRepository = receiveCouponRepository;
        this.couponQueryRepository = couponQueryRepository;
        this.notificationService = notificationService;
        this.redisService = redisService;
        this.adminRepository = adminRepository;
        this.firebaseMessaging = firebaseMessaging;
        this.emailSendService = emailSendService;
        this.aesUtil = aesUtil;
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','MARKETER')")
    public Coupon createCoupon(CouponReqDto couponReqDto){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Admin admin = adminRepository.findByEmployeeNumber(authentication.getName()).orElseThrow(()-> new CatchException(ResponseCode.ADMIN_NOT_FOUND));
            // UUID(Universally Unique Identifier)란?
            //범용 고유 식별자를 의미하며 중복이 되지 않는 유일한 값을 구성하고자 할때 주로 사용됨(ex)세션 식별자, 쿠키 값, 무작위 데이터베이스값 )
            if(couponRepository.findByName(couponReqDto.getName()).isPresent()){
                throw new CatchException(ResponseCode.EXISTING_COUPON_NAME);
            }
            String code = UUID.randomUUID().toString();
            Coupon new_coupon = Coupon.builder()
                    .name(couponReqDto.getName())
                    .code(code)
                    .quantity(couponReqDto.getQuantity())
                    .price(couponReqDto.getPrice())
                    .couponStatus(CouponStatus.ISSUANCE)
                    .startDate(LocalDate.parse(couponReqDto.getStartDate()))
                    .endDate(LocalDate.parse(couponReqDto.getEndDate()))
                    .companyId(admin.getCompany())
                    .build();
            Coupon coupon = couponRepository.save(new_coupon);
            return coupon;
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<CouponResDto> findAll(Pageable pageable){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Admin admin = adminRepository.findByEmployeeNumber(authentication.getName()).orElseThrow(()-> new CatchException(ResponseCode.ADMIN_NOT_FOUND));
        Company company = admin.getCompany();
        Page<Coupon> coupons = couponRepository.findByCompanyId(company, pageable);
        return coupons.map(CouponResDto::toCouponResDto);
    }

    public List<CouponResDto> findReceivable() throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND));
        Company company =  user.getCompany();
        List<Coupon> coupons = couponRepository.findByCompanyIdAndCouponStatus(company, CouponStatus.PUBLISH);
        List<CouponResDto> couponResDtos = new ArrayList<>();
        for(Coupon coupon : coupons){
            couponResDtos.add(CouponResDto.toCouponResDto(coupon));
        }
        return couponResDtos;
    }
    public Page<CouponResDto> findMyAll(Pageable pageable) throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND));
        Page<ReceiveCoupon> coupons = receiveCouponRepository.findByUserId(user.getId(), pageable);
        List<CouponResDto> couponResDtos = new ArrayList<>();
        return coupons.map(CouponResDto::publishToCouponDto);
    }

    public CouponResDto findById(Long id) {
        Coupon coupon = couponRepository.findById(id).orElseThrow(() -> new CatchException(ResponseCode.COUPON_NOT_FOUND));
        return CouponResDto.toCouponResDto(coupon);
    }

    public Page<CouponFindResDto> searchCoupon(SearchCouponCondition searchCouponCondition, Pageable pageable) throws Exception{
        String employeeNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin admin = adminRepository.findByEmployeeNumber(employeeNumber).orElseThrow(()-> new CatchException(ResponseCode.ADMIN_NOT_FOUND));
        return couponQueryRepository.findCouponList(searchCouponCondition, admin.getCompany(), pageable);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public String createCouponNotification(Long id, PublishUserDto publishUserDto) throws Exception {
        List<Long> userIds = publishUserDto.getUserIds();
        Coupon coupon = couponRepository.findById(id).orElseThrow(() -> new CatchException(ResponseCode.COUPON_NOT_FOUND));

        if(coupon.getCouponStatus() == CouponStatus.PUBLISH){
            throw new CatchException(ResponseCode.ALREADY_PUBLISH_COUPON);
        }
        List<User> users = new ArrayList<>();
        for(Long userId : userIds){
            users.add(userRepository.findById(userId).orElseThrow(() -> new CatchException(ResponseCode.USER_NOT_FOUND)));
        }
        for(User user : users){
            String fcmToken = redisService.getValues("PushToken"+ user.getId());
            if(fcmToken.equals("false")){
                boolean confirm = false;
                notificationService.saveNotification(user, confirm, coupon);
            }else {
                boolean confirm = true;
                notificationService.saveNotification(user, confirm, coupon);
                Message message = Message.builder()
                        .setToken(fcmToken)
                        .setNotification(Notification.builder()
                                .setTitle(coupon.getName())
                                .setBody(coupon.getCode())
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


    @Transactional
    public Coupon receive(CouponReceiveDto couponReceiveDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND));
        Coupon coupon = couponRepository.findByCode(couponReceiveDto.getCode()).orElseThrow(()->new CatchException(ResponseCode.COUPON_NOT_FOUND));
        if(!coupon.getCompanyId().equals(user.getCompany()) && !coupon.getCouponStatus().equals(CouponStatus.PUBLISH)){
            throw new CatchException(ResponseCode.NON_RECEIVABLE_COUPON);
        }
        if(coupon.getQuantity()<1){
            throw new CatchException(ResponseCode.COUPON_EXHAUSTION);
        }
        if(!receiveCouponRepository.findByCouponIdAndUserId(coupon.getId(), user.getId()).isEmpty()){
            throw new CatchException(ResponseCode.ALREADY_RECEIVED_COUPON);
        }
        if(coupon.getCouponStatus().equals(CouponStatus.PUBLISH)){
            ReceiveCoupon receiveCoupon = ReceiveCoupon.builder()
                    .user(user)
                    .coupon(coupon)
                    .couponStatus(CouponStatus.RECEIVE)
                    .build();
            receiveCouponRepository.save(receiveCoupon);
            coupon.receiveCoupon();
        }
        return coupon;
    }

    public Coupon couponUpdate(Long id, CouponReqDto couponReqDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Admin admin = adminRepository.findByEmployeeNumber(authentication.getName()).orElseThrow(()-> new CatchException(ResponseCode.ADMIN_NOT_FOUND));
        Coupon coupon = couponRepository.findById(id).orElseThrow(()->new CatchException(ResponseCode.COUPON_NOT_FOUND));
        if(coupon.getCouponStatus().equals(CouponStatus.ISSUANCE) && coupon.getCompanyId() == admin.getCompany()){
            coupon.updateCoupon(couponReqDto);
            couponRepository.save(coupon);
        }else{
            throw new CatchException(ResponseCode.COUPON_CAN_NOT_UPDATE);
        }
        return coupon;
    }
    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','CS','MARKETER')")
    public void couponDelete(Long id) {
        String employeeNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        Coupon coupon = couponRepository.findById(id).orElseThrow(()->new CatchException(ResponseCode.COUPON_NOT_FOUND));
        Admin admin = adminRepository.findByEmployeeNumber(employeeNumber).orElseThrow(
                () -> new CatchException(ResponseCode.ADMIN_NOT_FOUND)
        );
        if(coupon.getCouponStatus().equals(CouponStatus.ISSUANCE) && coupon.getCompanyId() == admin.getCompany()){
            couponRepository.delete(coupon);
        }else{
            throw new CatchException(ResponseCode.COUPON_CAN_NOT_DELETE);
        }
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','CS','MARKETER')")
    public Long couponPublishCount() {
        return couponQueryRepository.couponPublishCount();
    }


    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','CS','MARKETER')")
    public Long couponIssuanceCount() {
        return couponQueryRepository.couponIssuanceCount();
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','CS','MARKETER')")
    public Long couponExpirationCount() {
        return couponQueryRepository.couponExpirationCount();
    }

    public int findMyCouponCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND));
        List<ReceiveCoupon> coupons = receiveCouponRepository.findByUserId(user.getId());
        return coupons.size();
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','CS','MARKETER')")
    public Coupon couponPublish(Long id) throws Exception {
        String employeeNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin admin = adminRepository.findByEmployeeNumber(employeeNumber).orElseThrow(()-> new CatchException(ResponseCode.ADMIN_NOT_FOUND));
        Coupon coupon = couponRepository.findById(id).orElseThrow(()->new CatchException(ResponseCode.COUPON_NOT_FOUND));
        if(coupon.getCouponStatus().equals(CouponStatus.ISSUANCE) && coupon.getCompanyId() == admin.getCompany()){
            coupon.publishCoupon();
        }else{
            throw new CatchException(ResponseCode.COUPON_CAN_NOT_PUBlISH);
        }
        return coupon;
    }
}
