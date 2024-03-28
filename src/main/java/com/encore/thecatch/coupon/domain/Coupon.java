package com.encore.thecatch.coupon.domain;

import com.encore.thecatch.coupon.dto.CouponReqDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

//    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private int quantity;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @CreationTimestamp
    private LocalDateTime createdTime;

    @UpdateTimestamp
    private LocalDateTime updatedTime;


    @Enumerated(EnumType.STRING)
    private CouponStatus couponStatus;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "company_id", nullable = false)
    private Long companyId;

    public void updateCoupon(CouponReqDto couponReqDto){
        this.name = couponReqDto.getName();
        this.quantity = couponReqDto.getQuantity();
        this.startDate = LocalDateTime.parse(couponReqDto.getStartDate());
        this.endDate = LocalDateTime.parse(couponReqDto.getEndDate());
        this.companyId = couponReqDto.getCompanyId();
    }

    public void publishCoupon(){
        this.couponStatus = CouponStatus.PUBLISH;
    }

    public void deleteCoupon() { this.couponStatus = CouponStatus.DELETE;
    }
}
