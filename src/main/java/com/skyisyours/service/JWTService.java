package com.skyisyours.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    private SecretKey secretKey;

    public JWTService(@Value("${jwt.secret}") String secretKeyString)
    {
        byte [] keyarr = Base64.getDecoder().decode(secretKeyString);
        secretKey = (SecretKey) Keys.hmacShaKeyFor(keyarr);
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        // Implement JWT token generation logic here
        claims.put("username", username);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .issuedAt(new java.util.Date(System.currentTimeMillis()))
                .setExpiration(new java.util.Date(System.currentTimeMillis() + 1000 *60 * 60 * 24 * 10)) // Token valid for 10 days
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, secretKey) // Use a secure key
                .compact();
        //return "dummy-jwt-token-for-" + username;
    }

    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    private <T> T extractClaim (String jwt, Function<Claims, T> claimResolver)
    {
        final Claims claims = extractAllClaims(jwt);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwt)
    {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(jwt).getPayload();
    }

    public boolean validateToken(String jwt, UserDetails userDetails) {
        final String userName = extractUsername(jwt);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(jwt));
    }

    private boolean isTokenExpired(String jwt)
    {
        return extractExpiration(jwt).before(new Date());
    }

    private Date extractExpiration(String jwt)
    {
        return extractClaim(jwt, Claims::getExpiration);
    }
}
