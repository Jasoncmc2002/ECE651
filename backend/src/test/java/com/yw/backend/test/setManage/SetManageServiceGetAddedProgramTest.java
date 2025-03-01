package com.yw.backend.test.setManage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yw.backend.mapper.PNPsMapper;
import com.yw.backend.mapper.ProblemSetMapper;
import com.yw.backend.mapper.ProgrammingAnswerMapper;
import com.yw.backend.mapper.ProgrammingMapper;
import com.yw.backend.pojo.PNPs;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
class SetManageServiceGetAddedProgramTest {
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
    private List<PNPs> mockPnPsList;
    private List<Programming> mockProgrammingList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup mock users with different permission levels
        mockTeacher = new User(1, "teacher", "password", "张三(teacher)", 1, "photo.jpg");
        mockTeacherDetails = new UserDetailsImpl(mockTeacher);

        mockStudent = new User(3, "student", "password", "王五(student)", 0, "photo.jpg");
        mockStudentDetails = new UserDetailsImpl(mockStudent);

        // Setup mock problem set
        mockProblemSet = new ProblemSet();
        mockProblemSet.setProblemSetId(1);
        mockProblemSet.setPsName("Test Problem Set");
        mockProblemSet.setPsAuthorId(1); // Created by teacher (user_id = 1)

        // Setup mock PNPs (problem set and programming links)
        mockPnPsList = new ArrayList<>();

        PNPs link1 = new PNPs();
        link1.setProblemSetId(1);
        link1.setProgrammingId(9);
        mockPnPsList.add(link1);

        PNPs link2 = new PNPs();
        link2.setProblemSetId(1);
        link2.setProgrammingId(12);
        mockPnPsList.add(link2);

        // Setup mock programming problems
        mockProgrammingList = new ArrayList<>();

        Programming problem1 = new Programming();
        problem1.setProgrammingId(9);
        problem1.setPTitle("回文子串");
        problem1.setPTag("控制语句");
        problem1.setPDifficulty(4);
        mockProgrammingList.add(problem1);

        Programming problem2 = new Programming();
        problem2.setProgrammingId(12);
        problem2.setPTitle("函数题：光棍的悲伤");
        problem2.setPTag("函数");
        problem2.setPDifficulty(3);
        mockProgrammingList.add(problem2);
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
    void getAddedProgramming_NoPermission() {
        // Setup student authentication (no permission)
        setupAuthentication(mockStudentDetails);

        List<Map<String, String>> response = setManageService.getAddedProgramming(1);

        assertEquals(1, response.size());
        assertEquals("No permission to query problems from problem set",
                response.get(0).get("error_message"));
    }

    @Test
    void getAddedProgramming_NonExistentProblemSet() {
        // Setup teacher authentication
        setupAuthentication(mockTeacherDetails);

        // Configure mapper to return empty list for non-existent problem set
        doReturn(new ArrayList<>()).when(problemSetMapper).selectList(any());

        List<Map<String, String>> response = setManageService.getAddedProgramming(999);

        assertEquals(1, response.size());
        assertEquals("No problem set with this ID",
                response.get(0).get("error_message"));
    }

    @Test
    void getAddedProgramming_NoProgrammingProblemsAdded() {
        // Setup teacher authentication
        setupAuthentication(mockTeacherDetails);

        // Configure problem set query result
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        doReturn(problemSetList).when(problemSetMapper).selectList(any());

        // Configure empty PNPs list (no problems added to the set)
        doReturn(new ArrayList<>()).when(pnPsMapper).selectList(any());

        List<Map<String, String>> response = setManageService.getAddedProgramming(1);

        // Should return empty list
        assertTrue(response.isEmpty());
    }

    @Test
    void getAddedProgramming_SuccessfulRetrieval() {
        // Setup teacher authentication
        setupAuthentication(mockTeacherDetails);

        // Configure problem set query result
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        doReturn(problemSetList).when(problemSetMapper).selectList(any());

        // Configure PNPs list with two problem links
        doReturn(mockPnPsList).when(pnPsMapper).selectList(any());

        doReturn(mockProgrammingList.get(0)).when(programmingMapper).selectOne(any());

        doReturn(1L).when(pnPsMapper).selectCount(any());

        List<Map<String, String>> response = setManageService.getAddedProgramming(1);

        assertFalse(response.isEmpty());
        assertTrue(response.size() > 0);
    }
}
