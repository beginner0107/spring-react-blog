package com.zoo.boardback.docs.user;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zoo.boardback.WithAuthUser;
import com.zoo.boardback.docs.RestDocsSecuritySupport;
import com.zoo.boardback.domain.user.dto.request.NicknameUpdateRequestDto;
import com.zoo.boardback.domain.user.dto.request.UserProfileUpdateRequestDto;
import com.zoo.boardback.domain.user.dto.response.SignUserResponseDto;
import com.zoo.boardback.domain.user.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

public class UserControllerDocsTest extends RestDocsSecuritySupport {

    final static String EMAIL = "test123@naver.com";
    final static String NICKNAME = "개구리왕눈이123";


    @DisplayName("회원은 자신의 정보를 조회할 수 있다.")
    @WithAuthUser(userId = "1", role = "ROLE_USER")
    @Test
    void getSignInUser() throws Exception {
        given(userRepository.findById(1L)).willReturn(Optional.ofNullable(User.builder()
            .id(1L)
            .email(EMAIL)
            .nickname(NICKNAME)
            .profileImage(null)
            .build()));
        given(userService.getUser(EMAIL))
            .willReturn(SignUserResponseDto.builder()
                .email(EMAIL)
                .nickname(NICKNAME)
                .build()
            );

        mockMvc.perform(get("/api/v1/user")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("user-info",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER)
                        .description("코드"),
                    fieldWithPath("status").type(JsonFieldType.STRING)
                        .description("상태"),
                    fieldWithPath("message").type(JsonFieldType.STRING)
                        .description("메시지"),
                    fieldWithPath("field").type(JsonFieldType.STRING)
                        .optional()
                        .description("에러 발생 필드명"),
                    fieldWithPath("data.email").type(JsonFieldType.STRING)
                        .description("회원 이메일"),
                    fieldWithPath("data.nickname").type(JsonFieldType.STRING)
                        .description("회원 닉네임"),
                    fieldWithPath("data.profileImage").type(JsonFieldType.STRING)
                        .optional()
                        .description("회원 프로필 이미지")
                )
            ));
    }

    @DisplayName("회원은 자신의 닉네임을 변경할 수 있다.")
    @WithAuthUser(userId = "1", role = "ROLE_USER")
    @Test
    void updateNickname() throws Exception {
        NicknameUpdateRequestDto request = new NicknameUpdateRequestDto("마동석");
        given(userService.getUser(EMAIL))
            .willReturn(SignUserResponseDto.builder()
                .email(EMAIL)
                .nickname(NICKNAME)
                .profileImage("http://image.png")
                .build()
            );
        given(userRepository.findById(1L)).willReturn(Optional.ofNullable(User.builder()
            .id(1L)
            .email(EMAIL)
            .nickname(NICKNAME)
            .profileImage(null)
            .build()));

        mockMvc.perform(patch("/api/v1/user/nickname")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(document("user-updateNickname",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("nickname").type(JsonFieldType.STRING)
                        .description("변경할 닉네임")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER)
                        .description("코드"),
                    fieldWithPath("status").type(JsonFieldType.STRING)
                        .description("상태"),
                    fieldWithPath("message").type(JsonFieldType.STRING)
                        .description("메시지"),
                    fieldWithPath("field").type(JsonFieldType.STRING)
                        .optional()
                        .description("에러 발생 필드명"),
                    fieldWithPath("data").type(JsonFieldType.NULL)
                        .description("빈 값")
                )
            ));
    }

    @DisplayName("회원은 자신의 프로필 이미지를 변경할 수 있다.")
    @WithAuthUser(userId = "1", role = "ROLE_USER")
    @Test
    void updateProfileImage() throws Exception {
        UserProfileUpdateRequestDto request = new UserProfileUpdateRequestDto(
            "http://localhost:2344/cProfileImage.png"
        );
        given(userService.getUser(EMAIL))
            .willReturn(SignUserResponseDto.builder()
                .email(EMAIL)
                .nickname(NICKNAME)
                .profileImage("http://image.png")
                .build()
            );

        given(userRepository.findById(1L)).willReturn(Optional.ofNullable(User.builder()
            .id(1L)
            .email(EMAIL)
            .nickname(NICKNAME)
            .profileImage(null)
            .build()));

        mockMvc.perform(patch("/api/v1/user/profileImage")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(document("user-updateProfileImage",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("profileImage").type(JsonFieldType.STRING)
                        .description("변경할 프로필 이미지 경로")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER)
                        .description("코드"),
                    fieldWithPath("status").type(JsonFieldType.STRING)
                        .description("상태"),
                    fieldWithPath("message").type(JsonFieldType.STRING)
                        .description("메시지"),
                    fieldWithPath("field").type(JsonFieldType.STRING)
                        .optional()
                        .description("에러 발생 필드명"),
                    fieldWithPath("data").type(JsonFieldType.NULL)
                        .description("빈 값")
                )
            ));
    }
}
