package com.zoo.boardback.domain.image.dao;

import com.zoo.boardback.domain.board.entity.Board;
import com.zoo.boardback.domain.image.entity.Image;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Integer> {
  List<Image> findByBoard(Board board);
}
