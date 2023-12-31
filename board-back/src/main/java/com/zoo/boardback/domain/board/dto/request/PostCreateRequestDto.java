package com.zoo.boardback.domain.board.dto.request;

import com.zoo.boardback.domain.board.entity.Board;
import com.zoo.boardback.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostCreateRequestDto {

  @NotBlank(message = "게시글 제목을 입력해주세요.")
  private String title;
  @NotBlank
  private String content;
  @NotNull
  private List<String> boardImageList;

  @Builder
  public PostCreateRequestDto(String title, String content, List<String> boardImageList) {
    this.title = title;
    this.content = content;
    this.boardImageList = boardImageList;
  }

  public Board toEntity(User user) {
    return Board.builder()
        .user(user)
        .title(title)
        .content(content)
        .favoriteCount(0)
        .commentCount(0)
        .viewCount(0)
        .build();
  }
}
