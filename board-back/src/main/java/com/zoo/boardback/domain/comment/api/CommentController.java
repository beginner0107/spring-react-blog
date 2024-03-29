package com.zoo.boardback.domain.comment.api;

import com.zoo.boardback.domain.ApiResponse;
import com.zoo.boardback.domain.comment.application.CommentService;
import com.zoo.boardback.domain.comment.dto.request.CommentCreateRequestDto;
import com.zoo.boardback.domain.comment.dto.request.CommentUpdateRequestDto;
import com.zoo.boardback.domain.comment.dto.response.CommentListResponseDto;
import com.zoo.boardback.domain.user.entity.User;
import com.zoo.boardback.global.config.security.annotation.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ApiResponse<Void> createComment(
        @RequestBody @Valid CommentCreateRequestDto requestDto,
        @LoginUser User user
    ) {
        commentService.create(requestDto, user);
        return ApiResponse.create();
    }

    @GetMapping("/post/{postId}")
    public ApiResponse<CommentListResponseDto> getComments(
        @PathVariable Long postId,
        @PageableDefault(size = 5, sort = "createdAt", direction = Direction.DESC) Pageable pageable
    ) {
        CommentListResponseDto comments = commentService.getComments(
            postId, pageable);
        return ApiResponse.ok(comments);
    }

    @PutMapping("/{commentId}")
    public ApiResponse<Void> updateComment(
        @PathVariable Long commentId,
        @RequestBody @Valid CommentUpdateRequestDto commentUpdateRequestDto,
        @LoginUser User user
    ) {
        commentService.update(user.getEmail(), commentId, commentUpdateRequestDto);
        return ApiResponse.create();
    }

    @DeleteMapping("/{commentId}")
    public ApiResponse<Void> deleteComment(
        @LoginUser User user,
        @PathVariable Long commentId
    ) {
        commentService.delete(commentId, user.getEmail());
        return ApiResponse.noContent();
    }

    @GetMapping("/post/{postId}/parentId/{parentId}")
    public ApiResponse<CommentListResponseDto> getChildComments(
        @PathVariable Long postId,
        @PathVariable("parentId") Long commentId
    ) {
        CommentListResponseDto comments = commentService.getChildComments(
            postId, commentId);
        return ApiResponse.ok(comments);
    }
}
