package com.zoo.boardback.domain.user.api;

import com.zoo.boardback.domain.ApiResponse;
import com.zoo.boardback.domain.user.application.UserService;
import com.zoo.boardback.domain.user.dto.request.NicknameUpdateRequestDto;
import com.zoo.boardback.domain.user.dto.request.UserProfileUpdateRequestDto;
import com.zoo.boardback.domain.user.dto.response.SignUserResponseDto;
import com.zoo.boardback.domain.user.entity.User;
import com.zoo.boardback.global.config.security.annotation.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
  public ApiResponse<SignUserResponseDto> getUser(
      @LoginUser User user
  ) {
    return ApiResponse.ok(userService.getUser(user.getEmail()));
  }

  @PatchMapping("/nickname")
  public ApiResponse<Void> updateNickname(
      @RequestBody @Valid NicknameUpdateRequestDto nicknameUpdateRequestDto,
      @LoginUser User user
  ) {
    userService.updateNickname(user.getEmail(), nicknameUpdateRequestDto.getNickname());
    return ApiResponse.ok(null);
  }

  @PatchMapping("/profileImage")
  public ApiResponse<Void> updateProfileImage(
      @RequestBody @Valid UserProfileUpdateRequestDto userProfileUpdateDto,
      @LoginUser User user
  ) {
    userService.updateProfileImage(user.getEmail(), userProfileUpdateDto.getProfileImage());
    return ApiResponse.ok(null);
  }
}
