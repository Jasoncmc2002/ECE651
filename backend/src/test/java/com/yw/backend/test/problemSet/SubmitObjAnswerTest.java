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
class ProblemSetServiceSubmitObjectiveProblemAnswerTest {
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
        mockObjectiveProblemAnswer.setOpaActualAnswer("O(n)"); // Incorrect answer initially
        mockObjectiveProblemAnswer.setOpaActualScore(0);
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
    void testSubmitObjectiveProblemAnswer_SuccessNewAnswer() {
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

        // No previous answer
        when(objectiveProblemAnswerMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());
        when(objectiveProblemAnswerMapper.insert(any(ObjectiveProblemAnswer.class))).thenReturn(1);

        // Call the method with correct answer
        Map<String, String> result = problemSetService.submitObjectiveProblemAnswer(1, 1, "O(log n)");

        // Verify the result
        assertEquals("success", result.get("error_message"));

        // Verify that a new answer was inserted
        verify(objectiveProblemAnswerMapper, times(1)).insert(any(ObjectiveProblemAnswer.class));
        verify(objectiveProblemAnswerMapper, never()).update(any(ObjectiveProblemAnswer.class), any(QueryWrapper.class));
    }

    @Test
    void testSubmitObjectiveProblemAnswer_SuccessUpdateExistingAnswer() {
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

        // Existing answer
        List<ObjectiveProblemAnswer> existingAnswers = new ArrayList<>();
        existingAnswers.add(mockObjectiveProblemAnswer);
        when(objectiveProblemAnswerMapper.selectList(any(QueryWrapper.class))).thenReturn(existingAnswers);
        when(objectiveProblemAnswerMapper.update(any(ObjectiveProblemAnswer.class), any(QueryWrapper.class))).thenReturn(1);

        // Call the method with correct answer
        Map<String, String> result = problemSetService.submitObjectiveProblemAnswer(1, 1, "O(log n)");

        // Verify the result
        assertEquals("success", result.get("error_message"));

        // Verify that an existing answer was updated
        verify(objectiveProblemAnswerMapper, never()).insert(any(ObjectiveProblemAnswer.class));
        verify(objectiveProblemAnswerMapper, times(1)).update(any(ObjectiveProblemAnswer.class), any(QueryWrapper.class));
    }

    @Test
    void testSubmitObjectiveProblemAnswer_ProblemSetNotFound() {
        // Setup authentication
        setupAuthentication(mockStudentDetails);

        // Mock database queries
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());

        // Call the method
        Map<String, String> result = problemSetService.submitObjectiveProblemAnswer(999, 1, "O(log n)");

        // Verify the result
        assertEquals("No problem set with this ID", result.get("error_message"));

