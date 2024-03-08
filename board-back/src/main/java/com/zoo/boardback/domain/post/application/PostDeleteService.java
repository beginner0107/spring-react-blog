package com.zoo.boardback.domain.post.application;

import com.zoo.boardback.domain.comment.dao.CommentRepository;
import com.zoo.boardback.domain.favorite.dao.FavoriteRepository;
import com.zoo.boardback.domain.image.dao.ImageRepository;
import com.zoo.boardback.domain.post.dao.PostRepository;
import com.zoo.boardback.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostDeleteService {
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final CommentRepository commentRepository;
    private final FavoriteRepository favoriteRepository;

    public void delete(Post post) {
        imageRepository.deleteByPostId(post.getId());
        commentRepository.deleteByPostId(post.getId());
        favoriteRepository.deleteByPost(post);
        postRepository.delete(post);
    }
}
