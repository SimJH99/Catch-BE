package com.encore.thecatch.event.domain;

import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public enum EventStatus {
        ISSUANCE("생성"),
        DELETE("삭제"),
        PUBLISH("배포"),
        EXPIRATION("만료");

        private final String value;

        EventStatus(String value){
                this.value = value;
        }

        public static EventStatus fromValue(String value) {
                if (StringUtils.hasText(value)) {
                        for (EventStatus status : EventStatus.values()) {
                                if (status.getValue().equals(value)) {
                                        return status;
                                }
                        }
                }
                throw new IllegalArgumentException("No enum constant for value: " + value);
        }

        public static String toValue(EventStatus status) {
                return status.getValue();
        }

}