package com.zoo.boardback.domain.comment.dao;

import com.zoo.boardback.domain.comment.dto.query.ChildCommentQueryDto;
import com.zoo.boardback.domain.comment.entity.Comment;
import com.zoo.boardback.domain.post.entity.Post;
import com.zoo.boardback.domain.comment.dto.query.CommentQueryDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface CommentRepositoryCustom {

    Page<CommentQueryDto> getComments(@Param("post") Post post, Pageable pageable);

    List<ChildCommentQueryDto> getChildComments(Long postId, Long parentId);
}
