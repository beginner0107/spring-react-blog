package com.zoo.boardback.domain.post.application;

import static com.zoo.boardback.domain.auth.entity.role.UserRole.GENERAL_USER;
import static com.zoo.boardback.global.error.ErrorCode.BOARD_NOT_CUD_MATCHING_USER;
import static com.zoo.boardback.global.error.ErrorCode.BOARD_NOT_FOUND;
import static com.zoo.boardback.global.error.ErrorCode.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.zoo.boardback.IntegrationTestSupport;
import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.domain.post.dao.PostRepository;
import com.zoo.boardback.domain.post.dto.request.PostCreateRequestDto;
import com.zoo.boardback.domain.post.dto.request.PostSearchCondition;
import com.zoo.boardback.domain.post.dto.request.PostUpdateRequestDto;
import com.zoo.boardback.domain.post.dto.response.PostDetailResponseDto;
import com.zoo.boardback.domain.post.dto.response.PostSearchResponseDto;
import com.zoo.boardback.domain.post.dto.response.PostsTop3ResponseDto;
import com.zoo.boardback.domain.post.entity.Post;
import com.zoo.boardback.domain.comment.dao.CommentRepository;
import com.zoo.boardback.domain.comment.entity.Comment;
import com.zoo.boardback.domain.image.dao.ImageRepository;
import com.zoo.boardback.domain.image.entity.Image;
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
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

class PostServiceTest extends IntegrationTestSupport {

  @Autowired
  private PostRepository postRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ImageRepository imageRepository;
  @Autowired
  private CommentRepository commentRepository;
  @Autowired
  private SearchLogRepository searchLogRepository;
  @Autowired
  private PostService postService;

  @AfterEach
  void tearDown() {
    imageRepository.deleteAllInBatch();
    commentRepository.deleteAllInBatch();
    postRepository.deleteAllInBatch();
    userRepository.deleteAllInBatch();
    searchLogRepository.deleteAllInBatch();
  }

  @DisplayName("회원은 게시물을 작성하고 저장할 수 있다.")
  @Test
  void createPost() {
    // given
    String email = "test12@naver.com";
    User user = createUser(email, "testpassword123"
        , "01022222222", "개구리왕눈이");
    userRepository.save(user);

    String title = "테스트 글의 제목";
    String content = "테스트 글의 내용";
    PostCreateRequestDto request = createPostCreateRequest(title, content);

    // when
    postService.create(request, email);

    // then
    List<Post> postList = postRepository.findAll();
    assertThat(postList).hasSize(1);
    assertThat(postList.get(0).getTitle()).isEqualTo(title);
    assertThat(postList.get(0).getContent()).isEqualTo(content);
  }

  @DisplayName("존재하지 않는 회원은 게시물을 작성하고 저장할 수 없다.")
  @Test
  void createPostUserNotExist() {
    // given
    String email = "test12@naver.com";

    String title = "테스트 글의 제목";
    String content = "테스트 글의 내용";
    PostCreateRequestDto request = createPostCreateRequest(title, content);

    // when & then
    assertThatThrownBy(() -> postService.create(request, email))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(USER_NOT_FOUND.getMessage());
  }

  @DisplayName("게시글의 번호를 넘기면 게시글의 상세정보를 볼 수 있다.")
  @Test
  void findPostDetail() {
    // given
    String email = "test12@naver.com";
    String nickname = "개구리왕눈이";
    User user = createUser(email, "testpassword123"
        , "01022222222", nickname);
    userRepository.save(user);

    String title = "테스트 글의 제목";
    String content = "테스트 글의 내용";
    Post post = createPost(title, content, user);
    postRepository.save(post);

    String imageUrl = "https://testImage.png";
    Image image = createImage(imageUrl, post);
    imageRepository.save(image);

    List<Post> postList = postRepository.findAll();
    Long boardNumber = postList.get(0).getId();

    // when
    PostDetailResponseDto response = postService.find(boardNumber);

    // then
    assertThat(response.getTitle()).isEqualTo(title);
    assertThat(response.getContent()).isEqualTo(content);
    assertThat(response.getPostImageList()).hasSize(1);
    assertThat(response.getPostImageList().get(0)).isEqualTo(imageUrl);
    assertThat(response.getWriterEmail()).isEqualTo(email);
    assertThat(response.getWriterNickname()).isEqualTo(nickname);
    assertThat(response.getWriterProfileImage()).isNull();
  }

