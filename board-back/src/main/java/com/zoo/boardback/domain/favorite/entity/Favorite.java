package com.zoo.boardback.domain.favorite.entity;

import static lombok.AccessLevel.PROTECTED;

import com.zoo.boardback.domain.favorite.entity.primaryKey.FavoritePk;
import com.zoo.boardback.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "Favorite")
public class Favorite extends BaseEntity {

  @EmbeddedId
  private FavoritePk favoritePk;

  @Builder
  public Favorite(FavoritePk favoritePk) {
    this.favoritePk = favoritePk;
  }
}
