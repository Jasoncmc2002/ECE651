package com.yw.backend.test.problemSet;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yw.backend.mapper.*;
import com.yw.backend.pojo.*;
import com.yw.backend.service.impl.problemSet.ProblemSetServiceImpl;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.AdditionalAnswers.*;

@SpringBootTest
class ProblemSetServiceGetAllProgrammingTest {
    @Mock
    private ProblemSetMapper problemSetMapper;

    @Mock
    private StudentNPsMapper studentNPsMapper;

    @Mock
    private PNPsMapper pnPsMapper;

    @Mock
    private ProgrammingMapper programmingMapper;

    @Mock
    private ProgrammingAnswerMapper programmingAnswerMapper;

    @InjectMocks
    private ProblemSetServiceImpl problemSetService;

    private User mockStudent;
    private UserDetailsImpl mockStudentDetails;
    private ProblemSet mockProblemSet;
    private StudentNPs mockStudentNPs;
    private PNPs mockPNPs1;
    private PNPs mockPNPs2;
    private Programming mockProgramming1;
    private Programming mockProgramming2;
    private ProgrammingAnswer mockProgrammingAnswer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup mock student
        mockStudent = new User(1, "student", "password", "TestStudent", 0, "photo.jpg");
        mockStudentDetails = new UserDetailsImpl(mockStudent);

        // Setup mock problem set
        mockProblemSet = new ProblemSet();
        mockProblemSet.setProblemSetId(1);
        mockProblemSet.setPsName("Test Problem Set");
        mockProblemSet.setPsStartTime(LocalDateTime.now().minusHours(1)); // Started 1 hour ago
        mockProblemSet.setPsEndTime(LocalDateTime.now().plusHours(1)); // Ends in 1 hour

        // Setup mock student-problemset relation
        mockStudentNPs = new StudentNPs();
        mockStudentNPs.setStudentId(1);
        mockStudentNPs.setProblemSetId(1);
        mockStudentNPs.setFirstStartTime(LocalDateTime.now().minusMinutes(30)); // Started 30 minutes ago

        // Setup mock programming problems in problem set
        mockPNPs1 = new PNPs();
        mockPNPs1.setProblemSetId(1);
        mockPNPs1.setProgrammingId(1);

        mockPNPs2 = new PNPs();
        mockPNPs2.setProblemSetId(1);
        mockPNPs2.setProgrammingId(2);

        // Setup mock programming problems
        mockProgramming1 = new Programming();
        mockProgramming1.setProgrammingId(1);
        mockProgramming1.setPTitle("L1-010 Comparison");
        mockProgramming1.setPTotalScore(20);

        mockProgramming2 = new Programming();
        mockProgramming2.setProgrammingId(2);
        mockProgramming2.setPTitle("L1-001 Hello World");
        mockProgramming2.setPTotalScore(15);

        // Setup mock programming answer
        mockProgrammingAnswer = new ProgrammingAnswer();
        mockProgrammingAnswer.setProblemSetId(1);
        mockProgrammingAnswer.setProgrammingId(1);
        mockProgrammingAnswer.setAuthorId(1);
        mockProgrammingAnswer.setPaActualScore(18);
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
    void testGetAllProgramming_Success() {
        // Setup authentication
        setupAuthentication(mockStudentDetails);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        List<StudentNPs> studentNPsList = new ArrayList<>();
        studentNPsList.add(mockStudentNPs);
        when(studentNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(studentNPsList);

        List<PNPs> pnPsList = new ArrayList<>();
        pnPsList.add(mockPNPs1);
        pnPsList.add(mockPNPs2);
        when(pnPsMapper.selectList(any(QueryWrapper.class))).thenReturn(pnPsList);

        // Mock the first call to return programming 1, second call to return programming 2
        when(programmingMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(mockProgramming1)  // First call
                .thenReturn(mockProgramming2); // Second call

        List<ProgrammingAnswer> answeredList = new ArrayList<>();
        answeredList.add(mockProgrammingAnswer);

        // Mock answer mapper to return answered for problem 1, empty for problem 2
        when(programmingAnswerMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(answeredList)     // First call - problem is answered
                .thenReturn(new ArrayList<>()); // Second call - problem is not answered

        // Call the method
        List<Map<String, String>> result = problemSetService.getAllProgramming(1);

        // Verify the result
        assertEquals(2, result.size());

        // Check first problem (answered)
        Map<String, String> problem1 = result.get(0);
        assertEquals("1", problem1.get("programming_id"));
        assertEquals("L1-010 Comparison", problem1.get("p_title"));
        assertEquals("20", problem1.get("p_total_score"));
        assertEquals("Answered", problem1.get("pa_status"));
        assertEquals("18", problem1.get("pa_actual_score"));

        // Check second problem (not answered)
        Map<String, String> problem2 = result.get(1);
        assertEquals("2", problem2.get("programming_id"));
        assertEquals("L1-001 Hello World", problem2.get("p_title"));
        assertEquals("15", problem2.get("p_total_score"));
        assertEquals("Not Answered", problem2.get("pa_status"));
        assertEquals("0", problem2.get("pa_actual_score"));
    }

    @Test
    void testGetAllProgramming_ProblemSetNotFound() {
        // Setup authentication
        setupAuthentication(mockStudentDetails);

        // Mock database queries
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());

        // Call the method
        List<Map<String, String>> result = problemSetService.getAllProgramming(999);

        // Verify the result
        assertEquals(1, result.size());
        assertEquals("No problem set with this ID", result.get(0).get("error_message"));
    }

    @Test
    void testGetAllProgramming_UserNotBelongToProblemSet() {
        // Setup authentication
        setupAuthentication(mockStudentDetails);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        when(studentNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());

        // Call the method
        List<Map<String, String>> result = problemSetService.getAllProgramming(1);

        // Verify the result
        assertEquals(1, result.size());
        assertEquals("User does not belong to this problem set", result.get(0).get("error_message"));
    }

    @Test
    void testGetAllProgramming_ProblemSetNotStarted() {
        // Setup authentication
        setupAuthentication(mockStudentDetails);

        // Set problem set to start in the future
        mockProblemSet.setPsStartTime(LocalDateTime.now().plusHours(1));

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        List<StudentNPs> studentNPsList = new ArrayList<>();
        studentNPsList.add(mockStudentNPs);
        when(studentNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(studentNPsList);

        // Call the method
        List<Map<String, String>> result = problemSetService.getAllProgramming(1);

        // Verify the result
        assertEquals(1, result.size());
        assertEquals("Problem set not started yet", result.get(0).get("error_message"));
    }

    @Test
    void testGetAllProgramming_StudentNotStartedYet() {
        // Setup authentication
        setupAuthentication(mockStudentDetails);

        // Set student's first start time to null
        mockStudentNPs.setFirstStartTime(null);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        List<StudentNPs> studentNPsList = new ArrayList<>();
        studentNPsList.add(mockStudentNPs);
        when(studentNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(studentNPsList);

        // Call the method
        List<Map<String, String>> result = problemSetService.getAllProgramming(1);

        // Verify the result
        assertEquals(1, result.size());
        assertEquals("未开始作答", result.get(0).get("error_message"));
    }
}