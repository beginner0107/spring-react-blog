package com.zoo.boardback.domain.post.api;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.zoo.boardback.domain.ApiResponse;
import com.zoo.boardback.domain.favorite.application.FavoriteService;
import com.zoo.boardback.domain.favorite.dto.response.FavoriteListResponseDto;
import com.zoo.boardback.domain.post.application.PostCacheService;
import com.zoo.boardback.domain.post.application.PostService;
import com.zoo.boardback.domain.post.dto.request.PostCreateRequestDto;
import com.zoo.boardback.domain.post.dto.request.PostSearchCondition;
import com.zoo.boardback.domain.post.dto.request.PostUpdateRequestDto;
import com.zoo.boardback.domain.post.dto.response.PostDetailResponseDto;
import com.zoo.boardback.domain.post.dto.response.PostSearchResponseDto;
import com.zoo.boardback.domain.post.dto.response.PostsTop3ResponseDto;
import com.zoo.boardback.domain.user.entity.User;
import com.zoo.boardback.global.config.security.annotation.LoginUser;
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
    private final PostCacheService postCacheService;

    @PostMapping
    public ApiResponse<Void> createPost(
        @RequestBody @Valid PostCreateRequestDto requestDto,
        @LoginUser User user
    ) {
        String email = user.getEmail();
        postService.create(requestDto, email);
        return ApiResponse.create();
    }

    @GetMapping
    public ApiResponse<Page<PostSearchResponseDto>> getPosts(
        @PageableDefault(size = 5, sort = "createdAt", direction = Direction.DESC) Pageable pageable,
        PostSearchCondition condition
    ) {
        Page<PostSearchResponseDto> posts = postService.getPosts(condition, pageable);
        return ApiResponse.ok(posts);
    }

    @GetMapping("/{postId}")
    public ApiResponse<PostDetailResponseDto> getPost(
        @PathVariable Long postId
    ) {
        postCacheService.addViewCntToRedis(postId);
        PostDetailResponseDto postDetailResponseDto = postService.find(postId);
        return ApiResponse.ok(postDetailResponseDto);
    }

    @PutMapping("/{postId}")
    public ApiResponse<Void> updatePost(
        @PathVariable Long postId,
        @LoginUser User user,
        @RequestBody @Valid PostUpdateRequestDto postUpdateRequestDto
    ) {
        String email = user.getEmail();
        postService.update(postId, email, postUpdateRequestDto);
        return ApiResponse.create();
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<Void> deletePost(
        @PathVariable Long postId,
        @LoginUser User user) {
        String email = user.getEmail();
        postService.delete(postId, email);
        return ApiResponse.noContent();
    }

    @PutMapping("/{postId}/favorite")
    public ApiResponse<Void> likePost(
        @PathVariable Long postId,
        @LoginUser User user) {
        String email = user.getEmail();
        favoriteService.like(postId, email);
        return ApiResponse.create();
    }

    @PutMapping("/{postId}/favoriteCancel")
    public ApiResponse<Void> cancelPostLike(
        @PathVariable Long postId,
        @LoginUser User user) {
        String email = user.getEmail();
        favoriteService.cancelLike(postId, email);
        return ApiResponse.noContent();
    }

    @GetMapping("/{postId}/favorite-list")
    public ApiResponse<FavoriteListResponseDto> getFavoriteListForPost(
        @PathVariable Long postId
    ) {
        return ApiResponse.ok(favoriteService.getFavorites(postId));
    }

    @GetMapping("/top3")
    public ApiResponse<PostsTop3ResponseDto> getTop3PostsThisWeek() {
        PostsTop3ResponseDto posts = postService.getTop3PostsThisWeek();
        return ApiResponse.ok(posts);
    }
}
