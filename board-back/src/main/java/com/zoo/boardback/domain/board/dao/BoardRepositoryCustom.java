package com.zoo.boardback.domain.board.dao;

import com.zoo.boardback.domain.board.dto.request.PostSearchCondition;
import com.zoo.boardback.domain.board.dto.response.PostSearchResponseDto;
import com.zoo.boardback.domain.board.dto.response.object.PostRankItem;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardRepositoryCustom {
  Page<PostSearchResponseDto> searchPosts(PostSearchCondition condition, Pageable pageable);

  List<PostRankItem> getTop3Posts(LocalDateTime startDate, LocalDateTime endDate);
}
