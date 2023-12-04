package com.zoo.boardback.domain.file.api;

import com.zoo.boardback.global.util.file.FileUtil;
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
@RequestMapping("/file")
public class FileController {

  private final FileUtil fileUtil;

  @PostMapping("/upload")
  public String upload(@RequestParam("file")MultipartFile file) {
    String url = fileUtil.upload(file);
    return url;
  }

  @GetMapping(value="{fileName}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
  public Resource getImage(
      @PathVariable("fileName") String fileName
  ) {
    Resource resource = fileUtil.getImage(fileName);
    return resource;
  }
}
