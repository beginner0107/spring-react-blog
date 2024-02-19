package com.zoo.boardback.domain.comment.application;

import static com.zoo.boardback.domain.auth.entity.role.UserRole.GENERAL_USER;
import static org.assertj.core.api.Assertions.assertThat;

import com.zoo.boardback.IntegrationTestSupport;
import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.domain.post.dao.PostRepository;
import com.zoo.boardback.domain.post.entity.Post;
import com.zoo.boardback.domain.comment.dao.CommentRepository;
import com.zoo.boardback.domain.comment.dto.request.CommentCreateRequestDto;
import com.zoo.boardback.domain.comment.dto.request.CommentUpdateRequestDto;
import com.zoo.boardback.domain.comment.dto.response.CommentListResponseDto;
import com.zoo.boardback.domain.comment.dto.response.CommentResponse;
import com.zoo.boardback.domain.comment.entity.Comment;
import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.domain.user.entity.User;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

class CommentServiceTest extends IntegrationTestSupport {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PostRepository postRepository;
  @Autowired
  private CommentRepository commentRepository;
  @Autowired
  private CommentService commentService;


  @AfterEach
  void tearDown() {
    commentRepository.deleteAllInBatch();
    postRepository.deleteAllInBatch();
    userRepository.deleteAllInBatch();
  }

  @DisplayName("회원은 게시글에 댓글을 작성할 수 있다.")
  @Test
  void create() {
    // given
    User user = createUser("test12@naver.com", "testpassword123"
        , "01022222222", "개구리왕눈이");
    User newUser = userRepository.save(user);
    Post post = createPost(newUser);
    Post newPost = postRepository.save(post);
    String content = "안녕하세요. 댓글을 작성하겠습니다.";
    CommentCreateRequestDto commentCreateRequestDto = CommentCreateRequestDto.builder()
        .postId(newPost.getId())
        .content(content)
        .build();

    // when
    commentService.create(newUser, commentCreateRequestDto);

    // then
    List<Comment> comments = commentRepository.findAll();
    assertThat(comments).hasSize(1);
    assertThat(comments.get(0).getContent()).isEqualTo(content);
  }

  @DisplayName("블로그를 사용하는 유저는 댓글 목록을 볼 수 있다.")
  @Test
  void getComments() {
    // given
    User user = createUser("test12@naver.com", "testpassword123"
        , "01022222222", "개구리왕눈이");
    User newUser = userRepository.save(user);
    Post post = createPost(newUser);
    Post newPost = postRepository.save(post);

    Comment comment1 = createComment("댓글을 답니다1.!", newPost, newUser);
    Comment comment2 = createComment("댓글을 답니다2.!", newPost, newUser);
    commentRepository.save(comment1);
    commentRepository.save(comment2);

    // when
    CommentListResponseDto commentsList= commentService.getComments(newPost.getId(),
        PageRequest.of(0, 5));

    // then
    assertThat(commentsList).isNotNull();

    List<CommentResponse> commentsResponse = commentsList.getCommentListResponse();
    assertThat(commentsResponse).hasSize(2);
    assertThat(commentsResponse.get(0).getNickname()).isEqualTo("개구리왕눈이");
    assertThat(commentsResponse.get(0).getProfileImage()).isEqualTo("http://localhost:8080/profileImage.png");
    assertThat(commentsResponse.get(0).getContent()).isEqualTo("댓글을 답니다2.!");
    assertThat(commentsResponse.get(1).getNickname()).isEqualTo("개구리왕눈이");
    assertThat(commentsResponse.get(1).getProfileImage()).isEqualTo("http://localhost:8080/profileImage.png");
    assertThat(commentsResponse.get(1).getContent()).isEqualTo("댓글을 답니다1.!");
  }

  @DisplayName("회원은 게시글의 댓글을 수정할 수 있다.")
  @Test
  void editComment() {
    // given
    String email = "test12@naver.com";
    User user = createUser(email, "testpassword123"
        , "01022222222", "개구리왕눈이");
    User newUser = userRepository.save(user);
    Post post = createPost(newUser);
    Post newPost = postRepository.save(post);
    Comment comment = createComment("댓글을 답니다1.!", newPost, newUser);
    Comment newComment = commentRepository.save(comment);
    String updateContent = "댓글을 수정~ 하겠습니다.";
    CommentUpdateRequestDto updateRequestDto = CommentUpdateRequestDto.builder()
        .postId(1L)
        .content(updateContent)
        .build();

    // when
    commentService.update(email, newComment.getId(), updateRequestDto);

    // then
    List<Comment> comments = commentRepository.findAll();
    assertThat(comments).hasSize(1);
    assertThat(comments.get(0).getContent()).isEqualTo(updateContent);
  }

  @DisplayName("회원은 댓글을 삭제할 수 있다.")
  @Test
  void deleteComment() {
    // given
    User user = createUser("test12@naver.com", "testpassword123"
        , "01022222222", "개구리왕눈이");
    User newUser = userRepository.save(user);
    Post post = createPost(newUser);
    Post newPost = postRepository.save(post);
    Comment comment = createComment("댓글을 답니다1.!", newPost, newUser);
    Comment newComment = commentRepository.save(comment);

    // when
    commentService.delete(newComment.getId(), "test12@naver.com");

    // then
    List<Comment> comments = commentRepository.findAll();
    assertThat(comments).hasSize(0);
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

  private Post createPost(User user) {
    return Post.builder()
        .user(user)
        .title("글의 제목")
        .content("글의 컨텐츠")
        .commentCount(0)
        .favoriteCount(0)
        .viewCount(0)
        .build();
  }

  private Comment createComment(String content, Post post, User user
  ) {
    return Comment.builder()
        .content(content)
        .post(post)
        .user(user)
        .build();
  }

}