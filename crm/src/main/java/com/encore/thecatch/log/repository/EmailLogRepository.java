package com.encore.thecatch.log.repository;

import com.encore.thecatch.log.domain.EmailLog;
import com.encore.thecatch.log.domain.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
    Optional<EmailLog> findByToEmailAndEventId(String email, Long eventId);
    List<EmailLog> findByToEmail(String email);

}