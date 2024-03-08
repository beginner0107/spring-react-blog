package com.zoo.boardback.domain.favorite.application;

import static com.zoo.boardback.global.error.ErrorCode.*;
import static com.zoo.boardback.global.error.ErrorCode.POST_NOT_FOUND;
import static com.zoo.boardback.global.error.ErrorCode.USER_NOT_FOUND;

import com.zoo.boardback.domain.favorite.dao.FavoriteRepository;
import com.zoo.boardback.domain.favorite.dto.query.FavoriteQueryDto;
import com.zoo.boardback.domain.favorite.dto.response.FavoriteListResponseDto;
import com.zoo.boardback.domain.favorite.entity.Favorite;
import com.zoo.boardback.domain.favorite.entity.primaryKey.FavoritePk;
import com.zoo.boardback.domain.post.dao.PostRepository;
import com.zoo.boardback.domain.post.entity.Post;
import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.domain.user.entity.User;
import com.zoo.boardback.global.error.BusinessException;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void like(Long postId, String email) {
        Post post = findPostByPostId(postId);
        User user = findUserByEmail(email);

        FavoritePk favoritePk = new FavoritePk(post, user);
        validateFavoriteAlreadyExists(favoritePk, post);
        doLike(favoritePk, post);
    }

    private void validateFavoriteAlreadyExists(FavoritePk favoritePk, Post post) {
        Optional<Favorite> favoriteOp = favoriteRepository.findByFavoritePk(favoritePk);
        if (favoriteOp.isPresent()) {
            throw new BusinessException(post.getId(), "postId", FAVORITE_ALREADY_EXISTS);
        }
    }

    private void doLike(FavoritePk favoritePk, Post post) {
        Favorite favorite = Favorite.create(favoritePk);
        favoriteRepository.save(favorite);
        post.increaseFavoriteCount();
    }

    @Transactional
    public void cancelLike(Long postId, String email) {
        Post post = findPostByPostId(postId);

        User user = findUserByEmail(email);
        FavoritePk favoritePk = new FavoritePk(post, user);
        doCancelLike(favoritePk, post);
    }

    private void doCancelLike(FavoritePk favoritePk, Post post) {
        favoriteRepository.delete(favoriteRepository.findByFavoritePk(favoritePk)
            .orElseThrow(() -> new BusinessException(post.getId(), "postId", FAVORITE_CANCEL)));
        post.decreaseFavoriteCount();
    }

    public FavoriteListResponseDto getFavorites(Long postId) {
        Post post = findPostByPostId(postId);
        return FavoriteListResponseDto.from(
            favoriteRepository.findFavoritesByPost(post).stream()
            .map(FavoriteQueryDto::from)
            .collect(Collectors.toList()));
    }

    private Post findPostByPostId(Long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
            new BusinessException(postId, "postId", POST_NOT_FOUND));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
            new BusinessException(email, "email", USER_NOT_FOUND));
    }
}
