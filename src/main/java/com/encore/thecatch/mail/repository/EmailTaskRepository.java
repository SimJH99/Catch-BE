package com.encore.thecatch.mail.repository;

import com.encore.thecatch.mail.Entity.EmailTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailTaskRepository extends JpaRepository<EmailTask, Long> {
}
