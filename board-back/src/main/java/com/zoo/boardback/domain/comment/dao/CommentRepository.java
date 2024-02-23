package com.zoo.boardback.domain.comment.dao;

import com.zoo.boardback.domain.post.entity.Post;
import com.zoo.boardback.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

  @Modifying
  @Query("DELETE FROM Comment i WHERE i.post = :post")
  void deleteByPost(@Param("post") Post post);
}
