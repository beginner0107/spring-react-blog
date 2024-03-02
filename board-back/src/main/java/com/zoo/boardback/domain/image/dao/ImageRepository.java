package com.zoo.boardback.domain.image.dao;

import com.zoo.boardback.domain.post.entity.Post;
import com.zoo.boardback.domain.image.entity.Image;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByPost(Post post);

    @Modifying
    @Query("DELETE FROM Image i WHERE i.post = :post")
    void deleteByBoard(@Param("post") Post post);
}
