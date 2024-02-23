package com.zoo.boardback.domain.comment.application;

import static com.zoo.boardback.global.error.ErrorCode.POST_NOT_FOUND;
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
  public void create(User user, CommentCreateRequestDto requestDto) {
    Long postId = requestDto.getPostId();
    Post post = postRepository.findById(postId).orElseThrow(
        () -> new BusinessException(postId, "postId", POST_NOT_FOUND));
    post.increaseCommentCount();
    commentRepository.save(requestDto.toEntity(user, post));
  }

  public CommentListResponseDto getComments(Long postId, Pageable pageable) {
    Post post = postRepository.findById(postId).orElseThrow(
        () -> new BusinessException(postId, "postId", POST_NOT_FOUND));
    Page<CommentQueryDto> comments = commentRepository.getComments(post, pageable);
    return CommentListResponseDto.from(comments);
  }

  @Transactional
  public void update(String email, Long commentId, CommentUpdateRequestDto requestDto) {
    Comment comment = commentRepository.findById(commentId).orElseThrow(
        () -> new BusinessException(commentId, "commentId", COMMENT_NOT_FOUND));
    checkCommenterMatching(email, comment);
    comment.editComment(requestDto);
  }

  @Transactional
  public void delete(Long commentId, String email) {
    Comment comment = commentRepository.findById(commentId).orElseThrow(
        () -> new BusinessException(commentId, "commentId", COMMENT_NOT_FOUND));
    Post post = comment.getPost();
    checkCommenterMatching(email, comment);
    post.decreaseCommentCount();
    commentRepository.deleteById(commentId);
  }

  private void checkCommenterMatching(String email, Comment comment) {
    if (!comment.getUser().getEmail().equals(email)) {
      throw new BusinessException(comment, "comment", COMMENT_NOT_CUD_MATCHING_USER);
    }
  }
}
