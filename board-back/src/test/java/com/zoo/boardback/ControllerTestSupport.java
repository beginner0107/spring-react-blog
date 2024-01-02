package com.zoo.boardback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoo.boardback.domain.auth.api.AuthController;
import com.zoo.boardback.domain.auth.application.AuthService;
import com.zoo.boardback.domain.board.api.BoardController;
import com.zoo.boardback.domain.board.application.BoardService;
import com.zoo.boardback.domain.favorite.application.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = {
    AuthController.class,
    BoardController.class
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
}

