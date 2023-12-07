package com.zoo.boardback.domain.favorite.dao;

import com.zoo.boardback.domain.favorite.entity.Favorite;
import com.zoo.boardback.domain.favorite.entity.primaryKey.FavoritePk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, FavoritePk> {

  Favorite findByFavoritePk(FavoritePk favoritePk);
}
