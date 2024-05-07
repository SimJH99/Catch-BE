package com.encore.thecatch.common.jwt.RefreshToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByAdminEmployeeNumberAndReissueCountLessThan(String adminEmployeeNumber, int count);
    Optional<RefreshToken> findByUserEmailAndReissueCountLessThan(String userEmail, int count);


    Optional<RefreshToken> findByAdminEmployeeNumber(String adminEmployeeNumber);
    Optional<RefreshToken> findByUserEmail(String userEmail);

    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
