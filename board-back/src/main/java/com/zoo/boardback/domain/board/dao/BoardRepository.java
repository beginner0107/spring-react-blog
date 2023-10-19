package com.zoo.boardback.domain.board.dao;

import com.zoo.boardback.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, String> {

}
