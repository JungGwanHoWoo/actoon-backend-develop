package com.actoon.actoon.util;

import com.actoon.actoon.dto.ExceptionResponse;
import com.actoon.actoon.exception.ExceptionResponseHandler;
import com.actoon.actoon.service.interfaces.JwtService;
import com.actoon.actoon.service.interfaces.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 요청에 대해 보안 필터링 로직을 수행하는 곳
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionResponseHandler.class);
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        final String jwt;
        final String userId;

        // 토큰이 없을 경우 (= 로그인, 회원 가입 필터링)
        try {
            if (!StringUtils.hasText(authHeader) || !StringUtils.startsWithIgnoreCase(authHeader, "Bearer ")) {
                try {
                    LOGGER.info("NOT AUTHENTICATION. token not check");
                    filterChain.doFilter(request, response);
                }
                catch (Exception e) {
                    LOGGER.info("FILTER 에서 Expired jwt 제외 예외처리한 것");
                    LOGGER.info("E : " + e.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("utf-8");
                    ExceptionResponse errorResponse = new ExceptionResponse(e.getMessage(), 401);
                    new ObjectMapper().writeValue(response.getWriter(), errorResponse);
                }
                return;
            }

            // 토큰이 있는 경우, 만료되었거나 유효하지 않은 코드인지를 검사

            jwt = authHeader.substring(7);
            userId = jwtService.extractUserName(jwt);
            LOGGER.info("AUTHENTICATION 실시. token check");

            if (StringUtils.hasText(userId)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                LOGGER.info("CONTEXT에 사용자 정보가 없으므로 이메일을 찾습니다.");
                UserDetails userDetails = userService.userDetailsService()
                        .loadUserByUsername(userId);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    LOGGER.info("JWT가 정상적인 토큰임을 확인하였습니다.");
                    //LOGGER.info("사용자 권한 : " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    context.setAuthentication(authToken);
                    SecurityContextHolder.setContext(context);
                }
            }
            //System.out.println("현재 요청은 if문 넘어와서 출력되는겨");
            filterChain.doFilter(request, response);
        }
        catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("utf-8");
            ExceptionResponse errorResponse = new ExceptionResponse("Access Token이 만료되었습니다.", 401);
            new ObjectMapper().writeValue(response.getWriter(), errorResponse);
        }
        catch (MalformedJwtException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("utf-8");
            ExceptionResponse errorResponse = new ExceptionResponse("올바르지 않은 토큰입니다.", 403);
            new ObjectMapper().writeValue(response.getWriter(), errorResponse);
        }
        catch (SignatureException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("utf-8");
            ExceptionResponse errorResponse = new ExceptionResponse("유효하지 않은 토큰입니다.", 403);
            new ObjectMapper().writeValue(response.getWriter(), errorResponse);
        }
        catch (PermissionDeniedDataAccessException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("utf-8");
            ExceptionResponse errorResponse = new ExceptionResponse("권한이 없습니다.", 403);
            new ObjectMapper().writeValue(response.getWriter(), errorResponse);
        }
        catch (UsernameNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("utf-8");
            ExceptionResponse errorResponse = new ExceptionResponse("찾을 수 없는 유저입니다.", 403);
            new ObjectMapper().writeValue(response.getWriter(), errorResponse);
        }
        finally {

        }


//       catch (Exception e) {
//            LOGGER.info("FILTER 에서 Expired jwt 제외 예외처리한 것");
//            LOGGER.info("E : " + e.getMessage());
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//            response.setCharacterEncoding("utf-8");
//            ExceptionResponse errorResponse = new ExceptionResponse(e.getMessage(), 401);
//            new ObjectMapper().writeValue(response.getWriter(), errorResponse);
//        }
    }

}