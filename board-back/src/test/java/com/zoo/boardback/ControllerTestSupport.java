package com.zoo.boardback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoo.boardback.domain.auth.api.AuthController;
import com.zoo.boardback.domain.auth.application.AuthService;
import com.zoo.boardback.domain.post.api.PostController;
import com.zoo.boardback.domain.post.application.PostCacheService;
import com.zoo.boardback.domain.post.application.PostService;
import com.zoo.boardback.domain.comment.api.CommentController;
import com.zoo.boardback.domain.comment.application.CommentService;
import com.zoo.boardback.domain.favorite.application.FavoriteService;
import com.zoo.boardback.domain.file.api.FileController;
import com.zoo.boardback.domain.searchLog.api.SearchLogController;
import com.zoo.boardback.domain.searchLog.application.SearchLogService;
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
    PostController.class,
    FileController.class,
    UserController.class,
    CommentController.class,
    SearchLogController.class
})
public abstract class ControllerTestSupport {
  @Autowired
  protected MockMvc mockMvc;
  @Autowired
  protected ObjectMapper objectMapper;

  @MockBean
  protected AuthService authService;

  @MockBean
  protected PostService postService;

  @MockBean
  protected FavoriteService favoriteService;

  @MockBean
  protected FileUtil fileUtil;

  @MockBean
  protected UserService userService;

  @MockBean
  protected CommentService commentService;

  @MockBean
  protected SearchLogService searchLogService;

  @MockBean
  protected PostCacheService postCacheService;
}

