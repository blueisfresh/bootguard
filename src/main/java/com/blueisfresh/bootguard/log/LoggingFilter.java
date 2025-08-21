package com.blueisfresh.bootguard.log;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Servlet filter for logging incoming requests and outgoing responses.
 * <p>
 * Logs request method, URI, response status, duration, and exceptions.
 * Each request is tagged with a correlation ID for traceability.
 */

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        String correlationId = UUID.randomUUID().toString();

        log.info("[{}] ➡️ Incoming request: {} {}", correlationId, request.getMethod(), request.getRequestURI());

        // Wrap response to capture status
        StatusCaptureResponseWrapper responseWrapper = new StatusCaptureResponseWrapper(response);

        try {
            filterChain.doFilter(request, responseWrapper);
        } catch (Exception ex) {
            log.error("[{}] ❌ Exception during request: {} {} - {}",
                    correlationId, request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
            throw ex; // rethrow so GlobalExceptionHandler can handle it
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = responseWrapper.getStatus();
            if (status >= 400) {
                log.warn("[{}] ⬅️ Response: {} {} -> {} ({} ms)",
                        correlationId, request.getMethod(), request.getRequestURI(), status, duration);
            } else {
                log.info("[{}] ⬅️ Response: {} {} -> {} ({} ms)",
                        correlationId, request.getMethod(), request.getRequestURI(), status, duration);
            }
        }
    }
}
