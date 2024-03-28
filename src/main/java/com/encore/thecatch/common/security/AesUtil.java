//package com.encore.thecatch.common.security;
//
//import org.apache.commons.codec.binary.Hex;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//
//import javax.crypto.Cipher;
//import javax.crypto.spec.IvParameterSpec;
//import javax.crypto.spec.SecretKeySpec;
//import java.nio.charset.StandardCharsets;
//
//public class AesUtil {
//    private final String privateKey_256;
//    public AesUtil(@Value("${symmetricKey}") String privateKey_256) {
//        this.privateKey_256 = privateKey_256;
//    }
//    public String aesCBCEncode(String plainText) throws Exception {
//
//        SecretKeySpec secretKey = new SecretKeySpec(privateKey_256.getBytes(StandardCharsets.UTF_8), "AES");
//        IvParameterSpec IV = new IvParameterSpec(privateKey_256.substring(0,16).getBytes());
//
//        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
//
//        c.init(Cipher.ENCRYPT_MODE, secretKey, IV);
//
//        byte[] encrpytionByte = c.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
//
//        return Hex.encodeHexString(encrpytionByte);
//    }
//
//    public String aesCBCDecode(String encodeText) throws Exception {
//
//        SecretKeySpec secretKey = new SecretKeySpec(privateKey_256.getBytes(StandardCharsets.UTF_8), "AES");
//        IvParameterSpec IV = new IvParameterSpec(privateKey_256.substring(0,16).getBytes());
//
//        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
//
//        c.init(Cipher.DECRYPT_MODE, secretKey, IV);
//
//        byte[] decodeByte = Hex.decodeHex(encodeText.toCharArray());
//
//        return new String(c.doFinal(decodeByte), StandardCharsets.UTF_8);
//    }
//}
