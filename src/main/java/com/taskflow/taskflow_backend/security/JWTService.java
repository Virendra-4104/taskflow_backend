package com.taskflow.taskflow_backend.security;

import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.taskflow.taskflow_backend.config.JwtProperty;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProperty.class)
public class JWTService {
    private final JwtProperty jwtProperty;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperty.getSecretKey().getBytes()); 
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(HashMap<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtProperty.getExpiration()))
                .signWith(getSigningKey())
                .compact();
    }

    private Claims extractsAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolve){
        final Claims claims = extractsAllClaims(token);
        return claimsResolve.apply(claims);
    }

    public String extractUsername(String token){
        return extractClaims(token, claim -> claim.getSubject());
    }

    public Date extractExpiration(String token){
        return extractClaims(token, claim -> claim.getExpiration());
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username  = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
}
