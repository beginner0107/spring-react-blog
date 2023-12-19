package com.zoo.boardback.domain.favorite.application;

import static com.zoo.boardback.global.error.ErrorCode.BOARD_NOT_FOUND;
import static com.zoo.boardback.global.error.ErrorCode.USER_NOT_FOUND;

import com.zoo.boardback.domain.board.dao.BoardRepository;
import com.zoo.boardback.domain.board.entity.Board;
import com.zoo.boardback.domain.favorite.dao.FavoriteRepository;
import com.zoo.boardback.domain.favorite.dto.query.FavoriteQueryDto;
import com.zoo.boardback.domain.favorite.dto.response.FavoriteListResponseDto;
import com.zoo.boardback.domain.favorite.entity.Favorite;
import com.zoo.boardback.domain.favorite.entity.primaryKey.FavoritePk;
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
public class FavoriteService {

  private final FavoriteRepository favoriteRepository;
  private final BoardRepository boardRepository;
  private final UserRepository userRepository;

  @Transactional
  public void putFavorite(int boardNumber, String email) {
    Board board = boardRepository.findByBoardNumber(boardNumber).orElseThrow(() ->
        new BusinessException(boardNumber, "boardNumber", BOARD_NOT_FOUND));

    User user = userRepository.findByEmail(email).orElseThrow(() ->
        new BusinessException(email, "email", USER_NOT_FOUND));
    FavoritePk favoritePk = new FavoritePk(board, user);
    Favorite favorite = favoriteRepository.findByFavoritePk(favoritePk);
    if (favorite == null) {
      favorite = Favorite.builder()
          .favoritePk(favoritePk)
          .build();
      favoriteRepository.save(favorite);
      board.increaseFavoriteCount();
    } else {
      favoriteRepository.delete(favorite);
      board.decreaseFavoriteCount();
    }
  }

  public FavoriteListResponseDto getFavoriteList(int boardNumber) {
    Board board = boardRepository.findByBoardNumber(boardNumber).orElseThrow(() ->
        new BusinessException(boardNumber, "boardNumber", BOARD_NOT_FOUND));
    List<FavoriteQueryDto> favoriteList = favoriteRepository.findRecommendersByBoard(board);
    return FavoriteListResponseDto.from(favoriteList);
  }
}
