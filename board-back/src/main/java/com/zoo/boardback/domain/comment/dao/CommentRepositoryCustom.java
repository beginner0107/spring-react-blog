package com.zoo.boardback.domain.comment.dao;

import com.zoo.boardback.domain.post.entity.Post;
import com.zoo.boardback.domain.comment.dto.query.CommentQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface CommentRepositoryCustom {
  Page<CommentQueryDto> getCommentsList(@Param("post") Post post, Pageable pageable);

}
