package com.yw.backend.test.problemSet;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yw.backend.mapper.*;
import com.yw.backend.pojo.*;
import com.yw.backend.service.impl.problemSet.ProblemSetTeacherServiceImpl;
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
class TeacherGetOneStudentAllObjProblemTest {
    @Mock
    private ProblemSetMapper problemSetMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private StudentNPsMapper studentNPsMapper;

    @Mock
    private OpNPsMapper opNPsMapper;

    @Mock
    private ObjectiveProblemMapper objectiveProblemMapper;

    @Mock
    private ObjectiveProblemAnswerMapper objectiveProblemAnswerMapper;

    @InjectMocks
    private ProblemSetTeacherServiceImpl problemSetTeacherService;

    private User mockTeacher;
    private User mockOtherTeacher;
    private User mockAdmin;
    private User mockStudent;
    private UserDetailsImpl mockTeacherDetails;
    private UserDetailsImpl mockOtherTeacherDetails;
    private UserDetailsImpl mockAdminDetails;
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

        // Setup mock users with different permissions
        mockTeacher = new User(1, "teacher", "password", "Teacher User", 1, "photo.jpg");
        mockTeacherDetails = new UserDetailsImpl(mockTeacher);

        mockOtherTeacher = new User(2, "other_teacher", "password", "Other Teacher", 1, "photo.jpg");
        mockOtherTeacherDetails = new UserDetailsImpl(mockOtherTeacher);

        mockAdmin = new User(3, "admin", "password", "Admin User", 2, "photo.jpg");
        mockAdminDetails = new UserDetailsImpl(mockAdmin);

        mockStudent = new User(4, "student", "password", "Student User", 0, "photo.jpg");
        mockStudentDetails = new UserDetailsImpl(mockStudent);

        // Setup mock problem set
        mockProblemSet = new ProblemSet();
        mockProblemSet.setProblemSetId(1);
        mockProblemSet.setPsName("Test Problem Set");
        mockProblemSet.setPsAuthorId(1); // Created by teacher (user_id = 1)
        mockProblemSet.setPsStartTime(LocalDateTime.now().minusHours(1));
        mockProblemSet.setPsEndTime(LocalDateTime.now().plusHours(1));

        // Setup mock student-problemset relation
        mockStudentNPs = new StudentNPs();
        mockStudentNPs.setStudentId(4); // Student's ID
        mockStudentNPs.setProblemSetId(1);
        mockStudentNPs.setFirstStartTime(LocalDateTime.now().minusMinutes(30));

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
        mockObjectiveProblem1.setOpDescription("What is the time complexity of binary search?");
        mockObjectiveProblem1.setOpTotalScore(10);
        mockObjectiveProblem1.setOpCorrectAnswer("O(log n)");

        mockObjectiveProblem2 = new ObjectiveProblem();
        mockObjectiveProblem2.setObjectiveProblemId(2);
        mockObjectiveProblem2.setOpDescription("What is the time complexity of bubble sort?");
        mockObjectiveProblem2.setOpTotalScore(8);
        mockObjectiveProblem2.setOpCorrectAnswer("O(n^2)");

