package com.zoo.boardback.domain.favorite.dao;

import static com.zoo.boardback.domain.user.entity.role.UserRole.GENERAL_USER;
import static org.assertj.core.api.Assertions.assertThat;

import com.zoo.boardback.IntegrationTestSupport;
import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.domain.post.dao.PostRepository;
import com.zoo.boardback.domain.post.entity.Post;
import com.zoo.boardback.domain.favorite.entity.Favorite;
import com.zoo.boardback.domain.favorite.entity.primaryKey.FavoritePk;
import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.domain.user.entity.User;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class FavoriteRepositoryTest extends IntegrationTestSupport {

  @Autowired
  private FavoriteRepository favoriteRepository;
  @Autowired
  private PostRepository postRepository;
  @Autowired
  private UserRepository userRepository;

  @DisplayName("회원이 하나의 게시글에 좋아요 버튼을 누른 기록을 조회한다.[초기-빈 값]")
  @Test
  void findByFavoritePkNull() {
    // given
    User user1 = createUser("test12@naver.com", "testpassword123"
        , "01022222222", "개구리왕눈이");
    userRepository.save(user1);
    Post post1 = createBoard("제목1", "내용입니다.1", user1);
    postRepository.save(post1);
    FavoritePk favoritePk = new FavoritePk(post1, user1);

    // when
    Favorite favorite = favoriteRepository.findByFavoritePk(favoritePk);

    // then
    assertThat(favorite).isNull();
  }

  @DisplayName("회원이 하나의 게시글에 좋아요 버튼을 누른 기록을 조회한다.[좋아요 클릭 이후]")
  @Test
  void findByFavoritePk() {
    // given
    User user1 = createUser("test12@naver.com", "testpassword123"
        , "01022222222", "개구리왕눈이");
    userRepository.save(user1);
    Post post1 = createBoard("제목1", "내용입니다.1", user1);
    postRepository.save(post1);
    FavoritePk favoritePk = new FavoritePk(post1, user1);
    Favorite saveFavorite = createFavorite(favoritePk);
    favoriteRepository.save(saveFavorite);

    // when
    Favorite favorite = favoriteRepository.findByFavoritePk(favoritePk);

    // then
    assertThat(favorite.getFavoritePk().getUser()).isEqualTo(user1);
    assertThat(favorite.getFavoritePk().getPost()).isEqualTo(post1);
  }

  @DisplayName("좋아요를 누른 유저들의 목록을 가져온다.")
  @Test
  void findRecommendersByBoard() {
    // given
    User user1 = createUser("test12@naver.com", "testpassword123"
        , "01022222222", "개구리왕눈이1");
    userRepository.save(user1);

    Post post1 = createBoard("제목1", "내용입니다.1", user1);
    postRepository.save(post1);

    FavoritePk favoritePk1 = new FavoritePk(post1, user1);
    Favorite saveFavorite1 = createFavorite(favoritePk1);
    favoriteRepository.save(saveFavorite1);

    // when
    List<Favorite> recommenderUserList = favoriteRepository.findRecommendersByPost(post1);

    // then
    assertThat(recommenderUserList).hasSize(1);
    assertThat(recommenderUserList.get(0).getFavoritePk().getUser().getEmail())
        .isEqualTo("test12@naver.com");
    assertThat(recommenderUserList.get(0).getFavoritePk().getUser().getNickname())
        .isEqualTo("개구리왕눈이1");
    assertThat(recommenderUserList.get(0).getFavoritePk().getUser().getProfileImage())
        .isEqualTo("http://profileImage.png");
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