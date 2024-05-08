package com.encore.thecatch.notification.repository;

import com.encore.thecatch.notification.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
//    Optional<Notification> findByAdmin(Admin admin);
    Page<Notification> findByUserIdAndConfirm(Long user_id, Boolean confirm, Pageable pageable);

    List<Notification> findByUserIdAndConfirm(Long user_id, Boolean confirm);

}
