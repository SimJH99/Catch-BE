package com.encore.thecatch.coupon.service;

import com.encore.thecatch.admin.domain.Admin;
import com.encore.thecatch.admin.repository.AdminRepository;
import com.encore.thecatch.common.CatchException;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.Role;
import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.company.repository.CompanyRepository;
import com.encore.thecatch.coupon.domain.Coupon;
import com.encore.thecatch.coupon.domain.CouponStatus;
import com.encore.thecatch.coupon.dto.*;
import com.encore.thecatch.coupon.repository.CouponQueryRepository;
import com.encore.thecatch.coupon.repository.CouponRepository;
import com.encore.thecatch.publishcoupon.domain.PublishCoupon;
import com.encore.thecatch.publishcoupon.repository.PublishCouponRepository;
import com.encore.thecatch.user.domain.User;
import com.encore.thecatch.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CouponService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final PublishCouponRepository publishCouponRepository;
    private final CouponQueryRepository couponQueryRepository;

    private final AdminRepository adminRepository;

    public CouponService(CompanyRepository companyRepository, UserRepository userRepository, CouponRepository couponRepository, PublishCouponRepository publishCouponRepository, CouponQueryRepository couponQueryRepository, AdminRepository adminRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.couponRepository = couponRepository;
        this.publishCouponRepository = publishCouponRepository;
        this.couponQueryRepository = couponQueryRepository;
        this.adminRepository = adminRepository;
    }

    @Transactional
    public Coupon create(CouponReqDto couponReqDto) throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getName());
        Admin admin = adminRepository.findByEmployeeNumber(authentication.getName()).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND));
        Long companyId = admin.getCompany().getId();
        if(admin.getRole().equals(Role.USER)){
            throw new CatchException(ResponseCode.ACCESS_DENIED);
        }
        // UUID(Universally Unique Identifier)란?
        //범용 고유 식별자를 의미하며 중복이 되지 않는 유일한 값을 구성하고자 할때 주로 사용됨(ex)세션 식별자, 쿠키 값, 무작위 데이터베이스값 )
        String code = UUID.randomUUID().toString();
        Coupon new_coupon = Coupon.builder()
                .name(couponReqDto.getName())
                .code(code)
                .quantity(couponReqDto.getQuantity())
                .couponStatus(CouponStatus.ISSUANCE)
                .startDate(LocalDateTime.parse(couponReqDto.getStartDate()))
                .endDate(LocalDateTime.parse(couponReqDto.getEndDate()))
                .companyId(admin.getCompany())
                .build();
        Coupon coupon = couponRepository.save(new_coupon);
        return coupon;
        }



    public List<CouponResDto> findAll(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND));
        Company company = user.getCompany();
        List<Coupon> coupons = couponRepository.findByCompanyId(company);
        List<CouponResDto> couponResDtos = new ArrayList<>();
        for(Coupon coupon : coupons){
            couponResDtos.add(CouponResDto.toCouponResDto(coupon));
        }
        return couponResDtos;
    }

    public List<CouponResDto> findMyAll(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND));
        List<PublishCoupon> coupons = publishCouponRepository.findByUserId(user.getId());
        List<CouponResDto> couponResDtos = new ArrayList<>();
        for(PublishCoupon coupon : coupons){
            couponResDtos.add(CouponResDto.publishToCouponDto(coupon));
        }
        return couponResDtos;
    }

    public CouponResDto findById(Long id) {
        Coupon coupon = couponRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));
        return CouponResDto.toCouponResDto(coupon);
    }

    public Page<CouponFindResDto> searchCoupon(SearchCouponCondition searchCouponCondition, Pageable pageable) throws Exception{
        List<CouponFindResDto> couponFindResDtos = couponQueryRepository.findCouponList(searchCouponCondition);
        List<CouponFindResDto> couponFindResDtos1 =new ArrayList<>();
        for(CouponFindResDto couponFindResDto : couponFindResDtos){
            couponFindResDto = CouponFindResDto.builder()
                    .id(couponFindResDto.getId())
                    .name(couponFindResDto.getName())
                    .code(couponFindResDto.getCode())
                    .status(couponFindResDto.getStatus())
                    .quantity(couponFindResDto.getQuantity())
                    .startDate(couponFindResDto.getStartDate())
                    .endDate(couponFindResDto.getEndDate())
                    .company(couponFindResDto.getCompany())
                    .build();
            couponFindResDtos1.add(couponFindResDto);
        }
        return new PageImpl<>(couponFindResDtos1, pageable, couponFindResDtos.size());
    }
//
    @Transactional
    public Coupon publish(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND));
        Long companyId = user.getCompany().getId();
        Coupon coupon = couponRepository.findById(id).orElseThrow(()->new CatchException(ResponseCode.COUPON_NOT_FOUND));
        if(!coupon.getCompanyId().equals(companyId)&& !user.getRole().equals(Role.ADMIN)){
            throw new CatchException(ResponseCode.ACCESS_DENIED);
        }
        if(coupon.getCouponStatus() == CouponStatus.PUBLISH){
            throw new IllegalArgumentException("이미 발행된 쿠폰입니다.");
        }
        coupon.publishCoupon();
//        couponRepository.save(coupon);
        return coupon;
    }


    @Transactional
    public Coupon receive(CouponReceiveDto couponReceiveDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND));
        Coupon coupon = couponRepository.findByCode(couponReceiveDto.getCode()).orElseThrow(()->new EntityNotFoundException("존재하지 않는 쿠폰입니다."));
        if(!coupon.getCompanyId().equals(user.getCompany())){
            throw new CatchException(ResponseCode.NON_RECEIVABLE_COUPON);
        }
        if(coupon.getCouponStatus().equals(CouponStatus.PUBLISH) && publishCouponRepository.findByCouponIdAndUserId(coupon.getId(), user.getId()).isEmpty()){
            PublishCoupon publishCoupon = PublishCoupon.builder()
                    .user(user)
                    .coupon(coupon)
                    .couponStatus(CouponStatus.RECEIVE)
                    .build();
            publishCouponRepository.save(publishCoupon);
        }else if(!publishCouponRepository.findByCouponIdAndUserId(coupon.getId(), userRepository.count()).isEmpty()){
            throw new CatchException(ResponseCode.ALREADY_RECEIVED_COUPON);
        }
        return coupon;
    }

    public Coupon couponUpdate(Long id, CouponReqDto couponReqDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND));
        Long companyId = user.getCompany().getId();
        Coupon coupon = couponRepository.findById(id).orElseThrow(()->new CatchException(ResponseCode.COUPON_NOT_FOUND));
        if(coupon.getCouponStatus().equals(CouponStatus.ISSUANCE) && user.getRole().equals(Role.ADMIN) && coupon.getCompanyId() == user.getCompany()){
            coupon.updateCoupon(couponReqDto);
            couponRepository.save(coupon);
        }else{
            throw new CatchException(ResponseCode.COUPON_CAN_NOT_UPDATE);
        }
        return coupon;
    }
    @Transactional
    public Coupon couponDelete(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND));
        Coupon coupon = couponRepository.findById(id).orElseThrow(()->new CatchException(ResponseCode.COUPON_NOT_FOUND));
        if(coupon.getCouponStatus().equals(CouponStatus.ISSUANCE) && user.getRole().equals(Role.ADMIN) && coupon.getCompanyId() == user.getCompany()){
            coupon.deleteCoupon();
        }else{
            throw new IllegalArgumentException("삭제 불가한 쿠폰입니다.");
        }
        return coupon;
    }

    public Long couponPublishCount() {
        return couponQueryRepository.couponPublishCount();
    }
}
