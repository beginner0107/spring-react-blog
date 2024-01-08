package com.zoo.boardback.domain.comment.api;

import com.zoo.boardback.domain.ApiResponse;
import com.zoo.boardback.domain.auth.details.CustomUserDetails;
import com.zoo.boardback.domain.comment.application.CommentService;
import com.zoo.boardback.domain.comment.dto.request.CommentCreateRequestDto;
import com.zoo.boardback.domain.comment.dto.response.CommentListResponseDto;
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
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @PostMapping
  public ApiResponse<Void> create(
      @RequestBody @Valid CommentCreateRequestDto requestDto,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    commentService.create(userDetails.getUser(), requestDto);
    return ApiResponse.ok(null);
  }

  @GetMapping("/board/{boardNumber}")
  public ApiResponse<CommentListResponseDto> getComments(
      @PathVariable int boardNumber
      ) {
    CommentListResponseDto comments = commentService.getComments(
        boardNumber);
    return ApiResponse.ok(comments);
  }

  @PutMapping("/{commentNumber}/editComment")
  public ApiResponse<Void> editComment(@PathVariable int commentNumber) {
    commentService.editComment(commentNumber);
    return ApiResponse.ok(null);
  }

  @DeleteMapping("/{commentNumber}/deleteComment")
  public ApiResponse<Void> deleteComment(@PathVariable int commentNumber) {
    commentService.deleteComment(commentNumber);
    return ApiResponse.ok(null);
  }
}
