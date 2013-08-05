package com.robusta.commons.auth.service;

import com.robusta.commons.auth.api.EncodingFailureException;
import com.robusta.commons.auth.api.PasswordEncoder;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MessageDigestBasedPasswordEncoderTest {
    private static final String PLAIN_TEXT_PASSWORD = "welcome123";
    public static final String ENCODED_PASSWORD = "WFjqIozC7fiHIWmbLIY45Q==";
    PasswordEncoder passwordEncoder;
    @Before
    public void setUp() throws Exception {
        passwordEncoder = new MessageDigestBasedPasswordEncoder();
    }

    @Test
    public void proveThatDigesterIsAbleToDigestAndProvideEncodedPassword() throws Exception {
        String encoded = passwordEncoder.encodePlainTextPassword(PLAIN_TEXT_PASSWORD);
        assertThat(encoded, is(equalTo(ENCODED_PASSWORD)));
    }

    @Test(expected = EncodingFailureException.class)
    public void testEncodePassword_expectingNoSuchAlgorithmException_shouldResultInEncodingFailureException() throws Exception {
        new MessageDigestBasedPasswordEncoder("JUNK", "UTF-8").encodePlainTextPassword(PLAIN_TEXT_PASSWORD);
    }

    @Test(expected = EncodingFailureException.class)
    public void testEncodePassword_expectingUnsupportedEncodingException_shouldResultInEncodingFailureException() throws Exception {
        new MessageDigestBasedPasswordEncoder("MD5", "JUNK").encodePlainTextPassword(PLAIN_TEXT_PASSWORD);
    }
}
