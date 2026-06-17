package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.util.EntityFixture;
import com.project.greatcloud13.ClimbingWith.dto.PostCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.PostResponseDTO;
import com.project.greatcloud13.ClimbingWith.dto.PostSummaryDTO;
import com.project.greatcloud13.ClimbingWith.dto.PostUpdateDTO;
import com.project.greatcloud13.ClimbingWith.entity.*;
import com.project.greatcloud13.ClimbingWith.repository.GymRepository;
import com.project.greatcloud13.ClimbingWith.repository.PostRepository;
import com.project.greatcloud13.ClimbingWith.repository.UserRepository;
import com.project.greatcloud13.ClimbingWith.util.TestFixture;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.post.PostAccessDeniedException;
import com.project.greatcloud13.ClimbingWith.exception.post.PostNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.user.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private Post mockPost2;
    private Post mockPost3;
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

    LocalDateTime createdAt = LocalDateTime.of(2026, 3, 20, 14, 30);


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

//      Post Mock Entity Build
        mockPost = Post.builder().title(postName1).gym(mockGym1).content(postContent1).postType(PostType.PROMOTION).createdAt(createdAt).build();
        ReflectionTestUtils.setField(mockPost, "id", testPostId);
        ReflectionTestUtils.setField(mockPost, "writer", mockManagerUser1);

        mockPost2 = Post.builder().title("테스트 포스트2").gym(mockGym1).content("테스트 포스트2").createdAt(createdAt).build();
        ReflectionTestUtils.setField(mockPost2, "id", 212L);
        ReflectionTestUtils.setField(mockPost2, "writer", mockManagerUser1);

        mockPost3 = Post.builder().title("테스트 포스트3").gym(mockGym1).content("테스트 포스트3").createdAt(createdAt).build();
        ReflectionTestUtils.setField(mockPost3, "id", 213L);
        ReflectionTestUtils.setField(mockPost3, "writer", mockManagerUser1);

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
                    .isInstanceOf(UserNotFoundException.class);

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
                    .isInstanceOf(PostAccessDeniedException.class);

            verify(postRepository, times(0)).save(any());
        }

    }

    @Nested
    @DisplayName("getPost() 기능 테스트")
    class GetPostTest{

        @Test
        @DisplayName("게시글 단일 조회 성공")
        void getPostById_Success(){
//          [given]

            given(postRepository.findById(testPostId)).willReturn(Optional.of(mockPost));
//          [When]

            PostResponseDTO result = postService.getPostById(testPostId);
//          [Then]
            assertThat(result.getId()).isEqualTo(testPostId);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 게시글 ID 요청시 예외 발생")
        void getPost_PostNotFound(){
//          [given]

            given(postRepository.findById(invalidPostId)).willReturn(Optional.empty());
//          [When & Then]
            assertThatThrownBy(()->postService.getPostById(invalidPostId))
                    .isInstanceOf(PostNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getPostPage() 기능 테스트")
    class GetPostPageTest{

        @Test
        @DisplayName("요청한 암장 게시글 페이지 Repository를 통해 조회")
        void getPostPage_Success(){
//          [given]

            List<Post> postList = List.of(mockPost, mockPost2, mockPost3);
            Pageable pageable = PageRequest.of(0, 10);

            Page<Post> mockPage = new PageImpl<>(postList, pageable, postList.size());

            given(postRepository.findAllByGym(mockGym1, pageable)).willReturn(mockPage);
            given(gymRepository.findById(gymId1)).willReturn(Optional.of(mockGym1));
//          [When]
            Page<PostSummaryDTO> result = postService.getAllByGym(gymId1, pageable);

//          [Then]
            verify(postRepository, times(1)).findAllByGym(any(), any());

        }

        @Test
        @DisplayName("요청한 암장의 해당하는 요청한 태그의 게시글 페이지 조회")
        void getPostPage_WithTagPostsPage_Success() {
            // [given]
            List<Post> filteredPosts = new ArrayList<>();
            for (long i = 0L; i < 5; i++) {
                filteredPosts.add(EntityFixture.createPost(i, "암장1 게시글", "내용1", PostType.PROMOTION, mockGym1, mockManagerUser1, LocalDateTime.now()));
            }

            Pageable pageable = PageRequest.of(0, 5);
            // 2. Mock 객체는 필터링된 결과(5개)만 반환하도록 설정
            Page<Post> pageResult = new PageImpl<>(filteredPosts, pageable, filteredPosts.size());

            given(gymRepository.findById(gymId1)).willReturn(Optional.of(mockGym1));
            given(postRepository.findAllByGymAndPostType(eq(mockGym1), eq(PostType.PROMOTION), eq(pageable)))
                    .willReturn(pageResult);

            // [When]
            Page<PostSummaryDTO> result = postService.getAllByGymWithPostType(gymId1, PostType.PROMOTION, pageable);

            // [Then]
            verify(postRepository, times(1)).findAllByGymAndPostType(mockGym1, PostType.PROMOTION, pageable);
        }

    }


    @Nested
    @DisplayName("updatePost() 기능 테스트")
    class UpdatePostTest{

        @Test
        @DisplayName("게시글 업데이트 성공")
        void updatePost_Success(){
//          [Given]
            PostUpdateDTO request = new PostUpdateDTO();
            request.setTitle("수정된 제목");
            request.setContent("수정된 내용");
            request.setPostType(PostType.PROMOTION);

            given(postRepository.findById(testPostId)).willReturn(Optional.of(mockPost));
            given(userRepository.findById(testManagerId1)).willReturn(Optional.of(mockManagerUser1));
//          [When]
            PostResponseDTO result = postService.updatePost(testManagerId1, testPostId, request);

//          [Then]
            assertThat(result.getTitle()).isEqualTo(request.getTitle());
            assertThat(result.getContent()).isEqualTo(request.getContent());
            assertThat(result.getPostType()).isEqualTo(request.getPostType().toString());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자 ID 요청시 예외 발생")
        void updatePost_UserNotFound(){
//          [Given]
            PostUpdateDTO request = new PostUpdateDTO();
            request.setTitle("수정된 제목");
            request.setContent("수정된 내용");
            request.setPostType(PostType.PROMOTION);

            given(userRepository.findById(invalidUserId)).willReturn(Optional.empty());
//          [When & When]
            assertThatThrownBy(()-> postService.updatePost(invalidUserId, testPostId, request))
                    .isInstanceOf(UserNotFoundException.class);

            verify(postRepository, times(0)).save(any());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 게시글 ID 요청시 예외 발생")
        void updatePost_PostNotFound(){
//          [Given]
            PostUpdateDTO request = new PostUpdateDTO();
            request.setTitle("수정된 제목");
            request.setContent("수정된 내용");
            request.setPostType(PostType.PROMOTION);

            given(userRepository.findById(testManagerId1)).willReturn(Optional.of(mockManagerUser1));
            given(postRepository.findById(invalidPostId)).willReturn(Optional.empty());
//          [When & Then]
            assertThatThrownBy(()->postService.updatePost(testManagerId1, invalidPostId, request))
                    .isInstanceOf(PostNotFoundException.class);

            verify(postRepository, times(0)).save(any());
        }

        @Test
        @DisplayName("실패: 사용자가 매니저가 아닌 경우 예외 발생")
        void updatePost_IllegalAccess(){
//          [Given]
            PostUpdateDTO request = new PostUpdateDTO();
            request.setTitle("수정된 제목");
            request.setContent("수정된 내용");
            request.setPostType(PostType.PROMOTION);

            given(userRepository.findById(testUserId1)).willReturn(Optional.of(mockUser1));
//          [When & Then]
            assertThatThrownBy(()->postService.updatePost(testUserId1, testPostId, request))
                    .isInstanceOf(PostAccessDeniedException.class);

            verify(postRepository, times(0)).save(any());
        }

        @Test
        @DisplayName("실패: 작성자가 아닌 다른 사용자가 요청을 한 경우 예외 발생")
        void updatePost_AnotherManager(){
//          [Given]
            PostUpdateDTO request = new PostUpdateDTO();
            request.setTitle("수정된 제목");
            request.setContent("수정된 내용");
            request.setPostType(PostType.PROMOTION);

            given(userRepository.findById(testManagerId2)).willReturn(Optional.of(mockManagerUser2));
            given(postRepository.findById(testPostId)).willReturn(Optional.of(mockPost));
//          [When & Then]
            assertThatThrownBy(()->postService.updatePost(testManagerId2, testPostId, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("작성자만 게시글을 수정할 수 있습니다.");

            verify(postRepository, times(0)).save(any());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("실패: 제목이 비어있는 경우 예외 발생")
        void updatePost_NullTitle(String invalidTitle){
//          [Given]
            PostUpdateDTO request = new PostUpdateDTO();
            request.setTitle(invalidTitle);
            request.setContent("수정된 내용");
            request.setPostType(PostType.PROMOTION);

            given(userRepository.findById(testManagerId1)).willReturn(Optional.of(mockManagerUser1));
            given(postRepository.findById(testPostId)).willReturn(Optional.of(mockPost));
//          [When & Then]
            assertThatThrownBy(()->postService.updatePost(testManagerId1, testPostId, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("제목이 유효하지 않습니다.");

            verify(postRepository, times(0)).save(any());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("실패: 내용이 비어있는 경우 예외 발생")
        void update_NullContent(String invalidContent){
//          [Given]
            PostUpdateDTO request = new PostUpdateDTO();
            request.setTitle("수정된 제목");
            request.setContent(invalidContent);
            request.setPostType(PostType.PROMOTION);

            given(userRepository.findById(testManagerId1)).willReturn(Optional.of(mockManagerUser1));
            given(postRepository.findById(testPostId)).willReturn(Optional.of(mockPost));
//          [When & Then]
            assertThatThrownBy(()->postService.updatePost(testManagerId1, testPostId, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("내용이 유효하지 않습니다.");

            verify(postRepository, times(0)).save(any());
        }
    }
}
