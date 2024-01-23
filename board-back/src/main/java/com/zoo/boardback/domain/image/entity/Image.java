package com.zoo.boardback.domain.image.entity;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.zoo.boardback.domain.post.entity.Post;
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
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class Image extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "postId")
  private Post post;

  private String imageUrl;

  private Boolean titleImageYn;

  @Builder
  public Image(
      Post post,
      String imageUrl,
      Boolean titleImageYn
  ) {
    this.post = post;
    this.imageUrl = imageUrl;
    this.titleImageYn = titleImageYn;
  }
}
