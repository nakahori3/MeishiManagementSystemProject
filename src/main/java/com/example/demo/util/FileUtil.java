package com.example.demo.util;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.web.multipart.MultipartFile;

public class FileUtil {

    private static final String UPLOAD_DIR = "src/main/resources/static/images";

    public static String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }
        // ファイルを保存するディレクトリを作成
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        Path filePath = Paths.get(UPLOAD_DIR, file.getOriginalFilename());
        try {
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Failed to save file: " + file.getOriginalFilename(), e);
        }
        return "/images/" + file.getOriginalFilename();
    }
}





//package com.example.demo.util;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.multipart.MultipartFile;
//
//public class FileUtil {
//
//    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
//
//    public static String saveFile(String uploadDir, MultipartFile file) throws IOException {
//        if (file.isEmpty()) {
//            return null;
//        }
//        Path uploadPath = Paths.get(uploadDir);
//        if (!Files.exists(uploadPath)) {
//            Files.createDirectories(uploadPath);
//        }
//        String fileName = file.getOriginalFilename();
//        Path filePath = uploadPath.resolve(fileName);
//        Files.copy(file.getInputStream(), filePath);
//        logger.info("Saved file: " + filePath.toString()); // デバッグ用ログ
//        return "/images/" + fileName;
//    }
//}
