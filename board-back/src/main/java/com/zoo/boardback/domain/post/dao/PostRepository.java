package com.zoo.boardback.domain.post.dao;

import com.zoo.boardback.domain.post.entity.Post;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

  @EntityGraph(attributePaths = "user")
  Optional<Post> findById(Long id);
}
