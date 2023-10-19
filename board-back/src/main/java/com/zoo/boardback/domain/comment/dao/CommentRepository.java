package com.zoo.boardback.domain.comment.dao;

import com.zoo.boardback.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

}
