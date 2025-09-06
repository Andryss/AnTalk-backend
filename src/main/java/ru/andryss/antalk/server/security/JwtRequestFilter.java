package ru.andryss.antalk.server.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.andryss.antalk.server.config.SecurityConfig;
import ru.andryss.antalk.server.generated.model.ErrorObject;
import ru.andryss.antalk.server.service.ObjectMapperWrapper;

import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.authenticated;

@Slf4j
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final String ERROR_MESSAGE = "Пользователь не авторизован";

    private final JwtTokenUtil jwtTokenUtil;
    private final ObjectMapperWrapper objectMapper;

    @Override
    @SuppressWarnings("NullableProblems")
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Optional<String> tokenOptional = getTokenFromRequest(request);
        if (tokenOptional.isPresent() && jwtTokenUtil.isTokenValid(tokenOptional.get())) {
            String token = tokenOptional.get();
            try {
                SessionData sessionData = jwtTokenUtil.extractUserData(token);
                List<SimpleGrantedAuthority> authorities = sessionData.getPrivileges().stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();
                UsernamePasswordAuthenticationToken authentication = authenticated(sessionData, token, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                log.error("Catch exception when parsing user token", e);
                error(response, new ErrorObject()
                        .code(401).message("user.unauthorized.error").humanMessage(ERROR_MESSAGE));
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private Optional<String> getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer == null) return Optional.empty();
        return bearer.startsWith("Bearer ") ? Optional.of(bearer.substring(7)) : Optional.empty();
    }

    private void error(HttpServletResponse response, ErrorObject error) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(SecurityConfig.RESPONSE_CONTENT_TYPE);
        response.setCharacterEncoding(SecurityConfig.RESPONSE_CHARACTER_ENCODING);
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(error));
        writer.flush();
    }
}
