package ru.andryss.antalk.server.requestid;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpRequestIdAssignFilter implements RequestIdAware, Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            assignRequestId();

            HttpServletRequest request = (HttpServletRequest) servletRequest;
            log.info("Incoming HTTP request: {} {}", request.getMethod(), request.getRequestURI());

            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            clearRequestId();
        }
    }
}