  @DisplayName("게시글의 번호가 유효하지 않으면 게시글의 상세정보를 볼 수 없다.")
  @Test
  void findPostDetailFailBoardNumberFalse() {
    // given

    // when & then
    assertThatThrownBy(() -> postService.find(1L))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(BOARD_NOT_FOUND.getMessage());
  }

  @DisplayName("검색어와 함께 게시글을 검색하면, 게시글 목록을 반환한다.")
  @Test
  void givenNoSearchParameters_whenSearchPosts_thenReturnPosts() {
    // given
    String email = "test12@naver.com";
    String nickname = "개구리왕눈이";
    User user = createUser(email, "testpassword123"
        , "01022222222", nickname);
    userRepository.save(user);

    String title1 = "테스트 글의 제목1", content1 = "테스트 글의 내용1";
    String title2 = "테스트 글의 제목2", content2 = "테스트 글의 내용2";
    Post post1 = createPost(title1, content1, user);
    Post post2 = createPost(title2, content2, user);
    postRepository.saveAll(List.of(post1, post2));

    // when
    Page<PostSearchResponseDto> posts = postService.searchPosts(
        PostSearchCondition.builder()
            .build(), Pageable.ofSize(5)
    );

    // then
    assertThat(posts).hasSize(2);
  }

  @DisplayName("검색어와 함께 게시글을 검색하면, 게시글 목록을 반환한다.")
  @Test
  void givenSearchParameters_whenSearchPosts_thenReturnPosts() {
    // given
    String email = "test12@naver.com";
    String nickname = "개구리왕눈이";
    User user = createUser(email, "testpassword123"
        , "01022222222", nickname);
    userRepository.save(user);

    String title1 = "테스트 글의 제목1", content1 = "테스트 글의 내용1";
    String title2 = "테스트 글의 제목2", content2 = "테스트 글의 내용2";
    Post post1 = createPost(title1, content1, user);
    Post post2 = createPost(title2, content2, user);
    postRepository.saveAll(List.of(post1, post2));

    String imageUrl = "https://testImage.png";
    Image image = createImage(imageUrl, post1);
    imageRepository.save(image);

    // when
    Page<PostSearchResponseDto> posts = postService.searchPosts(
        PostSearchCondition.builder()
            .title("테스트 글의 제목1")
            .build(), Pageable.ofSize(5)
    );

    // then
    assertThat(posts).hasSize(1);
    assertThat(posts.getContent().get(0).getTitle()).isEqualTo(title1);
    assertThat(posts.getContent().get(0).getContent()).isEqualTo(content1);
    assertThat(posts.getContent().get(0).getContent()).isEqualTo(content1);
    assertThat(posts.getContent().get(0).getProfileImage()).isNull();
    assertThat(posts.getContent().get(0).getViewCount()).isZero();
    assertThat(posts.getContent().get(0).getFavoriteCount()).isZero();
    assertThat(posts.getContent().get(0).getCommentCount()).isZero();
    assertThat(posts.getContent().get(0).getPostTitleImage()).isNull();

    List<SearchLog> searchLogs = searchLogRepository.findAll();
    assertThat(searchLogs).hasSize(1);
    assertThat(searchLogs.get(0).getSearchType()).isEqualTo(SearchType.POST_WRITER_TITLE);
    assertThat(searchLogs.get(0).getSearchWord()).isEqualTo("테스트 글의 제목1");
  }

