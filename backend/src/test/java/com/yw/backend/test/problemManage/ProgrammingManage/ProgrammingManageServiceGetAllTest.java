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
class ProgrammingManageServiceGetAllTest {

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
    void getAll_NoPermission() {
        List<Map<String, String>> response = programmingManageService.getAll();

        assertEquals(1, response.size());
        assertEquals("No permission to obtain programming problems",
                response.get(0).get("error_message"));
    }

    @Test
    void getAll_EmptyList() {
        // Setup user with permission
        mockUser.setPermission(1);

        // Configure mapper to return empty list
        when(programmingMapper.selectList(null))
                .thenReturn(new ArrayList<>());

        List<Map<String, String>> response = programmingManageService.getAll();

        assertTrue(response.isEmpty());
    }

    @Test
    void getAll_SuccessfulRetrieval() {
        // Setup user with permission
        mockUser.setPermission(1);

        // Configure all necessary mocks
        when(programmingMapper.selectList(null))
                .thenReturn(mockProgrammingList);
        when(pnPsMapper.selectCount(any(QueryWrapper.class)))
                .thenReturn(2L, 3L); // First problem has 2 uses, second has 3
        when(userMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(mockAuthor);

        List<Map<String, String>> response = programmingManageService.getAll();

        // Verify response size
        assertEquals(2, response.size());

        // Verify first problem
        Map<String, String> firstProblem = response.get(0);
        assertEquals("1", firstProblem.get("programming_id"));
        assertEquals("L1-010 比较大小", firstProblem.get("p_title"));
        assertEquals("20", firstProblem.get("p_total_score"));
        assertEquals("5", firstProblem.get("p_difficulty"));
        assertEquals("函数", firstProblem.get("p_tag"));
        assertEquals("2", firstProblem.get("p_use_count"));
        assertEquals("李四(pyxc)", firstProblem.get("p_author_name"));

        // Verify second problem
        Map<String, String> secondProblem = response.get(1);
        assertEquals("2", secondProblem.get("programming_id"));
        assertEquals("L1-001 Hello World", secondProblem.get("p_title"));
        assertEquals("15", secondProblem.get("p_total_score"));
        assertEquals("1", secondProblem.get("p_difficulty"));
        assertEquals("基本语法", secondProblem.get("p_tag"));
        assertEquals("3", secondProblem.get("p_use_count"));
        assertEquals("李四(pyxc)", secondProblem.get("p_author_name"));
    }
}