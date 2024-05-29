package com.encore.thecatch.notification.domain;

import com.encore.thecatch.common.entity.BaseEntity;
import com.encore.thecatch.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
//
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String notificationTitle;
    @Column(nullable = false)
    @Lob
    private String notificationContent;
    private boolean confirm;

    public void readNotification(){
        this.confirm = true;
    }

}


