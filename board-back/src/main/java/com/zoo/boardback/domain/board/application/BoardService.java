package com.zoo.boardback.domain.board.application;

import com.zoo.boardback.domain.board.dao.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

  private final BoardRepository boardRepository;
}
