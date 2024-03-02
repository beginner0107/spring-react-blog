package com.zoo.boardback.global.util.file;

import java.io.File;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUtil {

    @Value("${file.path}")
    private String filePath;
    @Value("${file.url}")
    private String fileUrl;

    public String upload(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }

        // 디렉토리가 존재하지 않으면 생성
        File directory = new File(filePath);
        if (!directory.exists()) {
            directory.mkdirs(); // 디렉토리 생성
        }

        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        String saveFileName = uuid + extension;
        String savePath = filePath + saveFileName;

        try {
            file.transferTo(new File(savePath));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        String url = fileUrl + saveFileName;
        return url;
    }

    public Resource getImage(String fileName) {
        Resource resource = null;

        try {
            resource = new UrlResource("file:" + filePath + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return resource;
    }
}
