package com.encore.thecatch.common.jwt.RefreshToken;

import com.encore.thecatch.admin.domain.Admin;
import com.encore.thecatch.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor

public class RefreshToken implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_employee_number", referencedColumnName = "employeeNumber")
    private Admin admin;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_email", referencedColumnName = "email") // user_email 컬럼과 User 엔터티의 email 속성을 매핑
    private User user;

    @Column(name = "refresh_token")
    private String refreshToken;

    private int reissueCount = 0;

    public RefreshToken(User user, String refreshToken) {
        this.user = user;
        this.refreshToken = refreshToken;
    }

    public RefreshToken(Admin admin, String refreshToken) {
        this.admin = admin;
        this.refreshToken = refreshToken;
    }


    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public boolean validateRefreshToken(String refreshToken) {
        return this.refreshToken.equals(refreshToken);
    }

    public void increaseReissueCount(){
        reissueCount++;
    }
}
