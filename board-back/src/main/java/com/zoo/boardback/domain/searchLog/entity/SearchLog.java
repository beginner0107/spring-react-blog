package com.zoo.boardback.domain.searchLog.entity;

import static lombok.AccessLevel.PROTECTED;

import com.zoo.boardback.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "search_log")
@EqualsAndHashCode(of = {"sequence"}, callSuper = false)
public class SearchLog extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long sequence;

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
  public SearchLog(Long sequence, String searchWord, String relationWord, boolean relation
  ) {
    this.sequence = sequence;
    this.searchWord = searchWord;
    this.relationWord = relationWord;
    this.relation = relation;
  }
}

