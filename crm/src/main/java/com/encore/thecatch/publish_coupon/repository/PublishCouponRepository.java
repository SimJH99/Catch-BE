package com.encore.thecatch.publish_coupon.repository;

import com.encore.thecatch.publish_coupon.domain.PublishCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublishCouponRepository extends JpaRepository<PublishCoupon, Long> {
    List<PublishCoupon> findByCouponIdAndUserId(Long coupon_id, Long user_id);

    List<PublishCoupon> findByUserId(Long user_id);
}