  @DisplayName("회원 본인이 작성한 게시글을 수정할 수 있다.")
  @Test
  void editPost() {
    // given
    String email = "test12@naver.com";
    String nickname = "개구리왕눈이";
    User user = createUser(email, "testpassword123"
        , "01022222222", nickname);
    User newUser = userRepository.save(user);

    String title = "테스트 글의 제목";
    String content = "테스트 글의 내용";
    Post post = createPost(title, content, newUser);
    Post newPost = postRepository.save(post);

    String imageUrl = "https://testImage.png";
    Image image = createImage(imageUrl, post);
    imageRepository.save(image);

    String editTitle = "테스트 수정 글의 제목";
    String editContent = "테스트 수정 글의 내용";
    String updateImageUrl1 = "https://updateImage1.png";
    String updateImageUrl2 = "https://updateImage2.png";
    List<String> updateImages = List.of(updateImageUrl1,
        updateImageUrl2);
    PostUpdateRequestDto request = createPostUpdateRequest(editTitle, editContent, updateImages);

    // when
    postService.editPost(newPost.getId(), email, request);

    // then
    List<Post> posts = postRepository.findAll();
    assertThat(posts).hasSize(1);
    assertThat(posts.get(0).getTitle()).isEqualTo(editTitle);
    assertThat(posts.get(0).getContent()).isEqualTo(editContent);

    List<Image> images = imageRepository.findByPost(posts.get(0));
    assertThat(images.get(0).getImageUrl()).isEqualTo(updateImageUrl1);
    assertThat(images.get(1).getImageUrl()).isEqualTo(updateImageUrl2);
  }

  @DisplayName("회원 본인이 작성하지 않은 게시글을 수정할 수 없다.")
  @Test
  void editPostNotAuthorized() {
    // given
    String email = "test12@naver.com";
    String email2 = "hacker@naver.com";
    String nickname = "개구리왕눈이";
    User user = createUser(email, "testpassword123"
        , "01022222222", nickname);
    User newUser = userRepository.save(user);

    String title = "테스트 글의 제목";
    String content = "테스트 글의 내용";
    Post post = createPost(title, content, newUser);
    Post newPost = postRepository.save(post);

    String imageUrl = "https://testImage.png";
    Image image = createImage(imageUrl, post);
    imageRepository.save(image);

    String editTitle = "테스트 수정 글의 제목";
    String editContent = "테스트 수정 글의 내용";
    String updateImageUrl1 = "https://updateImage1.png";
    String updateImageUrl2 = "https://updateImage2.png";
    List<String> updateImages = List.of(updateImageUrl1,
        updateImageUrl2);
    PostUpdateRequestDto request = createPostUpdateRequest(editTitle, editContent, updateImages);

    // when & then
    assertThatThrownBy(() ->
        postService.editPost(newPost.getId(), email2, request))
        .isInstanceOf(BusinessException.class)
        .hasMessage(BOARD_NOT_CUD_MATCHING_USER.getMessage());
  }

  @DisplayName("회원 본인이 작성한 게시글을 삭제할 수 있다.")
  @Test
  void deletePost() {
    // given
    String email = "test12@naver.com";
    String nickname = "개구리왕눈이";
    User user = createUser(email, "testpassword123"
        , "01022222222", nickname);
    User newUser = userRepository.save(user);

    String title = "테스트 글의 제목";
    String content = "테스트 글의 내용";
    Post post = createPost(title, content, user);
    Post newPost = postRepository.save(post);

    String imageUrl = "https://testImage.png";
    Image image = createImage(imageUrl, post);
    imageRepository.save(image);

    LocalDateTime createdAt = LocalDateTime.now();
    LocalDateTime updatedAt = LocalDateTime.now();
    Comment comment = createComment("댓글을 답니다1.!", newPost, newUser);
    commentRepository.save(comment);

    // when
    postService.deletePost(newPost.getId(), email);

    // then
    List<Post> posts = postRepository.findAll();
    assertThat(posts).hasSize(0);
  }

