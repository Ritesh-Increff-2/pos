package com.increff.pos.filter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.increff.pos.helper.TokenService;
import com.increff.pos.model.data.UserData;
import java.util.Objects;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class AuthFilter extends OncePerRequestFilter {
    @Autowired
    private TokenService tokenService;
    private static final List<String> EXCLUDED_PATHS = Arrays.asList("/api/auth/login", "swagger-ui/index.html");
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        // Handle preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        String path = request.getRequestURI();
        if (EXCLUDED_PATHS.stream().anyMatch(path::endsWith)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = request.getHeader("Authorization");
        if (Objects.isNull(token) || token.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No token provided");
            return;
        }
        UserData tokenData = tokenService.validateToken(token, request.getRequestURI(), request.getMethod());
        if (Objects.isNull(tokenData)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }
        // Add user information to request attributes
        request.setAttribute("userEmail", tokenData.getEmail());
        request.setAttribute("userRole", tokenData.getRole());
        filterChain.doFilter(request, response);
    }
}