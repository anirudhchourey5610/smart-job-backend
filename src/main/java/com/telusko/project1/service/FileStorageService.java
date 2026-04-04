package com.telusko.project1.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import com.telusko.project1.dto.ResumeMetadataDto;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;
@Service
public class FileStorageService {
    private final String UPLOAD_DIR = "uploads/";
    public String storeFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }
    public Resource loadFileAsResource(String fileName) throws MalformedURLException {
        Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists()) {
            return resource;
        } else {
            throw new RuntimeException("File not found " + fileName);
        }
    }
    
    public List<ResumeMetadataDto> getAllResumesMetadata() throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            return List.of();
        }
        
        List<ResumeMetadataDto> metadataList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try (Stream<Path> stream = Files.list(uploadPath)) {
            List<Path> files = stream
                    .filter(file -> !Files.isDirectory(file))
                    .collect(Collectors.toList());

            for (Path file : files) {
                String name = file.getFileName().toString();
                long sizeInBytes = Files.size(file);
                String size = (sizeInBytes / 1024) + " KB";
                
                FileTime fileTime = Files.getLastModifiedTime(file);
                String date = dateFormat.format(new Date(fileTime.toMillis()));
                
                metadataList.add(new ResumeMetadataDto(name, date, size));
            }
        }
        return metadataList;
    }

    public boolean deleteFile(String fileName) throws IOException {
        Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName).normalize();
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            return true;
        }
        return false;
    }
}
