package com.zoo.boardback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoo.boardback.domain.auth.api.AuthController;
import com.zoo.boardback.domain.auth.application.AuthService;
import com.zoo.boardback.domain.board.api.BoardController;
import com.zoo.boardback.domain.board.application.BoardService;
import com.zoo.boardback.domain.comment.api.CommentController;
import com.zoo.boardback.domain.comment.application.CommentService;
import com.zoo.boardback.domain.favorite.application.FavoriteService;
import com.zoo.boardback.domain.file.api.FileController;
import com.zoo.boardback.domain.user.api.UserController;
import com.zoo.boardback.domain.user.application.UserService;
import com.zoo.boardback.global.util.file.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = {
    AuthController.class,
    BoardController.class,
    FileController.class,
    UserController.class,
    CommentController.class
})
public abstract class ControllerTestSupport {
  @Autowired
  protected MockMvc mockMvc;
  @Autowired
  protected ObjectMapper objectMapper;

  @MockBean
  protected AuthService authService;

  @MockBean
  protected BoardService boardService;

  @MockBean
  protected FavoriteService favoriteService;

  @MockBean
  protected FileUtil fileUtil;

  @MockBean
  protected UserService userService;

  @MockBean
  protected CommentService commentService;
}

