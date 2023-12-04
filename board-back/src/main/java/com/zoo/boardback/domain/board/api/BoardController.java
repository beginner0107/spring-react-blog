package com.zoo.boardback.domain.board.api;

import com.zoo.boardback.domain.auth.details.CustomUserDetails;
import com.zoo.boardback.domain.board.application.BoardService;
import com.zoo.boardback.domain.board.dto.request.PostCreateRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/board")
public class BoardController {

  private final BoardService boardService;

  @PostMapping("")
  public ResponseEntity<Void> createBoard(
      @RequestBody @Valid PostCreateRequestDto requestDto,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    boardService.create(requestDto, userDetails.getUsername());
    return ResponseEntity.ok().build();
  }

}
