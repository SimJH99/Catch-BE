package com.encore.thecatch.common.jwt;

import com.encore.thecatch.common.jwt.RefreshToken.RefreshToken;
import com.encore.thecatch.common.jwt.RefreshToken.RefreshTokenRepository;
import com.encore.thecatch.common.redis.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Service
@Getter
public class JwtTokenProvider {

    private final String secretKey;
    private final Long accessTokenExpirationMinutes;
    private final Long refreshTokenExpirationHours;
    private final String issuer;
    private final long reissueLimit;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisService redisService;

    private final ObjectMapper objectMapper = new ObjectMapper(); // JWT 역직렬화를 위한 ObjectMapper

    public JwtTokenProvider(
            @Value("${jwt.secretKey}") // 암호화할 키
            String secretKey,
            @Value("${jwt.access.expiration-minutes}") // 엑세스 토크 유효시간
            long accessTokenExpirationMinutes,
            @Value("${jwt.refresh.expiration-hours}") // 리프레시 토큰 유효시간
            long refreshTokenExpirationHours,
            @Value("${jwt.issuer}") // 토큰 발급자
            String issuer,
            RefreshTokenRepository refreshTokenRepository, RedisService redisService
    ) {
        this.secretKey = secretKey;
        this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
        this.refreshTokenExpirationHours = refreshTokenExpirationHours;
        this.issuer = issuer;
        this.refreshTokenRepository = refreshTokenRepository;
        this.redisService = redisService;
        reissueLimit = refreshTokenExpirationHours * 60 / accessTokenExpirationMinutes;
    }

    public String createAccessToken(String userSpecification){
        String accessToken = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256,secretKey.getBytes()) // HS256으로 암호화, secretKey를 이용해 서명
                .setSubject(userSpecification) // JWT 토큰제목 (이메일이 들어감)
                .setIssuer(issuer) // JWT 토큰 발급자
                .setIssuedAt(Timestamp.valueOf(LocalDateTime.now())) // JWT 토큰 발급 시간
                .setExpiration(Date.from(Instant.now().plus(accessTokenExpirationMinutes, ChronoUnit.HOURS))) // JWT 토큰의 만료시간 설정
//                .setExpiration(Date.from(Instant.now().plus(10, ChronoUnit.SECONDS))) // JWT 토큰의 만료시간 설정
                .compact(); // JWT 토큰 생성
        return accessToken;

    }
    public String createRefreshToken(Long id) {
        String refreshToken = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .setIssuer(issuer)
                .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))
                .setExpiration(Date.from(Instant.now().plus(refreshTokenExpirationHours, ChronoUnit.HOURS)))
                .compact();
        redisService.setValues(String.valueOf(id),refreshToken, Duration.ofMillis(1000L * 60 * 60 * 24));
        return refreshToken;
    }

    public String validateTokenAndGetSubject(String token){
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    @Transactional
    public String recreateAccessToken(String oldAccessToken) throws JsonProcessingException {
        // 기존 액세스 토큰 토대로 새로운 토큰 생성
        String subject = decodeJwtPayloadSubject(oldAccessToken);
        refreshTokenRepository.findByIdAndReissueCountLessThan(
                subject.split(":")[0],reissueLimit
        ).ifPresentOrElse(
                RefreshToken::increaseReissueCount,
                ()-> { throw new ExpiredJwtException(null, null, "Refresh token expired"); }
        );
        return createAccessToken(subject);
    }

    @Transactional
    public void validateRefreshToken(String refreshToken, String oldAccessToken) throws JsonProcessingException {
        validateAndPraseToken(refreshToken); // 리프레시 토큰이 유효한 토큰인지 검증
        String userEmail = decodeJwtPayloadSubject(oldAccessToken).split(":")[0];
        refreshTokenRepository.findByIdAndReissueCountLessThan(userEmail, reissueLimit)
                .filter(userRefreshToken -> userRefreshToken.validateRefreshToken(refreshToken))
                .orElseThrow(()-> new ExpiredJwtException(null,null, "Refresh token expired"));
    }

    private Jws<Claims> validateAndPraseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                // parseClaimsJws()에서 JWT를 파싱할때, 토큰이 유효한지 검사하여 예외를 던짐 = 토큰 검증
                .parseClaimsJws(token);
    }

    private String decodeJwtPayloadSubject(String oldAccessToken) throws JsonProcessingException {
        return objectMapper.readValue(
                // JWT를 복호화하고 데이터가 담겨있는 payload에서 Subject를 반환 (email:role)
                new String(Base64.getDecoder().decode(oldAccessToken.split("\\.")[1]), StandardCharsets.UTF_8),
                Map.class
        ).get("sub").toString();
    }


}