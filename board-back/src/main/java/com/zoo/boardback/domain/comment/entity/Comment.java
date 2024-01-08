package com.zoo.boardback.domain.comment.entity;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.zoo.boardback.domain.board.entity.Board;
import com.zoo.boardback.domain.user.entity.User;
import jakarta.persistence.CascadeType;
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
@Table(name = "Comment")
public class Comment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int commentNumber;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @ManyToOne(fetch = LAZY, cascade = CascadeType.REMOVE)
  @JoinColumn(name = "boardNumber")
  private Board board;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "email")
  private User user;

  @CreatedDate
  @Column(nullable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @Builder
  public Comment(int commentNumber, String content, Board board, User user, LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.commentNumber = commentNumber;
    this.content = content;
    this.board = board;
    this.user = user;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
}
