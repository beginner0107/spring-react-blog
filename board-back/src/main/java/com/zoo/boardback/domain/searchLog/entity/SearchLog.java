package com.zoo.boardback.domain.searchLog.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "search_log")
public class SearchLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int sequence;

  private String searchWord;

  private String relationWord;

  private boolean relation;

  @CreatedDate
  @Column(nullable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @Builder
  public SearchLog(int sequence, String searchWord, String relationWord, boolean relation,
      LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.sequence = sequence;
    this.searchWord = searchWord;
    this.relationWord = relationWord;
    this.relation = relation;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
}

