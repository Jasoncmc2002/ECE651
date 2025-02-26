package com.yw.backend.test.problemManage.ProgrammingManage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yw.backend.mapper.PNPsMapper;
import com.yw.backend.mapper.ProgrammingMapper;
import com.yw.backend.mapper.UserMapper;
import com.yw.backend.pojo.Programming;
import com.yw.backend.pojo.User;
import com.yw.backend.service.impl.problemManage.ProgrammingManageServiceImpl;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProgrammingManageServiceGetOneTest {

    @Mock
    private ProgrammingMapper programmingMapper;

    @Mock
    private PNPsMapper pnPsMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ProgrammingManageServiceImpl programmingManageService;

    private User mockUser;
    private UserDetailsImpl mockUserDetails;
    private List<Programming> mockProgrammingList;
    private User mockAuthor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup mock user with no permission
        mockUser = new User(1, "testUser", "password", "Test User", 0, "photo.jpg");
        mockUserDetails = new UserDetailsImpl(mockUser);

        // Setup mock author
        mockAuthor = new User(2, "author", "password", "李四(pyxc)", 1, "photo.jpg");

        // Setup mock programming problems
        mockProgrammingList = new ArrayList<>();

        Programming problem1 = new Programming();
        problem1.setProgrammingId(1);
        problem1.setPAuthorId(2);
        problem1.setPTitle("L1-010 比较大小");
        problem1.setPTotalScore(20);
        problem1.setPTag("函数");
        problem1.setPDifficulty(5);
        mockProgrammingList.add(problem1);

        Programming problem2 = new Programming();
        problem2.setProgrammingId(2);
        problem2.setPAuthorId(2);
        problem2.setPTitle("L1-001 Hello World");
        problem2.setPTotalScore(15);
        problem2.setPTag("基本语法");
        problem2.setPDifficulty(1);
        mockProgrammingList.add(problem2);

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
        // User has no permission (permission = 0)
        Map<String, String> response = programmingManageService.getOne(1);

        assertEquals(1, response.size());
        assertEquals("No permission to obtain programming problems",
                response.get("error_message"));
    }

    @Test
    void getOne_NonExistentProblem() {
        // Setup user with permission
        mockUser.setPermission(1);

        // Configure mapper to return empty list for non-existent problem
        when(programmingMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(new ArrayList<>());

        Map<String, String> response = programmingManageService.getOne(999);

        assertEquals(1, response.size());
        assertEquals("No programming problem with this ID",
                response.get("error_message"));
    }

    @Test
    void getOne_SuccessfulRetrieval() {
        // Setup user with permission
        mockUser.setPermission(1);

        // Create a mock programming problem with all fields
        Programming problem = new Programming();
        problem.setProgrammingId(1);
        problem.setPAuthorId(2);
        problem.setPTitle("L1-010 比较大小");
        problem.setPDescription("Compare three numbers");
        problem.setPTotalScore(20);
        problem.setTimeLimit(1000);
        problem.setCodeSizeLimit(16);
        problem.setPTag("函数");
        problem.setPDifficulty(5);
        problem.setPJudgeCode("def judge(a, b, c):");

        List<Programming> singleProblemList = new ArrayList<>();
        singleProblemList.add(problem);

        // Configure all necessary mocks
        when(programmingMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(singleProblemList);
        when(pnPsMapper.selectCount(any(QueryWrapper.class)))
                .thenReturn(2L); // Problem has been used 2 times
        when(userMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(mockAuthor);

        Map<String, String> response = programmingManageService.getOne(1);

        // Verify all fields in response
        assertEquals("success", response.get("error_message"));
        assertEquals("1", response.get("programming_id"));
        assertEquals("2", response.get("p_use_count"));
        assertEquals("Compare three numbers", response.get("p_description"));
        assertEquals("20", response.get("p_total_score"));
        assertEquals("1000", response.get("time_limit"));
        assertEquals("16", response.get("code_size_limit"));
        assertEquals("函数", response.get("p_tag"));
        assertEquals("2", response.get("p_author_id"));
        assertEquals("李四(pyxc)", response.get("p_author_name"));
        assertEquals("L1-010 比较大小", response.get("p_title"));
        assertEquals("def judge(a, b, c):", response.get("p_judge_code"));
        assertEquals("5", response.get("p_difficulty"));
    }
}