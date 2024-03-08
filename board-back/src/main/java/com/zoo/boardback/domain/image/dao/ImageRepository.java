package com.zoo.boardback.domain.image.dao;

import com.zoo.boardback.domain.post.entity.Post;
import com.zoo.boardback.domain.image.entity.Image;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByPost(Post post);

    void deleteByPostId(Long postId);
}
