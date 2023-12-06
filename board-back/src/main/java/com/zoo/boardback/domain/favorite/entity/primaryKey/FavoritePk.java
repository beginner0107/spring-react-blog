package com.zoo.boardback.domain.favorite.entity.primaryKey;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.*;
import static lombok.AccessLevel.PROTECTED;

import com.zoo.boardback.domain.board.entity.Board;
import com.zoo.boardback.domain.user.entity.User;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Embeddable
public class FavoritePk  {

  @ManyToOne(fetch = LAZY, cascade = ALL)
  @JoinColumn(name = "boardNumber")
  private Board board;

  @OneToOne(fetch = LAZY, cascade = ALL)
  @JoinColumn(name = "email")
  private User user;
}
