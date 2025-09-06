package ru.andryss.antalk.server.config.requestid;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpRequestIdAssignFilter implements RequestIdAware, Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        try {
            String requestId = request.getHeader(REQUEST_ID_HEADER);
            if (StringUtils.isBlank(requestId)) {
                requestId = generateRequestId();
            }
            assignRequestId(requestId);
            response.setHeader(REQUEST_ID_HEADER, requestId);

            log.info("Incoming HTTP request: {} {}", request.getMethod(), request.getRequestURI());

            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            clearRequestId();
        }
    }
}
