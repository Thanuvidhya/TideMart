package com.tidemart.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Allows 30 login-related requests per minute for each address. In memory, so it resets when the server restarts. */
@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private static final int LIMIT = 30;
    private static final long WINDOW_MS = 60_000;
    private final Map<String, Deque<Long>> hits = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        if (req.getRequestURI().startsWith("/api/auth/")) {
            Deque<Long> q = hits.computeIfAbsent(req.getRemoteAddr(), k -> new ArrayDeque<>());
            long now = System.currentTimeMillis();
            synchronized (q) {
                while (!q.isEmpty() && now - q.peekFirst() > WINDOW_MS) q.pollFirst();
                if (q.size() >= LIMIT) {
                    res.setStatus(429);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"success\":false,\"message\":\"Too many attempts. Please wait a minute.\"}");
                    return;
                }
                q.addLast(now);
            }
        }
        chain.doFilter(req, res);
    }
}
