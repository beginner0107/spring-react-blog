package com.zoo.boardback.domain.comment.dao;

import com.zoo.boardback.domain.board.entity.Board;
import com.zoo.boardback.domain.comment.dto.query.CommentQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface CommentRepositoryCustom {
  Page<CommentQueryDto> getCommentsList(@Param("board") Board board, Pageable pageable);

}
