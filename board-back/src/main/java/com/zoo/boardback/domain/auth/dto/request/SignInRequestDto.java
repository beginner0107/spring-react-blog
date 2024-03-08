package com.zoo.boardback.domain.auth.dto.request;


import static lombok.AccessLevel.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
public class SignInRequestDto {

    @Email(message = "이메일 형식을 맞춰주세요")
    private String email;
    @Pattern(regexp = "^(?=.*?[A-Za-z])(?=.*?\\d).{8,20}$", message = "비밀번호는 8~20자여야 하고 영어, 숫자가 포함되어야 합니다.")
    @JsonProperty("password")
    private String password;

    @Builder
    public SignInRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
