package com.yw.backend.test.problemManage.ObjectiveProblemManage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yw.backend.mapper.ObjectiveProblemMapper;
import com.yw.backend.mapper.OpNPsMapper;
import com.yw.backend.mapper.UserMapper;
import com.yw.backend.pojo.ObjectiveProblem;
import com.yw.backend.pojo.User;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.yw.backend.service.impl.problemManage.ObjectiveProblemManageServiceImpl;
import com.yw.backend.service.impl.utils.UserDetailsImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class ObjectiveProblemGetAllServiceTest {

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
    private List<ObjectiveProblem> mockObjectiveProblemList;
    private User mockAuthor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup mock user with no permission
        mockUser = new User(1, "testUser", "password", "Test User", 0, "photo.jpg");
        mockUserDetails = new UserDetailsImpl(mockUser);

        // Setup mock author
        mockAuthor = new User(2, "author", "password", "李四(pyxc)", 1, "photo.jpg");

        // Setup mock objective problems
        mockObjectiveProblemList = new ArrayList<>();

        ObjectiveProblem problem1 = new ObjectiveProblem();
        problem1.setObjectiveProblemId(1);
        problem1.setAuthorId(2);
        problem1.setOpDescription("下图给出的网络从$s$到$t$的最大流是：\n\n![img](https://images.ptausercontent.com/118)\n\nA. 13\n\nB. 14\n\nC. 18\n\nD. 11");
        problem1.setOpTotalScore(8);
        problem1.setOpTag("图论");
        problem1.setOpDifficulty(5);
        mockObjectiveProblemList.add(problem1);

        ObjectiveProblem problem2 = new ObjectiveProblem();
        problem2.setObjectiveProblemId(2);
        problem2.setAuthorId(2);
        problem2.setOpDescription("**判断题**\n\n最优二叉搜索树的根结点一定存放的是搜索概率最高的那个关键字。 \n\n填写`T/F`");
        problem2.setOpTotalScore(8);
        problem2.setOpTag("数据结构");
        problem2.setOpDifficulty(3);
        mockObjectiveProblemList.add(problem2);

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
    void getAll_NoPermission() {
        // Test with user having no permission
        List<Map<String, String>> response = objectiveProblemManageService.getAll();

        assertEquals(1, response.size());
        assertEquals("No permission to retrieve objective problems list",
                response.get(0).get("error_message"));
    }

    @Test
    void getAll_EmptyList() {
        // Setup user with permission
        mockUser.setPermission(1);

        // Configure mapper to return empty list
        when(objectiveProblemMapper.selectList(null))
                .thenReturn(new ArrayList<>());

        List<Map<String, String>> response = objectiveProblemManageService.getAll();

        assertTrue(response.isEmpty());
    }

    @Test
    void getAll_SuccessfulRetrieval() {
        // Setup user with permission
        mockUser.setPermission(1);

        // Configure all necessary mocks
        when(objectiveProblemMapper.selectList(null))
                .thenReturn(mockObjectiveProblemList);
        when(opNPsMapper.selectCount(any(QueryWrapper.class)))
                .thenReturn(0L, 2L); // First problem has 0 uses, second has 2
        when(userMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(mockAuthor);

        List<Map<String, String>> response = objectiveProblemManageService.getAll();

        // Verify response size
        assertEquals(2, response.size());

        // Verify first problem
        Map<String, String> firstProblem = response.get(0);
        assertEquals("1", firstProblem.get("objective_problem_id"));
        assertEquals("0", firstProblem.get("op_use_count"));
        assertEquals("李四(pyxc)", firstProblem.get("op_author_name"));
        assertEquals("5", firstProblem.get("op_difficulty"));
        assertEquals("图论", firstProblem.get("op_tag"));
        assertEquals("8", firstProblem.get("op_total_score"));
        assertTrue(firstProblem.get("op_description").length() <= 128); // 125 + "..."
        assertTrue(firstProblem.get("op_description").endsWith("..."));

        // Verify second problem
        Map<String, String> secondProblem = response.get(1);
        assertEquals("2", secondProblem.get("objective_problem_id"));
        assertEquals("2", secondProblem.get("op_use_count"));
        assertEquals("李四(pyxc)", secondProblem.get("op_author_name"));
        assertEquals("3", secondProblem.get("op_difficulty"));
        assertEquals("数据结构", secondProblem.get("op_tag"));
        assertEquals("8", secondProblem.get("op_total_score"));
        assertTrue(secondProblem.get("op_description").length() <= 128);
        assertTrue(secondProblem.get("op_description").endsWith("..."));
    }
}