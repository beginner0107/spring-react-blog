package com.zoo.boardback.domain.post.api;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.zoo.boardback.domain.ApiResponse;
import com.zoo.boardback.domain.auth.details.CustomUserDetails;
import com.zoo.boardback.domain.favorite.application.FavoriteService;
import com.zoo.boardback.domain.favorite.dto.response.FavoriteListResponseDto;
import com.zoo.boardback.domain.post.application.PostService;
import com.zoo.boardback.domain.post.dto.request.PostCreateRequestDto;
import com.zoo.boardback.domain.post.dto.request.PostSearchCondition;
import com.zoo.boardback.domain.post.dto.request.PostUpdateRequestDto;
import com.zoo.boardback.domain.post.dto.response.PostDetailResponseDto;
import com.zoo.boardback.domain.post.dto.response.PostSearchResponseDto;
import com.zoo.boardback.domain.post.dto.response.PostsTop3ResponseDto;
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
@RequestMapping("/api/v1/post")
public class PostController {

  private final PostService postService;
  private final FavoriteService favoriteService;

  @PostMapping
  public ApiResponse<Void> create(
      @RequestBody @Valid PostCreateRequestDto requestDto,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    String email = userDetails.getUsername();
    postService.create(requestDto, email);
    return ApiResponse.ok(null);
  }

  @GetMapping
  public ApiResponse<Page<PostSearchResponseDto>> getPosts(
      @PageableDefault(size = 5, sort = "createdAt", direction = Direction.DESC) Pageable pageable,
      PostSearchCondition condition
  ) {
    Page<PostSearchResponseDto> posts = postService.searchPosts(condition, pageable);
    return ApiResponse.ok(posts);
  }

  @GetMapping("/{postId}")
  public ApiResponse<PostDetailResponseDto> getPost(
      @PathVariable Long postId
  ) {
    PostDetailResponseDto postDetailResponseDto = postService.find(postId);
    return ApiResponse.ok(postDetailResponseDto);
  }

  @PutMapping("/{postId}")
  public ApiResponse<Void> editPost(
      @PathVariable Long postId,
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody @Valid PostUpdateRequestDto postUpdateRequestDto
  ) {
    String email = userDetails.getUsername();
    postService.editPost(postId, email, postUpdateRequestDto);
    return ApiResponse.ok(null);
  }

  @DeleteMapping("/{postId}")
  public ApiResponse<Void> deletePost(
      @PathVariable Long postId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    String email = userDetails.getUsername();
    postService.deletePost(postId, email);
    return ApiResponse.of(NO_CONTENT, null);
  }

  @PutMapping("/{postId}/favorite")
  public ApiResponse<Void> putFavorite(
      @PathVariable Long postId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    String email = userDetails.getUsername();
    favoriteService.putFavorite(postId, email);
    return ApiResponse.ok(null);
  }

  @PutMapping("/{postId}/favoriteCancel")
  public ApiResponse<Void> putFavoriteCancel(
      @PathVariable Long postId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    String email = userDetails.getUsername();
    favoriteService.putFavoriteCancel(postId, email);
    return ApiResponse.ok(null);
  }

  @GetMapping("/{postId}/favorite-list")
  public ApiResponse<FavoriteListResponseDto> getFavoriteList(
      @PathVariable Long postId
  ) {
    return ApiResponse.ok(favoriteService.getFavoriteList(postId));
  }

  @GetMapping("/top3")
  public ApiResponse<PostsTop3ResponseDto> getPostsTop3() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime startDate = getStartOfWeek(now);
    LocalDateTime endDate = getEndOfWeek(now);
    PostsTop3ResponseDto posts = postService.getTop3Posts(startDate, endDate);
    return ApiResponse.ok(posts);
  }

  private LocalDateTime getStartOfWeek(LocalDateTime dateTime) {
    return dateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).truncatedTo(ChronoUnit.DAYS);
  }

  private LocalDateTime getEndOfWeek(LocalDateTime dateTime) {
    return dateTime.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).with(LocalTime.MAX);
  }
}
