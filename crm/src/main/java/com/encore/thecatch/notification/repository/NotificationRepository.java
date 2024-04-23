package com.encore.thecatch.notification.repository;
//
//import com.encore.thecatch.admin.domain.Admin;
//import com.encore.thecatch.notification.domain.Notification;
//import com.encore.thecatch.user.domain.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//

import com.encore.thecatch.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
//    Optional<Notification> findByAdmin(Admin admin);
}
