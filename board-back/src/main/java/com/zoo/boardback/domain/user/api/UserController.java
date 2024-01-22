package com.zoo.boardback.domain.user.api;

import com.zoo.boardback.domain.ApiResponse;
import com.zoo.boardback.domain.auth.details.CustomUserDetails;
import com.zoo.boardback.domain.user.application.UserService;
import com.zoo.boardback.domain.user.dto.request.NicknameUpdateRequestDto;
import com.zoo.boardback.domain.user.dto.request.UserProfileUpdateRequestDto;
import com.zoo.boardback.domain.user.dto.response.GetSignUserResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
  private final UserService userService;

  @GetMapping
  public ApiResponse<GetSignUserResponseDto> getSignInUser(
      @AuthenticationPrincipal CustomUserDetails user
  ) {
    return ApiResponse.ok(userService.getSignUser(user.getUsername()));
  }

  @PatchMapping("/nickname")
  public ApiResponse<Void> updateNickname(
      @RequestBody @Valid NicknameUpdateRequestDto nicknameUpdateRequestDto,
      @AuthenticationPrincipal CustomUserDetails user
  ) {
    userService.updateNickname(user.getUsername(), nicknameUpdateRequestDto.getNickname());
    return ApiResponse.ok(null);
  }

  @PatchMapping("/profileImage")
  public ApiResponse<Void> updateProfileImage(
      @RequestBody @Valid UserProfileUpdateRequestDto userProfileUpdateDto,
      @AuthenticationPrincipal CustomUserDetails user
  ) {
    userService.updateProfileImage(user.getUsername(), userProfileUpdateDto.getProfileImage());
    return ApiResponse.ok(null);
  }
}
