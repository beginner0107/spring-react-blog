package com.zoo.boardback.domain.post.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.zoo.boardback.domain.post.entity.Post;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor
@Builder
public class PostDetailResponseDto {

  private Long postId;
  private String title;
  private String content;
  private List<String> postImageList;
  private String createdAt;
  private String updatedAt;
  private String writerEmail;
  private String writerNickname;
  private String writerProfileImage;

  public static PostDetailResponseDto of(Post post, List<String> postImageList) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    return PostDetailResponseDto.builder()
        .postId(post.getId())
        .title(post.getTitle())
        .content(post.getContent())
        .postImageList(postImageList)
        .createdAt(post.getCreatedAt().format(formatter))
        .updatedAt(post.getUpdatedAt().format(formatter))
        .writerEmail(post.getUser().getEmail())
        .writerNickname(post.getUser().getNickname())
        .writerProfileImage(post.getUser().getProfileImage())
        .build();
  }
}
