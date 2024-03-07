package com.zoo.boardback.domain.image.api;

import com.zoo.boardback.domain.ApiResponse;
import com.zoo.boardback.global.util.image.ImageFileManager;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
public class ImageController {

    private final ImageFileManager imageFileManager;

    @PostMapping("/upload")
    public ApiResponse<String> imageUpload(@RequestParam("file") MultipartFile file) {
        return ApiResponse.ok(imageFileManager.upload(file));
    }

    @GetMapping(value = "{fileName}", produces = {MediaType.IMAGE_JPEG_VALUE,
        MediaType.IMAGE_PNG_VALUE})
    public Resource getImage(
        @PathVariable("fileName") String fileName
    ) {
        return imageFileManager.getImage(fileName);
    }
}
