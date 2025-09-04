package ru.andryss.antalk.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.andryss.antalk.server.generated.api.AuthApi;
import ru.andryss.antalk.server.generated.model.SignInRequest;
import ru.andryss.antalk.server.generated.model.SignInResponse;
import ru.andryss.antalk.server.service.AuthService;

@RestController
@RequiredArgsConstructor
public class AuthApiController implements AuthApi {

    private final AuthService authService;

    @Override
    public SignInResponse signIn(SignInRequest request) {
        return authService.signIn(request);
    }
}
