package com.zoo.boardback.domain.post.application;

import static com.zoo.boardback.domain.searchLog.entity.type.SearchType.NOT_EXIST_SEARCH_WORD;
import static com.zoo.boardback.domain.searchLog.entity.type.SearchType.findSearchWord;
import static com.zoo.boardback.global.error.ErrorCode.POST_NOT_CUD_MATCHING_USER;
import static com.zoo.boardback.global.error.ErrorCode.POST_NOT_FOUND;
import static com.zoo.boardback.global.error.ErrorCode.USER_NOT_FOUND;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.hasText;

import com.zoo.boardback.domain.comment.dao.CommentRepository;
import com.zoo.boardback.domain.favorite.dao.FavoriteRepository;
import com.zoo.boardback.domain.image.dao.ImageRepository;
import com.zoo.boardback.domain.image.entity.Image;
import com.zoo.boardback.domain.post.dao.PostRepository;
import com.zoo.boardback.domain.post.dto.request.PostCreateRequestDto;
import com.zoo.boardback.domain.post.dto.request.PostSearchCondition;
import com.zoo.boardback.domain.post.dto.request.PostUpdateRequestDto;
import com.zoo.boardback.domain.post.dto.response.PostDetailResponseDto;
import com.zoo.boardback.domain.post.dto.response.PostSearchResponseDto;
import com.zoo.boardback.domain.post.dto.response.PostsTop3ResponseDto;
import com.zoo.boardback.domain.post.dto.response.object.PostRankItem;
import com.zoo.boardback.domain.post.entity.Post;
import com.zoo.boardback.domain.searchLog.dao.SearchLogRepository;
import com.zoo.boardback.domain.searchLog.entity.SearchLog;
import com.zoo.boardback.domain.searchLog.entity.type.SearchType;
import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.domain.user.entity.User;
import com.zoo.boardback.global.error.BusinessException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final CommentRepository commentRepository;
    private final FavoriteRepository favoriteRepository;
    private final SearchLogRepository searchLogRepository;

    @Transactional
    public void create(PostCreateRequestDto request, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
            new BusinessException(email, "email", USER_NOT_FOUND));

        Post post = request.toEntity(user);
        postRepository.save(post);

        String boardTitleImage = request.getPostTitleImage();
        if (hasText(boardTitleImage)) {
            imageRepository.save(Image.builder()
                .imageUrl(boardTitleImage)
                .post(post)
                .titleImageYn(true)
                .build()
            );
        }
        List<String> postImageList = request.getPostImageList();
        if (!postImageList.isEmpty()) {
            saveImages(postImageList, post);
        }
    }

    public Page<PostSearchResponseDto> getPosts(PostSearchCondition condition, Pageable pageable) {
        this.saveSearchLog(condition);
        return postRepository.searchPosts(condition, pageable);
    }

    @Transactional
    private void saveSearchLog(PostSearchCondition condition) {
        SearchType searchType = SearchType.findSearchType(condition);
        if (searchType != NOT_EXIST_SEARCH_WORD) {
            String searchWord = findSearchWord(searchType, condition);
            searchLogRepository.save(createdSearchLog(searchType, searchWord));
        }
    }

    private SearchLog createdSearchLog(SearchType searchType, String searchWord) {
        return SearchLog.builder()
            .searchType(searchType)
            .searchWord(searchWord)
            .build();
    }

    @Transactional
    public PostDetailResponseDto find(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
            new BusinessException(postId, "postId", POST_NOT_FOUND));

        List<String> boardImageList = findBoardImages(post);
        return PostDetailResponseDto.of(post, boardImageList);
    }

    @Transactional
    public void update(Long postId, String email, PostUpdateRequestDto requestDto) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
            new BusinessException(postId, "postId", POST_NOT_FOUND));
        checkPostAuthorMatching(email, post);
        post.editPost(requestDto.getTitle(), requestDto.getContent());

        List<String> boardImageList = requestDto.getBoardImageList();
        List<Image> imageEntities = new ArrayList<>();

        editImages(post, boardImageList, imageEntities);
    }

    @Transactional
    public void delete(Long postId, String email) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
            new BusinessException(postId, "postId", POST_NOT_FOUND));
        checkPostAuthorMatching(email, post);
        imageRepository.deleteByBoard(post);
        commentRepository.deleteByPost(post);
        favoriteRepository.deleteByPost(post);
        postRepository.delete(post);
    }

    private void saveImages(List<String> postImageUrls, Post post) {
        imageRepository.saveAll(postImageUrls.stream()
            .map(image -> Image.builder()
                .post(post)
                .imageUrl(image)
                .titleImageYn(false)
                .build())
            .collect(toList()));
    }

    private void checkPostAuthorMatching(String email, Post post) {
        if (!post.getUser().getEmail().equals(email)) {
            throw new BusinessException(email, "email", POST_NOT_CUD_MATCHING_USER);
        }
    }

    private List<String> findBoardImages(Post post) {
        List<Image> imageList = imageRepository.findByPost(post);
        List<String> boardImageList = new ArrayList<>();
        for (Image image : imageList) {
            String imageUrl = image.getImageUrl();
            boardImageList.add(imageUrl);
        }
        return boardImageList;
    }

    private void editImages(Post post, List<String> boardImageList, List<Image> imageEntities) {
        imageRepository.deleteByBoard(post);
        for (String image : boardImageList) {
            Image imageEntity = Image.builder()
                .post(post)
                .imageUrl(image)
                .build();
            imageEntities.add(imageEntity);
        }
        imageRepository.saveAll(imageEntities);
    }

    public PostsTop3ResponseDto getTop3Posts(LocalDateTime startDate, LocalDateTime endDate) {
        List<PostRankItem> posts = postRepository.getTop3Posts(startDate, endDate);
        return PostsTop3ResponseDto.builder().top3List(posts).build();
    }
}
