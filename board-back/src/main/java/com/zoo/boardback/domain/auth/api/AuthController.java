package com.zoo.boardback.domain.auth.api;

import com.zoo.boardback.domain.auth.dto.SignUpReqDto;
import com.zoo.boardback.domain.user.application.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<Void> register(@RequestBody SignUpReqDto reqDto) {
    userService.register(reqDto);
    return ResponseEntity.ok().build();
  }
}
