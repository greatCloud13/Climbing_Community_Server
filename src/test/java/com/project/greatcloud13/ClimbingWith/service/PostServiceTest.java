package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.PostCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.PostResponseDTO;
import com.project.greatcloud13.ClimbingWith.entity.*;
import com.project.greatcloud13.ClimbingWith.repository.GymRepository;
import com.project.greatcloud13.ClimbingWith.repository.PostRepository;
import com.project.greatcloud13.ClimbingWith.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import java.time.LocalDate;
import java.util.Optional;

/*==================테스트 코드 작성 규칙===========================
1. 테스트 대상 Class 내부 메소드 외의 의존성은 Mock 주입을 통해 동작 정의
2. 테스트에 사용되는 중복되는 파라미터 및 Entity는 클래스 영역에 정의
3. 테스트 코드 작성 양식은 [Given], [When], [Then] 양식으로 작성
    3.1 [Given] 테스트에 필요한 파라미터, Mock 동작 정의
    3.2 [When] 테스트에 대한 상황을 정의
    3.3 [Then] 동작 이후 결과를 검증
    3.4 상황과 동시에 동작 결과가 발생하는 경우 [When & Then]을 통해 정의
4. verify를 통한 메소드의 동작 여부 또한 검증
 */

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    
    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GymRepository gymRepository;

//   ========================= Mock Objects =========================
    private Gym mockGym1;
    private Gym mockGym2;
    private User mockUser1;
    private User mockAdminUser;
    private User mockManagerUser1;
    private User mockManagerUser2;
    private Post mockPost;
//    ===============================================================
//    ==================== Mock Object parameter ====================

    String gymName1 = "테스트 암장1";
    Long gymId1 = 1L;
    String gymName2 = "테스트 암장2";
    Long gymId2 = 2L;

    Long testPostId = 601L;
    String postName1 = "테스트 게시글 이름 1";
    String postName2 = "테스트 게시글 이름2";
    String postContent1 = "테스트 게시글 내용1";
    String postContent2 = "테스트 게시글 내용2";
    String testUsername1 = "테스트 사용자1";
    Long testUserId1 = 500L;
    String testAdminUsername ="테스트 어드민1";
    Long adminUserId = 501L;
    String testManagerUsername1 = "테스트 암장 1 매니저";
    Long testManagerId1 = 502L;
    String testManagerUsername2 = "테스트 암장 2 매니저";
    Long testManagerId2 = 503L;


    Long invalidPostId = 999L;
    Long invalidUserId = 998L;
    Long invalidGymId = 997L;

//    ===============================================================
    
    
    @BeforeEach
    void setUp(){
//      Gym Mock Entity Build
        mockGym1=Gym.builder().gymName(gymName1).build();
        ReflectionTestUtils.setField(mockGym1, "id", gymId1);
        mockGym2=Gym.builder().gymName(gymName2).build();
        ReflectionTestUtils.setField(mockGym2, "id", gymId2);
//      User Mock Entity Build
        mockUser1=User.builder().username(testUsername1).build();
        ReflectionTestUtils.setField(mockUser1, "id", testUserId1);
        ReflectionTestUtils.setField(mockUser1, "role", Role.MEMBER);
        mockAdminUser=User.builder().username(testAdminUsername).build();
        ReflectionTestUtils.setField(mockAdminUser, "id", adminUserId);
        ReflectionTestUtils.setField(mockAdminUser, "role", Role.ADMIN);
        mockManagerUser1=User.builder().username(testManagerUsername1).build();
        ReflectionTestUtils.setField(mockManagerUser1, "id", testManagerId1);
        ReflectionTestUtils.setField(mockManagerUser1, "gym", mockGym1);
        ReflectionTestUtils.setField(mockManagerUser1, "role", Role.GYM_MANAGER);
        mockManagerUser2=User.builder().username(testManagerUsername2).build();
        ReflectionTestUtils.setField(mockManagerUser2, "id", testManagerId2);
        ReflectionTestUtils.setField(mockManagerUser2, "gym", mockGym2);
        ReflectionTestUtils.setField(mockManagerUser2, "role", Role.GYM_MANAGER);
        mockPost = Post.builder().title(postName1).content(postContent1).build();
        ReflectionTestUtils.setField(mockPost, "id", testPostId);



    }

    @Nested
    @DisplayName("createPost() 기능 테스트")
    class CreatePostTest{

        @Test
        @DisplayName("게시글 작성 성공")
        void createPost_Success(){
            //[Given] 테스트 환경 셋업
            PostCreateDTO requestDTO = new PostCreateDTO();
            requestDTO.setTitle(postName1);
            requestDTO.setPostType(PostType.NOTICE);
            requestDTO.setContent(postContent1);

            given(userRepository.findById(testManagerId1)).willReturn(Optional.of(mockManagerUser1));

            //[When]
            PostResponseDTO response = postService.createPost(testManagerId1, requestDTO);

            //[Then]
            assertThat(response).isNotNull();
            assertThat(response.getWriter()).isEqualTo(mockManagerUser1.getUsername());
            assertThat(response.getTitle()).isEqualTo(postName1);
            assertThat(response.getPostType()).isEqualTo(PostType.NOTICE.toString());
            assertThat(response.getContent()).isEqualTo(postContent1);
            assertThat(response.getGymName()).isEqualTo(mockManagerUser1.getGym().getGymName());

            verify(postRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자 ID 요청시 예외 발생")
        void createPost_UserNotFound(){
//          [Given] 테스트 환경 셋업
            PostCreateDTO requestDTO = new PostCreateDTO();
            given(userRepository.findById(invalidUserId)).willReturn(Optional.empty());

//          [When && Then]
            assertThatThrownBy(()-> postService.createPost(invalidUserId, requestDTO))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("사용자를 찾을 수 없습니다.");

            verify(postRepository, times(0)).save(any());
        }

        @Test
        @DisplayName("실패: 요청자가 Admin, GymManager가 아닌 경우")
        void createPost_illegalAccess(){
//          [Given] 테스트 환경 셋업
            PostCreateDTO requestDTO = new PostCreateDTO();
            given(userRepository.findById(testUserId1)).willReturn(Optional.of(mockUser1));

//          [When & Then]
            assertThatThrownBy(()->postService.createPost(testUserId1, requestDTO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("잘못된 접근 입니다.");

            verify(postRepository, times(0)).save(any());
        }

    }

}
