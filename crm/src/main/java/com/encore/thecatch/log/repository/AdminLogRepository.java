package com.encore.thecatch.log.repository;

import com.encore.thecatch.log.domain.AdminLog;
import com.encore.thecatch.log.domain.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminLogRepository extends JpaRepository<AdminLog, Long> {
}
