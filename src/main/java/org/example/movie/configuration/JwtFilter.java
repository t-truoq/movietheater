package org.example.movie.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            logger.info("No Bearer token found in request - Header: " + header);
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        logger.info("Processing token: " + token.substring(0, Math.min(token.length(), 20)) + "..."); // Log một phần token để tránh log dài
        try {
            if (!jwtUtil.validateToken(token)) {
                logger.warn("Token validation failed for token: " + token.substring(0, Math.min(token.length(), 20)) + "...");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"code\": 1020, \"message\": \"Token is invalid or expired\"}");
                return;
            }

            Claims claims = Jwts.parser()
                    .setSigningKey(Base64.getEncoder().encodeToString(jwtUtil.getSecret().getBytes()))
                    .parseClaimsJws(token)
                    .getBody();
            String username = claims.getSubject();
            String role = claims.get("role", String.class);
            Long accountId = claims.get("accountId", Long.class);

            logger.info("Token parsed successfully - Username: " + username + ", Role: " + role + ", AccountId: " + accountId);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User userDetails = new User(username, "", Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, token, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Authentication set in SecurityContextHolder - Authorities: " + authentication.getAuthorities());
            }
        } catch (Exception e) {
            logger.error("Error parsing token: " + e.getMessage());
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\": 1020, \"message\": \"Invalid token: " + e.getMessage() + "\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}