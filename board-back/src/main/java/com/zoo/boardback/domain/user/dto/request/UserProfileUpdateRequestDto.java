package com.zoo.boardback.domain.user.dto.request;

import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor
public class UserProfileUpdateRequestDto {

    private String profileImage;
}
