package com.robusta.commons.auth.api;

public class EncodingFailureException extends RuntimeException {
    public EncodingFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
