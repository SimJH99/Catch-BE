package com.encore.thecatch.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CatchException extends RuntimeException{

    Object data;

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    public CatchException() {
        super();
    }

    public CatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public CatchException(String message, ResponseCode responseCode) {
        super(message);
        this.data = responseCode;
    }

    public CatchException(ResponseCode responseCode) {
        super(responseCode.getLabel());
        this.data = responseCode;
    }

    public CatchException(ResponseCode responseCode, Object data) {
        super(responseCode.getLabel());

        this.data = data;
    }
}
