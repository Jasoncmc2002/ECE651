package com.yw.backend.test.setManage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yw.backend.mapper.PNPsMapper;
import com.yw.backend.mapper.ProblemSetMapper;
import com.yw.backend.mapper.ProgrammingMapper;
import com.yw.backend.pojo.ProblemSet;
import com.yw.backend.pojo.Programming;
import com.yw.backend.pojo.User;
import com.yw.backend.service.impl.setManage.SetManageServiceImpl;
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
import static org.mockito.Mockito.*;

@SpringBootTest
class SetManageServiceSearchProgramTest {
    @Mock
    private ProblemSetMapper problemSetMapper;

    @Mock
    private ProgrammingMapper programmingMapper;

    @Mock
    private PNPsMapper pnPsMapper;

    @InjectMocks
    private SetManageServiceImpl setManageService;

    private User mockTeacher;
    private User mockStudent;
    private UserDetailsImpl mockTeacherDetails;
    private UserDetailsImpl mockStudentDetails;
    private ProblemSet mockProblemSet;
    private List<Programming> mockProgrammingList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup mock teacher (permission level 1)
        mockTeacher = new User(1, "teacher", "password", "张三(teacher)", 1, "photo.jpg");
        mockTeacherDetails = new UserDetailsImpl(mockTeacher);

        // Setup mock student (permission level 0)
        mockStudent = new User(3, "student", "password", "王五(student)", 0, "photo.jpg");
        mockStudentDetails = new UserDetailsImpl(mockStudent);

        // Setup mock problem set
        mockProblemSet = new ProblemSet();
        mockProblemSet.setProblemSetId(1);
        mockProblemSet.setPsName("Test Problem Set");
        mockProblemSet.setPsAuthorId(1);

        // Setup mock programming problems
        mockProgrammingList = new ArrayList<>();

        Programming problem1 = new Programming();
        problem1.setProgrammingId(1);
        problem1.setPTitle("L1-010 比较大小");
        problem1.setPTag("函数");
        problem1.setPDifficulty(5);
        mockProgrammingList.add(problem1);

        Programming problem2 = new Programming();
        problem2.setProgrammingId(3);
        problem2.setPTitle("L1-015 跟奥巴马一起画方块");
        problem2.setPTag("控制语句");
        problem2.setPDifficulty(5);
        mockProgrammingList.add(problem2);

        Programming problem3 = new Programming();
        problem3.setProgrammingId(10);
        problem3.setPTitle("结尾0的个数");
        problem3.setPTag("函数");
        problem3.setPDifficulty(5);
        mockProgrammingList.add(problem3);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setupAuthentication(UserDetailsImpl userDetails) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void searchProgramming_NoPermission() {
        // Setup student authentication (no permission)
        setupAuthentication(mockStudentDetails);

        List<Map<String, String>> response = setManageService.searchProgramming(
                1, "", "", 1, 10);

        assertEquals(1, response.size());
        assertEquals("No permission to search for the programming problems",
                response.get(0).get("error_message"));
        verify(problemSetMapper, never()).selectList(any(QueryWrapper.class));
    }

    @Test
    void searchProgramming_NonExistentProblemSet() {
        // Setup teacher authentication
        setupAuthentication(mockTeacherDetails);

        // Configure mapper to return empty list for non-existent problem set
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(new ArrayList<>());

        List<Map<String, String>> response = setManageService.searchProgramming(
                999, "", "", 1, 10);

        assertEquals(1, response.size());
        assertEquals("No problem set with this ID",
                response.get(0).get("error_message"));
        verify(programmingMapper, never()).selectList(any(QueryWrapper.class));
    }

