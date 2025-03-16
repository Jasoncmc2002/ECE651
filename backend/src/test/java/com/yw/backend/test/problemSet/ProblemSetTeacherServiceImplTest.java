package com.yw.backend.test.problemSet;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yw.backend.mapper.*;
import com.yw.backend.pojo.*;
import com.yw.backend.service.impl.problemSet.ProblemSetTeacherServiceImpl;
import com.yw.backend.service.impl.utils.UserDetailsImpl;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ProblemSetTeacherServiceImplTest {

    @Mock
    private ProblemSetMapper problemSetMapper;
    @Mock
    private UserMapper userMapper;
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
    private StudentNPsMapper studentNPsMapper;
    @Mock
    private ProgrammingAnswerMapper programmingAnswerMapper;
    @InjectMocks
    private ProblemSetTeacherServiceImpl problemSetTeacherService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        setAuthenticationUser(1, 1); // default teacher
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setAuthenticationUser(int permission, int userId) {
        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setPermission(permission);
        UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authToken);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetOneProblemSetInfo_Success() {
        ProblemSet mockProblemSet = new ProblemSet();
        mockProblemSet.setProblemSetId(1);
        mockProblemSet.setPsName("Test Set");
        mockProblemSet.setPsAuthorId(1);
        mockProblemSet.setPsStartTime(LocalDateTime.now().minusDays(1));
        mockProblemSet.setPsEndTime(LocalDateTime.now().plusDays(1));
        mockProblemSet.setDuration(60);

        User mockAuthor = new User();
        mockAuthor.setUserId(1);
        mockAuthor.setName("Teacher A");

        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.singletonList(mockProblemSet));
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(mockAuthor);

        Map<String, String> response = problemSetTeacherService.getOneProblemSetInfo(1);

        assertEquals("success", response.get("error_message"));
        assertEquals("Test Set", response.get("ps_name"));
        assertEquals("1", response.get("ps_author_id"));
        assertEquals("Teacher A", response.get("ps_author_name"));
        assertEquals("The problem set has started", response.get("ps_status_message"));
    }

    @Test
    void testGetOneProblemSetInfo_NoPermission() {
        setAuthenticationUser(0, 1); // no permission user

        Map<String, String> response = problemSetTeacherService.getOneProblemSetInfo(1);

        assertEquals("No operation permission for obtaining the integrated performance report of the problems",
                response.get("error_message"));
    }

    @Test
    void testGetOneProblemSetInfo_NotFound() {
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());

        Map<String, String> response = problemSetTeacherService.getOneProblemSetInfo(999);

        assertEquals("No such problem set found through ID query", response.get("error_message"));
    }

    @Test
    void testGetOneProblemSetInfo_TeacherCannotAccessOthersSet() {
        ProblemSet mockProblemSet = new ProblemSet();
        mockProblemSet.setProblemSetId(2);
        mockProblemSet.setPsAuthorId(99); // created by other user

        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.singletonList(mockProblemSet));

        Map<String, String> response = problemSetTeacherService.getOneProblemSetInfo(2);

        assertEquals("Teachers are not allowed to search for transcripts created by others for problem sets",
                response.get("error_message"));
    }

    @Test
    void testGetOneProblemSetInfo_AdminCanAccessAnySet() {
        setAuthenticationUser(2, 1); // manager permission

        ProblemSet mockProblemSet = new ProblemSet();
        mockProblemSet.setProblemSetId(3);
        mockProblemSet.setPsAuthorId(99); // created by another user
        mockProblemSet.setPsName("Admin Set");
        mockProblemSet.setPsStartTime(LocalDateTime.now().minusDays(1));
        mockProblemSet.setPsEndTime(LocalDateTime.now().plusDays(1));
        mockProblemSet.setDuration(120);

        User mockAuthor = new User();
        mockAuthor.setUserId(99);
        mockAuthor.setName("Teacher B");

        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.singletonList(mockProblemSet));
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(mockAuthor);

        Map<String, String> response = problemSetTeacherService.getOneProblemSetInfo(3);

        assertEquals("success", response.get("error_message"));
        assertEquals("Admin Set", response.get("ps_name"));
        assertEquals("99", response.get("ps_author_id"));
        assertEquals("Teacher B", response.get("ps_author_name"));
    }

    @Test
    void testGetOneProblemSetInfo_SetNotStarted() {
        ProblemSet mockProblemSet = new ProblemSet();
        mockProblemSet.setProblemSetId(4);
        mockProblemSet.setPsAuthorId(1);
        mockProblemSet.setPsStartTime(LocalDateTime.now().plusDays(1));
        mockProblemSet.setPsEndTime(LocalDateTime.now().plusDays(2));
        mockProblemSet.setDuration(90);

        User mockAuthor = new User();
        mockAuthor.setUserId(1);
        mockAuthor.setName("Teacher A");

        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.singletonList(mockProblemSet));
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(mockAuthor);
        when(opNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());
        when(pnPsMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());

        Map<String, String> response = problemSetTeacherService.getOneProblemSetInfo(4);

        assertEquals("The problem set has not started yet", response.get("ps_status_message"));
    }


    @Test
    void testGetOneProblemSetInfo_SetEnded() {
        ProblemSet mockProblemSet = new ProblemSet();
        mockProblemSet.setProblemSetId(5);
        mockProblemSet.setPsAuthorId(1);
        mockProblemSet.setPsStartTime(LocalDateTime.now().minusDays(3));
        mockProblemSet.setPsEndTime(LocalDateTime.now().minusDays(1));
        mockProblemSet.setDuration(120);

        User mockAuthor = new User();
        mockAuthor.setUserId(1);
        mockAuthor.setName("Teacher A");

        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.singletonList(mockProblemSet));
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(mockAuthor);

        Map<String, String> response = problemSetTeacherService.getOneProblemSetInfo(5);

        assertEquals("The problem set has ended", response.get("ps_status_message"));
    }

    @Test
    void testGetOneProblemSetInfo_WithTotalScore() {
        ProblemSet mockProblemSet = new ProblemSet();
        mockProblemSet.setProblemSetId(6);
        mockProblemSet.setPsAuthorId(1);
        mockProblemSet.setPsStartTime(LocalDateTime.now().minusDays(1));
        mockProblemSet.setPsEndTime(LocalDateTime.now().plusDays(1));
        mockProblemSet.setDuration(120);

        User mockAuthor = new User();
        mockAuthor.setUserId(1);
        mockAuthor.setName("Teacher A");


        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.singletonList(mockProblemSet));
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(mockAuthor);

        OpNPs mockOpNPs = new OpNPs();
        mockOpNPs.setProblemSetId(6);
        mockOpNPs.setObjectiveProblemId(101);

        ObjectiveProblem mockObjectiveProblem = new ObjectiveProblem();
        mockObjectiveProblem.setObjectiveProblemId(101);
        mockObjectiveProblem.setOpTotalScore(30);

        when(opNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.singletonList(mockOpNPs));
        when(objectiveProblemMapper.selectOne(any(QueryWrapper.class))).thenReturn(mockObjectiveProblem);

        PNPs mockPNPs = new PNPs();
        mockPNPs.setProblemSetId(6);
        mockPNPs.setProgrammingId(202);

        Programming mockProgramming = new Programming();
        mockProgramming.setProgrammingId(202);
        mockProgramming.setPTotalScore(70);

        when(pnPsMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.singletonList(mockPNPs));
        when(programmingMapper.selectOne(any(QueryWrapper.class))).thenReturn(mockProgramming);

        Map<String, String> response = problemSetTeacherService.getOneProblemSetInfo(6);

        assertEquals("success", response.get("error_message"));
        assertEquals("100", response.get("ps_total_score")); // 30 + 70 = 100
    }

    @Test
    void testGetAllStudentRecord_Success() {
        ProblemSet mockProblemSet = new ProblemSet();
        mockProblemSet.setProblemSetId(1);
        mockProblemSet.setPsAuthorId(1);

        StudentNPs mockStudentNPs = new StudentNPs();
        mockStudentNPs.setStudentId(101);
        mockStudentNPs.setProblemSetId(1);

        User mockStudent = new User();
        mockStudent.setUserId(101);
        mockStudent.setName("Student A");
        mockStudent.setUsername("student_a");
        mockStudent.setPermission(0);

        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.singletonList(mockProblemSet));
        when(studentNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.singletonList(mockStudentNPs));
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(mockStudent);

        ObjectiveProblemAnswer mockOpAnswer = new ObjectiveProblemAnswer();
        mockOpAnswer.setOpaActualScore(20);

        ProgrammingAnswer mockPaAnswer = new ProgrammingAnswer();
        mockPaAnswer.setPaActualScore(80);

        when(objectiveProblemAnswerMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.singletonList(mockOpAnswer));
        when(programmingAnswerMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.singletonList(mockPaAnswer));

        List<Map<String, String>> response = problemSetTeacherService.getAllStudentRecord(1);

        assertEquals(1, response.size());
        assertEquals("Student A", response.get(0).get("name"));
        assertEquals("100", response.get(0).get("ps_actual_score")); // 20 + 80 = 100
    }

    @Test
    void testGetAllObjectiveProblemRecord_Success() {
        ProblemSet mockProblemSet = new ProblemSet();
        mockProblemSet.setProblemSetId(1);
        mockProblemSet.setPsAuthorId(1);

        OpNPs mockOpNPs = new OpNPs();
        mockOpNPs.setProblemSetId(1);
        mockOpNPs.setObjectiveProblemId(201);

        ObjectiveProblem mockObjectiveProblem = new ObjectiveProblem();
        mockObjectiveProblem.setObjectiveProblemId(201);
        mockObjectiveProblem.setOpDescription("Sample problem description");
        mockObjectiveProblem.setOpTotalScore(10);

        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.singletonList(mockProblemSet));
        when(opNPsMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.singletonList(mockOpNPs));
        when(objectiveProblemMapper.selectOne(any(QueryWrapper.class))).thenReturn(mockObjectiveProblem);

        ObjectiveProblemAnswer correctAnswer = new ObjectiveProblemAnswer();
        correctAnswer.setOpaActualScore(10);

        ObjectiveProblemAnswer wrongAnswer = new ObjectiveProblemAnswer();
        wrongAnswer.setOpaActualScore(5);

        when(objectiveProblemAnswerMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(correctAnswer, wrongAnswer));

        List<Map<String, String>> response = problemSetTeacherService.getAllObjectiveProblemRecord(1);

        assertEquals(1, response.size());
        assertEquals("1", response.get(0).get("op_correct_count")); // only 1 person full score
        assertEquals("2", response.get(0).get("op_answer_count")); // 2 person do the problem
    }

    @Test
    void testGetAllStudentRecord_TeacherCannotAccessOthers() {
        setAuthenticationUser(1, 2);

        ProblemSet mockProblemSet = new ProblemSet();
        mockProblemSet.setProblemSetId(1);
        mockProblemSet.setPsAuthorId(99);

        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.singletonList(mockProblemSet));

        List<Map<String, String>> response = problemSetTeacherService.getAllStudentRecord(1);

        assertEquals(1, response.size());
        assertEquals("Teachers are not allowed to search for transcripts created by others for problem sets",
                response.get(0).get("error_message"));
    }

    @Test
    void testGetAllObjectiveProblemRecord_TeacherCannotAccessOthers() {
        setAuthenticationUser(1, 2);

        ProblemSet mockProblemSet = new ProblemSet();
        mockProblemSet.setProblemSetId(1);
        mockProblemSet.setPsAuthorId(99);

        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.singletonList(mockProblemSet));

        List<Map<String, String>> response = problemSetTeacherService.getAllObjectiveProblemRecord(1);

        assertEquals(1, response.size());
        assertEquals("Teachers are not allowed to search for transcripts created by others for problem sets",
                response.get(0).get("error_message"));
    }

    @Test
    void testGetAllStudentRecord_NoPermission() {
        setAuthenticationUser(0, 1);

        List<Map<String, String>> response = problemSetTeacherService.getAllStudentRecord(1);

        assertEquals(1, response.size());
        assertEquals("No operation permission for obtaining the integrated performance report of the problems",
                response.get(0).get("error_message"));
    }

    @Test
    void testGetAllStudentRecord_ProblemSetNotFound() {
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());

        List<Map<String, String>> response = problemSetTeacherService.getAllStudentRecord(999);

        assertEquals(1, response.size());
        assertEquals("No such problem set found through ID query", response.get(0).get("error_message"));
    }


    @Test
    void testGetAllObjectiveProblemRecord_NoPermission() {
        setAuthenticationUser(0, 1);

        List<Map<String, String>> response = problemSetTeacherService.getAllObjectiveProblemRecord(1);

        assertEquals(1, response.size());
        assertEquals("No operation permission for obtaining the integrated performance report of the problems",
                response.get(0).get("error_message"));
    }

    @Test
    void testGetAllObjectiveProblemRecord_ProblemSetNotFound() {
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());

        List<Map<String, String>> response = problemSetTeacherService.getAllObjectiveProblemRecord(999);

        assertEquals(1, response.size());
        assertEquals("No such problem set found through ID query", response.get(0).get("error_message"));
    }



}
