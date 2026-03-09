package com.project.greatcloud13.ClimbingWith.controller;

import com.project.greatcloud13.ClimbingWith.dto.FileUploadResponseDTO;
import com.project.greatcloud13.ClimbingWith.service.FileService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponseDTO> uploadFile(@RequestPart("file")MultipartFile file){
        FileUploadResponseDTO fileUploadResponseDTO = new FileUploadResponseDTO();
        try {
            String savedName = fileService.saveFile(file);

            fileUploadResponseDTO.setMessage("업로드에 성공하였습니다.");
            fileUploadResponseDTO.setFileName(savedName);
            return ResponseEntity.ok(fileUploadResponseDTO);
        }catch (IOException e){
            fileUploadResponseDTO.setMessage("파일 업로드에 실패하였습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(fileUploadResponseDTO);
        } catch (IllegalArgumentException e){
            fileUploadResponseDTO.setMessage("파일이 비어있습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(fileUploadResponseDTO);
        }

    }

}
