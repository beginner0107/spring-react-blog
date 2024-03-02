package com.zoo.boardback.domain.user.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.zoo.boardback.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
@Builder
public class SignUserResponseDto {

    private String email;
    private String nickname;
    private String profileImage;

    public static SignUserResponseDto from(User user) {
        return SignUserResponseDto.builder()
            .email(user.getEmail())
            .nickname(user.getNickname())
            .profileImage(user.getProfileImage())
            .build();
    }
}
