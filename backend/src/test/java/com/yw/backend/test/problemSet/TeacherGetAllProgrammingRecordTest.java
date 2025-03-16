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
class problemSetTeacherServiceGetAllProgrammingRecordTest {
    @Mock
    private ProblemSetMapper problemSetMapper;

    @Mock
    private PNPsMapper pnPsMapper;

    @Mock
    private ProgrammingMapper programmingMapper;

    @Mock
    private ProgrammingAnswerMapper programmingAnswerMapper;

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
    private PNPs mockPNPs1;
    private PNPs mockPNPs2;
    private Programming mockProgramming1;
    private Programming mockProgramming2;
    private ProgrammingAnswer mockProgrammingAnswer1;
    private ProgrammingAnswer mockProgrammingAnswer2;
    private ProgrammingAnswer mockProgrammingAnswer3;

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
        mockProgramming1.setPTitle("Question 1");
        mockProgramming1.setPTotalScore(20);

        mockProgramming2 = new Programming();
        mockProgramming2.setProgrammingId(2);
        mockProgramming2.setPTitle("Question 2");
        mockProgramming2.setPTotalScore(15);

        // Setup mock programming answers
        mockProgrammingAnswer1 = new ProgrammingAnswer();
        mockProgrammingAnswer1.setProblemSetId(1);
        mockProgrammingAnswer1.setProgrammingId(1);
        mockProgrammingAnswer1.setAuthorId(4); // Student's answer
        mockProgrammingAnswer1.setPaActualScore(20); // Full score

        mockProgrammingAnswer2 = new ProgrammingAnswer();
        mockProgrammingAnswer2.setProblemSetId(1);
        mockProgrammingAnswer2.setProgrammingId(1);
        mockProgrammingAnswer2.setAuthorId(5); // Another student's answer
        mockProgrammingAnswer2.setPaActualScore(10); // Partial score

