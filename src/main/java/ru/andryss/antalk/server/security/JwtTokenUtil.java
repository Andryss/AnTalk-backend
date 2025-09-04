package ru.andryss.antalk.server.security;

import java.security.Key;
import java.time.Clock;
import java.util.Date;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import ru.andryss.antalk.server.config.JwtProperties;
import ru.andryss.antalk.server.service.ObjectMapperWrapper;

@RequiredArgsConstructor
public class JwtTokenUtil implements InitializingBean {

    public static final String USER_DATA_KEY = "data";

    private final JwtProperties properties;
    private final Clock clock;
    private final ObjectMapperWrapper objectMapper;

    private Key signingKey;

    @Override
    public void afterPropertiesSet() {
        byte[] bytes = Decoders.BASE64.decode(properties.getSecret());
        signingKey = Keys.hmacShaKeyFor(bytes);
        properties.setSecret(null);
    }

    /**
     * Сгенерировать JWT-токен по информации о пользователе
     */
    public String generateAccessToken(SessionData sessionData) {
        Date now = new Date(clock.millis());
        Date expired = new Date(now.getTime() + properties.getTokenExpirationMillis());
        return Jwts.builder()
                .setSubject(String.valueOf(sessionData.getSessionId()))
                .setClaims(Map.of(USER_DATA_KEY, objectMapper.writeValueAsString(sessionData)))
                .setIssuedAt(now)
                .setExpiration(expired)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Провалидировать заданный токен (корректно сформирован + срок действия не истек)
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = getTokenClaims(token);
            long expirationTime = claims.getExpiration().getTime();
            long currentTime = clock.millis();
            return currentTime <= expirationTime;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Извлечь данные пользователя из токена
     */
    public SessionData extractUserData(String token) {
        Claims body = getTokenClaims(token);
        String data = body.get(USER_DATA_KEY, String.class);
        return objectMapper.readValue(data, SessionData.class);
    }

    private Claims getTokenClaims(String token) throws JwtException {
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .setClock(() -> Date.from(clock.instant()))
                .build();
        return jwtParser.parseClaimsJws(token).getBody();
    }
}
