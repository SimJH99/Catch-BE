package com.encore.event.common.respone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@Builder
public class ResponseDto {

    private HttpStatus httpStatus;
    private Object message;
    private Object result;
}
