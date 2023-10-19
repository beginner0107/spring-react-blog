package com.zoo.boardback.domain.favorite.entity.primaryKey;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.zoo.boardback.domain.board.entity.Board;
import com.zoo.boardback.domain.user.entity.User;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class FavoritePk  {

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "boardNumber")
  private Board board;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "email")
  private User user;

  @Builder
  public FavoritePk(Board board, User user) {
    this.board = board;
    this.user = user;
  }
}
