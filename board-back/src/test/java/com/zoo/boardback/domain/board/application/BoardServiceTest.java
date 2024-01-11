package com.zoo.boardback.domain.board.application;

import static com.zoo.boardback.global.error.ErrorCode.BOARD_NOT_FOUND;
import static com.zoo.boardback.global.error.ErrorCode.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.zoo.boardback.IntegrationTestSupport;
import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.domain.board.dao.BoardRepository;
import com.zoo.boardback.domain.board.dto.request.PostCreateRequestDto;
import com.zoo.boardback.domain.board.dto.response.PostDetailResponseDto;
import com.zoo.boardback.domain.board.entity.Board;
import com.zoo.boardback.domain.comment.dao.CommentRepository;
import com.zoo.boardback.domain.comment.entity.Comment;
import com.zoo.boardback.domain.image.dao.ImageRepository;
import com.zoo.boardback.domain.image.entity.Image;
import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.domain.user.entity.User;
import com.zoo.boardback.global.error.BusinessException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BoardServiceTest extends IntegrationTestSupport {

  @Autowired
  private BoardRepository boardRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ImageRepository imageRepository;
  @Autowired
  private CommentRepository commentRepository;
  @Autowired
  private BoardService boardService;

  @AfterEach
  void tearDown() {
    imageRepository.deleteAllInBatch();
    commentRepository.deleteAllInBatch();
    boardRepository.deleteAllInBatch();
    userRepository.deleteAllInBatch();
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
    boardService.create(request, email);

    // then
    List<Board> boardList = boardRepository.findAll();
    assertThat(boardList).hasSize(1);
    assertThat(boardList.get(0).getTitle()).isEqualTo(title);
    assertThat(boardList.get(0).getContent()).isEqualTo(content);
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
    assertThatThrownBy(() -> boardService.create(request, email))
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
    Board board = createBoard(title, content, user);
    boardRepository.save(board);

    String imageUrl = "https://testImage.png";
    Image image = createImage(imageUrl, board);
    imageRepository.save(image);

    List<Board> boardList = boardRepository.findAll();
    int boardNumber = boardList.get(0).getBoardNumber();

    // when
    PostDetailResponseDto response = boardService.find(boardNumber);

    // then
    assertThat(response.getTitle()).isEqualTo(title);
    assertThat(response.getContent()).isEqualTo(content);
    assertThat(response.getBoardImageList()).hasSize(1);
    assertThat(response.getBoardImageList().get(0)).isEqualTo(imageUrl);
    assertThat(response.getWriterEmail()).isEqualTo(email);
    assertThat(response.getWriterNickname()).isEqualTo(nickname);
    assertThat(response.getWriterProfileImage()).isNull();
  }

  @DisplayName("게시글의 번호가 유효하지 않으면 게시글의 상세정보를 볼 수 없다.")
  @Test
  void findPostDetailFailBoardNumberFalse() {
    // given

    // when & then
    assertThatThrownBy(() -> boardService.find(1))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(BOARD_NOT_FOUND.getMessage());
  }

  @DisplayName("회원 본인이 작성한 게시글을 삭제할 수 있다.")
  @Test
  void deleteComment() {
    // given
    String email = "test12@naver.com";
    String nickname = "개구리왕눈이";
    User user = createUser(email, "testpassword123"
        , "01022222222", nickname);
    User newUser = userRepository.save(user);

    String title = "테스트 글의 제목";
    String content = "테스트 글의 내용";
    Board board = createBoard(title, content, user);
    Board newBoard = boardRepository.save(board);

    String imageUrl = "https://testImage.png";
    Image image = createImage(imageUrl, board);
    imageRepository.save(image);

    LocalDateTime createdAt = LocalDateTime.now();
    LocalDateTime updatedAt = LocalDateTime.now();
    Comment comment = createComment("댓글을 답니다1.!", newBoard, newUser, createdAt, updatedAt);
    commentRepository.save(comment);

    // when
    boardService.deletePost(newBoard.getBoardNumber(), email);

    // then
    List<Board> posts = boardRepository.findAll();
    assertThat(posts).hasSize(0);
  }

  private static Image createImage(String imageUrl, Board board) {
    return Image.builder()
        .imageUrl(imageUrl)
        .board(board)
        .build();
  }

  private static Board createBoard(String title, String content, User user) {
    return Board.builder()
        .title(title)
        .content(content)
        .viewCount(0)
        .favoriteCount(0)
        .commentCount(0)
        .user(user)
        .build();
  }

  private static PostCreateRequestDto createPostCreateRequest(String title, String content) {
    return PostCreateRequestDto.builder()
        .title(title)
        .content(content)
        .boardImageList(List.of("https://testImage2.png",
            "https://testImage3.png"))
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
    return Collections.singletonList(Authority.builder().name("ROLE_USER").build());
  }

  private Comment createComment(String content, Board board, User user
      , LocalDateTime createdAt, LocalDateTime updatedAt) {
    return Comment.builder()
        .content(content)
        .board(board)
        .user(user)
        .createdAt(createdAt)
        .updatedAt(updatedAt)
        .build();
  }
}