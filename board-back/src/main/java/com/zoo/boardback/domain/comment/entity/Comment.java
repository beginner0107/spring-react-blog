package com.zoo.boardback.domain.comment.entity;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.zoo.boardback.domain.comment.dto.request.CommentUpdateRequestDto;
import com.zoo.boardback.domain.post.entity.Post;
import com.zoo.boardback.domain.user.entity.User;
import com.zoo.boardback.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

  @Builder
  public Comment(Long id, String content, Post post, User user) {
    this.id = id;
    this.content = content;
    this.post = post;
    this.user = user;
  }

  public void editComment(CommentUpdateRequestDto commentUpdateRequestDto) {
    this.content = commentUpdateRequestDto.getContent();
  }
}
