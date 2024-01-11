package com.zoo.boardback.domain.comment.dao;

import com.zoo.boardback.domain.board.entity.Board;
import com.zoo.boardback.domain.comment.dto.query.CommentQueryDto;
import com.zoo.boardback.domain.comment.entity.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

  @Query("SELECT " +
      "new com.zoo.boardback.domain.comment.dto.query.CommentQueryDto(A.commentNumber, B.nickname, B.profileImage, A.content, A.createdAt, A.updatedAt) " +
      "FROM Comment A " +
      "INNER JOIN A.user B " +
      "WHERE A.board = :board " +
      "ORDER BY A.createdAt DESC")
  List<CommentQueryDto> getCommentsList(@Param("board") Board board);

  @Modifying
  @Query("DELETE FROM Comment i WHERE i.board = :board")
  void deleteByBoard(@Param("board") Board board);
}
