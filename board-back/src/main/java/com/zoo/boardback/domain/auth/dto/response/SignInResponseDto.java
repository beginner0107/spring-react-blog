package com.zoo.boardback.domain.auth.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.zoo.boardback.domain.user.entity.User;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor
public class SignInResponseDto {

    private Long userId;
    private String email;
    private String nickname;
    private List<String> roles;

    public static SignInResponseDto of(User user, List<String> roles) {
        return new SignInResponseDto(
            user.getId(),
            user.getEmail(),
            user.getNickname(),
            roles
        );
    }
}