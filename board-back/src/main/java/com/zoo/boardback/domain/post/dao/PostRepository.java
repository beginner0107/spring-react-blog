package com.zoo.boardback.domain.post.dao;

import com.zoo.boardback.domain.post.entity.Post;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

  @EntityGraph(attributePaths = "user")
  Optional<Post> findById(Long id);

  @Query("select p.viewCount from Post p where p.id = :id")
  Long findViewCount(@Param("id") Long id);

  @Transactional
  @Modifying
  @Query("update Post p set p.viewCount = :viewCount where p.id = :id")
  void applyViewCntToRDB(@Param("id") Long id, @Param("viewCount") Long viewCount);
}
