package com.zoo.boardback.domain.image.dao;

import com.zoo.boardback.domain.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Integer> {

}
