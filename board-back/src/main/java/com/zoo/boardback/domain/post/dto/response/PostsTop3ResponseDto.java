package com.zoo.boardback.domain.post.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.zoo.boardback.domain.post.dto.response.object.PostRankItem;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
public class PostsTop3ResponseDto {

  private List<PostRankItem> top3List;

  @Builder
  public PostsTop3ResponseDto(List<PostRankItem> top3List) {
    this.top3List = top3List;
  }
}
