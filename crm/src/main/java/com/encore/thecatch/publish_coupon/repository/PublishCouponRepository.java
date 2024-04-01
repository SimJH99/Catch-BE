package com.encore.thecatch.publish_coupon.repository;

import com.encore.thecatch.publish_coupon.domain.PublishCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublishCouponRepository extends JpaRepository<PublishCoupon, Long> {
}
