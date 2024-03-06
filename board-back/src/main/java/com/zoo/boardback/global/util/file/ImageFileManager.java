package com.zoo.boardback.global.util.file;

import com.zoo.boardback.domain.image.dao.ImageRepository;
import com.zoo.boardback.domain.image.entity.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Component
public class ImageFileManager {

    @Value("${file.path}")
    private String filePath;
    @Value("${file.url}")
    private String fileUrl;

    private final ImageRepository imageRepository;

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

    public void deleteFile(String fileName) {
        try {
            Files.deleteIfExists(Paths.get(filePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanUpImages() {
        List<String> usedImageUrls = imageRepository.findAll().stream()
            .map(Image::getImageUrl)
            .toList();
        List<String> allUploadedImageUrls = getAllImageUrls();

        allUploadedImageUrls.removeAll(usedImageUrls);
        deleteUnusedImageFiles(allUploadedImageUrls.stream()
            .map(imageUrl -> imageUrl.substring(imageUrl.lastIndexOf("/")))
            .collect(Collectors.toList())
        );
    }

    private void deleteUnusedImageFiles(List<String> unusedImageFileNames) {
        for (String fileName : unusedImageFileNames) {
            try {
                Files.deleteIfExists(Paths.get(filePath + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> getAllImageUrls() {
        File directory = new File(filePath);
        List<String> fileNames = new ArrayList<>();

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    fileNames.add(fileUrl + file.getName());
                }
            }
        }
        return fileNames;
    }

}
