package com.zoo.boardback.domain.image.dao;

import com.zoo.boardback.domain.board.entity.Board;
import com.zoo.boardback.domain.image.entity.Image;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImageRepository extends JpaRepository<Image, Long> {
  List<Image> findByBoard(Board board);

  @Modifying
  @Query("DELETE FROM Image i WHERE i.board = :board")
  void deleteByBoard(@Param("board") Board board);
}
