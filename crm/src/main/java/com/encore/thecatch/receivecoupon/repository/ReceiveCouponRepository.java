package com.encore.thecatch.receivecoupon.repository;

import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.coupon.domain.Coupon;
import com.encore.thecatch.receivecoupon.domain.ReceiveCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiveCouponRepository extends JpaRepository<ReceiveCoupon, Long> {
    List<ReceiveCoupon> findByCouponIdAndUserId(Long coupon_id, Long user_id);

    Page<ReceiveCoupon> findByUserId(Long user_id, Pageable pageable);

    List<ReceiveCoupon> findByUserId(Long user_id);
}
