package com.zoo.boardback.domain.board.api;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.zoo.boardback.domain.ApiResponse;
import com.zoo.boardback.domain.auth.details.CustomUserDetails;
import com.zoo.boardback.domain.board.application.BoardService;
import com.zoo.boardback.domain.board.dto.request.PostCreateRequestDto;
import com.zoo.boardback.domain.board.dto.response.PostDetailResponseDto;
import com.zoo.boardback.domain.favorite.application.FavoriteService;
import com.zoo.boardback.domain.favorite.dto.response.FavoriteListResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/board")
public class BoardController {

  private final BoardService boardService;
  private final FavoriteService favoriteService;

  @PostMapping("")
  public ApiResponse<Void> createBoard(
      @RequestBody @Valid PostCreateRequestDto requestDto,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    boardService.create(requestDto, userDetails.getUsername());
    return ApiResponse.ok(null);
  }

  @GetMapping("/{boardNumber}")
  public ApiResponse<PostDetailResponseDto> getPost(
      @PathVariable int boardNumber
  ) {
    PostDetailResponseDto postDetailResponseDto = boardService.find(boardNumber);
    return ApiResponse.ok(postDetailResponseDto);
  }

  @PutMapping("/{boardNumber}/favorite")
  public ApiResponse<Void> putFavorite(
      @PathVariable int boardNumber,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
      favoriteService.putFavorite(boardNumber, userDetails.getUsername());
      return ApiResponse.ok(null);
  }

  @DeleteMapping("/{boardNumber}")
  public ApiResponse<Void> deletePost(
      @PathVariable int boardNumber,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    boardService.deletePost(boardNumber, userDetails.getUsername());
    return ApiResponse.of(NO_CONTENT, null);
  }

  @GetMapping("/{boardNumber}/favorite-list")
  public ApiResponse<FavoriteListResponseDto> getFavoriteList(
      @PathVariable int boardNumber
  ) {
    return ApiResponse.ok(favoriteService.getFavoriteList(boardNumber));
  }
}
