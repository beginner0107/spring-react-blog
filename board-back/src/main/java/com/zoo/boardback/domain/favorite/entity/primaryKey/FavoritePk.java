package com.zoo.boardback.domain.favorite.entity.primaryKey;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.zoo.boardback.domain.board.entity.Board;
import com.zoo.boardback.domain.user.entity.User;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Embeddable
public class FavoritePk implements Serializable {

  @ManyToOne(fetch = LAZY, cascade = ALL)
  @JoinColumn(name = "boardNumber")
  private Board board;

  @OneToOne(fetch = LAZY, cascade = ALL)
  @JoinColumn(name = "id")
  private User user;

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }
    FavoritePk that = (FavoritePk) object;
    return Objects.equals(board, that.board) && Objects.equals(user, that.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(board, user);
  }
}
