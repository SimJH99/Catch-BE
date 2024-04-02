package com.encore.thecatch.log.repository;

import com.encore.thecatch.log.domain.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
}
