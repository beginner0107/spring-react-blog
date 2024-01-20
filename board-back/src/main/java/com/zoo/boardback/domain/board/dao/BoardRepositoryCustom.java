package com.zoo.boardback.domain.board.dao;

import com.zoo.boardback.domain.board.dto.request.PostSearchCondition;
import com.zoo.boardback.domain.board.dto.response.PostSearchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardRepositoryCustom {
  Page<PostSearchResponseDto> searchPosts(PostSearchCondition condition, Pageable pageable);
}
