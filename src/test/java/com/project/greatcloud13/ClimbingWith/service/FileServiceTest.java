package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.common.ErrorCode;
import com.project.greatcloud13.ClimbingWith.entity.FileEntity;
import com.project.greatcloud13.ClimbingWith.exception.file.FileUploadException;
import com.project.greatcloud13.ClimbingWith.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/*==================테스트 코드 작성 규칙===========================
1. 테스트 대상 Class 내부 메소드 외의 의존성은 Mock 주입을 통해 동작 정의
2. 테스트에 사용되는 중복되는 파라미터 및 Entity는 클래스 영역에 정의
3. 테스트 코드 작성 양식은 [Given], [When], [Then] 양식으로 작성
4. verify를 통한 메소드의 동작 여부 또한 검증
 */

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    @InjectMocks
    private FileService fileService;

    @Mock private FileRepository fileRepository;
    @Mock private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileService, "uploadPath", System.getProperty("java.io.tmpdir"));
    }

    @Nested
    @DisplayName("saveFile() 메서드 테스트")
    class SaveFileTest {

        @Test
        @DisplayName("정상 파일 업로드 성공 - savedName 반환")
        void saveFile_success() throws IOException {
            // [Given]
            given(mockFile.isEmpty()).willReturn(false);
            given(mockFile.getOriginalFilename()).willReturn("test.jpg");
            given(mockFile.getSize()).willReturn(1024L);
            doNothing().when(mockFile).transferTo(any(java.io.File.class));

            // [When]
            String result = fileService.saveFile(mockFile);

            // [Then]
            assertThat(result).endsWith(".jpg");
            verify(fileRepository, times(1)).save(any(FileEntity.class));
        }

        @Test
        @DisplayName("빈 파일 → FILE_EMPTY(F001)")
        void saveFile_emptyFile() {
            // [Given]
            given(mockFile.isEmpty()).willReturn(true);

            // [When] & [Then]
            assertThatThrownBy(() -> fileService.saveFile(mockFile))
                    .isInstanceOf(FileUploadException.class)
                    .satisfies(e -> assertThat(((FileUploadException) e).getErrorCode())
                            .isEqualTo(ErrorCode.FILE_EMPTY));

            verify(fileRepository, never()).save(any());
        }

        @Test
        @DisplayName("originalFilename null → FILE_INVALID_NAME(F002)")
        void saveFile_nullFileName() {
            // [Given]
            given(mockFile.isEmpty()).willReturn(false);
            given(mockFile.getOriginalFilename()).willReturn(null);

            // [When] & [Then]
            assertThatThrownBy(() -> fileService.saveFile(mockFile))
                    .isInstanceOf(FileUploadException.class)
                    .satisfies(e -> assertThat(((FileUploadException) e).getErrorCode())
                            .isEqualTo(ErrorCode.FILE_INVALID_NAME));

            verify(fileRepository, never()).save(any());
        }

        @Test
        @DisplayName("확장자 없는 파일 → FILE_NO_EXTENSION(F003)")
        void saveFile_noExtension() {
            // [Given]
            given(mockFile.isEmpty()).willReturn(false);
            given(mockFile.getOriginalFilename()).willReturn("filename_without_extension");

            // [When] & [Then]
            assertThatThrownBy(() -> fileService.saveFile(mockFile))
                    .isInstanceOf(FileUploadException.class)
                    .satisfies(e -> assertThat(((FileUploadException) e).getErrorCode())
                            .isEqualTo(ErrorCode.FILE_NO_EXTENSION));

            verify(fileRepository, never()).save(any());
        }

        @Test
        @DisplayName("파일 저장 실패(IOException) → FILE_UPLOAD_FAILED(F004)")
        void saveFile_ioException() throws IOException {
            // [Given]
            given(mockFile.isEmpty()).willReturn(false);
            given(mockFile.getOriginalFilename()).willReturn("test.png");
            doThrow(new IOException("disk full")).when(mockFile).transferTo(any(java.io.File.class));

            // [When] & [Then]
            assertThatThrownBy(() -> fileService.saveFile(mockFile))
                    .isInstanceOf(FileUploadException.class)
                    .satisfies(e -> assertThat(((FileUploadException) e).getErrorCode())
                            .isEqualTo(ErrorCode.FILE_UPLOAD_FAILED));

            verify(fileRepository, never()).save(any());
        }
    }
}
