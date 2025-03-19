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
import static org.mockito.AdditionalAnswers.*;

@SpringBootTest
class TeacherGetOneStudentRecordTest {
    @Mock
    private ProblemSetMapper problemSetMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private StudentNPsMapper studentNPsMapper;

    @Mock
    private OpNPsMapper opNPsMapper;

    @Mock
    private PNPsMapper pnPsMapper;

    @Mock
    private ObjectiveProblemMapper objectiveProblemMapper;

    @Mock
    private ProgrammingMapper programmingMapper;

    @Mock
    private ObjectiveProblemAnswerMapper objectiveProblemAnswerMapper;

    @Mock
    private ProgrammingAnswerMapper programmingAnswerMapper;

    @InjectMocks
    private ProblemSetTeacherServiceImpl problemSetTeacherService;

    private User mockTeacher;
    private User mockOtherTeacher;
    private User mockAdmin;
    private User mockStudent;
    private User mockAnotherStudent;
    private UserDetailsImpl mockTeacherDetails;
    private UserDetailsImpl mockOtherTeacherDetails;
    private UserDetailsImpl mockAdminDetails;
    private UserDetailsImpl mockStudentDetails;
    private ProblemSet mockProblemSet;
    private StudentNPs mockStudentNPs;
    private OpNPs mockOpNPs;
    private PNPs mockPNPs;
    private ObjectiveProblem mockObjectiveProblem;
    private Programming mockProgramming;
    private ObjectiveProblemAnswer mockObjectiveProblemAnswer;
    private ProgrammingAnswer mockProgrammingAnswer;

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

        mockAnotherStudent = new User(5, "another_student", "password", "Another Student", 0, "photo.jpg");

        // Setup mock problem set
        LocalDateTime now = LocalDateTime.now();
        mockProblemSet = new ProblemSet();
        mockProblemSet.setProblemSetId(1);
        mockProblemSet.setPsName("Test Problem Set");
        mockProblemSet.setPsAuthorId(1); // Created by teacher (user_id = 1)
        mockProblemSet.setPsStartTime(now.minusHours(1)); // Started 1 hour ago
        mockProblemSet.setPsEndTime(now.plusHours(1)); // Ends in 1 hour
        mockProblemSet.setDuration(120); // 120 minutes duration

        // Setup mock student-problemset relation
        mockStudentNPs = new StudentNPs();
        mockStudentNPs.setStudentId(4); // Student's ID
        mockStudentNPs.setProblemSetId(1);
        mockStudentNPs.setFirstStartTime(now.minusMinutes(30)); // Started 30 minutes ago

        // Setup mock objective problem in problem set
        mockOpNPs = new OpNPs();
        mockOpNPs.setProblemSetId(1);
        mockOpNPs.setObjectiveProblemId(1);

        // Setup mock programming problem in problem set
        mockPNPs = new PNPs();
        mockPNPs.setProblemSetId(1);
        mockPNPs.setProgrammingId(1);

        // Setup mock objective problem
        mockObjectiveProblem = new ObjectiveProblem();
        mockObjectiveProblem.setObjectiveProblemId(1);
        mockObjectiveProblem.setOpDescription("What is the time complexity of binary search?");
        mockObjectiveProblem.setOpTotalScore(10);
        mockObjectiveProblem.setOpCorrectAnswer("O(log n)");

        // Setup mock programming problem
        mockProgramming = new Programming();
        mockProgramming.setProgrammingId(1);
        mockProgramming.setPTitle("Write a binary search function");
        mockProgramming.setPTotalScore(20);

        // Setup mock answers
        mockObjectiveProblemAnswer = new ObjectiveProblemAnswer();
        mockObjectiveProblemAnswer.setProblemSetId(1);
        mockObjectiveProblemAnswer.setObjectiveProblemId(1);
        mockObjectiveProblemAnswer.setAuthorId(4); // Student's answer
        mockObjectiveProblemAnswer.setOpaActualAnswer("O(log n)");
        mockObjectiveProblemAnswer.setOpaActualScore(10); // Full score

