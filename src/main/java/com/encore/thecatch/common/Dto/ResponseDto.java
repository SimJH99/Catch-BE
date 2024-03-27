package com.encore.thecatch.common.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@Builder
public class ResponseDto {

    private HttpStatus httpStatus;
    private String message;
    private Object result;

}