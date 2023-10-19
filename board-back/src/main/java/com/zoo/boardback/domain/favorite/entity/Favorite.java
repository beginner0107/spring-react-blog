package com.zoo.boardback.domain.favorite.entity;

import static lombok.AccessLevel.PROTECTED;

import com.zoo.boardback.domain.favorite.entity.primaryKey.FavoritePk;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
@Table(name = "Favorite")
public class Favorite {

  @EmbeddedId
  private FavoritePk favoritePk;

  @CreatedDate
  @Column(nullable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @Builder
  public Favorite(FavoritePk favoritePk, LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.favoritePk = favoritePk;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
}
