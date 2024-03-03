package com.zoo.boardback.domain.post.application;

import static com.zoo.boardback.domain.searchLog.entity.type.SearchType.findSearchWord;
import static com.zoo.boardback.global.error.ErrorCode.POST_NOT_CUD_MATCHING_USER;
import static com.zoo.boardback.global.error.ErrorCode.POST_NOT_FOUND;
import static com.zoo.boardback.global.error.ErrorCode.USER_NOT_FOUND;
import static java.util.stream.Collectors.toList;

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
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
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
        User user = findUserByEmail(email);
        Post post = request.toEntity(user);
        postRepository.save(post);

        savePostTitleImage(request, post);

        savePostImages(request, post);
    }

    private void savePostTitleImage(PostCreateRequestDto request, Post post) {
        if (!request.existsByPostTitleImageUrl()) return;
        imageRepository.save(Image.createPostTitleImage(post, request.getPostTitleImageUrl()));
    }

    private void savePostImages(PostCreateRequestDto request, Post post) {
        if (!request.existsByPostImageUrls()) return;
        imageRepository.saveAll(createPostImages(request.getPostImageUrls(), post));
    }

    private List<Image> createPostImages(List<String> postImageUrls, Post post) {
        return postImageUrls.stream()
            .map(imageUrl -> Image.createPostImage(post, imageUrl))
            .collect(toList());
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
            new BusinessException(email, "email", USER_NOT_FOUND));
    }

    @Transactional
    public Page<PostSearchResponseDto> getPosts(PostSearchCondition condition, Pageable pageable) {
        this.saveSearchLog(condition);
        return postRepository.searchPosts(condition, pageable);
    }

    private void saveSearchLog(PostSearchCondition condition) {
        SearchType.findSearchType(condition).ifPresent(searchType ->
            findSearchWord(searchType, condition).ifPresent(searchWord ->
                searchLogRepository.save(SearchLog.create(searchType, searchWord))));
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
    public void delete(Long postId, String email) { // cascade 옵션 활용하기
        Post post = postRepository.findById(postId).orElseThrow(() ->
            new BusinessException(postId, "postId", POST_NOT_FOUND));
        checkPostAuthorMatching(email, post);
        imageRepository.deleteByBoard(post);
        commentRepository.deleteByPost(post);
        favoriteRepository.deleteByPost(post);
        postRepository.delete(post);
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

    public PostsTop3ResponseDto getTop3PostsThisWeek() {
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime weekStartDate = getStartOfWeek(currentDate);
        LocalDateTime weekEndDate = getEndOfWeek(currentDate);
        List<PostRankItem> posts = postRepository.getTop3Posts(weekStartDate, weekEndDate);
        return PostsTop3ResponseDto.create(posts);
    }

    private LocalDateTime getStartOfWeek(LocalDateTime dateTime) {
        return dateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .truncatedTo(ChronoUnit.DAYS);
    }

    private LocalDateTime getEndOfWeek(LocalDateTime dateTime) {
        return dateTime.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).with(LocalTime.MAX);
    }
}
