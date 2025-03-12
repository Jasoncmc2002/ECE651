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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.AdditionalAnswers.*;

@SpringBootTest
class ProblemSetServiceGetAllObjectiveProblemTest {
    @Mock
    private ProblemSetMapper problemSetMapper;

    @Mock
    private StudentNPsMapper studentNPsMapper;

    @Mock
    private OpNPsMapper opNPsMapper;

    @Mock
    private ObjectiveProblemMapper objectiveProblemMapper;

    @Mock
    private ObjectiveProblemAnswerMapper objectiveProblemAnswerMapper;

    @InjectMocks
    private ProblemSetServiceImpl problemSetService;

    private User mockStudent;
    private UserDetailsImpl mockStudentDetails;
    private ProblemSet mockProblemSet;
    private StudentNPs mockStudentNPs;
    private OpNPs mockOpNPs1;
    private OpNPs mockOpNPs2;
    private ObjectiveProblem mockObjectiveProblem1;
    private ObjectiveProblem mockObjectiveProblem2;
    private ObjectiveProblemAnswer mockObjectiveProblemAnswer;

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

        // Setup mock objective problems in problem set
        mockOpNPs1 = new OpNPs();
        mockOpNPs1.setProblemSetId(1);
        mockOpNPs1.setObjectiveProblemId(1);

        mockOpNPs2 = new OpNPs();
        mockOpNPs2.setProblemSetId(1);
        mockOpNPs2.setObjectiveProblemId(2);

        // Setup mock objective problems
        mockObjectiveProblem1 = new ObjectiveProblem();
        mockObjectiveProblem1.setObjectiveProblemId(1);
        mockObjectiveProblem1.setOpDescription("Test Problem 1 Description");
        mockObjectiveProblem1.setOpTotalScore(10);

        mockObjectiveProblem2 = new ObjectiveProblem();
        mockObjectiveProblem2.setObjectiveProblemId(2);
        mockObjectiveProblem2.setOpDescription("Test Problem 2 Description");
        mockObjectiveProblem2.setOpTotalScore(5);

        // Setup mock objective problem answer
        mockObjectiveProblemAnswer = new ObjectiveProblemAnswer();
        mockObjectiveProblemAnswer.setProblemSetId(1);
        mockObjectiveProblemAnswer.setObjectiveProblemId(1);
        mockObjectiveProblemAnswer.setAuthorId(1);
        mockObjectiveProblemAnswer.setOpaActualScore(8);
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
    void testGetAllObjectiveProblem_Success() {
        // Setup authentication
        setupAuthentication(mockStudentDetails);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        List<StudentNPs> studentNPsList = new ArrayList<>();
        studentNPsList.add(mockStudentNPs);
        when(studentNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(studentNPsList);

        List<OpNPs> opNPsList = new ArrayList<>();
        opNPsList.add(mockOpNPs1);
        opNPsList.add(mockOpNPs2);
        when(opNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(opNPsList);

        // Mock the first call to return problem 1, second call to return problem 2
        when(objectiveProblemMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(mockObjectiveProblem1)  // First call
                .thenReturn(mockObjectiveProblem2); // Second call

        List<ObjectiveProblemAnswer> answeredList = new ArrayList<>();
        answeredList.add(mockObjectiveProblemAnswer);

        // Mock answer mapper to return answered for problem 1, empty for problem 2
        when(objectiveProblemAnswerMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(answeredList)     // First call - problem is answered
                .thenReturn(new ArrayList<>()); // Second call - problem is not answered

        // Call the method
        List<Map<String, String>> result = problemSetService.getAllObjectiveProblem(1);

        // Verify the result
        assertEquals(2, result.size());

        // Check first problem (answered)
        Map<String, String> problem1 = result.get(0);
        assertEquals("1", problem1.get("objective_problem_id"));
        assertTrue(problem1.get("op_description").startsWith("Test Problem 1"));
        assertEquals("10", problem1.get("op_total_score"));
        assertEquals("Questions answered", problem1.get("opa_status"));
        assertEquals("--", problem1.get("opa_actual_score")); // Exam not ended yet

        // Check second problem (not answered)
        Map<String, String> problem2 = result.get(1);
        assertEquals("2", problem2.get("objective_problem_id"));
        assertTrue(problem2.get("op_description").startsWith("Test Problem 2"));
        assertEquals("5", problem2.get("op_total_score"));
        assertEquals("Questions not answered", problem2.get("opa_status"));
        assertEquals("0", problem2.get("opa_actual_score"));
    }

    @Test
    void testGetAllObjectiveProblem_ProblemSetNotFound() {
        // Setup authentication
        setupAuthentication(mockStudentDetails);

        // Mock database queries
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());

        // Call the method
        List<Map<String, String>> result = problemSetService.getAllObjectiveProblem(999);

        // Verify the result
        assertEquals(1, result.size());
        assertEquals("No problem set with this ID", result.get(0).get("error_message"));
    }

    @Test
    void testGetAllObjectiveProblem_UserNotBelongToProblemSet() {
        // Setup authentication
        setupAuthentication(mockStudentDetails);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        when(studentNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());

        // Call the method
        List<Map<String, String>> result = problemSetService.getAllObjectiveProblem(1);

        // Verify the result
        assertEquals(1, result.size());
        assertEquals("User does not belong to this problem set", result.get(0).get("error_message"));
    }

    @Test
    void testGetAllObjectiveProblem_ProblemSetNotStarted() {
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
        List<Map<String, String>> result = problemSetService.getAllObjectiveProblem(1);

        // Verify the result
        assertEquals(1, result.size());
        assertEquals("Problem set not started yet", result.get(0).get("error_message"));
    }

    @Test
    void testGetAllObjectiveProblem_StudentNotStartedYet() {
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
        List<Map<String, String>> result = problemSetService.getAllObjectiveProblem(1);

        // Verify the result
        assertEquals(1, result.size());
        assertEquals("Not started yet", result.get(0).get("error_message"));
    }

    @Test
    void testGetAllObjectiveProblem_AfterExamEnded() {
        // Setup authentication
        setupAuthentication(mockStudentDetails);

        // Set problem set to have ended
        mockProblemSet.setPsEndTime(LocalDateTime.now().minusHours(1));

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        List<StudentNPs> studentNPsList = new ArrayList<>();
        studentNPsList.add(mockStudentNPs);
        when(studentNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(studentNPsList);

        List<OpNPs> opNPsList = new ArrayList<>();
        opNPsList.add(mockOpNPs1);
        when(opNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(opNPsList);

        // Using thenReturn for the mapper calls
        when(objectiveProblemMapper.selectOne(any(QueryWrapper.class))).thenReturn(mockObjectiveProblem1);

        List<ObjectiveProblemAnswer> answeredList = new ArrayList<>();
        answeredList.add(mockObjectiveProblemAnswer);
        when(objectiveProblemAnswerMapper.selectList(any(QueryWrapper.class))).thenReturn(answeredList);

        // Call the method
        List<Map<String, String>> result = problemSetService.getAllObjectiveProblem(1);

        // Verify the result
        assertEquals(1, result.size());

        // Should show actual score after exam ended
        Map<String, String> problem = result.get(0);
        assertEquals("8", problem.get("opa_actual_score"));
    }
}