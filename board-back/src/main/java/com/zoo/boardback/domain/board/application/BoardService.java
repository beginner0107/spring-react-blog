package com.zoo.boardback.domain.board.application;

import static com.zoo.boardback.global.error.ErrorCode.USER_NOT_FOUND;
import static java.util.stream.Collectors.*;

import com.zoo.boardback.domain.board.dao.BoardRepository;
import com.zoo.boardback.domain.board.dto.request.PostCreateRequestDto;
import com.zoo.boardback.domain.board.entity.Board;
import com.zoo.boardback.domain.image.dao.ImageRepository;
import com.zoo.boardback.domain.image.entity.Image;
import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.domain.user.entity.User;
import com.zoo.boardback.global.error.BusinessException;
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
}
