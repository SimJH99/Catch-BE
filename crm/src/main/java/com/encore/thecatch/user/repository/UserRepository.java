package com.encore.thecatch.user.repository;

import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.coupon.domain.Coupon;
import com.encore.thecatch.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Page<User> findByCompany(Company company, Pageable pageable);
    Page<User> findByCompanyAndConsentReceiveMarketing(Company company, Pageable pageable , Boolean consentReceiveMarketing);
}
