package com.robusta.commons.auth.api;

public interface PasswordEncoder {
    String encodePlainTextPassword(String plainTextPassword) throws EncodingFailureException;
}
