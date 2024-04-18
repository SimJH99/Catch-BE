//package com.encore.thecatch.notification.domain;
//
//import com.encore.thecatch.admin.domain.Admin;
//import com.encore.thecatch.user.domain.User;
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//
//@Entity
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class Notification {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "notification_id")
//    private Long id;
//
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "admin_id")
//    private Admin admin;
//
//    private String token;
//
//    @Builder
//    public Notification(String token) {
//        this.token = token;
//    }
//
//    public void confirmUser(Admin admin) {
//        this.admin = admin;
//    }
//}
