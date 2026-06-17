package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.entity.FileEntity;
import com.project.greatcloud13.ClimbingWith.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadPath;
    private final FileRepository fileRepository;

    @Transactional
    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new IllegalArgumentException("파일 이름이 유효하지 않습니다.");
        }

        int dotIndex = originalName.lastIndexOf(".");
        if (dotIndex == -1) {
            throw new IllegalArgumentException("확장자가 없는 파일은 업로드할 수 없습니다.");
        }

        String extension = originalName.substring(dotIndex);
        String uuid = UUID.randomUUID().toString();
        String savedName = uuid + extension;

        String separator = uploadPath.endsWith(File.separator) ? "" : File.separator;
        File targetFile = new File(uploadPath + separator + savedName);
        file.transferTo(targetFile);

        FileEntity fileEntity = FileEntity.builder()
                .orgName(originalName)
                .savedName(savedName)
                .savedPath(targetFile.getAbsolutePath())
                .ext(extension)
                .size(file.getSize())
                .build();

        fileRepository.save(fileEntity);

        return savedName;
    }
}
