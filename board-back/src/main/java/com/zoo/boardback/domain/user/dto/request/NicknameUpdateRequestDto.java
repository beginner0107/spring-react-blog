package com.zoo.boardback.domain.user.dto.request;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor
public class NicknameUpdateRequestDto {

    @Size(max = 20, message = "닉네임은 20자 이하입니다.")
    private String nickname;
}
