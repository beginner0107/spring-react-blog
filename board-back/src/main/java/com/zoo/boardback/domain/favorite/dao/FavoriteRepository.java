package com.zoo.boardback.domain.favorite.dao;

import com.zoo.boardback.domain.board.entity.Board;
import com.zoo.boardback.domain.favorite.dto.query.FavoriteQueryDto;
import com.zoo.boardback.domain.favorite.entity.Favorite;
import com.zoo.boardback.domain.favorite.entity.primaryKey.FavoritePk;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FavoriteRepository extends JpaRepository<Favorite, FavoritePk> {

  Favorite findByFavoritePk(FavoritePk favoritePk);

  @Query("SELECT new com.zoo.boardback.domain.favorite.dto.query.FavoriteQueryDto(u.email, u.nickname, u.profileImage) " +
      "FROM Favorite f " +
      "JOIN f.favoritePk.user u " +
      "WHERE f.favoritePk.board = :board " +
      "ORDER BY f.createdAt DESC"
  )
  List<FavoriteQueryDto> findRecommendersByBoard(@Param("board") Board board);

  @Modifying
  @Query("DELETE FROM Favorite f WHERE f.favoritePk.board = :board")
  void deleteByBoard(@Param("board") Board board);
}
