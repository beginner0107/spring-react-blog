package com.zoo.boardback.domain.board.dto.request;

import static lombok.AccessLevel.PRIVATE;

import com.zoo.boardback.domain.board.entity.Board;
import com.zoo.boardback.domain.image.entity.Image;
import com.zoo.boardback.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
public class PostCreateRequestDto {

  @NotBlank(message = "게시글 제목을 입력해주세요.")
  private String title;
  @NotBlank
  private String content;
  private List<String> boardImageList;
  private String boardTitleImage;

  @Builder
  public PostCreateRequestDto(
      String title,
      String content,
      List<String> boardImageList,
      String boardTitleImage
      ) {
    this.title = title;
    this.content = content;
    this.boardImageList = boardImageList;
    this.boardTitleImage = boardTitleImage;
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
