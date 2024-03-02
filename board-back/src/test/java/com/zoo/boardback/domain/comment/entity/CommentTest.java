package com.zoo.boardback.domain.comment.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.zoo.boardback.domain.comment.dto.request.CommentUpdateRequestDto;
import com.zoo.boardback.domain.post.entity.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommentTest {

    @DisplayName("댓글을 수정한다.")
    @Test
    void increaseViewCount() {
        // given
        Post post = createPost();
        Comment comment = createComment("댓글을 답니다.!", post);
        String updateContent = "댓글 수정입니다. ㅎㅎ";
        CommentUpdateRequestDto updateRequestDto = createUpdateRequestDto(updateContent);

        // when
        comment.editComment(updateRequestDto);

        // then
        assertThat(comment.getContent()).isEqualTo(updateContent);
    }

    private Post createPost() {
        return Post.builder()
            .title("글의 제목")
            .content("글의 컨텐츠")
            .favoriteCount(0)
            .viewCount(0)
            .build();
    }

    private Comment createComment(String content, Post post) {
        return Comment.builder()
            .content(content)
            .post(post)
            .build();
    }

    private CommentUpdateRequestDto createUpdateRequestDto(String content) {
        return CommentUpdateRequestDto.builder()
            .content(content)
            .build();
    }
}