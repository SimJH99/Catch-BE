package com.encore.thecatch.log.domain;

import com.encore.thecatch.common.entity.BaseEntity;
import com.encore.thecatch.coupon.domain.Coupon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponEmailLog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LogType type; // EMAIL,

    private String message; // 발송여부

    private String CODE; // S-1 성공 , F-1 실패

    private String toEmail; // 받는 사람 이메일

    @ManyToOne
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    private boolean emailCheck;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdTime;

    public void check() {
        this.emailCheck = true;
    }
}

