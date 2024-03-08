package com.zoo.boardback.domain.auth.api;

import com.zoo.boardback.domain.ApiResponse;
import com.zoo.boardback.domain.auth.application.AuthService;
import com.zoo.boardback.domain.auth.dto.request.SignInRequestDto;
import com.zoo.boardback.domain.auth.dto.request.SignUpRequestDto;
import com.zoo.boardback.domain.auth.dto.response.SignInResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/sign-up")
    public ApiResponse<Void> signUp(@RequestBody @Valid SignUpRequestDto request) {
        authService.signUp(request);
        return ApiResponse.of(HttpStatus.CREATED, null);
    }

    @PostMapping(value = "/sign-in")
    public ApiResponse<SignInResponseDto> signIn(@RequestBody @Valid SignInRequestDto request,
        HttpServletRequest httpRequest, HttpServletResponse httpResponse
    ) {
        SignInResponseDto signInResponseDto = authService.signIn(request, httpRequest,
            httpResponse);
        return ApiResponse.ok(signInResponseDto);
    }

    // TODO: 로그아웃 빠짐
}

