package com.zoo.boardback.docs.file;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zoo.boardback.docs.RestDocsSupport;
import com.zoo.boardback.domain.file.api.FileController;
import com.zoo.boardback.global.util.file.FileUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;

public class FileControllerDocsTest extends RestDocsSupport {

    private final FileUtil fileUtil = mock(FileUtil.class);

    @Override
    protected Object initController() {
        return new FileController(fileUtil);
    }

    @Test
    @DisplayName("회원은 파일을 업로드 한다.")
    void upload() throws Exception {
        String originalFileName = "test.jpg";
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String saveFileName = "some-uuid" + extension;
        MockMultipartFile file = new MockMultipartFile("file", originalFileName, IMAGE_JPEG_VALUE,
            "test data".getBytes());
        String savePath = "http://localhost:8084/file/" + saveFileName;
        given(fileUtil.upload(file)).willReturn(savePath);

        mockMvc.perform(multipart("/file/upload")
                .file(file))
            .andExpect(status().isOk())
            .andDo(document("file-upload",
                preprocessResponse(prettyPrint()),
                requestParts(
                    partWithName("file").description("업로드할 파일")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER)
                        .description("코드"),
                    fieldWithPath("status").type(JsonFieldType.STRING)
                        .description("상태"),
                    fieldWithPath("message").type(JsonFieldType.STRING)
                        .description("메시지"),
                    fieldWithPath("field").type(JsonFieldType.STRING)
                        .optional()
                        .description("에러 발생 필드명"),
                    fieldWithPath("data").type(JsonFieldType.STRING)
                        .description("파일이 업로드된 경로")
                )
            ));
    }

    @Test
    @DisplayName("이미지를 화면에 보여준다.")
    void getImage() throws Exception {
        String fileName = "test.jpg";

        given(fileUtil.getImage(fileName)).willReturn(new ByteArrayResource(new byte[0]));

        mockMvc.perform(get("/file/{fileName}", fileName))
            .andExpect(status().isOk())
            .andExpect(content().contentType(IMAGE_JPEG_VALUE + ";charset=UTF-8"))
            .andDo(document("file-showImage",
                pathParameters(
                    parameterWithName("fileName").description("파일 이름")
                )
            ));
    }
}
