package com.yw.backend.test.problemManage.ObjectiveProblemManage;

import com.yw.backend.mapper.ObjectiveProblemMapper;
import com.yw.backend.mapper.OpNPsMapper;
import com.yw.backend.mapper.UserMapper;
import com.yw.backend.pojo.User;
import com.yw.backend.service.impl.utils.UserDetailsImpl;
import com.yw.backend.service.impl.problemManage.ObjectiveProblemManageServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.yw.backend.pojo.ObjectiveProblem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class ObjectiveProblemGetOneServiceTest {

    @Mock
    private ObjectiveProblemMapper objectiveProblemMapper;

    @Mock
    private OpNPsMapper opNPsMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ObjectiveProblemManageServiceImpl objectiveProblemManageService;

    private User mockUser;
    private UserDetailsImpl mockUserDetails;
    private ObjectiveProblem mockObjectiveProblem;
    private User mockAuthor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup mock user with no permission
        mockUser = new User(1, "testUser", "password", "Test User", 0, "photo.jpg");
        mockUserDetails = new UserDetailsImpl(mockUser);

        // Setup mock objective problem
        mockObjectiveProblem = new ObjectiveProblem();
        mockObjectiveProblem.setObjectiveProblemId(1);
        mockObjectiveProblem.setAuthorId(2);
        mockObjectiveProblem.setOpDescription("Test Description");
        mockObjectiveProblem.setOpTotalScore(10);
        mockObjectiveProblem.setOpCorrectAnswer("A");
        mockObjectiveProblem.setOpTag("Math");
        mockObjectiveProblem.setOpDifficulty(3);

        // Setup mock author
        mockAuthor = new User(2, "author", "password", "Author Name", 1, "photo.jpg");

        // Setup security context
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mockUserDetails, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getOne_NoPermission() {
        // User with permission level 0 should be denied
        Map<String, String> response = objectiveProblemManageService.getOne(1);

        assertEquals("No permission to retrieve objective problem", response.get("error_message"));
    }

    @Test
    void getOne_ProblemNotFound() {
        // Setup user with permission
        mockUser.setPermission(1);

        // Configure mapper to return empty list
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        Map<String, String> response = objectiveProblemManageService.getOne(999);

        assertEquals("No objective problem correspond to this ID", response.get("error_message"));
    }

    @Test
    void getOne_SuccessfulRetrieval() {
        // Setup user with permission
        mockUser.setPermission(1);

        // Configure all necessary mocks
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(mockObjectiveProblem));
        when(opNPsMapper.selectCount(any(QueryWrapper.class)))
                .thenReturn(5L);
        when(userMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(mockAuthor);

        Map<String, String> response = objectiveProblemManageService.getOne(1);

        // Verify all fields in the response
        assertEquals("success", response.get("error_message"));
        assertEquals("1", response.get("objective_problem_id"));
        assertEquals("5", response.get("op_use_count"));
        assertEquals("2", response.get("op_author_id"));
        assertEquals("Author Name", response.get("op_author_name"));
        assertEquals("Test Description", response.get("op_description"));
        assertEquals("10", response.get("op_total_score"));
        assertEquals("A", response.get("op_correct_answer"));
        assertEquals("Math", response.get("op_tag"));
        assertEquals("3", response.get("op_difficulty"));
    }
}