  @DisplayName("회원 본인이 작성하지 않은 게시글을 삭제할 수 없다.")
  @Test
  void deletePostNotAuthorized() {
    // given
    String email = "test12@naver.com";
    String email2 = "hacker@naver.com";
    String nickname = "개구리왕눈이";
    User user = createUser(email, "testpassword123"
        , "01022222222", nickname);
    User newUser = userRepository.save(user);

    String title = "테스트 글의 제목";
    String content = "테스트 글의 내용";
    Post post = createPost(title, content, user);
    Post newPost = postRepository.save(post);

    String imageUrl = "https://testImage.png";
    Image image = createImage(imageUrl, post);
    imageRepository.save(image);

    LocalDateTime createdAt = LocalDateTime.now();
    LocalDateTime updatedAt = LocalDateTime.now();
    Comment comment = createComment("댓글을 답니다1.!", newPost, newUser);
    commentRepository.save(comment);

    // when & then
    assertThatThrownBy(() ->
        postService.deletePost(newPost.getId(), email2))
        .isInstanceOf(BusinessException.class)
        .hasMessage(BOARD_NOT_CUD_MATCHING_USER.getMessage());
  }

  @DisplayName("회원은 상위 3개의 게시물을 볼 수 있다.")
  @Test
  void getTop3Posts() {
    // given
    String email = "test12@naver.com";
    String nickname = "개구리왕눈이";
    User user = createUser(email, "testpassword123"
        , "01022222222", nickname);
    User newUser = userRepository.save(user);

    String title1 = "테스트 글의 제목1", content1 = "테스트 글의 내용1";
    String title2 = "테스트 글의 제목2", content2 = "테스트 글의 내용2";
    String title3 = "테스트 글의 제목3", content3 = "테스트 글의 내용3";
    Post post1 = createPostViewCount(title1, content1, user, 1);
    Post post2 = createPostViewCount(title2, content2, user, 2);
    Post post3 = createPostViewCount(title3, content3, user, 3);
    postRepository.saveAll(List.of(post1, post2, post3));

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime startDate = getStartOfWeek(now);
    LocalDateTime endDate = getEndOfWeek(now);

    // when
    PostsTop3ResponseDto posts = postService.getTop3Posts(startDate, endDate);

    // then
    assertThat(posts).isNotNull();
    assertThat(posts.getTop3List()).hasSize(3);
    assertThat(posts.getTop3List().get(0).getTitle()).isEqualTo(title3);
    assertThat(posts.getTop3List().get(0).getContent()).isEqualTo(content3);
    assertThat(posts.getTop3List().get(0).getFavoriteCount()).isEqualTo(3);
  }

  private static Image createImage(String imageUrl, Post post) {
    return Image.builder()
        .imageUrl(imageUrl)
        .post(post)
        .build();
  }

  private Post createPost(String title, String content, User user) {
    return Post.builder()
        .title(title)
        .content(content)
        .viewCount(0)
        .favoriteCount(0)
        .commentCount(0)
        .user(user)
        .build();
  }

  private Post createPostViewCount(
      String title, String content,
      User user, Integer favoriteCount
  ) {
    return Post.builder()
        .title(title)
        .content(content)
        .viewCount(0)
        .favoriteCount(favoriteCount)
        .commentCount(0)
        .user(user)
        .build();
  }

  private PostCreateRequestDto createPostCreateRequest(String title, String content) {
    return PostCreateRequestDto.builder()
        .title(title)
        .content(content)
        .postImageList(List.of("https://testImage2.png",
            "https://testImage3.png"))
        .build();
  }

  private PostUpdateRequestDto createPostUpdateRequest(String title, String content
      , List<String> updateImages) {
    return PostUpdateRequestDto.builder()
        .title(title)
        .content(content)
        .boardImageList(updateImages)
        .build();
  }


  private User createUser(String email, String password, String telNumber, String nickname) {
    return User.builder()
        .email(email)
        .password(password)
        .telNumber(telNumber)
        .nickname(nickname)
        .address("용인시 기흥구 보정로")
        .roles(initRole())
        .build();
  }

  private List<Authority> initRole() {
    return Collections.singletonList(Authority.builder().role(GENERAL_USER).build());
  }

  private Comment createComment(String content, Post post, User user) {
    return Comment.builder()
        .content(content)
        .post(post)
        .user(user)
        .build();
  }

  private LocalDateTime getStartOfWeek(LocalDateTime dateTime) {
    return dateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).truncatedTo(ChronoUnit.DAYS);
  }

  private LocalDateTime getEndOfWeek(LocalDateTime dateTime) {
    return dateTime.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).with(LocalTime.MAX);
  }
}