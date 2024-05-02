package com.encore.thecatch.common.jwt;

import com.encore.thecatch.common.CatchException;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.Role;
import com.encore.thecatch.common.jwt.RefreshToken.RefreshToken;
import com.encore.thecatch.common.jwt.RefreshToken.RefreshTokenRepository;
import com.encore.thecatch.common.redis.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class JwtTokenProvider {

    private final String secretKey;
    private final Long accessTokenExpirationMinutes;
    private final Long refreshTokenExpirationHours;
    private final String issuer;
    private final int reissueLimit;
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
            RefreshTokenRepository refreshTokenRepository,
            RedisService redisService
    ) {
        this.secretKey = secretKey;
        this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
        this.refreshTokenExpirationHours = refreshTokenExpirationHours;
        this.issuer = issuer;
        this.refreshTokenRepository = refreshTokenRepository;
        this.redisService = redisService;
        reissueLimit = (int) (refreshTokenExpirationHours * 60 / accessTokenExpirationMinutes);
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
    public String createRefreshToken(Role role, Long id) {
        String refreshToken = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .setIssuer(issuer)
                .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))
                .setExpiration(Date.from(Instant.now().plus(refreshTokenExpirationHours, ChronoUnit.HOURS)))
                .compact();
        redisService.setValues(String.valueOf(role)+id ,refreshToken, Duration.ofHours(24L));
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
        if(decodeJwtPayloadSubject(oldAccessToken).split(":")[1].equals("USER")){
            refreshTokenRepository.findByUserEmailAndReissueCountLessThan(
                    subject.split(":")[0],reissueLimit
            ).ifPresentOrElse(
                    RefreshToken::increaseReissueCount,
                    ()-> { throw new ExpiredJwtException(null, null, "Refresh token expired"); }
            );
        }else {
            refreshTokenRepository.findByAdminEmployeeNumberAndReissueCountLessThan(
                    subject.split(":")[0],reissueLimit
            ).ifPresentOrElse(
                    RefreshToken::increaseReissueCount,
                    ()-> { throw new ExpiredJwtException(null, null, "Refresh token expired"); }
            );
        }
        return createAccessToken(subject);

    }

    @Transactional
    public void validateRefreshToken(String refreshToken, String oldAccessToken) throws JsonProcessingException {
        validateAndPraseToken(refreshToken); // 리프레시 토큰이 유효한 토큰인지 검증
        if(decodeJwtPayloadSubject(oldAccessToken).split(":")[1].equals("USER")){
            String key = decodeJwtPayloadSubject(oldAccessToken).split(":")[0];
            try {
                refreshTokenRepository.findByUserEmailAndReissueCountLessThan(key, reissueLimit)
                        .filter(userRefreshToken -> userRefreshToken.validateRefreshToken(refreshToken))
                        .orElseThrow(() -> new ExpiredJwtException(null, null, "Refresh token expired"));
            } catch (ExpiredJwtException e) {
                // 만료된 리프레시 토큰 처리
                RefreshToken findtoken = refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(
                        () -> new CatchException(ResponseCode.REFRESH_TOKEN_NOT_FOUND)
                );
                refreshTokenRepository.delete(findtoken);
                redisService.deleteValues(findtoken.getUser().getRole() + "" + findtoken.getUser().getId());
                // ExpiredJwtException 다시 던지거나 적절한 처리를 진행
            }
        }else {
            String key = decodeJwtPayloadSubject(oldAccessToken).split(":")[0];
            try {
                refreshTokenRepository.findByAdminEmployeeNumberAndReissueCountLessThan(key, reissueLimit)
                        .filter(userRefreshToken -> userRefreshToken.validateRefreshToken(refreshToken))
                        .orElseThrow(() -> new ExpiredJwtException(null, null, "Refresh token expired"));
            } catch (ExpiredJwtException e) {
                // 만료된 리프레시 토큰 처리
                RefreshToken findtoken = refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(
                        () -> new CatchException(ResponseCode.REFRESH_TOKEN_NOT_FOUND)
                );
                refreshTokenRepository.delete(findtoken);
                redisService.deleteValues(findtoken.getAdmin().getRole() + "" + findtoken.getAdmin().getId());
                // ExpiredJwtException 다시 던지거나 적절한 처리를 진행
            }
        }
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