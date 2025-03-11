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

@SpringBootTest
class ProblemSetServiceGetOneObjectiveProblemTest {
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
    private OpNPs mockOpNPs;
    private ObjectiveProblem mockObjectiveProblem;
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
        mockProblemSet.setDuration(120); // 120 minutes duration

        // Setup mock student-problemset relation
        mockStudentNPs = new StudentNPs();
        mockStudentNPs.setStudentId(1);
        mockStudentNPs.setProblemSetId(1);
        mockStudentNPs.setFirstStartTime(LocalDateTime.now().minusMinutes(30)); // Started 30 minutes ago

        // Setup mock objective problem in problem set
        mockOpNPs = new OpNPs();
        mockOpNPs.setProblemSetId(1);
        mockOpNPs.setObjectiveProblemId(1);

        // Setup mock objective problem
        mockObjectiveProblem = new ObjectiveProblem();
        mockObjectiveProblem.setObjectiveProblemId(1);
        mockObjectiveProblem.setOpDescription("What is the time complexity of binary search?");
        mockObjectiveProblem.setOpTotalScore(10);
        mockObjectiveProblem.setOpCorrectAnswer("O(log n)");

        // Setup mock objective problem answer
        mockObjectiveProblemAnswer = new ObjectiveProblemAnswer();
        mockObjectiveProblemAnswer.setProblemSetId(1);
        mockObjectiveProblemAnswer.setObjectiveProblemId(1);
        mockObjectiveProblemAnswer.setAuthorId(1);
        mockObjectiveProblemAnswer.setOpaActualAnswer("O(log n)");
        mockObjectiveProblemAnswer.setOpaActualScore(10);
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
    void testGetOneObjectiveProblem_Success() {
        // Setup authentication
        setupAuthentication(mockStudentDetails);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        List<ObjectiveProblem> objectiveProblemList = new ArrayList<>();
        objectiveProblemList.add(mockObjectiveProblem);
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class))).thenReturn(objectiveProblemList);

        List<StudentNPs> studentNPsList = new ArrayList<>();
        studentNPsList.add(mockStudentNPs);
        when(studentNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(studentNPsList);

        List<OpNPs> opNPsList = new ArrayList<>();
        opNPsList.add(mockOpNPs);
        when(opNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(opNPsList);

        List<ObjectiveProblemAnswer> objectiveProblemAnswerList = new ArrayList<>();
        objectiveProblemAnswerList.add(mockObjectiveProblemAnswer);
        when(objectiveProblemAnswerMapper.selectList(any(QueryWrapper.class))).thenReturn(objectiveProblemAnswerList);

        // Call the method
        Map<String, String> result = problemSetService.getOneObjectiveProblem(1, 1);

        // Verify the result
        assertEquals("success", result.get("error_message"));
        assertEquals("What is the time complexity of binary search?", result.get("op_description"));
        assertEquals("10", result.get("op_total_score"));
        assertNotNull(result.get("first_start_time"));
        assertEquals("120", result.get("duration"));
        assertNotNull(result.get("ps_end_time"));
        assertEquals("started", result.get("ps_status"));
        assertEquals("", result.get("op_correct_answer")); // Problem set not ended yet
        assertEquals("O(log n)", result.get("opa_actual_answer"));
        assertEquals("--", result.get("opa_actual_score")); // Problem set not ended yet
    }

    @Test
    void testGetOneObjectiveProblem_AfterExamEnded() {
        // Setup authentication
        setupAuthentication(mockStudentDetails);

        // Set problem set to have ended
        mockProblemSet.setPsEndTime(LocalDateTime.now().minusHours(1));

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        List<ObjectiveProblem> objectiveProblemList = new ArrayList<>();
        objectiveProblemList.add(mockObjectiveProblem);
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class))).thenReturn(objectiveProblemList);

        List<StudentNPs> studentNPsList = new ArrayList<>();
        studentNPsList.add(mockStudentNPs);
        when(studentNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(studentNPsList);

        List<OpNPs> opNPsList = new ArrayList<>();
        opNPsList.add(mockOpNPs);
        when(opNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(opNPsList);

        List<ObjectiveProblemAnswer> objectiveProblemAnswerList = new ArrayList<>();
        objectiveProblemAnswerList.add(mockObjectiveProblemAnswer);
        when(objectiveProblemAnswerMapper.selectList(any(QueryWrapper.class))).thenReturn(objectiveProblemAnswerList);

        // Call the method
        Map<String, String> result = problemSetService.getOneObjectiveProblem(1, 1);

        // Verify the result
        assertEquals("success", result.get("error_message"));
        assertEquals("O(log n)", result.get("op_correct_answer")); // Should show correct answer after exam ended
        assertEquals("10", result.get("opa_actual_score")); // Should show actual score after exam ended
        assertEquals("closed", result.get("ps_status")); // Status should be closed
    }

    @Test
    void testGetOneObjectiveProblem_NoAnswer() {
        // Setup authentication
        setupAuthentication(mockStudentDetails);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        List<ObjectiveProblem> objectiveProblemList = new ArrayList<>();
        objectiveProblemList.add(mockObjectiveProblem);
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class))).thenReturn(objectiveProblemList);

        List<StudentNPs> studentNPsList = new ArrayList<>();
        studentNPsList.add(mockStudentNPs);
        when(studentNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(studentNPsList);

        List<OpNPs> opNPsList = new ArrayList<>();
        opNPsList.add(mockOpNPs);
        when(opNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(opNPsList);

        // Empty answer list
        when(objectiveProblemAnswerMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());

        // Call the method
        Map<String, String> result = problemSetService.getOneObjectiveProblem(1, 1);

        // Verify the result
        assertEquals("success", result.get("error_message"));
        assertEquals("", result.get("opa_actual_answer")); // No answer yet
        assertEquals("0", result.get("opa_actual_score")); // Score should be 0
    }

    @Test
    void testGetOneObjectiveProblem_ProblemSetNotFound() {
        // Setup authentication
        setupAuthentication(mockStudentDetails);

        // Mock database queries
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());

        // Call the method
        Map<String, String> result = problemSetService.getOneObjectiveProblem(999, 1);

        // Verify the result
        assertEquals("No problem set with this ID", result.get("error_message"));
    }

    @Test
    void testGetOneObjectiveProblem_ObjectiveProblemNotFound() {
        // Setup authentication
        setupAuthentication(mockStudentDetails);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        when(objectiveProblemMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());

        // Call the method
        Map<String, String> result = problemSetService.getOneObjectiveProblem(1, 999);

        // Verify the result
        assertEquals("No problem with this ID", result.get("error_message"));
    }

    @Test
    void testGetOneObjectiveProblem_UserNotBelongToProblemSet() {
        // Setup authentication
        setupAuthentication(mockStudentDetails);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        List<ObjectiveProblem> objectiveProblemList = new ArrayList<>();
        objectiveProblemList.add(mockObjectiveProblem);
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class))).thenReturn(objectiveProblemList);

        when(studentNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());

        // Call the method
        Map<String, String> result = problemSetService.getOneObjectiveProblem(1, 1);

        // Verify the result
        assertEquals("User does not belong to this problem set", result.get("error_message"));
    }

    @Test
    void testGetOneObjectiveProblem_ProblemSetNotStarted() {
        // Setup authentication
        setupAuthentication(mockStudentDetails);

        // Set problem set to start in the future
        mockProblemSet.setPsStartTime(LocalDateTime.now().plusHours(1));

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        List<ObjectiveProblem> objectiveProblemList = new ArrayList<>();
        objectiveProblemList.add(mockObjectiveProblem);
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class))).thenReturn(objectiveProblemList);

        List<StudentNPs> studentNPsList = new ArrayList<>();
        studentNPsList.add(mockStudentNPs);
        when(studentNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(studentNPsList);

        // Call the method
        Map<String, String> result = problemSetService.getOneObjectiveProblem(1, 1);

        // Verify the result
        assertEquals("Problem set not started yet", result.get("error_message"));
    }

    @Test
    void testGetOneObjectiveProblem_StudentNotStartedYet() {
        // Setup authentication
        setupAuthentication(mockStudentDetails);

        // Set student's first start time to null
        mockStudentNPs.setFirstStartTime(null);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        List<ObjectiveProblem> objectiveProblemList = new ArrayList<>();
        objectiveProblemList.add(mockObjectiveProblem);
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class))).thenReturn(objectiveProblemList);

        List<StudentNPs> studentNPsList = new ArrayList<>();
        studentNPsList.add(mockStudentNPs);
        when(studentNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(studentNPsList);

        // Call the method
        Map<String, String> result = problemSetService.getOneObjectiveProblem(1, 1);

        // Verify the result
        assertEquals("Problem set not started yet", result.get("error_message"));
    }

    @Test
    void testGetOneObjectiveProblem_ProblemNotBelongToProblemSet() {
        // Setup authentication
        setupAuthentication(mockStudentDetails);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        List<ObjectiveProblem> objectiveProblemList = new ArrayList<>();
        objectiveProblemList.add(mockObjectiveProblem);
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class))).thenReturn(objectiveProblemList);

        List<StudentNPs> studentNPsList = new ArrayList<>();
        studentNPsList.add(mockStudentNPs);
        when(studentNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(studentNPsList);

        // Problem not belong to problem set
        when(opNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());

        // Call the method
        Map<String, String> result = problemSetService.getOneObjectiveProblem(1, 1);

        // Verify the result
        assertEquals("Problem not belong to problem set", result.get("error_message"));
    }

    @Test
    void testGetOneObjectiveProblem_DurationTimeEnded() {
        // Setup authentication
        setupAuthentication(mockStudentDetails);

        // Set first start time to be more than duration ago (120 + 10 minutes)
        mockStudentNPs.setFirstStartTime(LocalDateTime.now().minusMinutes(130));

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        List<ObjectiveProblem> objectiveProblemList = new ArrayList<>();
        objectiveProblemList.add(mockObjectiveProblem);
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class))).thenReturn(objectiveProblemList);

        List<StudentNPs> studentNPsList = new ArrayList<>();
        studentNPsList.add(mockStudentNPs);
        when(studentNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(studentNPsList);

        List<OpNPs> opNPsList = new ArrayList<>();
        opNPsList.add(mockOpNPs);
        when(opNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(opNPsList);

        List<ObjectiveProblemAnswer> objectiveProblemAnswerList = new ArrayList<>();
        objectiveProblemAnswerList.add(mockObjectiveProblemAnswer);
        when(objectiveProblemAnswerMapper.selectList(any(QueryWrapper.class))).thenReturn(objectiveProblemAnswerList);

        // Call the method
        Map<String, String> result = problemSetService.getOneObjectiveProblem(1, 1);

        // Verify the result
        assertEquals("success", result.get("error_message"));
        assertEquals("ended", result.get("ps_status")); // Status should be ended due to duration
    }

    @Test
    void testGetOneObjectiveProblem_Assignment() {
        // Setup authentication
        setupAuthentication(mockStudentDetails);

        // Set duration to 0 (assignment mode)
        mockProblemSet.setDuration(0);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        List<ObjectiveProblem> objectiveProblemList = new ArrayList<>();
        objectiveProblemList.add(mockObjectiveProblem);
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class))).thenReturn(objectiveProblemList);

        List<StudentNPs> studentNPsList = new ArrayList<>();
        studentNPsList.add(mockStudentNPs);
        when(studentNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(studentNPsList);

        List<OpNPs> opNPsList = new ArrayList<>();
        opNPsList.add(mockOpNPs);
        when(opNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(opNPsList);

        List<ObjectiveProblemAnswer> objectiveProblemAnswerList = new ArrayList<>();
        objectiveProblemAnswerList.add(mockObjectiveProblemAnswer);
        when(objectiveProblemAnswerMapper.selectList(any(QueryWrapper.class))).thenReturn(objectiveProblemAnswerList);

        // Call the method
        Map<String, String> result = problemSetService.getOneObjectiveProblem(1, 1);

        // Verify the result
        assertEquals("success", result.get("error_message"));
        assertEquals("started", result.get("ps_status")); // Status should be started for assignment
        assertEquals("0", result.get("duration")); // Duration should be 0
    }
}