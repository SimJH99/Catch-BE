package com.encore.thecatch.coupon.domain;

import com.encore.thecatch.common.entity.BaseEntity;
import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.coupon.dto.CouponReqDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Coupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private LocalDate startDate;
    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private CouponStatus couponStatus;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    @JsonIgnore
    private Company companyId;

    public void updateCoupon(CouponReqDto couponReqDto){
        this.name = couponReqDto.getName();
        this.quantity = couponReqDto.getQuantity();
        this.startDate = LocalDate.parse(couponReqDto.getStartDate());
        this.endDate = LocalDate.parse(couponReqDto.getEndDate());
    }

    public void publishCoupon(){
        this.couponStatus = CouponStatus.PUBLISH;
    }

    public void deleteCoupon() { this.couponStatus = CouponStatus.DELETE; }
}
