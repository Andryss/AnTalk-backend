package ru.andryss.antalk.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.andryss.antalk.server.service.ObjectMapperWrapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public abstract class BaseAuthTest extends BaseApiTest {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapperWrapper objectMapper;

    public void registerUser(long id, String username, String rawPassword) {
        dbTestUtil.saveUser(id, username, passwordEncoder.encode(rawPassword));
    }

    public AuthData singIn(String username, String rawPassword) throws Exception {
        String request = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, rawPassword);

        String response = mockMvc.perform(
                        post("/auth/signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
        ).andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, AuthData.class);
    }

    public AuthData registerUserAndSignIn(long id, String username, String rawPassword) throws Exception {
        registerUser(id, username, rawPassword);
        return singIn(username, rawPassword);
    }

    public static String formatAuthorization(AuthData authData) {
        return "Bearer " + authData.session().token();
    }

    public record AuthData(SessionData session){
    }

    public record SessionData(String id, String token){
    }
}
