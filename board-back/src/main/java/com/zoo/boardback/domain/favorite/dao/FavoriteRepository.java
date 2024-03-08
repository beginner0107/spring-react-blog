package com.zoo.boardback.domain.favorite.dao;

import com.zoo.boardback.domain.post.entity.Post;
import com.zoo.boardback.domain.favorite.entity.Favorite;
import com.zoo.boardback.domain.favorite.entity.primaryKey.FavoritePk;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FavoriteRepository extends JpaRepository<Favorite, FavoritePk> {

    Optional<Favorite> findByFavoritePk(FavoritePk favoritePk);

    @Query("SELECT f " +
        "FROM Favorite f " +
        "JOIN FETCH f.favoritePk.user u " +
        "WHERE f.favoritePk.post = :post " +
        "ORDER BY f.createdAt DESC"
    )
    List<Favorite> findFavoritesByPost(@Param("post") Post post);

    @Modifying
    @Query("DELETE FROM Favorite f WHERE f.favoritePk.post = :post")
    void deleteByPost(@Param("post") Post post);
}
