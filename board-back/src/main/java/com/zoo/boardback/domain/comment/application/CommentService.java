package com.zoo.boardback.domain.comment.application;

import static com.zoo.boardback.global.error.ErrorCode.BOARD_NOT_FOUND;
import static com.zoo.boardback.global.error.ErrorCode.COMMENT_NOT_CUD_MATCHING_USER;
import static com.zoo.boardback.global.error.ErrorCode.COMMENT_NOT_FOUND;

import com.zoo.boardback.domain.post.dao.PostRepository;
import com.zoo.boardback.domain.post.entity.Post;
import com.zoo.boardback.domain.comment.dao.CommentRepository;
import com.zoo.boardback.domain.comment.dto.query.CommentQueryDto;
import com.zoo.boardback.domain.comment.dto.request.CommentCreateRequestDto;
import com.zoo.boardback.domain.comment.dto.request.CommentUpdateRequestDto;
import com.zoo.boardback.domain.comment.dto.response.CommentListResponseDto;
import com.zoo.boardback.domain.comment.entity.Comment;
import com.zoo.boardback.domain.user.entity.User;
import com.zoo.boardback.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

  private final PostRepository postRepository;
  private final CommentRepository commentRepository;

  @Transactional
  public void create(User user, CommentCreateRequestDto commentRequestDto) {
    Long postId = commentRequestDto.getPostId();
    Post post = postRepository.findById(postId).orElseThrow(
        () -> new BusinessException(postId, "postId", BOARD_NOT_FOUND));
    post.increaseCommentCount();
    commentRepository.save(commentRequestDto.toEntity(user, post));
  }

  public CommentListResponseDto getComments(Long postId, Pageable pageable) {
    Post post = postRepository.findById(postId).orElseThrow(
        () -> new BusinessException(postId, "postId", BOARD_NOT_FOUND));
    Page<CommentQueryDto> comments = commentRepository.getCommentsList(post, pageable);
    return CommentListResponseDto.from(comments);
  }

  @Transactional
  public void editComment(String email, Long commentId, CommentUpdateRequestDto commentUpdateRequestDto) {
    Comment comment = commentRepository.findById(commentId).orElseThrow(
        () -> new BusinessException(commentId, "commentId", COMMENT_NOT_FOUND));
    verifyCommentOwnership(email, comment);
    comment.editComment(commentUpdateRequestDto);
  }

  @Transactional
  public void deleteComment(Long commentId, String email) {
    Comment comment = commentRepository.findById(commentId).orElseThrow(
        () -> new BusinessException(commentId, "commentId", COMMENT_NOT_FOUND));
    Post post = comment.getPost();
    verifyCommentOwnership(email, comment);
    post.decreaseCommentCount();
    commentRepository.deleteById(commentId);
  }

  private void verifyCommentOwnership(String email, Comment comment) {
    if (!comment.getUser().getEmail().equals(email)) {
      throw new BusinessException(comment, "comment", COMMENT_NOT_CUD_MATCHING_USER);
    }
  }
}
