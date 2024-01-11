package com.zoo.boardback.docs;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoo.boardback.domain.board.api.BoardController;
import com.zoo.boardback.domain.board.application.BoardService;
import com.zoo.boardback.domain.comment.api.CommentController;
import com.zoo.boardback.domain.comment.application.CommentService;
import com.zoo.boardback.domain.favorite.application.FavoriteService;
import com.zoo.boardback.domain.user.api.UserController;
import com.zoo.boardback.domain.user.application.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Import(HttpEncodingAutoConfiguration.class)
@WebMvcTest({
    BoardController.class,
    UserController.class,
    CommentController.class
})
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public abstract class RestDocsSecuritySupport {

  @Autowired
  protected MockMvc mockMvc;
  protected ObjectMapper objectMapper = new ObjectMapper();

  @MockBean
  protected BoardService boardService;

  @MockBean
  protected FavoriteService favoriteService;

  @MockBean
  protected UserService userService;

  @MockBean
  protected CommentService commentService;

  @BeforeEach
  void setUp(WebApplicationContext webApplicationContext
      ,RestDocumentationContextProvider provider) {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .apply(documentationConfiguration(provider))
        .build();
  }
}
