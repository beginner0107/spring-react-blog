package com.zoo.boardback.domain.comment.dto.query;

import com.zoo.boardback.domain.comment.entity.Comment;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class ChildCommentQueryDto {

    private Long commentId;
    private String nickname;
    private String profileImage;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long childCount;
    private Boolean delYn;

    public ChildCommentQueryDto(Long commentId, String nickname, String profileImage, String content
        , LocalDateTime createdAt, LocalDateTime updatedAt, Long childCount, Boolean delYn
    ) {
        this.commentId = commentId;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.childCount = childCount;
        this.delYn = delYn;
    }

    public static CommentQueryDto from(Comment comment) {
        return CommentQueryDto.builder()
            .commentId(comment.getId())
            .nickname(comment.getUser().getNickname())
            .content(comment.getContent())
            .profileImage(comment.getUser().getProfileImage())
            .createdAt(comment.getCreatedAt())
            .updatedAt(comment.getUpdatedAt())
            .delYn(comment.getDelYn())
            .build();
    }
}