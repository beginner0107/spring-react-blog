package com.zoo.boardback.domain.image.api;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zoo.boardback.ControllerTestSupport;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class ImageControllerTest extends ControllerTestSupport {


    @DisplayName("회원은 파일을 업로드 한다.")
    @Test
    void upload() throws Exception {
        // given
        String originalFileName = "test.jpg";
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String saveFileName = UUID.randomUUID() + extension;
        MockMultipartFile file = new MockMultipartFile("file", originalFileName, IMAGE_JPEG_VALUE,
            "test data".getBytes());
        String savePath = "http://localhost:8084/image" + saveFileName;
        given(fileUtil.upload(file)).willReturn(savePath);

        // when & then
        mockMvc.perform(multipart("/image/upload")
                .file(file))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.message").value("OK"))
            .andExpect(jsonPath("$.data").value(savePath));
    }

    @Test
    @DisplayName("이미지를 화면에 보여준다.")
    void getImage() throws Exception {
        // given
        String fileName = "test.jpg";
        ByteArrayResource imageResource = new ByteArrayResource(new byte[0]);
        given(fileUtil.getImage(fileName)).willReturn(imageResource);

        // when & then
        mockMvc.perform(get("/image/{fileName}", fileName))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE));
    }
}