package com.zoo.boardback.domain.board.entity;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.zoo.boardback.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "Board")
public class Board {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int boardNumber;

  private String title;

  private String content;

  private Integer favoriteCount;

  private Integer viewCount;

  @CreatedDate
  @Column(nullable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "email")
  private User user;

  private Integer commentCount;

  @Builder
  public Board(int boardNumber, String title, String content, Integer favoriteCount,
      Integer viewCount, LocalDateTime createdAt, LocalDateTime updatedAt, User user,
      Integer commentCount) {
    this.boardNumber = boardNumber;
    this.title = title;
    this.content = content;
    this.favoriteCount = favoriteCount;
    this.viewCount = viewCount;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
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
    this.favoriteCount--;
  }
}