package com.zoo.boardback.domain.favorite.application;

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
import java.util.List;
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
        Post post = postRepository.findById(postId).orElseThrow(() ->
            new BusinessException(postId, "postId", POST_NOT_FOUND));

        User user = userRepository.findByEmail(email).orElseThrow(() ->
            new BusinessException(email, "email", USER_NOT_FOUND));
        FavoritePk favoritePk = new FavoritePk(post, user);
        Favorite favorite = favoriteRepository.findByFavoritePk(favoritePk);
        if (favorite == null) {
            favorite = Favorite.builder()
                .favoritePk(favoritePk)
                .build();
            favoriteRepository.save(favorite);
            post.increaseFavoriteCount();
        }
    }

    @Transactional
    public void cancelLike(Long postId, String email) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
            new BusinessException(postId, "postId", POST_NOT_FOUND));

        User user = userRepository.findByEmail(email).orElseThrow(() ->
            new BusinessException(email, "email", USER_NOT_FOUND));
        FavoritePk favoritePk = new FavoritePk(post, user);
        Favorite favorite = favoriteRepository.findByFavoritePk(favoritePk);
        if (favorite != null) {
            favoriteRepository.delete(favorite);
            post.decreaseFavoriteCount();
        }
    }

    public FavoriteListResponseDto getFavorites(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
            new BusinessException(postId, "postId", POST_NOT_FOUND));
        List<Favorite> favorites = favoriteRepository.findRecommendersByPost(post);
        List<FavoriteQueryDto> favoritesQueryDtos = favorites.stream()
            .map(FavoriteQueryDto::from)
            .collect(Collectors.toList());
        return FavoriteListResponseDto.from(favoritesQueryDtos);
    }
}
