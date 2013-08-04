package com.robusta.commons.auth.service;

import com.robusta.commons.auth.api.SessionTokenGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDSessionTokenGenerator implements SessionTokenGenerator {
    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
