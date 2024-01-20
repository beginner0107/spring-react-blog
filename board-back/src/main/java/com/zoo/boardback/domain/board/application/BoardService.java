package com.zoo.boardback.domain.board.application;

import static com.zoo.boardback.global.error.ErrorCode.BOARD_NOT_CUD_MATCHING_USER;
import static com.zoo.boardback.global.error.ErrorCode.BOARD_NOT_FOUND;
import static com.zoo.boardback.global.error.ErrorCode.USER_NOT_FOUND;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.hasText;

import com.zoo.boardback.domain.board.dao.BoardRepository;
import com.zoo.boardback.domain.board.dto.request.PostCreateRequestDto;
import com.zoo.boardback.domain.board.dto.request.PostSearchCondition;
import com.zoo.boardback.domain.board.dto.request.PostUpdateRequestDto;
import com.zoo.boardback.domain.board.dto.response.PostDetailResponseDto;
import com.zoo.boardback.domain.board.dto.response.PostSearchResponseDto;
import com.zoo.boardback.domain.board.entity.Board;
import com.zoo.boardback.domain.comment.dao.CommentRepository;
import com.zoo.boardback.domain.favorite.dao.FavoriteRepository;
import com.zoo.boardback.domain.image.dao.ImageRepository;
import com.zoo.boardback.domain.image.entity.Image;
import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.domain.user.entity.User;
import com.zoo.boardback.global.error.BusinessException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

  private final BoardRepository boardRepository;
  private final UserRepository userRepository;
  private final ImageRepository imageRepository;
  private final CommentRepository commentRepository;
  private final FavoriteRepository favoriteRepository;

  @Transactional
  public void create(PostCreateRequestDto request, String email) {
    User user = userRepository.findByEmail(email).orElseThrow(() ->
        new BusinessException(email, "email", USER_NOT_FOUND));

    Board board = request.toEntity(user);
    boardRepository.save(board);

    String boardTitleImage = request.getBoardTitleImage();
    if (hasText(boardTitleImage)) {
      imageRepository.save(Image.builder()
          .imageUrl(boardTitleImage)
          .board(board)
          .titleImageYn(true)
          .build()
      );
    }
    List<String> boardImageList = request.getBoardImageList();
    if (!boardImageList.isEmpty()) {
      saveImages(boardImageList, board);
    }
  }

  public Page<PostSearchResponseDto> searchPosts(PostSearchCondition condition, Pageable pageable) {
    return boardRepository.searchPosts(condition, pageable);
  }

  @Transactional
  public PostDetailResponseDto find(Long boardNumber) {
    Board board = boardRepository.findByBoardNumber(boardNumber).orElseThrow(() ->
        new BusinessException(boardNumber, "boardNumber", BOARD_NOT_FOUND));

    board.increaseViewCount();
    List<String> boardImageList = findBoardImages(board);
    return PostDetailResponseDto.of(board, boardImageList);
  }

  @Transactional
  public void editPost(Long boardNumber, String email, PostUpdateRequestDto requestDto) {
    Board board = boardRepository.findByBoardNumber(boardNumber).orElseThrow(() ->
        new BusinessException(boardNumber, "boardNumber", BOARD_NOT_FOUND));
    isPostWriterMatches(email, board);
    board.editPost(requestDto.getTitle(), requestDto.getContent());

    List<String> boardImageList = requestDto.getBoardImageList();
    List<Image> imageEntities = new ArrayList<>();

    editImages(board, boardImageList, imageEntities);
  }

  @Transactional
  public void deletePost(Long boardNumber, String email) {
    Board board = boardRepository.findByBoardNumber(boardNumber).orElseThrow(() ->
        new BusinessException(boardNumber, "boardNumber", BOARD_NOT_FOUND));
    isPostWriterMatches(email, board);
    imageRepository.deleteByBoard(board);
    commentRepository.deleteByBoard(board);
    favoriteRepository.deleteByBoard(board);
    boardRepository.delete(board);
  }

  private void saveImages(List<String> boardImageList, Board board) {
    List<Image> images = boardImageList.stream()
        .map(image -> Image.builder()
            .board(board)
            .imageUrl(image)
            .titleImageYn(false)
            .build())
        .collect(toList());
    imageRepository.saveAll(images);
  }

  private void isPostWriterMatches(String email, Board board) {
    if (!board.getUser().getEmail().equals(email)) {
      throw new BusinessException(email, "email", BOARD_NOT_CUD_MATCHING_USER);
    }
  }

  private List<String> findBoardImages(Board board) {
    List<Image> imageList = imageRepository.findByBoard(board);
    List<String> boardImageList = new ArrayList<>();
    for (Image image : imageList) {
      String imageUrl = image.getImageUrl();
      boardImageList.add(imageUrl);
    }
    return boardImageList;
  }

  private void editImages(Board board, List<String> boardImageList, List<Image> imageEntities) {
    imageRepository.deleteByBoard(board);
    for (String image : boardImageList) {
      Image imageEntity = Image.builder()
          .board(board)
          .imageUrl(image)
          .build();
      imageEntities.add(imageEntity);
    }
    imageRepository.saveAll(imageEntities);
  }
}
