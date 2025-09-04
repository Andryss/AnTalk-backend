package ru.andryss.antalk.server.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.andryss.antalk.server.entity.SessionEntity;
import ru.andryss.antalk.server.entity.SessionStatus;
import ru.andryss.antalk.server.entity.UserEntity;
import ru.andryss.antalk.server.exception.Errors;
import ru.andryss.antalk.server.generated.model.SignInRequest;
import ru.andryss.antalk.server.generated.model.SignInResponse;
import ru.andryss.antalk.server.generated.model.SignInResponseSession;
import ru.andryss.antalk.server.repository.SessionRepository;
import ru.andryss.antalk.server.repository.UserRepository;
import ru.andryss.antalk.server.security.JwtTokenUtil;
import ru.andryss.antalk.server.security.SessionData;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionRepository sessionRepository;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * Выполнить вход для заданных параметров пользователя.
     * Инициализирует сессию (вход на устройстве) и возвращает данные для ее идентификации
     */
    public SignInResponse signIn(SignInRequest request) {
        log.info("Sign in for user {}", request.getUsername());

        Optional<UserEntity> optionalUser = userRepository.findByUsername(request.getUsername());
        if (optionalUser.isEmpty()) {
            throw Errors.unauthorized();
        }

        UserEntity user = optionalUser.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw Errors.unauthorized();
        }

        SessionEntity session = new SessionEntity();
        session.setUserId(user.getId());
        session.setMeta(Map.of());
        session.setStatus(SessionStatus.DISCONNECTED);
        session.setLastNotification(-1L);

        SessionEntity saved = sessionRepository.save(session);

        SessionData sessionData = new SessionData();
        sessionData.setSessionId(saved.getId());
        sessionData.setUserId(saved.getUserId());
        sessionData.setPrivileges(List.of());

        String token = jwtTokenUtil.generateAccessToken(sessionData);

        return new SignInResponse()
                .session(new SignInResponseSession()
                        .id(saved.getId())
                        .token(token));
    }
}
