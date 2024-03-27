package com.encore.thecatch.User.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
public class HttpDto {
    private HttpStatus httpStatus;
    private String message;
    private Object result;
}
