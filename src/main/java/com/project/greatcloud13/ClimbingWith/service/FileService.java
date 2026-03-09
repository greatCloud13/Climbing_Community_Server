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
    public String saveFile(MultipartFile file) throws IOException{
        if(file.isEmpty()){
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        try {
            String originalName = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            assert originalName != null;
            String extenstion = originalName.substring(originalName.lastIndexOf("."));
            String savedName = uuid + extenstion;

            File targetFile = new File(uploadPath + savedName);
            file.transferTo(targetFile);

            FileEntity fileEntity = FileEntity.builder()
                    .orgName(originalName)
                    .savedName(savedName)
                    .savedPath(targetFile.getAbsolutePath())
                    .build();

            fileRepository.save(fileEntity);

            return savedName;
        }catch (Exception e){
            System.out.print(e.getMessage());
            throw e;
        }
    }
}

