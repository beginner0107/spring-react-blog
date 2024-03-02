package com.zoo.boardback.domain.post.dto.request;

import static lombok.AccessLevel.PRIVATE;

import com.zoo.boardback.domain.post.entity.Post;
import com.zoo.boardback.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;
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
    private List<String> postImageUrls;
    private String postTitleImageUrl;

    @Builder
    public PostCreateRequestDto(
        String title,
        String content,
        List<String> postImageUrls,
        String postTitleImageUrl
    ) {
        this.title = title;
        this.content = content;
        this.postImageUrls = postImageUrls;
        this.postTitleImageUrl = postTitleImageUrl;
    }

    public Optional<String> getPostTitleImageUrl() {
        return Optional.ofNullable(postTitleImageUrl);
    }

    public Optional<List<String>> getPostImageUrls() {
        return Optional.ofNullable(postImageUrls);
    }

    public Post toEntity(User user) {
        return Post.builder()
            .user(user)
            .title(title)
            .content(content)
            .favoriteCount(0)
            .commentCount(0)
            .viewCount(0)
            .build();
    }
}
