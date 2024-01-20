package com.zoo.boardback.domain.board.dao;

import com.zoo.boardback.domain.board.entity.Board;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {

  @EntityGraph(attributePaths = "user")
  Optional<Board> findByBoardNumber(Long boardNumber);
}