        // Verify that no database operations were performed
        verify(objectiveProblemAnswerMapper, never()).insert(any(ObjectiveProblemAnswer.class));
        verify(objectiveProblemAnswerMapper, never()).update(any(ObjectiveProblemAnswer.class), any(QueryWrapper.class));
    }

    @Test
    void testSubmitObjectiveProblemAnswer_ProblemSetNotStarted() {
        // Setup authentication
        setupAuthentication(mockStudentDetails);

        // Set problem set to start in the future
        mockProblemSet.setPsStartTime(LocalDateTime.now().plusHours(1));

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        // Call the method
        Map<String, String> result = problemSetService.submitObjectiveProblemAnswer(1, 1, "O(log n)");

        // Verify the result
        assertEquals("Problem set not started yet", result.get("error_message"));
    }

    @Test
    void testSubmitObjectiveProblemAnswer_ObjectiveProblemNotFound() {
        // Setup authentication
        setupAuthentication(mockStudentDetails);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        when(objectiveProblemMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());

        // Call the method
        Map<String, String> result = problemSetService.submitObjectiveProblemAnswer(1, 999, "O(log n)");

        // Verify the result
        assertEquals("No problem with this ID", result.get("error_message"));
    }

    @Test
    void testSubmitObjectiveProblemAnswer_UserNotBelongToProblemSet() {
        // Setup authentication
        setupAuthentication(mockStudentDetails);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        List<ObjectiveProblem> objectiveProblemList = new ArrayList<>();
        objectiveProblemList.add(mockObjectiveProblem);
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class))).thenReturn(objectiveProblemList);

        // Student doesn't belong to problem set
        when(studentNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());

        // Call the method
        Map<String, String> result = problemSetService.submitObjectiveProblemAnswer(1, 1, "O(log n)");

        // Verify the result
        assertEquals("User does not belong to this problem set", result.get("error_message"));
    }

    @Test
    void testSubmitObjectiveProblemAnswer_StudentNotStartedYet() {
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
        Map<String, String> result = problemSetService.submitObjectiveProblemAnswer(1, 1, "O(log n)");

        // Verify the result
        assertEquals("Not Answered", result.get("error_message"));
    }

    @Test
    void testSubmitObjectiveProblemAnswer_ProblemNotBelongToProblemSet() {
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

        // Problem doesn't belong to problem set
        when(opNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());

        // Call the method
        Map<String, String> result = problemSetService.submitObjectiveProblemAnswer(1, 1, "O(log n)");

        // Verify the result
        assertEquals("Problem does not belong to this problem set", result.get("error_message"));
    }

    @Test
    void testSubmitObjectiveProblemAnswer_EmptyAnswer() {
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

        // Call the method with empty answer
        Map<String, String> result = problemSetService.submitObjectiveProblemAnswer(1, 1, "");

        // Verify the result
        assertEquals("Answer cannot be empty", result.get("error_message"));
    }

    @Test
    void testSubmitObjectiveProblemAnswer_AnswerTooLong() {
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

        // Create a very long answer (1025 characters)
        StringBuilder longAnswer = new StringBuilder();
        for (int i = 0; i < 1025; i++) {
            longAnswer.append("a");
        }

        // Call the method with too long answer
        Map<String, String> result = problemSetService.submitObjectiveProblemAnswer(1, 1, longAnswer.toString());

        // Verify the result
        assertEquals("Answer cannot exceed 1024 characters", result.get("error_message"));
    }

    @Test
    void testSubmitObjectiveProblemAnswer_TimeEnded() {
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

        // Call the method
        Map<String, String> result = problemSetService.submitObjectiveProblemAnswer(1, 1, "O(log n)");

        // Verify the result
        assertEquals("Time ended", result.get("error_message"));
    }

    @Test
    void testSubmitObjectiveProblemAnswer_ProblemSetFinished() {
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

        // Call the method
        Map<String, String> result = problemSetService.submitObjectiveProblemAnswer(1, 1, "O(log n)");

        // Verify the result
        assertEquals("Problem set finished", result.get("error_message"));
    }

    @Test
    void testSubmitObjectiveProblemAnswer_Assignment() {
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

        // No previous answer
        when(objectiveProblemAnswerMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());
        when(objectiveProblemAnswerMapper.insert(any(ObjectiveProblemAnswer.class))).thenReturn(1);

        // Call the method with correct answer
        Map<String, String> result = problemSetService.submitObjectiveProblemAnswer(1, 1, "O(log n)");

        // Verify the result
        assertEquals("success", result.get("error_message"));

        // Verify that a new answer was inserted
        verify(objectiveProblemAnswerMapper, times(1)).insert(any(ObjectiveProblemAnswer.class));
    }

    @Test
    void testSubmitObjectiveProblemAnswer_IncorrectAnswer() {
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

        // No previous answer
        when(objectiveProblemAnswerMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());
        when(objectiveProblemAnswerMapper.insert(any(ObjectiveProblemAnswer.class))).thenReturn(1);

        // Call the method with incorrect answer
        Map<String, String> result = problemSetService.submitObjectiveProblemAnswer(1, 1, "O(n)");

        // Verify the result
        assertEquals("success", result.get("error_message"));

        // Verify that a new answer was inserted and score is 0
        verify(objectiveProblemAnswerMapper, times(1)).insert(argThat(answer -> {
            ObjectiveProblemAnswer opa = (ObjectiveProblemAnswer) answer;
            return opa.getOpaActualScore() == 0 && opa.getOpaActualAnswer().equals("O(n)");
        }));
    }
}