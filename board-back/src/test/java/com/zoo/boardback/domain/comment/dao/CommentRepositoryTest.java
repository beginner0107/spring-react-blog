package com.zoo.boardback.domain.comment.dao;

import static com.zoo.boardback.domain.auth.entity.role.UserRole.GENERAL_USER;
import static org.assertj.core.api.Assertions.assertThat;

import com.zoo.boardback.IntegrationTestSupport;
import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.domain.post.dao.PostRepository;
import com.zoo.boardback.domain.post.entity.Post;
import com.zoo.boardback.domain.comment.dto.query.CommentQueryDto;
import com.zoo.boardback.domain.comment.entity.Comment;
import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.domain.user.entity.User;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class CommentRepositoryTest extends IntegrationTestSupport {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PostRepository postRepository;
  @Autowired
  private CommentRepository commentRepository;

  @DisplayName("게시글의 댓글 목록을 조회하면서, 댓글 작성자의 정보도 조회한다.")
  @Test
  void getCommentsList() {
    // given
    User user = createUser("test12@naver.com", "testpassword123"
        , "01022222222", "개구리왕눈이");
    User newUser = userRepository.save(user);
    Post post = createPost(newUser);
    Post newPost = postRepository.save(post);
    Comment comment1 = createComment("댓글을 답니다1.!", newPost, newUser);
    commentRepository.save(comment1);

    // when
    Page<CommentQueryDto> comments = commentRepository.getCommentsList(newPost, PageRequest.of(0, 5));

    // then
    assertThat(comments).hasSize(1);
    assertThat(comments.getContent().get(0).getNickname()).isEqualTo("개구리왕눈이");
    assertThat(comments.getContent().get(0).getProfileImage()).isEqualTo("http://localhost:8080/profileImage.png");
    assertThat(comments.getContent().get(0).getContent()).isEqualTo("댓글을 답니다1.!");
  }

  private Post createPost(User user) {
    return Post.builder()
        .user(user)
        .title("글의 제목")
        .content("글의 컨텐츠")
        .favoriteCount(0)
        .viewCount(0)
        .build();
  }

  private Comment createComment(String content, Post post, User user) {
    return Comment.builder()
        .content(content)
        .post(post)
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
        .profileImage("http://localhost:8080/profileImage.png")
        .roles(initRole())
        .build();
  }

  private List<Authority> initRole() {
    return Collections.singletonList(Authority.builder().role(GENERAL_USER).build());
  }
}