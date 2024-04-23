package com.encore.thecatch.coupon.repository;

import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.coupon.domain.Coupon;
import com.encore.thecatch.coupon.domain.CouponStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Page<Coupon> findByCompanyId(Company company, Pageable pageable);
    List<Coupon> findByCompanyIdAndCouponStatus(Company company, CouponStatus couponStatus);
    Optional<Coupon> findByCode(String code);
    Optional<Coupon> findByName(String name);


}
