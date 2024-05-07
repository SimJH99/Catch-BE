package com.encore.thecatch.log.repository;

import com.encore.thecatch.log.domain.CouponEmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponEmailLogRepository extends JpaRepository<CouponEmailLog, Long> {
    Optional<CouponEmailLog> findByToEmailAndCouponId(String email, Long eventId);
    List<CouponEmailLog> findByToEmail(String email);

}