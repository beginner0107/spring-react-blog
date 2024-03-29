package com.zoo.boardback.docs;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoo.boardback.domain.auth.application.AuthCookieService;
import com.zoo.boardback.domain.comment.api.CommentController;
import com.zoo.boardback.domain.comment.application.CommentService;
import com.zoo.boardback.domain.favorite.application.FavoriteService;
import com.zoo.boardback.domain.post.api.PostController;
import com.zoo.boardback.domain.post.application.PostCacheService;
import com.zoo.boardback.domain.post.application.PostService;
import com.zoo.boardback.domain.user.api.UserController;
import com.zoo.boardback.domain.user.application.UserService;
import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.global.config.security.filter.RefreshTokenFilter;
import com.zoo.boardback.global.config.security.filter.token_condition.JwtTokenConditionFactory;
import com.zoo.boardback.global.config.security.jwt.JwtProvider;
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
    PostController.class,
    UserController.class,
    CommentController.class
})
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public abstract class RestDocsSecuritySupport {

    @Autowired
    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    protected PostService postService;

    @MockBean
    protected FavoriteService favoriteService;

    @MockBean
    protected UserService userService;

    @MockBean
    protected CommentService commentService;

    @MockBean
    protected PostCacheService postCacheService;

    @MockBean
    protected UserRepository userRepository;

    @MockBean
    protected RefreshTokenFilter refreshTokenFilter;

    @MockBean
    protected JwtProvider jwtProvider;

    @MockBean
    protected JwtTokenConditionFactory jwtTokenConditionFactory;

    @MockBean
    protected AuthCookieService authCookieService;


    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext
        , RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(provider))
            .build();
    }
}
