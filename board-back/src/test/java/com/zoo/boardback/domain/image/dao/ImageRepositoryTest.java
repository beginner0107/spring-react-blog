package com.zoo.boardback.domain.image.dao;

import static com.zoo.boardback.domain.auth.entity.role.UserRole.GENERAL_USER;
import static org.assertj.core.api.Assertions.assertThat;

import com.zoo.boardback.IntegrationTestSupport;
import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.domain.post.dao.PostRepository;
import com.zoo.boardback.domain.post.entity.Post;
import com.zoo.boardback.domain.image.entity.Image;
import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.domain.user.entity.User;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ImageRepositoryTest extends IntegrationTestSupport {

  @Autowired
  private ImageRepository imageRepository;
  @Autowired
  private PostRepository postRepository;
  @Autowired
  private UserRepository userRepository;


  @DisplayName("게시글에 포함된 이미지 목록을 가지고 온다.")
  @Test
  void findByPost() {
    // given
    User user1 = createUser("test12@naver.com", "testpassword123"
        , "01022222222", "개구리왕눈이");
    User user2 = createUser("test13@naver.com", "testpassword123"
        , "01022222221", "황소고집1");
    userRepository.saveAll(List.of(user1, user2));

    Post post1 = createPost("제목1", "내용입니다.1", user1);
    postRepository.save(post1);

    Image image1 = Image.builder()
        .post(post1).build();
    Image image2 = Image.builder()
        .post(post1).build();
    imageRepository.saveAll(List.of(image1, image2));

    // when
    List<Image> images = imageRepository.findByPost(post1);

    // then
    assertThat(images).hasSize(2);
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