        // Setup mock objective problem answer
        mockObjectiveProblemAnswer = new ObjectiveProblemAnswer();
        mockObjectiveProblemAnswer.setProblemSetId(1);
        mockObjectiveProblemAnswer.setObjectiveProblemId(1);
        mockObjectiveProblemAnswer.setAuthorId(4); // Student's answer
        mockObjectiveProblemAnswer.setOpaActualAnswer("O(log n)");
        mockObjectiveProblemAnswer.setOpaActualScore(10); // Full score
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
    void testGetOneStudentAllObjectiveProblem_SuccessTeacher() {
        // Setup authentication for the teacher who created the problem set
        setupAuthentication(mockTeacherDetails);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        List<User> studentList = new ArrayList<>();
        studentList.add(mockStudent);
        when(userMapper.selectList(any(QueryWrapper.class))).thenReturn(studentList);

        List<StudentNPs> studentNPsList = new ArrayList<>();
        studentNPsList.add(mockStudentNPs);
        when(studentNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(studentNPsList);

        List<OpNPs> opNPsList = new ArrayList<>();
        opNPsList.add(mockOpNPs1);
        opNPsList.add(mockOpNPs2);
        when(opNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(opNPsList);

        // Handle returning different objective problems for different queries
        when(objectiveProblemMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(mockObjectiveProblem1)  // First call
                .thenReturn(mockObjectiveProblem2); // Second call

        // For the first problem - student has answered
        List<ObjectiveProblemAnswer> answeredList = new ArrayList<>();
        answeredList.add(mockObjectiveProblemAnswer);

        // For the second problem - student has not answered
        List<ObjectiveProblemAnswer> notAnsweredList = new ArrayList<>();

        // Mock the objectiveProblemAnswerMapper to return different values for different queries
        when(objectiveProblemAnswerMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(answeredList)     // First call
                .thenReturn(notAnsweredList); // Second call

        // Call the method
        List<Map<String, String>> result = problemSetTeacherService.getOneStudentAllObjectiveProblem(1, 4);

        // Verify the result
        assertEquals(2, result.size());

        // Check first problem (answered)
        Map<String, String> problem1 = result.get(0);
        assertEquals("1", problem1.get("objective_problem_id"));
        assertTrue(problem1.get("op_description").startsWith("What is the time complexity of binary search?"));
        assertEquals("10", problem1.get("op_total_score"));
        assertEquals("Already answered", problem1.get("opa_status"));
        assertEquals("10", problem1.get("opa_actual_score"));

        // Check second problem (not answered)
        Map<String, String> problem2 = result.get(1);
        assertEquals("2", problem2.get("objective_problem_id"));
        assertTrue(problem2.get("op_description").startsWith("What is the time complexity of bubble sort?"));
        assertEquals("8", problem2.get("op_total_score"));
        assertEquals("Not answered yet", problem2.get("opa_status"));
        assertEquals("0", problem2.get("opa_actual_score"));
    }

    @Test
    void testGetOneStudentAllObjectiveProblem_SuccessAdmin() {
        // Setup authentication for admin (who didn't create the problem set)
        setupAuthentication(mockAdminDetails);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        List<User> studentList = new ArrayList<>();
        studentList.add(mockStudent);
        when(userMapper.selectList(any(QueryWrapper.class))).thenReturn(studentList);

        List<StudentNPs> studentNPsList = new ArrayList<>();
        studentNPsList.add(mockStudentNPs);
        when(studentNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(studentNPsList);

        List<OpNPs> opNPsList = new ArrayList<>();
        opNPsList.add(mockOpNPs1);
        when(opNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(opNPsList);

        when(objectiveProblemMapper.selectOne(any(QueryWrapper.class))).thenReturn(mockObjectiveProblem1);

        List<ObjectiveProblemAnswer> answeredList = new ArrayList<>();
        answeredList.add(mockObjectiveProblemAnswer);
        when(objectiveProblemAnswerMapper.selectList(any(QueryWrapper.class))).thenReturn(answeredList);

        // Call the method
        List<Map<String, String>> result = problemSetTeacherService.getOneStudentAllObjectiveProblem(1, 4);

        // Verify the result
        assertEquals(1, result.size());
        assertFalse(result.get(0).containsKey("error_message")); // No error
    }

    @Test
    void testGetOneStudentAllObjectiveProblem_NoPermission() {
        // Setup authentication for student (who doesn't have permission)
        setupAuthentication(mockStudentDetails);

        // Call the method
        List<Map<String, String>> result = problemSetTeacherService.getOneStudentAllObjectiveProblem(1, 4);

        // Verify the result
        assertEquals(1, result.size());
        assertEquals("No permission to obtain student record of objective problems for this problem set", result.get(0).get("error_message"));
    }

    @Test
    void testGetOneStudentAllObjectiveProblem_ProblemSetNotFound() {
        // Setup authentication for teacher
        setupAuthentication(mockTeacherDetails);

        // Mock database queries
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());

        // Call the method
        List<Map<String, String>> result = problemSetTeacherService.getOneStudentAllObjectiveProblem(999, 4);

        // Verify the result
        assertEquals(1, result.size());
        assertEquals("No problem set under this ID", result.get(0).get("error_message"));
    }

    @Test
    void testGetOneStudentAllObjectiveProblem_NotAuthorTeacher() {
        // Setup authentication for another teacher (who didn't create the problem set)
        setupAuthentication(mockOtherTeacherDetails);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        // Call the method
        List<Map<String, String>> result = problemSetTeacherService.getOneStudentAllObjectiveProblem(1, 4);

        // Verify the result
        assertEquals(1, result.size());
        assertEquals("No permission to obtain student record of objective problems created by others", result.get(0).get("error_message"));
    }

    @Test
    void testGetOneStudentAllObjectiveProblem_StudentNotFound() {
        // Setup authentication for teacher
        setupAuthentication(mockTeacherDetails);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        // Student not found
        when(userMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());

        // Call the method
        List<Map<String, String>> result = problemSetTeacherService.getOneStudentAllObjectiveProblem(1, 999);

        // Verify the result
        assertEquals(1, result.size());
        assertEquals("No student under this ID", result.get(0).get("error_message"));
    }

    @Test
    void testGetOneStudentAllObjectiveProblem_StudentNotBelongToProblemSet() {
        // Setup authentication for teacher
        setupAuthentication(mockTeacherDetails);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        List<User> studentList = new ArrayList<>();
        studentList.add(mockStudent);
        when(userMapper.selectList(any(QueryWrapper.class))).thenReturn(studentList);

        // Student doesn't belong to problem set
        when(studentNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());

        // Call the method
        List<Map<String, String>> result = problemSetTeacherService.getOneStudentAllObjectiveProblem(1, 4);

        // Verify the result
        assertEquals(1, result.size());
        assertEquals("Student does not belong to problem set", result.get(0).get("error_message"));
    }

    @Test
    void testGetOneStudentAllObjectiveProblem_EmptyProblemList() {
        // Setup authentication for teacher
        setupAuthentication(mockTeacherDetails);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        List<User> studentList = new ArrayList<>();
        studentList.add(mockStudent);
        when(userMapper.selectList(any(QueryWrapper.class))).thenReturn(studentList);

        List<StudentNPs> studentNPsList = new ArrayList<>();
        studentNPsList.add(mockStudentNPs);
        when(studentNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(studentNPsList);

        // Empty problem list
        when(opNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());

        // Call the method
        List<Map<String, String>> result = problemSetTeacherService.getOneStudentAllObjectiveProblem(1, 4);

        // Verify the result
        assertEquals(0, result.size()); // Empty list returned
    }
}