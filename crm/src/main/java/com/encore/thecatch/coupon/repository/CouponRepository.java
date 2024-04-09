package com.encore.thecatch.coupon.repository;

import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.coupon.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    List<Coupon> findByCompanyId(Company company);
    Optional<Coupon> findByCode(String code);
}