        mockProgrammingAnswer = new ProgrammingAnswer();
        mockProgrammingAnswer.setProblemSetId(1);
        mockProgrammingAnswer.setProgrammingId(1);
        mockProgrammingAnswer.setAuthorId(4); // Student's answer
        mockProgrammingAnswer.setPaActualScore(15); // Partial score
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
    void testGetOneStudentRecord_SuccessTeacher() {
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

        // Return the teacher as problem set author
        doReturn(mockTeacher).when(userMapper).selectOne(any(QueryWrapper.class));

        // Setup OpNPs and PNPs
        List<OpNPs> opNPsList = new ArrayList<>();
        opNPsList.add(mockOpNPs);
        when(opNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(opNPsList);

        List<PNPs> pnPsList = new ArrayList<>();
        pnPsList.add(mockPNPs);
        when(pnPsMapper.selectList(any(QueryWrapper.class))).thenReturn(pnPsList);

        // Setup objective problem and programming mapper
        doReturn(mockObjectiveProblem).when(objectiveProblemMapper).selectOne(any(QueryWrapper.class));
        doReturn(mockProgramming).when(programmingMapper).selectOne(any(QueryWrapper.class));

        // Setup answers
        List<ObjectiveProblemAnswer> objectiveProblemAnswerList = new ArrayList<>();
        objectiveProblemAnswerList.add(mockObjectiveProblemAnswer);
        when(objectiveProblemAnswerMapper.selectList(any(QueryWrapper.class))).thenReturn(objectiveProblemAnswerList);

        List<ProgrammingAnswer> programmingAnswerList = new ArrayList<>();
        programmingAnswerList.add(mockProgrammingAnswer);
        when(programmingAnswerMapper.selectList(any(QueryWrapper.class))).thenReturn(programmingAnswerList);

        // Call the method
        Map<String, String> result = problemSetTeacherService.getOneStudentRecord(1, 4);

        // Verify the result
        assertEquals("success", result.get("error_message"));
        assertEquals("1", result.get("problem_set_id"));
        assertEquals("4", result.get("student_id"));
        assertEquals("Test Problem Set", result.get("ps_name"));
        assertEquals("Student User", result.get("student_name"));
        assertEquals("student", result.get("student_username"));
        assertNotNull(result.get("ps_start_time"));
        assertNotNull(result.get("ps_end_time"));
        assertEquals("120", result.get("duration"));
        assertEquals("Teacher User", result.get("ps_author_name"));
        assertEquals("30", result.get("ps_total_score")); // 10 + 20
        assertEquals("25", result.get("ps_actual_score")); // 10 + 15
        assertNotNull(result.get("first_start_time"));
        assertEquals("The problem set has started", result.get("ps_status_message"));
    }

    @Test
    void testGetOneStudentRecord_SuccessAdmin() {
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

        // Return the teacher as problem set author
        doReturn(mockTeacher).when(userMapper).selectOne(any(QueryWrapper.class));

        // Setup OpNPs and PNPs
        List<OpNPs> opNPsList = new ArrayList<>();
        opNPsList.add(mockOpNPs);
        when(opNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(opNPsList);

        List<PNPs> pnPsList = new ArrayList<>();
        pnPsList.add(mockPNPs);
        when(pnPsMapper.selectList(any(QueryWrapper.class))).thenReturn(pnPsList);

        // Setup objective problem and programming mapper
        doReturn(mockObjectiveProblem).when(objectiveProblemMapper).selectOne(any(QueryWrapper.class));
        doReturn(mockProgramming).when(programmingMapper).selectOne(any(QueryWrapper.class));

        // Setup answers
        List<ObjectiveProblemAnswer> objectiveProblemAnswerList = new ArrayList<>();
        objectiveProblemAnswerList.add(mockObjectiveProblemAnswer);
        when(objectiveProblemAnswerMapper.selectList(any(QueryWrapper.class))).thenReturn(objectiveProblemAnswerList);

        List<ProgrammingAnswer> programmingAnswerList = new ArrayList<>();
        programmingAnswerList.add(mockProgrammingAnswer);
        when(programmingAnswerMapper.selectList(any(QueryWrapper.class))).thenReturn(programmingAnswerList);

        // Call the method
        Map<String, String> result = problemSetTeacherService.getOneStudentRecord(1, 4);

        // Verify the result
        assertEquals("success", result.get("error_message"));
    }

    @Test
    void testGetOneStudentRecord_NoPermission() {
        // Setup authentication for student (who doesn't have permission)
        setupAuthentication(mockStudentDetails);

        // Call the method
        Map<String, String> result = problemSetTeacherService.getOneStudentRecord(1, 4);

        // Verify the result
        assertEquals("No permission to obtain student record for this problem set", result.get("error_message"));
    }

    @Test
    void testGetOneStudentRecord_ProblemSetNotFound() {
        // Setup authentication for teacher
        setupAuthentication(mockTeacherDetails);

        // Mock database queries
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());

        // Call the method
        Map<String, String> result = problemSetTeacherService.getOneStudentRecord(999, 4);

        // Verify the result
        assertEquals("No problem set under this ID", result.get("error_message"));
    }

    @Test
    void testGetOneStudentRecord_NotAuthorTeacher() {
        // Setup authentication for another teacher (who didn't create the problem set)
        setupAuthentication(mockOtherTeacherDetails);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        // Call the method
        Map<String, String> result = problemSetTeacherService.getOneStudentRecord(1, 4);

        // Verify the result
        assertEquals("No permission to obtain student record from problem sets created by others", result.get("error_message"));
    }

    @Test
    void testGetOneStudentRecord_StudentNotFound() {
        // Setup authentication for teacher
        setupAuthentication(mockTeacherDetails);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        // Student not found
        when(userMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());

        // Call the method
        Map<String, String> result = problemSetTeacherService.getOneStudentRecord(1, 999);

        // Verify the result
        assertEquals("No student under this ID", result.get("error_message"));
    }

    @Test
    void testGetOneStudentRecord_StudentNotBelongToProblemSet() {
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
        Map<String, String> result = problemSetTeacherService.getOneStudentRecord(1, 4);

        // Verify the result
        assertEquals("Student does not belong to this problem set", result.get("error_message"));
    }

    @Test
    void testGetOneStudentRecord_ProblemSetEnded() {
        // Setup authentication for teacher
        setupAuthentication(mockTeacherDetails);

        // Set problem set to have ended
        mockProblemSet.setPsEndTime(LocalDateTime.now().minusHours(1));

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

        // Return the teacher as problem set author
        doReturn(mockTeacher).when(userMapper).selectOne(any(QueryWrapper.class));

        // Setup OpNPs and PNPs
        List<OpNPs> opNPsList = new ArrayList<>();
        opNPsList.add(mockOpNPs);
        when(opNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(opNPsList);

        List<PNPs> pnPsList = new ArrayList<>();
        pnPsList.add(mockPNPs);
        when(pnPsMapper.selectList(any(QueryWrapper.class))).thenReturn(pnPsList);

        // Setup objective problem and programming mapper
        doReturn(mockObjectiveProblem).when(objectiveProblemMapper).selectOne(any(QueryWrapper.class));
        doReturn(mockProgramming).when(programmingMapper).selectOne(any(QueryWrapper.class));

        // Setup answers
        List<ObjectiveProblemAnswer> objectiveProblemAnswerList = new ArrayList<>();
        objectiveProblemAnswerList.add(mockObjectiveProblemAnswer);
        when(objectiveProblemAnswerMapper.selectList(any(QueryWrapper.class))).thenReturn(objectiveProblemAnswerList);

        List<ProgrammingAnswer> programmingAnswerList = new ArrayList<>();
        programmingAnswerList.add(mockProgrammingAnswer);
        when(programmingAnswerMapper.selectList(any(QueryWrapper.class))).thenReturn(programmingAnswerList);

        // Call the method
        Map<String, String> result = problemSetTeacherService.getOneStudentRecord(1, 4);

        // Verify the result
        assertEquals("success", result.get("error_message"));
        assertEquals("The problem set has ended", result.get("ps_status_message"));
    }

    @Test
    void testGetOneStudentRecord_AssignmentMode() {
        // Setup authentication for teacher
        setupAuthentication(mockTeacherDetails);

        // Set problem set to assignment mode (duration = 0)
        mockProblemSet.setDuration(0);

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

        // Return the teacher as problem set author
        doReturn(mockTeacher).when(userMapper).selectOne(any(QueryWrapper.class));

        // Setup OpNPs and PNPs
        List<OpNPs> opNPsList = new ArrayList<>();
        opNPsList.add(mockOpNPs);
        when(opNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(opNPsList);

        List<PNPs> pnPsList = new ArrayList<>();
        pnPsList.add(mockPNPs);
        when(pnPsMapper.selectList(any(QueryWrapper.class))).thenReturn(pnPsList);

        // Setup objective problem and programming mapper
        doReturn(mockObjectiveProblem).when(objectiveProblemMapper).selectOne(any(QueryWrapper.class));
        doReturn(mockProgramming).when(programmingMapper).selectOne(any(QueryWrapper.class));

        // Setup answers
        List<ObjectiveProblemAnswer> objectiveProblemAnswerList = new ArrayList<>();
        objectiveProblemAnswerList.add(mockObjectiveProblemAnswer);
        when(objectiveProblemAnswerMapper.selectList(any(QueryWrapper.class))).thenReturn(objectiveProblemAnswerList);

        List<ProgrammingAnswer> programmingAnswerList = new ArrayList<>();
        programmingAnswerList.add(mockProgrammingAnswer);
        when(programmingAnswerMapper.selectList(any(QueryWrapper.class))).thenReturn(programmingAnswerList);

        // Call the method
        Map<String, String> result = problemSetTeacherService.getOneStudentRecord(1, 4);

        // Verify the result
        assertEquals("success", result.get("error_message"));
        assertEquals("0", result.get("duration"));
    }

    @Test
    void testGetOneStudentRecord_NoFirstStartTime() {
        // Setup authentication for teacher
        setupAuthentication(mockTeacherDetails);

        // Set first start time to null
        mockStudentNPs.setFirstStartTime(null);

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

        // Return the teacher as problem set author
        doReturn(mockTeacher).when(userMapper).selectOne(any(QueryWrapper.class));

        // Setup OpNPs and PNPs
        List<OpNPs> opNPsList = new ArrayList<>();
        opNPsList.add(mockOpNPs);
        when(opNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(opNPsList);

        List<PNPs> pnPsList = new ArrayList<>();
        pnPsList.add(mockPNPs);
        when(pnPsMapper.selectList(any(QueryWrapper.class))).thenReturn(pnPsList);

        // Setup objective problem and programming mapper
        doReturn(mockObjectiveProblem).when(objectiveProblemMapper).selectOne(any(QueryWrapper.class));
        doReturn(mockProgramming).when(programmingMapper).selectOne(any(QueryWrapper.class));

        // Setup answers
        List<ObjectiveProblemAnswer> objectiveProblemAnswerList = new ArrayList<>();
        objectiveProblemAnswerList.add(mockObjectiveProblemAnswer);
        when(objectiveProblemAnswerMapper.selectList(any(QueryWrapper.class))).thenReturn(objectiveProblemAnswerList);

        List<ProgrammingAnswer> programmingAnswerList = new ArrayList<>();
        programmingAnswerList.add(mockProgrammingAnswer);
        when(programmingAnswerMapper.selectList(any(QueryWrapper.class))).thenReturn(programmingAnswerList);

        // Call the method
        Map<String, String> result = problemSetTeacherService.getOneStudentRecord(1, 4);

        // Verify the result
        assertEquals("success", result.get("error_message"));
        assertEquals("", result.get("first_start_time"));
    }
}