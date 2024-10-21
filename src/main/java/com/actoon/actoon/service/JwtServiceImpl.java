package com.actoon.actoon.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.actoon.actoon.service.interfaces.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

// 토큰 생성 및 검증을 담당하는 서비스 로직
@Service
public class JwtServiceImpl implements JwtService {

    // 암호화하는 데에 사용되는 string signing key
    @Value("${token.signing.key}")
    private String jwtSigningKey;

    // 토큰에서 사용자 정보 추출하는 메서드를 호출하는 로직
    @Override
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Integer extractUserId(String token){
        return (Integer) extractAllClaims(token).get("userId");
    }

    // 토큰 생성 메서드를 호출하는 로직
    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // 토큰 생성 메서드를 호출하는 로직
    public String generateToken(UserDetails userDetails, int time) {
        return generateToken(new HashMap<>(), userDetails, time);
    }

    // 토큰 생성 메서드를 호출하는 로직
    public String generateToken(UserDetails userDetails, int time, int userId) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        return generateToken(claims, userDetails, time);
    }


    // 토큰 생성 메서드
    private String generateToken(HashMap<String,Object> extraClaims, UserDetails userDetails, int time) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                //.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // token 설정
                .setExpiration(new Date(System.currentTimeMillis() + time)) // token 설정
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

//
//    private String generateToken(HashMap<String,Object> extraClaims, UserDetails userDetails, int time) {
//        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                //.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // token 설정
//                .setExpiration(new Date(System.currentTimeMillis() + time)) // token 설정
//                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
//    }

    // 토큰 유효성 검사
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        System.out.println("VALID : " + userName);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // 실제로 토큰에서 사용자 정보 추출하는 메서드를
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    // 토큰 생성 메서드
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                //.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // token 설정
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    // 토큰이 만료되었는지를 검사하는 로직
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 토큰에서 유효기간 추출하는 로직
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 토큰에 담긴 Payload 값을 추출하는 로직
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
                .getBody();
    }

    // key 객체 생성
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}