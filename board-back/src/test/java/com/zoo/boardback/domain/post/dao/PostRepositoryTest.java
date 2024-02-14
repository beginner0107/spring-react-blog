package com.zoo.boardback.domain.post.dao;

import static com.zoo.boardback.domain.auth.entity.role.UserRole.GENERAL_USER;
import static org.assertj.core.api.Assertions.assertThat;

import com.zoo.boardback.IntegrationTestSupport;
import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.domain.post.entity.Post;
import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.domain.user.entity.User;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class PostRepositoryTest extends IntegrationTestSupport {

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private UserRepository userRepository;

  @DisplayName("게시글을 조회할 때 작성한 회원의 정보도 가지고 온다.")
  @Test
  void findByPostId() {
    // given
    User user1 = createUser("test12@naver.com", "testpassword123"
    , "01022222222", "개구리왕눈이");
    User user2 = createUser("test13@naver.com", "testpassword123"
        , "01022222221", "황소고집1");
    userRepository.saveAll(List.of(user1, user2));

    Post post1 = createPost("제목1", "내용입니다.1", user1);
    Post post2 = createPost("제목2", "내용입니다.2", user2);
    postRepository.saveAll(List.of(post1, post2));

    List<Post> postList = postRepository.findAll();

    // when
    Post post = postRepository.findById(
        postList.get(0).getId()).orElseThrow();

    // then
    assertThat(post.getTitle()).isEqualTo("제목1");
    assertThat(post.getContent()).isEqualTo("내용입니다.1");
    assertThat(post.getUser().getEmail()).isEqualTo("test12@naver.com");
    assertThat(post.getUser().getNickname()).isEqualTo("개구리왕눈이");
    assertThat(post.getUser().getAddress()).isEqualTo("용인시 기흥구 보정로");
  }

  private static Post createPost(String title, String content, User user) {
    return Post.builder()
        .title(title)
        .content(content)
        .favoriteCount(0)
        .viewCount(0)
        .user(user)
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
}