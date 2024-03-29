package com.encore.thecatch.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum ResponseCode {

    SUCCESS("SUCCESS", "성공"),
    SUCCESS_CREATE_MEMBER("SUCCESS_CREATE_MEMBER","계정 생성 성공"),
    SUCCESS_CHANGE_MEMBER_PASSWORD("SUCCESS_CREATE_MEMBER","비밀번호 변경 성공"),
    EXISTING_EMAIL("EXISTING_EMAIL", "이미 존재하는 이메일 주소입니다."),
    CODE_NOT_CONFIRMED("CODE_NOT_CONFIRMED", "인증번호를 확인해주세요."),
    CODE_EXPIRED("CODE_EXPIRED", "만료된 인증번호입니다."),
    USER_NOT_FOUND("USER_NOT_FOUND", "입력하신 정보와 일치하는 정보가 없습니다. <br /> 확인 후 다시 입력해주세요."),
    INVALID_PW("INVALID_PW", "비밀번호를 확인해주세요."),
    PAYLOAD_INVALID("PAYLOAD_INVALID","입력값을 확인해주세요"),
    INAPPROPRIATE_PARAMETER_VALUE("INAPPROPRIATE_PARAMETER_VALUE", "잘못된 입력값입니다."),
    POST_NOT_FOUND("POST_NOT_FOUND", "게시글이 존재하지 않습니다."),
    SUCCESS_CREATE_COMMENT("SUCCESS_CREATE_COMMENT", "댓글을 성공적으로 저장했습니다."),
    AES_ENCODE_FAIL("AES_ENCODE_FAIL","데이터 암호화 오류 발생"),
    AES_DECODE_FAIL("AES_DECODE_FAIL", "데이터 복호화 오류 발생"),
    SUCCESS_CREATE_COUPON("SUCCESS_CREATE_COUPON", "쿠폰 생성 성공"),
    SUCCESS_PUBLISH_COUPON("SUCCESS_PUBLISH_COUPON", "쿠폰 발행 성공"),
    SUCCESS_RECEIVE_COUPON("SUCCESS_RECEIVE_COUPON", "쿠폰 수령 성공"),
    SUCCESS_DELETE_COUPON("SUCCESS_DELETE_COUPON", "쿠폰 삭제 성공"),
    COMPANY_NOT_FOUND("COMPANY_NOT_FOUND", "해당 회사가 존재하지 않습니다."),
    ACCESS_DENIED("ACCESS_DENIED", "접근 권한 없음")
    SUCCESS_CREATE_POST("SUCCESS_CREATE_POST", "게시물 생성 완료"),
    SUCCESS_FOUND_POST("SUCCESS_FOUND_POST", "게시물 상세 조회 성공"),
    SUCCESS_FOUND_MY_POSTS("SUCCESS_FOUND_MY_POSTS", "내 문의 내역 조회 성공"),
    SUCCESS_UPDATE_MY_POST("SUCCESS_UPDATE_MY_POST", "문의 글 수정 완료"),
    SUCCESS_DELETE_MY_POST("SUCCESS_DELETE_MY_POST", "문의 글 삭제 완료"),
    ;


    private final String code;
    private final String label;

    ResponseCode(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
