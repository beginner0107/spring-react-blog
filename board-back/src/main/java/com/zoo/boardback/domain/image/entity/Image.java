package com.zoo.boardback.domain.image.entity;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.zoo.boardback.domain.board.entity.Board;
import com.zoo.boardback.global.entity.BaseEntity;
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
@Table(name = "Images")
@EqualsAndHashCode(of = {"imageId"}, callSuper = false)
public class Image extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long imageId;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "boardNumber")
  private Board board;

  private String imageUrl;

  @Builder
  public Image(Long imageId, Board board, String imageUrl) {
    this.imageId = imageId;
    this.board = board;
    this.imageUrl = imageUrl;
  }
}
