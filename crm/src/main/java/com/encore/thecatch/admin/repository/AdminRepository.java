package com.encore.thecatch.admin.repository;

import com.encore.thecatch.admin.domain.Admin;
import com.encore.thecatch.common.dto.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmployeeNumber(String employeeNumber);
    Optional<Admin> findByEmail(String email);
}
