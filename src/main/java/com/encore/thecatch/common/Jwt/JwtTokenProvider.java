package com.encore.thecatch.common.Jwt;

import com.encore.thecatch.common.Jwt.RefreshToken.RefreshTokenRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@Getter
public class JwtTokenProvider {
    @Value("${jwt.secretKey}") // 암호화할 키
    private String secretKey;
    @Value("${jwt.access.expiration-minutes}") // 엑세스 토크 유효시간
    private Long accessTokenExpirationMinutes;
    @Value("${jwt.refresh.expiration-hours}") // 리프레시 토큰 유효시간
    private Long refreshTokenExpirationHours;
    @Value("${jwt.issuer}") // 토큰 발급자
    private String issuer;

    private final RefreshTokenRepository refreshTokenRepository;

    public JwtTokenProvider(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }


    public String createAccessToken(String userSpecification){
        String accessToken = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256,secretKey.getBytes()) // HS256으로 암호화, secretKey를 이용해 서명
                .setSubject(userSpecification) // JWT 토큰제목 (이메일이 들어감)
                .setIssuer(issuer) // JWT 토큰 발급자
                .setIssuedAt(Timestamp.valueOf(LocalDateTime.now())) // JWT 토큰 발급 시간
                .setExpiration(Date.from(Instant.now().plus(accessTokenExpirationMinutes, ChronoUnit.HOURS))) // JWT 토큰의 만료시간 설정
                .compact(); // JWT 토큰 생성
        return accessToken;

    }
    public String createRefreshToken() {
        String refreshToken = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .setIssuer(issuer)
                .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))
                .setExpiration(Date.from(Instant.now().plus(refreshTokenExpirationHours, ChronoUnit.HOURS)))
                .compact();
        return refreshToken;
    }

    public String validateTokenAndGetSubject(String token){
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJwt(token)
                .getBody()
                .getSubject();
    }

    public String recreateAccessToken(String oldAccessToken) {
        String subject = decodeJwtPayloadSubject(oldAccessToken);

        return subject;
    }

    private String decodeJwtPayloadSubject(String oldAccessToken) {

        return oldAccessToken;
    }


}
