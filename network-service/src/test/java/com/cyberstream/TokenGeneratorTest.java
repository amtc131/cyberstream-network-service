package com.cyberstream;

import io.smallrye.jwt.build.Jwt;
import org.junit.jupiter.api.Test;

public class TokenGeneratorTest {
    @Test
    void generateToken() {
        String token = Jwt.issuer("https://cyberstream.local/issuer")
                .subject("collector-test-user")
                .groups("viewer")
                .claim("upn", "test@cyberstream.local")
                .sign();
        System.out.println("TOKEN: " + token);
    }
}
