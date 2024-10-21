package com.actoon.actoon.service.interfaces;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractUserName(String token);

    Integer extractUserId(String token);

    String generateToken(UserDetails userDetails);

    String generateToken(UserDetails userDetails, int time, int userId);

    boolean isTokenValid(String token, UserDetails userDetails);


}