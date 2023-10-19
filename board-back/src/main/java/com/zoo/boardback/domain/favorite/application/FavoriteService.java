package com.zoo.boardback.domain.favorite.application;

import com.zoo.boardback.domain.favorite.dao.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

  private FavoriteRepository favoriteRepository;
}
