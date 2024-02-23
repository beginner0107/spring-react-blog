package com.zoo.boardback.domain.favorite.application;

import static com.zoo.boardback.domain.user.entity.role.UserRole.GENERAL_USER;
import static org.assertj.core.api.Assertions.assertThat;

import com.zoo.boardback.IntegrationTestSupport;
import com.zoo.boardback.domain.auth.dao.AuthRepository;
import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.domain.favorite.dao.FavoriteRepository;
import com.zoo.boardback.domain.favorite.dto.response.FavoriteListResponseDto;
import com.zoo.boardback.domain.favorite.entity.Favorite;
import com.zoo.boardback.domain.favorite.entity.primaryKey.FavoritePk;
import com.zoo.boardback.domain.post.dao.PostRepository;
import com.zoo.boardback.domain.post.entity.Post;
import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.domain.user.entity.User;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FavoriteServiceTest extends IntegrationTestSupport {
  @Autowired
  private FavoriteRepository favoriteRepository;
  @Autowired
  private PostRepository postRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private AuthRepository authRepository;
  @Autowired
  private FavoriteService favoriteService;

  @AfterEach
  void tearDown() {
    favoriteRepository.deleteAllInBatch();
    postRepository.deleteAllInBatch();
    authRepository.deleteAllInBatch();
    userRepository.deleteAllInBatch();
  }

  @DisplayName("회원은 게시물 좋아요 버튼을 ON 할 수 있다.")
  @Test
  void putFavoriteOn() {
    // given
    User user = createUser("test12@naver.com", "testpassword123"
        , "01022222222", "개구리왕눈이1");
    userRepository.save(user);

    Post post = createBoard("제목1", "내용입니다.1", user);
    postRepository.save(post);
    List<Post> postList = postRepository.findAll();
    Long boardNumber = postList.get(0).getId();

    // when
    favoriteService.like(boardNumber, "test12@naver.com");

    // then
    List<Favorite> favoriteList = favoriteRepository.findAll();
    assertThat(favoriteList).hasSize(1);
    assertThat(favoriteList.get(0).getFavoritePk().getUser()).isNotNull();
    assertThat(favoriteList.get(0).getFavoritePk().getPost()).isNotNull();

    List<Post> postList1 = postRepository.findAll();
    assertThat(postList1.get(0).getFavoriteCount()).isEqualTo(1);
  }

  @DisplayName("회원은 게시물 좋아요 버튼을 OFF 할 수 있다.")
  @Test
  void putFavoriteOff() {
    // given
    User user = createUser("test12@naver.com", "testpassword123"
        , "01022222222", "개구리왕눈이1");
    userRepository.save(user);

    Post post = createBoard("제목1", "내용입니다.1", user);
    post.increaseFavoriteCount();
    postRepository.save(post);
    List<Post> postList = postRepository.findAll();
    Long boardNumber = postList.get(0).getId();

    FavoritePk favoritePk = new FavoritePk(post, user);
    Favorite saveFavorite = createFavorite(favoritePk);
    favoriteRepository.save(saveFavorite);

    // when
    favoriteService.cancelLike(boardNumber, "test12@naver.com");

    // then
    List<Favorite> favoriteList = favoriteRepository.findAll();
    assertThat(favoriteList).hasSize(0);

    List<Post> postList1 = postRepository.findAll();
    assertThat(postList1.get(0).getFavoriteCount()).isEqualTo(0);
  }

  @DisplayName("게시글에 좋아요를 누른 회원들의 목록을 조회한다.")
  @Test
  void getFavoriteList() {
    // given
    User user1 = createUser("test12@naver.com", "testpassword123"
        , "01022222222", "개구리왕눈이1");
    User user2 = createUser("test13@naver.com", "testpassword123"
        , "01022222221", "개구리왕눈이2");
    userRepository.saveAll(List.of(user1, user2));

    Post post = createBoard("제목1", "내용입니다.1", user1);
    postRepository.save(post);

    FavoritePk favoritePk1 = new FavoritePk(post, user1);
    Favorite saveFavorite1 = createFavorite(favoritePk1);
    FavoritePk favoritePk2 = new FavoritePk(post, user2);
    Favorite saveFavorite2 = createFavorite(favoritePk2);
    favoriteRepository.saveAll(List.of(saveFavorite1, saveFavorite2));

    List<Post> postList = postRepository.findAll();
    Long boardNumber = postList.get(0).getId();

    // when
    FavoriteListResponseDto favoriteList = favoriteService.getFavorites(boardNumber);

    // then
    assertThat(favoriteList.getFavoriteList()).hasSize(2);
    assertThat(favoriteList.getFavoriteList().get(0).getEmail()).isEqualTo("test13@naver.com");
    assertThat(favoriteList.getFavoriteList().get(0).getNickname()).isEqualTo("개구리왕눈이2");
    assertThat(favoriteList.getFavoriteList().get(0).getProfileImage()).isEqualTo("http://profileImage.png");
    assertThat(favoriteList.getFavoriteList().get(1).getEmail()).isEqualTo("test12@naver.com");
    assertThat(favoriteList.getFavoriteList().get(1).getNickname()).isEqualTo("개구리왕눈이1");
    assertThat(favoriteList.getFavoriteList().get(1).getProfileImage()).isEqualTo("http://profileImage.png");
  }

  private User createUser(String email, String password, String telNumber, String nickname) {
    return User.builder()
        .email(email)
        .password(password)
        .telNumber(telNumber)
        .nickname(nickname)
        .profileImage("http://profileImage.png")
        .address("용인시 기흥구 보정로")
        .roles(initRole())
        .build();
  }

  private List<Authority> initRole() {
    return Collections.singletonList(Authority.builder().role(GENERAL_USER).build());
  }

  private static Post createBoard(String title, String content, User user) {
    return Post.builder()
        .title(title)
        .content(content)
        .favoriteCount(0)
        .viewCount(0)
        .user(user)
        .build();
  }

  private static Favorite createFavorite(FavoritePk favoritePk) {
    return Favorite.builder()
        .favoritePk(favoritePk).build();
  }
}