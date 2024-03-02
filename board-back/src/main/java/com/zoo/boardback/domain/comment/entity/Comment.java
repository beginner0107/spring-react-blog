package com.zoo.boardback.domain.comment.entity;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.zoo.boardback.domain.comment.dto.request.CommentUpdateRequestDto;
import com.zoo.boardback.domain.post.entity.Post;
import com.zoo.boardback.domain.user.entity.User;
import com.zoo.boardback.global.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@Table(name = "Comment")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "postId")
    private Post post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parentId")
    private Comment parent;

    @OneToMany(mappedBy = "parent", fetch = LAZY, cascade = CascadeType.ALL)
    private List<Comment> children = new ArrayList<>();

    @Column(name = "delYn", nullable = false)
    private Boolean delYn;

    @Builder
    public Comment(Long id, String content, Post post, User user, Comment parent,
        Boolean delYn) {
        this.id = id;
        this.content = content;
        this.post = post;
        this.user = user;
        this.parent = parent;
        this.delYn = delYn;
    }

    public void editComment(CommentUpdateRequestDto commentUpdateRequestDto) {
        this.content = commentUpdateRequestDto.getContent();
    }

    public void deleteComment() {
        this.content = "[삭제된 댓글입니다]";
        this.delYn = true;
    }

    public Comment addChild(Comment child) {
        this.children.add(child);
        child.addParent(this);
        return this;
    }

    private void addParent(Comment comment) {
        this.parent = comment;
    }
}
