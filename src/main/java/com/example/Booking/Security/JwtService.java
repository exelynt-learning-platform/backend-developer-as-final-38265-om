package com.example.Booking.Security;

import com.example.Booking.Entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Identity convention: the JWT subject is the user's USERNAME.
 * Tokens carry an issuer and an audience, and both are enforced on parsing.
 * Roles are NOT read from the token; they are loaded from the database on
 * each request, so role changes take effect immediately.
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.audience}")
    private String audience;

    // Short-lived access token (default 15 minutes)
    @Value("${jwt.expiration-ms:900000}")
    private long jwtExpirationMs;

    private SecretKey signingKey;

    @PostConstruct
    void init() {
        // Fails fast at startup if the secret is missing, not Base64, or too short
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String generateToken(User user) {
        Date now = new Date();
        return Jwts.builder()
                .subject(user.getUsername())
                .issuer(issuer)
                .audience().add(audience).and()
                .issuedAt(now)
                .expiration(new Date(now.getTime() + jwtExpirationMs))
                .signWith(signingKey)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        // parseClaims verifies signature, expiry, issuer and audience,
        // and throws a JwtException if any of them fail.
        String subject = parseClaims(token).getSubject();
        return subject != null && subject.equals(userDetails.getUsername());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(issuer)
                .requireAudience(audience)
                .clockSkewSeconds(30)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}