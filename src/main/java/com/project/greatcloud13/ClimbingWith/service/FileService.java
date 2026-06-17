package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.common.ErrorCode;
import com.project.greatcloud13.ClimbingWith.entity.FileEntity;
import com.project.greatcloud13.ClimbingWith.exception.file.FileUploadException;
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
    public String saveFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileUploadException(ErrorCode.FILE_EMPTY);
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new FileUploadException(ErrorCode.FILE_INVALID_NAME);
        }

        int dotIndex = originalName.lastIndexOf(".");
        if (dotIndex == -1) {
            throw new FileUploadException(ErrorCode.FILE_NO_EXTENSION);
        }

        String extension = originalName.substring(dotIndex);
        String savedName = UUID.randomUUID() + extension;
        String separator = uploadPath.endsWith(File.separator) ? "" : File.separator;
        File targetFile = new File(uploadPath + separator + savedName);

        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            throw new FileUploadException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        fileRepository.save(FileEntity.builder()
                .orgName(originalName)
                .savedName(savedName)
                .savedPath(targetFile.getAbsolutePath())
                .ext(extension)
                .size(file.getSize())
                .build());

        return savedName;
    }
}
