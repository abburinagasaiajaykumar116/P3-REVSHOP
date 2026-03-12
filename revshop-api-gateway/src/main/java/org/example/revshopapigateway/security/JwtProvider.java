package org.example.revshopapigateway.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    private final String SECRET = "revshop-secret-key-revshop-secret-key-123456";

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    private final long EXPIRATION = 86400000;

    public Key getKey() {
        return key;
    }

    public String extractEmail(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}