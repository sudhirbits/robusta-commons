package com.robusta.commons.auth.service;

import com.robusta.commons.auth.api.EncodingFailureException;
import com.robusta.commons.auth.api.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class MessageDigestBasedPasswordEncoder implements PasswordEncoder {

    private final String algorithm;
    private final String charsetName;

    public MessageDigestBasedPasswordEncoder() {
        this("MD5", "UTF-8");
    }

    public MessageDigestBasedPasswordEncoder(String algorithm, String charsetName) {
        this.algorithm = algorithm;
        this.charsetName = charsetName;
    }

    @Override
    public String encodePlainTextPassword(String plainTextPassword) throws EncodingFailureException {
        MessageDigest msgDigest = null;
        try {
            msgDigest = MessageDigest.getInstance(algorithm);
            msgDigest.update(plainTextPassword.getBytes(charsetName));
        } catch (NoSuchAlgorithmException e) {
            throw new EncodingFailureException(String.format("Using algorithm: %s for Message Digest failed.", algorithm), e);
        } catch (UnsupportedEncodingException e) {
            throw new EncodingFailureException(String.format("Using charset: %s for Message Digest failed.", charsetName), e);
        }

        return DatatypeConverter.printBase64Binary(msgDigest.digest());
    }
}
