package com.zoo.boardback.domain.post.dto.request;

import static lombok.AccessLevel.PRIVATE;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 제목 + 내용 제목 내용 댓글 닉네임
 */
@Getter
@NoArgsConstructor(access = PRIVATE)
public class PostSearchCondition {

    private String title;
    private String content;
    private String commentCont;
    private String titleOrContent;
    private String nickname;

    @Builder
    public PostSearchCondition(String title, String content, String commentCont,
        String titleOrContent,
        String nickname) {
        this.title = title;
        this.content = content;
        this.commentCont = commentCont;
        this.titleOrContent = titleOrContent;
        this.nickname = nickname;
    }
}
