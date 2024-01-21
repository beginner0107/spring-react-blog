package com.zoo.boardback.domain.board.api;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.zoo.boardback.domain.ApiResponse;
import com.zoo.boardback.domain.auth.details.CustomUserDetails;
import com.zoo.boardback.domain.board.application.BoardService;
import com.zoo.boardback.domain.board.dto.request.PostCreateRequestDto;
import com.zoo.boardback.domain.board.dto.request.PostSearchCondition;
import com.zoo.boardback.domain.board.dto.request.PostUpdateRequestDto;
import com.zoo.boardback.domain.board.dto.response.PostDetailResponseDto;
import com.zoo.boardback.domain.board.dto.response.PostSearchResponseDto;
import com.zoo.boardback.domain.board.dto.response.PostsTop3ResponseDto;
import com.zoo.boardback.domain.favorite.application.FavoriteService;
import com.zoo.boardback.domain.favorite.dto.response.FavoriteListResponseDto;
import jakarta.validation.Valid;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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

  @PostMapping
  public ApiResponse<Void> createPost(
      @RequestBody @Valid PostCreateRequestDto requestDto,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    String email = userDetails.getUsername();
    boardService.create(requestDto, email);
    return ApiResponse.ok(null);
  }

  @GetMapping
  public ApiResponse<Page<PostSearchResponseDto>> getPosts(
      @PageableDefault(size = 5, sort = "createdAt", direction = Direction.DESC) Pageable pageable,
      PostSearchCondition condition
  ) {
    Page<PostSearchResponseDto> posts = boardService.searchPosts(condition, pageable);
    return ApiResponse.ok(posts);
  }

  @GetMapping("/{boardNumber}")
  public ApiResponse<PostDetailResponseDto> getPost(
      @PathVariable Long boardNumber
  ) {
    PostDetailResponseDto postDetailResponseDto = boardService.find(boardNumber);
    return ApiResponse.ok(postDetailResponseDto);
  }

  @PutMapping("/{boardNumber}/favorite")
  public ApiResponse<Void> putFavorite(
      @PathVariable Long boardNumber,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    String email = userDetails.getUsername();
    favoriteService.putFavorite(boardNumber, email);
    return ApiResponse.ok(null);
  }

  @PutMapping("/{boardNumber}")
  public ApiResponse<Void> editPost(
      @PathVariable Long boardNumber,
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody @Valid PostUpdateRequestDto postUpdateRequestDto
  ) {
    String email = userDetails.getUsername();
    boardService.editPost(boardNumber, email, postUpdateRequestDto);
    return ApiResponse.ok(null);
  }

  @DeleteMapping("/{boardNumber}")
  public ApiResponse<Void> deletePost(
      @PathVariable Long boardNumber,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    String email = userDetails.getUsername();
    boardService.deletePost(boardNumber, email);
    return ApiResponse.of(NO_CONTENT, null);
  }

  @GetMapping("/{boardNumber}/favorite-list")
  public ApiResponse<FavoriteListResponseDto> getFavoriteList(
      @PathVariable Long boardNumber
  ) {
    return ApiResponse.ok(favoriteService.getFavoriteList(boardNumber));
  }

  @GetMapping("/top3")
  public ApiResponse<PostsTop3ResponseDto> getPostsTop3() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime startDate = getStartOfWeek(now);
    LocalDateTime endDate = getEndOfWeek(now);
    PostsTop3ResponseDto posts = boardService.getTop3Posts(startDate, endDate);
    return ApiResponse.ok(posts);
  }

  private LocalDateTime getStartOfWeek(LocalDateTime dateTime) {
    return dateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).truncatedTo(ChronoUnit.DAYS);
  }

  private LocalDateTime getEndOfWeek(LocalDateTime dateTime) {
    return dateTime.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).with(LocalTime.MAX);
  }
}
