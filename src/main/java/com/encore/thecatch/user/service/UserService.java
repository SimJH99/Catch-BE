package com.encore.thecatch.user.service;

import com.encore.thecatch.common.CatchException;
import com.encore.thecatch.common.dto.ResponseDto;
import com.encore.thecatch.common.jwt.JwtTokenProvider;
import com.encore.thecatch.common.jwt.RefreshToken.RefreshToken;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.log.domain.Log;
import com.encore.thecatch.log.domain.LogType;
import com.encore.thecatch.log.repository.LogRepository;
import com.encore.thecatch.user.domain.User;
import com.encore.thecatch.user.dto.request.UserLoginDto;
import com.encore.thecatch.user.dto.request.UserSignUpDto;
import com.encore.thecatch.user.dto.response.UserInfoDto;
import com.encore.thecatch.common.jwt.RefreshToken.RefreshTokenRepository;
import com.encore.thecatch.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.transaction.Transactional;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AesBytesEncryptor encryptor;
    private final JwtTokenProvider jwtTokenProvider;
    private final LogRepository logRepository;
    private final String privateKey_256;

    public UserService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       AesBytesEncryptor encryptor,
                       JwtTokenProvider jwtTokenProvider,
                       LogRepository logRepository,
                       @Value("${symmetricKey}")
                       String privateKey256
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.encryptor = encryptor;
        this.jwtTokenProvider = jwtTokenProvider;
        this.logRepository = logRepository;
        privateKey_256 = privateKey256;
    }

    @Transactional
    public User signUp(UserSignUpDto userSignUpDto) throws Exception {
        if (userRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
            throw new CatchException(ResponseCode.EXISTING_EMAIL);
        }
        System.out.println(userSignUpDto.getPassword());
        User user = User.toEntity(userSignUpDto);

        user.passwordEncode(passwordEncoder);

        String name = aesCBCEncode(user.getName());
        String email = aesCBCEncode(user.getEmail());
        String phoneNumber = aesCBCEncode(user.getPhoneNumber());
        user.dataEncode(name, email, phoneNumber);

        return userRepository.save(user);
    }

    public UserInfoDto userDetail(Long id) throws Exception {
        User user = userRepository.findById(id).orElseThrow(()-> new CatchException(ResponseCode.USER_NOT_FOUND));
        String name = aesCBCDecode(user.getName());
        String email = aesCBCDecode(user.getEmail());
        String phoneNumber = aesCBCDecode(user.getPhoneNumber());
        user.dataDecode(name, email, phoneNumber);

        UserInfoDto userInfoDto = UserInfoDto.toUserInfoDto(user);
        return userInfoDto;
    }

    @Transactional
    public ResponseDto doLogin(UserLoginDto userLoginDto, String ip) throws Exception {
        String email = aesCBCEncode(userLoginDto.getEmail());

        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("이메일이 일치하지 않습니다."));
        if(!passwordEncoder.matches(userLoginDto.getPassword(),user.getPassword())){
            throw new IllegalArgumentException("패스워드가 일치하지 않습니다.");
        }
        String accessToken = jwtTokenProvider.createAccessToken(String.format("%s:%s", user.getEmail(), user.getRole())); // 토큰 생성
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId()); // 리프레시 토큰 생성
        // 리프레시 토큰이 이미 있으면 토큰을 갱신하고 없으면 토큰을 추가한다.
        refreshTokenRepository.findById(user.getId())
                .ifPresentOrElse(
                        it -> it.updateRefreshToken(refreshToken),
                        () -> refreshTokenRepository.save(new RefreshToken(user, refreshToken))
                );
        Map<String, String> result = new HashMap<>();
        result.put("access_token", accessToken);
        result.put("refresh_token", refreshToken);


        Log loginLog = Log.builder()
                .type(LogType.LOGIN) // DB로 나눠 관리하지 않고 LogType으로 구별
                .ip(ip)
                .email(user.getEmail())
                .method("POST")
                .data("user login")
                .build();

        logRepository.save(loginLog);

        return new ResponseDto(HttpStatus.OK, "JWT token is created", result);
    }

    // 암호화
    public String encrypt(String data) {
        byte[] encrypt = encryptor.encrypt(data.getBytes(StandardCharsets.UTF_8));
        return byteArrayToString(encrypt);
    }

    // 복호화
    public String decrypt(String data) {
        byte[] decryptBytes = stringToByteArray(data);
        byte[] decrypt = encryptor.decrypt(decryptBytes);
        return new String(decrypt, StandardCharsets.UTF_8);
    }

    // byte -> String
    public String byteArrayToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte abyte : bytes) {
            sb.append(abyte);
            sb.append(" ");
        }
        return sb.toString();
    }

    // String -> byte
    public byte[] stringToByteArray(String byteString) {
        String[] split = byteString.split("\\s");
        ByteBuffer buffer = ByteBuffer.allocate(split.length);
        for (String s : split) {
            buffer.put((byte) Integer.parseInt(s));
        }
        return buffer.array();
    }
    public String aesCBCEncode(String plainText) throws Exception {

        SecretKeySpec secretKey = new SecretKeySpec(privateKey_256.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec IV = new IvParameterSpec("0123456789abcdef".getBytes());

        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");

        c.init(Cipher.ENCRYPT_MODE, secretKey, IV);

        byte[] encrpytionByte = c.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        return Hex.encodeHexString(encrpytionByte);
    }

    public String aesCBCDecode(String encodeText) throws Exception {

        SecretKeySpec secretKey = new SecretKeySpec(privateKey_256.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec IV = new IvParameterSpec("0123456789abcdef".getBytes());

        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");

        c.init(Cipher.DECRYPT_MODE, secretKey, IV);

        byte[] decodeByte = Hex.decodeHex(encodeText.toCharArray());

        return new String(c.doFinal(decodeByte), StandardCharsets.UTF_8);
    }
}