    @Test
    void searchProgramming_NoMatchingProblems() {
        // Setup teacher authentication
        setupAuthentication(mockTeacherDetails);

        // Setup problem set query result
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(problemSetList);

        // No matching programming problems
        when(programmingMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(new ArrayList<>());

        List<Map<String, String>> response = setManageService.searchProgramming(
                1, "NonExistentTitle", "", 1, 10);

        assertTrue(response.isEmpty());
    }

    @Test
    void searchProgramming_FilterOutAlreadyAddedProblems() {
        // Setup teacher authentication
        setupAuthentication(mockTeacherDetails);

        // Setup problem set query result
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(problemSetList);

        // Setup programming problems
        when(programmingMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(mockProgrammingList);

        // First problem is already added to the problem set
        when(pnPsMapper.selectCount(any(QueryWrapper.class)))
                .thenReturn(1L)  // Problem 1 is already added
                .thenReturn(0L)  // Problem 3 is not added
                .thenReturn(2L)  // Problem 1 use count
                .thenReturn(0L)  // Problem 3 is not added
                .thenReturn(1L)  // Problem 3 use count
                .thenReturn(0L); // Problem 10 use count

        List<Map<String, String>> response = setManageService.searchProgramming(
                1, "", "", 1, 10);

        // Should return 2 problems (Problem 3 and Problem 10, since Problem 1 is already added)
        assertEquals(2, response.size());

        // Verify first result (Problem 3)
        Map<String, String> firstResult = response.get(0);
        assertEquals("3", firstResult.get("programming_id"));
        assertEquals("L1-015 跟奥巴马一起画方块", firstResult.get("p_title"));
        assertEquals("控制语句", firstResult.get("p_tag"));
        assertEquals("5", firstResult.get("p_difficulty"));
        assertEquals("2", firstResult.get("p_use_count"));

        // Verify second result (Problem 10)
        Map<String, String> secondResult = response.get(1);
        assertEquals("10", secondResult.get("programming_id"));
        assertEquals("结尾0的个数", secondResult.get("p_title"));
        assertEquals("函数", secondResult.get("p_tag"));
        assertEquals("5", secondResult.get("p_difficulty"));
        assertEquals("1", secondResult.get("p_use_count"));
    }

    @Test
    void searchProgramming_WithFilters() {
        // Setup teacher authentication
        setupAuthentication(mockTeacherDetails);

        // Setup problem set query result
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(problemSetList);

        // Setup filtered programming problems (only Problem 1 and Problem 10 match the filter)
        List<Programming> filteredList = new ArrayList<>();
        filteredList.add(mockProgrammingList.get(0)); // Problem 1
        filteredList.add(mockProgrammingList.get(2)); // Problem 10
        when(programmingMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(filteredList);

        // None of the problems are added to the problem set
        when(pnPsMapper.selectCount(any(QueryWrapper.class)))
                .thenReturn(0L)  // Problem 1 is not added
                .thenReturn(2L)  // Problem 1 use count
                .thenReturn(0L)  // Problem 10 is not added
                .thenReturn(0L); // Problem 10 use count

        List<Map<String, String>> response = setManageService.searchProgramming(
                1, "", "函数", 5, 5);  // Filter by tag="函数" and difficulty=5

        // Should return both problems that match the filter
        assertEquals(2, response.size());

        // Verify first result (Problem 1)
        Map<String, String> firstResult = response.get(0);
        assertEquals("1", firstResult.get("programming_id"));
        assertEquals("L1-010 比较大小", firstResult.get("p_title"));
        assertEquals("函数", firstResult.get("p_tag"));
        assertEquals("5", firstResult.get("p_difficulty"));
        assertEquals("2", firstResult.get("p_use_count"));

        // Verify second result (Problem 10)
        Map<String, String> secondResult = response.get(1);
        assertEquals("10", secondResult.get("programming_id"));
        assertEquals("结尾0的个数", secondResult.get("p_title"));
        assertEquals("函数", secondResult.get("p_tag"));
        assertEquals("5", secondResult.get("p_difficulty"));
        assertEquals("0", secondResult.get("p_use_count"));
    }
}
