package com.zoo.boardback.domain.auth.api;

import com.zoo.boardback.domain.auth.application.AuthService;
import com.zoo.boardback.domain.auth.dto.request.SignRequestDto;
import com.zoo.boardback.domain.auth.dto.response.SignResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping(value = "/login")
  public ResponseEntity<SignResponseDto> signin(@RequestBody SignRequestDto request) throws Exception {
    return new ResponseEntity<>(authService.login(request), HttpStatus.OK);
  }

  @PostMapping(value = "/register")
  public ResponseEntity<Boolean> signup(@RequestBody SignRequestDto request) throws Exception {
    return new ResponseEntity<>(authService.register(request), HttpStatus.OK);
  }
}
