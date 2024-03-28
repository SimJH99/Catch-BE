package com.encore.thecatch.coupon.service;

import com.encore.thecatch.coupon.domain.Coupon;
import com.encore.thecatch.coupon.domain.CouponStatus;
import com.encore.thecatch.coupon.dto.CouponReceiveDto;
import com.encore.thecatch.coupon.dto.CouponReqDto;
import com.encore.thecatch.coupon.dto.CouponResDto;
import com.encore.thecatch.coupon.repository.CouponRepository;
import com.encore.thecatch.publish_coupon.domain.PublishCoupon;
import com.encore.thecatch.publish_coupon.repository.PublishCouponRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CouponService {
    private final CouponRepository couponRepository;
    private final PublishCouponRepository publishCouponRepository;

    public CouponService(CouponRepository couponRepository, PublishCouponRepository publishCouponRepository) {
        this.couponRepository = couponRepository;
        this.publishCouponRepository = publishCouponRepository;
    }

    public Coupon create(CouponReqDto couponReqDto){
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
                .companyId(couponReqDto.getCompanyId())
                .build();
        Coupon coupon = couponRepository.save(new_coupon);
        return coupon;
    }

    public List<CouponResDto> findAll(Long companyId){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Long companyId = memberRepository.findByEmail(authentication.getEmail()).orElseThrow(
//                () -> "존재하지 않는 회사 입니다.").getCompanyId();
        List<Coupon> coupons = couponRepository.findByCompanyId(companyId);
        List<CouponResDto> couponResDtos = new ArrayList<>();
        for(Coupon coupon : coupons){
            couponResDtos.add(CouponResDto.toCouponResDto(coupon));
        }
        return couponResDtos;
    }

    public Coupon findById(Long id) {
        return couponRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));
    }

    @Transactional
    public Coupon publish(Long id) {
//        Long companyId = memberRepository.findByEmail(authentication.getEmail()).orElseThrow(
//                () -> "존재하지 않는 회사 입니다.").getCompanyId();
        Coupon coupon = couponRepository.findById(id).orElseThrow(()->new EntityNotFoundException("존재하지 않는 쿠폰입니다."));
//        if(!coupon.getCompanyId().equals(companyId)&&)!authentication.getAuthorities().contains((new SimpleGrantedAuthority("ROLE_ADMIN")))){
//            throw new AccessDeniedException("권한이 없습니다.");
//        }
        if(coupon.getCouponStatus() == CouponStatus.PUBLISH){
            throw new IllegalArgumentException("이미 발행된 쿠폰입니다.");
        }
        coupon.publishCoupon();
//        couponRepository.save(coupon);
        return coupon;
    }


    @Transactional
    public Coupon receive(CouponReceiveDto couponReceiveDto) {
//    public Coupon receive(String code) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Long memberId = memberRepository.findByEmail(authentication.getEmail()).orElseThrow(
//                () -> "존재하지 않는 회원 입니다.").getId();
        Long memberId = 1L;
        Coupon coupon = couponRepository.findByCode(couponReceiveDto.getCode()).orElseThrow(()->new EntityNotFoundException("존재하지 않는 쿠폰입니다."));
        if(coupon.getCouponStatus().equals(CouponStatus.PUBLISH) && coupon.getCode().equals(couponReceiveDto.getCode())){
            PublishCoupon publishCoupon = PublishCoupon.builder()
                    .member_id(memberId)
                    .coupon(coupon)
                    .couponStatus(CouponStatus.RECEIVE)
                    .build();
            publishCouponRepository.save(publishCoupon);
        }
        return coupon;
    }

    public Coupon couponUpdate(Long id, CouponReqDto couponReqDto){
        Coupon coupon = couponRepository.findById(id).orElseThrow(()->new EntityNotFoundException("존재하지 않는 쿠폰입니다."));
        if(coupon.getCouponStatus().equals(CouponStatus.ISSUANCE)){
            coupon.updateCoupon(couponReqDto);
            couponRepository.save(coupon);
        }else{
            throw new IllegalArgumentException("수정 불가한 쿠폰입니다.");
        }

        return coupon;
    }
    @Transactional
    public Coupon couponDelete(Long id) {
//        Long companyId = memberRepository.findByEmail(authentication.getEmail()).orElseThrow(
//                () -> "존재하지 않는 회사 입니다.").getCompanyId();
        Coupon coupon = couponRepository.findById(id).orElseThrow(()->new EntityNotFoundException("존재하지 않는 쿠폰입니다."));
        if(coupon.getCouponStatus().equals(CouponStatus.PUBLISH)){
            throw new IllegalArgumentException("삭제 불가한 쿠폰입니다.");
        }else{
            coupon.deleteCoupon();
        }

        return coupon;
    }
}
