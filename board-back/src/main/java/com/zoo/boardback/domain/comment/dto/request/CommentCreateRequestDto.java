package com.zoo.boardback.domain.comment.dto.request;

import static lombok.AccessLevel.PRIVATE;

import com.zoo.boardback.domain.post.entity.Post;
import com.zoo.boardback.domain.comment.entity.Comment;
import com.zoo.boardback.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
public class CommentCreateRequestDto {

    public static final int MAX_REQUEST_COMMENT_LENGTH = 300;

    @NotNull(message = "게시글 번호를 입력해주세요")
    private Long postId;

    private Long commentId;

    @NotBlank(message = "댓글 내용을 입력해주세요")
    @Size(max = MAX_REQUEST_COMMENT_LENGTH, message = "댓글 내용은 300자 이하로 입력해주세요.")
    private String content;

    @Builder
    public CommentCreateRequestDto(Long postId, String content, Long commentId) {
        this.postId = postId;
        this.content = content;
        this.commentId = commentId;
    }

    public Comment toEntity(User user, Post post) {
        return Comment.builder()
            .post(post)
            .user(user)
            .content(content)
            .delYn(false)
            .build();
    }
}
