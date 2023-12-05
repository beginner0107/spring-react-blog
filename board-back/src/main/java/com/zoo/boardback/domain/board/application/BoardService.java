package com.zoo.boardback.domain.board.application;

import static com.zoo.boardback.global.error.ErrorCode.BOARD_NOT_FOUND;
import static com.zoo.boardback.global.error.ErrorCode.USER_NOT_FOUND;
import static java.util.stream.Collectors.toList;

import com.zoo.boardback.domain.board.dao.BoardRepository;
import com.zoo.boardback.domain.board.dto.request.PostCreateRequestDto;
import com.zoo.boardback.domain.board.dto.response.PostDetailResponseDto;
import com.zoo.boardback.domain.board.entity.Board;
import com.zoo.boardback.domain.image.dao.ImageRepository;
import com.zoo.boardback.domain.image.entity.Image;
import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.domain.user.entity.User;
import com.zoo.boardback.global.error.BusinessException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

  private final BoardRepository boardRepository;
  private final UserRepository userRepository;
  private final ImageRepository imageRepository;

  @Transactional
  public void create(PostCreateRequestDto request, String email) {
    User user = userRepository.findByEmail(email).orElseThrow(() ->
        new BusinessException(email, "email", USER_NOT_FOUND));

    Board board = request.toEntity(user);
    boardRepository.save(board);

    List<String> boardImageList = request.getBoardImageList();
    List<Image> images = boardImageList.stream()
        .map(image -> Image.builder()
            .board(board)
            .imageUrl(image)
            .build())
        .collect(toList());
    imageRepository.saveAll(images);
  }

  @Transactional
  public PostDetailResponseDto find(int boardNumber) {
    Board board = boardRepository.findByBoardNumber(boardNumber).orElseThrow(() ->
        new BusinessException(boardNumber, "boardNumber", BOARD_NOT_FOUND));

    board.increaseViewCount();
    boardRepository.save(board);
    List<Image> imageList = imageRepository.findByBoard(board);
    List<String> boardImageList = new ArrayList<>();
    for (Image image : imageList) {
      String imageUrl = image.getImageUrl();
      boardImageList.add(imageUrl);
    }
    return PostDetailResponseDto.of(board, boardImageList);
  }
}
