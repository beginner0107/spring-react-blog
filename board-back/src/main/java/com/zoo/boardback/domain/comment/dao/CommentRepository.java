package com.zoo.boardback.domain.comment.dao;

import com.zoo.boardback.domain.board.entity.Board;
import com.zoo.boardback.domain.comment.dto.query.CommentQueryDto;
import com.zoo.boardback.domain.comment.entity.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  @Query("SELECT " +
      "A " +
      "FROM Comment A " +
      "JOIN FETCH A.user " +
      "WHERE A.board = :board " +
      "ORDER BY A.createdAt DESC")
  List<Comment> getCommentsList(@Param("board") Board board);

  @Modifying
  @Query("DELETE FROM Comment i WHERE i.board = :board")
  void deleteByBoard(@Param("board") Board board);
}
