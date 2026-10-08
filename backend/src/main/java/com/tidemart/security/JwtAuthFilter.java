package com.tidemart.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwt;

    public JwtAuthFilter(JwtUtil jwt) { this.jwt = jwt; }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String h = req.getHeader("Authorization");
        if (h != null && h.startsWith("Bearer ")) {
            try {
                Claims c = jwt.parse(h.substring(7));
                var auth = new UsernamePasswordAuthenticationToken(Long.valueOf(c.getSubject()), null,
                        roles(c));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(req, res);
    }

    private List<SimpleGrantedAuthority> roles(Claims c) {
        java.util.Set<String> set = new java.util.LinkedHashSet<>();
        Object raw = c.get("roles");
        if (raw instanceof java.util.List<?> l) for (Object x : l) if (x != null) set.add(String.valueOf(x));
        String primary = c.get("role", String.class); if (primary != null) set.add(primary);
        return set.stream().map(x -> new SimpleGrantedAuthority("ROLE_" + x)).toList();
    }
}
