package com.zoo.boardback.domain.comment.application;

import static com.zoo.boardback.global.error.ErrorCode.BOARD_NOT_CUD_MATCHING_USER;
import static com.zoo.boardback.global.error.ErrorCode.BOARD_NOT_FOUND;
import static com.zoo.boardback.global.error.ErrorCode.COMMENT_NOT_CUD_MATCHING_USER;
import static com.zoo.boardback.global.error.ErrorCode.COMMENT_NOT_FOUND;

import com.zoo.boardback.domain.board.dao.BoardRepository;
import com.zoo.boardback.domain.board.entity.Board;
import com.zoo.boardback.domain.comment.dao.CommentRepository;
import com.zoo.boardback.domain.comment.dto.query.CommentQueryDto;
import com.zoo.boardback.domain.comment.dto.request.CommentCreateRequestDto;
import com.zoo.boardback.domain.comment.dto.request.CommentUpdateRequestDto;
import com.zoo.boardback.domain.comment.dto.response.CommentListResponseDto;
import com.zoo.boardback.domain.comment.entity.Comment;
import com.zoo.boardback.domain.user.entity.User;
import com.zoo.boardback.global.error.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

  private final BoardRepository boardRepository;
  private final CommentRepository commentRepository;

  @Transactional
  public void create(User user, CommentCreateRequestDto commentRequestDto) {
    Long boardNumber = commentRequestDto.getBoardNumber();
    Board board = boardRepository.findById(boardNumber).orElseThrow(
        () -> new BusinessException(boardNumber, "boardNumber", BOARD_NOT_FOUND));
    board.increaseCommentCount();
    commentRepository.save(commentRequestDto.toEntity(user, board));
  }

  public CommentListResponseDto getComments(Long boardNumber) {
    Board board = boardRepository.findById(boardNumber).orElseThrow(
        () -> new BusinessException(boardNumber, "boardNumber", BOARD_NOT_FOUND));
    List<CommentQueryDto> comments = commentRepository.getCommentsList(board);
    return CommentListResponseDto.from(comments);
  }

  @Transactional
  public void editComment(Long commentNumber, CommentUpdateRequestDto commentUpdateRequestDto) {
    Comment comment = commentRepository.findById(commentNumber).orElseThrow(
        () -> new BusinessException(commentNumber, "commentNumber", COMMENT_NOT_FOUND));
    comment.editComment(commentUpdateRequestDto);
  }

  @Transactional
  public void deleteComment(Long commentNumber, String email) {
    Comment comment = commentRepository.findById(commentNumber).orElseThrow(
        () -> new BusinessException(commentNumber, "commentNumber", COMMENT_NOT_FOUND));
    Board board = comment.getBoard();
    isCommentWriterMatches(email, comment);
    board.decreaseCommentCount();
    commentRepository.deleteById(commentNumber);
  }

  private void isCommentWriterMatches(String email, Comment comment) {
    if (!comment.getUser().getEmail().equals(email)) {
      throw new BusinessException(comment, "comment", COMMENT_NOT_CUD_MATCHING_USER);
    }
  }
}
