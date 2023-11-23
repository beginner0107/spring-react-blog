package com.zoo.boardback.domain.auth.api;

import com.zoo.boardback.domain.auth.application.AuthService;
import com.zoo.boardback.domain.auth.dto.request.SignInRequestDto;
import com.zoo.boardback.domain.auth.dto.request.SignUpRequestDto;
import com.zoo.boardback.domain.auth.dto.response.SignInResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpRequestDto request) {
    authService.signUp(request);
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = "/sign-in")
  public ResponseEntity<SignInResponseDto> signIn(@RequestBody @Valid SignInRequestDto request) {
    return ResponseEntity.ok(authService.signIn(request));
  }
}

