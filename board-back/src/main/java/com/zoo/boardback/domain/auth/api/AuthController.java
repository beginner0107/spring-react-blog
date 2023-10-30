package com.zoo.boardback.domain.auth.api;

import com.zoo.boardback.domain.auth.dto.request.SignInReqDto;
import com.zoo.boardback.domain.auth.dto.response.SignInResDto;
import com.zoo.boardback.domain.auth.dto.request.SignUpReqDto;
import com.zoo.boardback.domain.user.application.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

  @PostMapping("/signIn")
  public ResponseEntity<SignInResDto> signIn(@RequestBody SignInReqDto request) {
    System.out.println("로그인 로직");
    return new ResponseEntity<>(userService.login(request), HttpStatus.OK);
  }
}
