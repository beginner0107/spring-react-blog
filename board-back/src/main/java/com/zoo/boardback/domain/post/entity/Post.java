package com.zoo.boardback.domain.post.entity;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.zoo.boardback.domain.user.entity.User;
import com.zoo.boardback.global.entity.BaseEntity;
import com.zoo.boardback.global.error.BusinessException;
import com.zoo.boardback.global.error.ErrorCode;
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
@Table(name = "Post")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class Post extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  @Column(length = 4000)
  private String content;

  private Integer favoriteCount;

  private Integer viewCount;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "email")
  private User user;

  private Integer commentCount;

  @Builder
  public Post(Long id, String title, String content, Integer favoriteCount,
      Integer viewCount, User user,
      Integer commentCount) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.favoriteCount = favoriteCount;
    this.viewCount = viewCount;
    this.user = user;
    this.commentCount = commentCount;
  }

  public void increaseViewCount() {
    this.viewCount++;
  }
  public void increaseFavoriteCount() {
    this.favoriteCount++;
  }
  public void decreaseFavoriteCount() {
    if (this.favoriteCount <= 0) {
      throw new BusinessException(favoriteCount, "favoriteCount", ErrorCode.FAVORITE_CANCEL);
    }
    this.favoriteCount--;
  }
  public void increaseCommentCount() {
    this.commentCount++;
  }
  public void decreaseCommentCount() {
    this.commentCount--;
  }

  public void editPost(String title, String content) {
    this.title = title;
    this.content = content;
  }
}