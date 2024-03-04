package com.zoo.boardback.domain.comment.application;

import static com.zoo.boardback.domain.user.entity.role.UserRole.GENERAL_USER;
import static org.assertj.core.api.Assertions.assertThat;

import com.zoo.boardback.IntegrationTestSupport;
import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.domain.comment.dao.CommentRepository;
import com.zoo.boardback.domain.comment.dto.query.ChildCommentQueryDto;
import com.zoo.boardback.domain.comment.dto.request.CommentCreateRequestDto;
import com.zoo.boardback.domain.comment.dto.request.CommentUpdateRequestDto;
import com.zoo.boardback.domain.comment.dto.response.CommentListResponseDto;
import com.zoo.boardback.domain.comment.dto.response.CommentResponse;
import com.zoo.boardback.domain.comment.entity.Comment;
import com.zoo.boardback.domain.post.dao.PostRepository;
import com.zoo.boardback.domain.post.entity.Post;
import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.domain.user.entity.User;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
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
        commentService.create(commentCreateRequestDto, newUser);

        // then
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getContent()).isEqualTo(content);
    }

    @DisplayName("회원은 게시글의 댓글의 대댓글을 작성할 수 있다.")
    @Test
    void givenComment_whenChildCommentSave_thenSavedChildComment() {
        // given
        User user = createUser("test12@naver.com", "testpassword123"
            , "01022222222", "개구리왕눈이");
        User newUser = userRepository.save(user);
        Post post = createPost(newUser);
        Post newPost = postRepository.save(post);
        String content = "1번 부모 댓글을 작성하겠습니다.";
        Comment comment = createComment(content, newPost, user);
        Comment savedComment = commentRepository.save(comment);

        String childContent = "1번 부모 댓글의 자식 1번 댓글입니다.";
        CommentCreateRequestDto commentCreateRequestDto = CommentCreateRequestDto.builder()
            .postId(newPost.getId())
            .commentId(savedComment.getId())
            .content(childContent)
            .build();

        // when
        commentService.create(commentCreateRequestDto, newUser);

        // then
        List<ChildCommentQueryDto> comments = commentRepository.getChildComments(newPost.getId(),
            savedComment.getId());
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getContent()).isEqualTo(childContent);
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
        CommentListResponseDto commentsList = commentService.getComments(newPost.getId(),
            PageRequest.of(0, 5));

        // then
        assertThat(commentsList).isNotNull();

        List<CommentResponse> commentsResponse = commentsList.getCommentListResponse();
        assertThat(commentsResponse).hasSize(2);
        assertThat(commentsResponse.get(0).getNickname()).isEqualTo("개구리왕눈이");
        assertThat(commentsResponse.get(0).getProfileImage()).isEqualTo(
            "http://localhost:8080/profileImage.png");
        assertThat(commentsResponse.get(0).getContent()).isEqualTo("댓글을 답니다2.!");
        assertThat(commentsResponse.get(1).getNickname()).isEqualTo("개구리왕눈이");
        assertThat(commentsResponse.get(1).getProfileImage()).isEqualTo(
            "http://localhost:8080/profileImage.png");
        assertThat(commentsResponse.get(1).getContent()).isEqualTo("댓글을 답니다1.!");
    }

    @DisplayName("대댓글이 있는 경우 버튼을 클릭해 대댓글 목록을 볼 수 있다.")
    @Test
    void givenPostIdAndCommentId_whenGetChildComments_thenReturnChildComments() {
        // given
        User user = createUser("test12@naver.com", "testpassword123"
            , "01022222222", "개구리왕눈이");
        User newUser = userRepository.save(user);
        Post post = createPost(newUser);
        Post newPost = postRepository.save(post);

        Comment comment1 = createComment("1번 게시글의 1번댓글을 답니다.", newPost, newUser);
        Comment comment2 = createComment("1번 게시글의 2번댓글을 답니다.", newPost, newUser);
        Comment savedComment1 = commentRepository.save(comment1);
        Comment savedComment2 = commentRepository.save(comment2);

        String content3 = "1번 게시글의 1번댓글의 1번댓글을 답니다.";
        String content4 = "1번 게시글의 1번댓글의 2번댓글을 답니다.";
        Comment comment3 = createComment(content3, newPost, newUser, savedComment1);
        Comment comment4 = createComment(content4, newPost, newUser, savedComment1);
        commentRepository.save(comment3);
        commentRepository.save(comment4);
        // when
        CommentListResponseDto commentsList = commentService.getChildComments(newPost.getId(),
            comment1.getId());

        // then
        assertThat(commentsList).isNotNull();

        List<CommentResponse> commentsResponse = commentsList.getCommentListResponse();
        assertThat(commentsResponse).hasSize(2);
        assertThat(commentsResponse.get(0).getNickname()).isEqualTo("개구리왕눈이");
        assertThat(commentsResponse.get(0).getProfileImage()).isEqualTo(
            "http://localhost:8080/profileImage.png");
        assertThat(commentsResponse.get(0).getContent()).isEqualTo(content4);
        assertThat(commentsResponse.get(1).getNickname()).isEqualTo("개구리왕눈이");
        assertThat(commentsResponse.get(1).getProfileImage()).isEqualTo(
            "http://localhost:8080/profileImage.png");
        assertThat(commentsResponse.get(1).getContent()).isEqualTo(content3);
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
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getDelYn()).isTrue();
        assertThat(comments.get(0).getContent()).isEqualTo("[삭제된 댓글입니다]");
    }

    @Disabled
    @DisplayName("동시에 여러 회원이 댓글 생성 및 카운트 증가를 수행하면 게시글의 댓글 갯수가 증가한다.")
    @Test
    void givenComments_whenCommentsCreate_thenCountIncrement() throws InterruptedException {
        // given
        User user = createUser("test12@naver.com", "testpassword123"
            , "01022222222", "개구리왕눈이");
        User newUser = userRepository.save(user);
        Post post = createPost(newUser);
        Post newPost = postRepository.save(post);
        String content = "안녕하세요. 댓글을 작성하겠습니다.";

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        CommentCreateRequestDto commentCreateRequestDto = CommentCreateRequestDto.builder()
            .postId(newPost.getId())
            .content(content)
            .build();

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                commentService.create(commentCreateRequestDto, newUser);

                countDownLatch.countDown();
            });
        }
        countDownLatch.await();

        // then
        List<Comment> comments = commentRepository.findAll();
        List<Post> resultPosts = postRepository.findAll();
        assertThat(comments).hasSize(100);
        assertThat(resultPosts.get(0).getCommentCount()).isEqualTo(100);
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
            .parent(null)
            .delYn(false)
            .build();
    }

    private Comment createComment(String content, Post post, User user, Comment parent
    ) {
        return Comment.builder()
            .content(content)
            .post(post)
            .user(user)
            .parent(parent)
            .delYn(false)
            .build();
    }
}