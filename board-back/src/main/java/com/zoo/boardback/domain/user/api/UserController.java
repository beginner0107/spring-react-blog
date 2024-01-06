package com.zoo.boardback.domain.user.api;

import com.zoo.boardback.domain.ApiResponse;
import com.zoo.boardback.domain.auth.details.CustomUserDetails;
import com.zoo.boardback.domain.user.application.UserService;
import com.zoo.boardback.domain.user.dto.response.GetSignUserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
  private final UserService userService;

  @GetMapping("")
  public ApiResponse<GetSignUserResponseDto> getSignInUser(
      @AuthenticationPrincipal CustomUserDetails user
  ) {
    return ApiResponse.ok(userService.getSignUser(user.getUsername()));
  }
}
