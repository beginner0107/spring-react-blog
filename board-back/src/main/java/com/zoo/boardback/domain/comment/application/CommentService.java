package com.zoo.boardback.domain.comment.application;

import static com.zoo.boardback.global.error.ErrorCode.BOARD_NOT_FOUND;

import com.zoo.boardback.domain.board.dao.BoardRepository;
import com.zoo.boardback.domain.board.entity.Board;
import com.zoo.boardback.domain.comment.dao.CommentRepository;
import com.zoo.boardback.domain.comment.dto.request.CommentCreateRequestDto;
import com.zoo.boardback.domain.comment.dto.response.CommentListResponseDto;
import com.zoo.boardback.domain.comment.dto.response.CommentResponse;
import com.zoo.boardback.domain.comment.entity.Comment;
import com.zoo.boardback.domain.user.entity.User;
import com.zoo.boardback.global.error.BusinessException;
import java.util.List;
import java.util.stream.Collectors;
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
    int boardNumber = commentRequestDto.getBoardNumber();
    Board board = boardRepository.findById(boardNumber).orElseThrow(
        () -> new BusinessException(boardNumber, "boardNumber", BOARD_NOT_FOUND));
    board.increaseCommentCount();
    commentRepository.save(commentRequestDto.toEntity(user, board));
  }

  public CommentListResponseDto getComments(int boardNumber) {
    Board board = boardRepository.findById(boardNumber).orElseThrow(
        () -> new BusinessException(boardNumber, "boardNumber", BOARD_NOT_FOUND));
    List<CommentResponse> comments = commentRepository.getCommentsList(board);
    return CommentListResponseDto.from(comments);
  }

  public void editComment(int commentNumber) {
  }

  public void deleteComment(int commentNumber) {
  }
}
