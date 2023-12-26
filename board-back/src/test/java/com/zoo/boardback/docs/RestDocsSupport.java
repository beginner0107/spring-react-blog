package com.zoo.boardback.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@Import(HttpEncodingAutoConfiguration.class)
//@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsSupport {

  protected MockMvc mockMvc;
  protected ObjectMapper objectMapper = new ObjectMapper();

//  @BeforeEach
//  void setUp(RestDocumentationContextProvider provider) {
//    this.mockMvc = MockMvcBuilders.standaloneSetup(initController())
//        .addFilters(new CharacterEncodingFilter("UTF-8", true))
//        .apply(documentationConfiguration(provider))
//        .build();
//  }

  protected abstract Object initController();

}