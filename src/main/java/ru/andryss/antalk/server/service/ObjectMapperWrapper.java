package ru.andryss.antalk.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

/**
 * Класс-обертка вокруг {@link ObjectMapper}. Изолирует работу с исключениями и упрощает сериализацию
 */
@Service
@RequiredArgsConstructor
public class ObjectMapperWrapper {

    private final ObjectMapper mapper;

    /**
     * Сериализовать объект в JSON строку
     */
    public String writeValueAsString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return String.valueOf(obj);
        }
    }

    /**
     * Десериализовать объект из JSON строки
     */
    @SneakyThrows
    public <T> T readValue(String data, Class<T> cls) {
        return mapper.readValue(data, cls);
    }

    /**
     * Десериализовать JSON-дерево из строки
     */
    @SneakyThrows
    public JsonNode readTree(String data) {
        return mapper.readTree(data);
    }
}