        mockProgrammingAnswer3 = new ProgrammingAnswer();
        mockProgrammingAnswer3.setProblemSetId(1);
        mockProgrammingAnswer3.setProgrammingId(2);
        mockProgrammingAnswer3.setAuthorId(4); // Student's answer
        mockProgrammingAnswer3.setPaActualScore(15); // Full score
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
    void testGetAllProgrammingRecord_SuccessTeacher() {
        // Setup authentication for the teacher who created the problem set
        setupAuthentication(mockTeacherDetails);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        List<PNPs> pnPsList = new ArrayList<>();
        pnPsList.add(mockPNPs1);
        pnPsList.add(mockPNPs2);
        when(pnPsMapper.selectList(any(QueryWrapper.class))).thenReturn(pnPsList);

        // Mock the programming mapper to return different values on successive calls
        when(programmingMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(mockProgramming1)  // First call
                .thenReturn(mockProgramming2); // Second call

        // For Question 1 - First call to programmingAnswerMapper
        List<ProgrammingAnswer> programmingAnswerList1 = new ArrayList<>();
        programmingAnswerList1.add(mockProgrammingAnswer1);
        programmingAnswerList1.add(mockProgrammingAnswer2);

        // For Question 2 - Second call to programmingAnswerMapper
        List<ProgrammingAnswer> programmingAnswerList2 = new ArrayList<>();
        programmingAnswerList2.add(mockProgrammingAnswer3);

        // Mock the programmingAnswerMapper to return different lists on successive calls
        when(programmingAnswerMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(programmingAnswerList1)  // First call
                .thenReturn(programmingAnswerList2); // Second call

        // Call the method
        List<Map<String, String>> result = problemSetTeacherService.getAllProgrammingRecord(1);

        // Verify the result
        assertEquals(2, result.size());

        // Check first problem
        Map<String, String> problem1 = result.get(0);
        assertEquals("1", problem1.get("programming_id"));
        assertEquals("Question 1", problem1.get("p_title"));
        assertEquals("1", problem1.get("p_correct_count")); // 1 student got full score
        assertEquals("2", problem1.get("p_answer_count")); // 2 students answered

        // Check second problem
        Map<String, String> problem2 = result.get(1);
        assertEquals("2", problem2.get("programming_id"));
        assertEquals("Question 2", problem2.get("p_title"));
        assertEquals("1", problem2.get("p_correct_count")); // 1 student got full score
        assertEquals("1", problem2.get("p_answer_count")); // 1 student answered
    }

    @Test
    void testGetAllProgrammingRecord_SuccessAdmin() {
        // Setup authentication for admin (who didn't create the problem set)
        setupAuthentication(mockAdminDetails);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        List<PNPs> pnPsList = new ArrayList<>();
        pnPsList.add(mockPNPs1);
        when(pnPsMapper.selectList(any(QueryWrapper.class))).thenReturn(pnPsList);

        // Use doReturn().when() pattern to avoid NPE
        doReturn(mockProgramming1).when(programmingMapper).selectOne(any(QueryWrapper.class));

        List<ProgrammingAnswer> programmingAnswerList = new ArrayList<>();
        programmingAnswerList.add(mockProgrammingAnswer1);
        when(programmingAnswerMapper.selectList(any(QueryWrapper.class))).thenReturn(programmingAnswerList);

        // Call the method
        List<Map<String, String>> result = problemSetTeacherService.getAllProgrammingRecord(1);

        // Verify the result
        assertEquals(1, result.size());
        assertFalse(result.get(0).containsKey("error_message")); // No error
    }

    @Test
    void testGetAllProgrammingRecord_NoPermission() {
        // Setup authentication for student (who doesn't have permission)
        setupAuthentication(mockStudentDetails);

        // Call the method
        List<Map<String, String>> result = problemSetTeacherService.getAllProgrammingRecord(1);

        // Verify the result
        assertEquals(1, result.size());
        assertEquals("No permission to obtain problem set record", result.get(0).get("error_message"));
    }

    @Test
    void testGetAllProgrammingRecord_ProblemSetNotFound() {
        // Setup authentication for teacher
        setupAuthentication(mockTeacherDetails);

        // Mock database queries
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());

        // Call the method
        List<Map<String, String>> result = problemSetTeacherService.getAllProgrammingRecord(999);

        // Verify the result
        assertEquals(1, result.size());
        assertEquals("No problem set under this ID", result.get(0).get("error_message"));
    }

    @Test
    void testGetAllProgrammingRecord_NotAuthorTeacher() {
        // Setup authentication for another teacher (who didn't create the problem set)
        setupAuthentication(mockOtherTeacherDetails);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        // Call the method
        List<Map<String, String>> result = problemSetTeacherService.getAllProgrammingRecord(1);

        // Verify the result
        assertEquals(1, result.size());
        assertEquals("You cannot query records of problem sets created by others", result.get(0).get("error_message"));
    }

    @Test
    void testGetAllProgrammingRecord_EmptyProblemList() {
        // Setup authentication for teacher
        setupAuthentication(mockTeacherDetails);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        // Empty problem list
        when(pnPsMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());

        // Call the method
        List<Map<String, String>> result = problemSetTeacherService.getAllProgrammingRecord(1);

        // Verify the result
        assertEquals(0, result.size()); // Empty list returned
    }

    @Test
    void testGetAllProgrammingRecord_NoAnswers() {
        // Setup authentication for teacher
        setupAuthentication(mockTeacherDetails);

        // Mock database queries
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(problemSetList);

        List<PNPs> pnPsList = new ArrayList<>();
        pnPsList.add(mockPNPs1);
        when(pnPsMapper.selectList(any(QueryWrapper.class))).thenReturn(pnPsList);

        when(programmingMapper.selectOne(any(QueryWrapper.class))).thenReturn(mockProgramming1);

        // No answers
        when(programmingAnswerMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());

        // Call the method
        List<Map<String, String>> result = problemSetTeacherService.getAllProgrammingRecord(1);

        // Verify the result
        assertEquals(1, result.size());
        Map<String, String> problem = result.get(0);
        assertEquals("0", problem.get("p_correct_count")); // 0 students got full score
        assertEquals("0", problem.get("p_answer_count")); // 0 students answered
    }
}