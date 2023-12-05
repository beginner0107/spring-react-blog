package com.zoo.boardback.domain.board.dto.response;

import com.zoo.boardback.domain.board.entity.Board;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDetailResponseDto {

  private int boardNumber;
  private String title;
  private String content;
  private List<String> boardImageList;
  private String createdAt;
  private String updatedAt;
  private String writerEmail;
  private String writerNickname;
  private String writerProfileImage;

  public static PostDetailResponseDto of(Board board, List<String> boardImageList) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    return PostDetailResponseDto.builder()
        .boardNumber(board.getBoardNumber())
        .title(board.getTitle())
        .content(board.getContent())
        .boardImageList(boardImageList)
        .createdAt(board.getCreatedAt().format(formatter))
        .updatedAt(board.getUpdatedAt().format(formatter))
        .writerEmail(board.getUser().getEmail())
        .writerNickname(board.getUser().getNickname())
        .writerProfileImage(board.getUser().getProfileImage())
        .build();
  }
}
