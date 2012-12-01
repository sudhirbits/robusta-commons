package com.robusta.commons.auth.service;

import com.robusta.commons.auth.api.SessionTokenGenerator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class UUIDSessionTokenGeneratorTest {
    SessionTokenGenerator tokenGenerator;
    @Before
    public void setUp() throws Exception {
        tokenGenerator = new UUIDSessionTokenGenerator();
    }

    @Test
    public void testGenerate() throws Exception {
        assertNotNull(tokenGenerator.generate());
    }
}
