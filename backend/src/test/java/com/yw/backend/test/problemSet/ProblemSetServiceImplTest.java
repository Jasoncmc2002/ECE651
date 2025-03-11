package com.yw.backend.test.problemSet;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yw.backend.mapper.*;
import com.yw.backend.pojo.*;
import com.yw.backend.service.impl.problemSet.ProblemSetServiceImpl;
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
public class ProblemSetServiceImplTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private StudentNPsMapper studentNPsMapper;
    @Mock
    private ProblemSetMapper problemSetMapper;
    @Mock
    private ObjectiveProblemMapper objectiveProblemMapper;
    @Mock
    private OpNPsMapper opNPsMapper;
    @Mock
    private ProgrammingMapper programmingMapper;
    @Mock
    private PNPsMapper pnPsMapper;
    @Mock
    private ObjectiveProblemAnswerMapper objectiveProblemAnswerMapper;
    @Mock
    private ProgrammingAnswerMapper programmingAnswerMapper;

    @InjectMocks
    private ProblemSetServiceImpl problemSetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        User mockUser = new User();
        mockUser.setUserId(1);
        UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authToken);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetActiveProblemSet_Success() {
        LocalDateTime now = LocalDateTime.now();
        StudentNPs studentNPs = new StudentNPs();
        studentNPs.setProblemSetId(1);
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.singletonList(studentNPs));

        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsName("Test Set");
        problemSet.setPsAuthorId(2);
        problemSet.setPsStartTime(now.minusHours(1));
        problemSet.setPsEndTime(now.plusHours(1));
        problemSet.setDuration(60);
        when(problemSetMapper.selectOne(any())).thenReturn(problemSet);

        User author = new User();
        author.setName("Test Author");
        when(userMapper.selectOne(any())).thenReturn(author);

        List<Map<String, String>> result = problemSetService.getActiveProblemSet();
        assertEquals(1, result.size());
        assertEquals("Test Set", result.get(0).get("ps_name"));
    }

    @Test
    void testGetActiveProblemSet_OutsideTimeRange() {
        LocalDateTime now = LocalDateTime.now();

        StudentNPs studentNPs = new StudentNPs();
        studentNPs.setProblemSetId(1);
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.singletonList(studentNPs));

        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsStartTime(now.plusHours(1));
        problemSet.setPsEndTime(now.plusDays(1));
        problemSet.setPsName("Test Set");
        problemSet.setPsAuthorId(2);
        when(problemSetMapper.selectOne(any())).thenReturn(problemSet);

        User author = new User();
        author.setName("Test Author");
        when(userMapper.selectOne(any())).thenReturn(author);

        List<Map<String, String>> result = problemSetService.getActiveProblemSet();

        assertTrue(result.isEmpty());
    }


    @Test
    void testGetAllProblemSet_Success() {
        LocalDateTime now = LocalDateTime.now();

        StudentNPs studentNPs = new StudentNPs();
        studentNPs.setProblemSetId(1);
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.singletonList(studentNPs));

        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsStartTime(now.minusDays(1));
        problemSet.setPsEndTime(now.plusDays(1));
        problemSet.setPsName("Test Set");
        problemSet.setPsAuthorId(2);
        problemSet.setDuration(60);
        when(problemSetMapper.selectOne(any())).thenReturn(problemSet);

        User author = new User();
        author.setName("Test Author");
        when(userMapper.selectOne(any())).thenReturn(author);

        List<Map<String, String>> result = problemSetService.getAllProblemSet();

        // 断言结果是否正确
        assertEquals(1, result.size());
        assertEquals("Test Set", result.get(0).get("ps_name"));
        assertEquals("Test Author", result.get(0).get("ps_author_name"));
    }


    @Test
    void testGetOne_NotFound() {
        when(problemSetMapper.selectList(any())).thenReturn(Collections.emptyList());

        Map<String, String> result = problemSetService.getOne(1);
        assertEquals("No such problem set found through ID query", result.get("error_message"));
    }

    @Test
    void testGetOne_UserNotInSet() {
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(new ProblemSet()));
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.emptyList());

        Map<String, String> result = problemSetService.getOne(1);
        assertEquals("The user does not belong to this problem set", result.get("error_message"));
    }

    @Test
    void testGetOne_BeforeStart() {
        ProblemSet problemSet = new ProblemSet();
        problemSet.setPsStartTime(LocalDateTime.now().plusDays(1)); // 还没开始
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(problemSet));
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.singletonList(new StudentNPs()));

        Map<String, String> result = problemSetService.getOne(1);
        assertEquals("The problem set has not started yet", result.get("error_message"));
    }

    @Test
    void testStartProblemSet_Success() {
        ProblemSet problemSet = new ProblemSet();
        problemSet.setPsStartTime(LocalDateTime.now().minusDays(1));
        problemSet.setPsEndTime(LocalDateTime.now().plusDays(1));

        StudentNPs studentNPs = new StudentNPs();
        studentNPs.setFirstStartTime(null);

        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(problemSet));
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.singletonList(studentNPs));

        Map<String, String> result = problemSetService.startProblemSet(1);
        assertEquals("success", result.get("error_message"));
    }

    @Test
    void testStartProblemSet_NotStarted() {
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1);

        ProblemSet problemSet = new ProblemSet();
        problemSet.setPsStartTime(futureTime);
        problemSet.setPsEndTime(futureTime.plusDays(1));

        StudentNPs studentNPs = new StudentNPs();

        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(problemSet));
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.singletonList(studentNPs)); // 关键点

        Map<String, String> result = problemSetService.startProblemSet(1);
        assertEquals("The problem set has not started yet", result.get("error_message"));
    }


    @Test
    void testStartProblemSet_AlreadyStarted() {
        LocalDateTime now = LocalDateTime.now();

        ProblemSet problemSet = new ProblemSet();
        problemSet.setPsStartTime(now.minusDays(1));
        problemSet.setPsEndTime(now.plusDays(1));

        StudentNPs studentNPs = new StudentNPs();
        studentNPs.setFirstStartTime(now.minusHours(1));

        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(problemSet));
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.singletonList(studentNPs));

        Map<String, String> result = problemSetService.startProblemSet(1);
        assertEquals("The answer to the problem set has started", result.get("error_message"));
    }


    @Test
    void testGetOne_UserNotFound() {
        LocalDateTime now = LocalDateTime.now();

        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsStartTime(now.minusDays(1));
        problemSet.setPsEndTime(now.plusDays(1));
        problemSet.setDuration(60);
        problemSet.setPsAuthorId(2);
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(problemSet));

        StudentNPs studentNPs = new StudentNPs();
        studentNPs.setProblemSetId(1);
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.singletonList(studentNPs));

        User author = new User();
        author.setUserId(2);
        author.setName("");
        when(userMapper.selectOne(any())).thenReturn(author);

        Map<String, String> result = problemSetService.getOne(1);

        assertNotNull(result.get("ps_author_name"));
        assertEquals("", result.get("ps_author_name"));
    }


    @Test
    void testGetOne_StudentNotInProblemSet() {
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(new ProblemSet()));
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.emptyList()); // 空列表

        Map<String, String> result = problemSetService.getOne(1);
        assertEquals("The user does not belong to this problem set", result.get("error_message"));
    }


    @Test
    void testGetOne_State_StartedWithZeroDuration() {
        LocalDateTime now = LocalDateTime.now();

        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsStartTime(now.minusDays(1));
        problemSet.setPsEndTime(now.plusDays(1));
        problemSet.setDuration(0);
        problemSet.setPsAuthorId(2);
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(problemSet));

        StudentNPs studentNPs = new StudentNPs();
        studentNPs.setProblemSetId(1);
        studentNPs.setFirstStartTime(now.minusHours(1));
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.singletonList(studentNPs));

        User author = new User();
        author.setUserId(2);
        author.setName("Test Author");
        when(userMapper.selectOne(any())).thenReturn(author);

        Map<String, String> result = problemSetService.getOne(1);

        assertNotNull(result.get("ps_status"));
        assertEquals("started", result.get("ps_status"));
    }

    @Test
    void testGetOne_State_Case2_OpNPsExists() {
        LocalDateTime now = LocalDateTime.now();

        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsStartTime(now.minusDays(1));
        problemSet.setPsEndTime(now.plusDays(1));
        problemSet.setDuration(60);
        problemSet.setPsAuthorId(2);
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(problemSet));

        StudentNPs studentNPs = new StudentNPs();
        studentNPs.setProblemSetId(1);
        studentNPs.setFirstStartTime(now.minusHours(1));
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.singletonList(studentNPs));

        OpNPs opNPs = new OpNPs();
        opNPs.setObjectiveProblemId(10);
        when(opNPsMapper.selectList(any())).thenReturn(Collections.singletonList(opNPs));

        ObjectiveProblem objectiveProblem = new ObjectiveProblem();
        objectiveProblem.setOpTotalScore(20);
        when(objectiveProblemMapper.selectOne(any())).thenReturn(objectiveProblem);

        User author = new User();
        author.setUserId(2);
        author.setName("Test Author");
        when(userMapper.selectOne(any())).thenReturn(author);

        Map<String, String> result = problemSetService.getOne(1);

        assertNotNull(result.get("ps_total_score"));
        assertEquals("20", result.get("ps_total_score"));
    }

    @Test
    void testGetOne_State_Case2_PNPsExists() {
        LocalDateTime now = LocalDateTime.now();

        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsStartTime(now.minusDays(1));
        problemSet.setPsEndTime(now.plusDays(1));
        problemSet.setDuration(60);
        problemSet.setPsAuthorId(2);
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(problemSet));

        StudentNPs studentNPs = new StudentNPs();
        studentNPs.setProblemSetId(1);
        studentNPs.setFirstStartTime(now.minusHours(1));
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.singletonList(studentNPs));

        PNPs pnPs = new PNPs();
        pnPs.setProgrammingId(15);
        when(pnPsMapper.selectList(any())).thenReturn(Collections.singletonList(pnPs));

        Programming programming = new Programming();
        programming.setPTotalScore(30);
        when(programmingMapper.selectOne(any())).thenReturn(programming);

        User author = new User();
        author.setUserId(2);
        author.setName("Test Author");
        when(userMapper.selectOne(any())).thenReturn(author);

        Map<String, String> result = problemSetService.getOne(1);

        assertNotNull(result.get("ps_total_score"));
        assertEquals("30", result.get("ps_total_score"));
    }


    @Test
    void testGetOne_State_Closed() {
        LocalDateTime now = LocalDateTime.now();

        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsStartTime(now.minusDays(2));
        problemSet.setPsEndTime(now.minusHours(1));
        problemSet.setDuration(60);
        problemSet.setPsAuthorId(2);
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(problemSet));

        StudentNPs studentNPs = new StudentNPs();
        studentNPs.setProblemSetId(1);
        studentNPs.setFirstStartTime(now.minusHours(3));
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.singletonList(studentNPs));

        OpNPs opNPs = new OpNPs();
        opNPs.setObjectiveProblemId(10);
        when(opNPsMapper.selectList(any())).thenReturn(Collections.singletonList(opNPs));

        ObjectiveProblem objectiveProblem = new ObjectiveProblem();
        objectiveProblem.setOpTotalScore(20);
        when(objectiveProblemMapper.selectOne(any())).thenReturn(objectiveProblem);

        PNPs pnPs = new PNPs();
        pnPs.setProgrammingId(15);
        when(pnPsMapper.selectList(any())).thenReturn(Collections.singletonList(pnPs));

        Programming programming = new Programming();
        programming.setPTotalScore(30);
        when(programmingMapper.selectOne(any())).thenReturn(programming);

        User author = new User();
        author.setUserId(2);
        author.setName("Test Author");
        when(userMapper.selectOne(any())).thenReturn(author);

        Map<String, String> result = problemSetService.getOne(1);

        assertNotNull(result.get("ps_status"));
        assertEquals("closed", result.get("ps_status")); //  `ps_status == "closed"`
        assertNotNull(result.get("ps_total_score"));
        assertEquals("50", result.get("ps_total_score")); // 20 + 30 = 50
    }

    @Test
    void testStartProblemSet_NotFound() {
        when(problemSetMapper.selectList(any())).thenReturn(Collections.emptyList());

        Map<String, String> result = problemSetService.startProblemSet(1);

        assertEquals("No such problem set found through ID query", result.get("error_message"));
    }

    @Test
    void testStartProblemSet_UserNotInSet() {
        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(problemSet));

        when(studentNPsMapper.selectList(any())).thenReturn(Collections.emptyList());

        Map<String, String> result = problemSetService.startProblemSet(1);

        assertEquals("The user does not belong to this problem set", result.get("error_message"));
    }


    @Test
    void testStartProblemSet_Ended() {
        LocalDateTime pastTime = LocalDateTime.now().minusHours(1);

        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsStartTime(pastTime.minusDays(1));
        problemSet.setPsEndTime(pastTime);
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(problemSet));

        StudentNPs studentNPs = new StudentNPs();
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.singletonList(studentNPs));

        Map<String, String> result = problemSetService.startProblemSet(1);


    }

    @Test
    void testGetAllProblemSet_BeforeStart() {
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1);

        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsStartTime(futureTime);
        when(problemSetMapper.selectOne(any())).thenReturn(problemSet);

        StudentNPs studentNPs = new StudentNPs();
        studentNPs.setProblemSetId(1);
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.singletonList(studentNPs));

        List<Map<String, String>> result = problemSetService.getAllProblemSet();

        assertTrue(result.isEmpty());
    }


    @Test
    void testGetOne_Case4_ComputeActualScore() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime pastTime = now.minusHours(1);
        LocalDateTime startTime = now.minusDays(2);

        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsStartTime(startTime);
        problemSet.setPsEndTime(pastTime);
        problemSet.setDuration(60);
        problemSet.setPsAuthorId(2);

        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(problemSet));

        StudentNPs studentNPs = new StudentNPs();
        studentNPs.setFirstStartTime(startTime.plusHours(1));
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.singletonList(studentNPs));

        ObjectiveProblemAnswer objAnswer = new ObjectiveProblemAnswer();
        objAnswer.setOpaActualScore(20);
        when(objectiveProblemAnswerMapper.selectList(any())).thenReturn(Collections.singletonList(objAnswer));

        ProgrammingAnswer progAnswer = new ProgrammingAnswer();
        progAnswer.setPaActualScore(30);
        when(programmingAnswerMapper.selectList(any())).thenReturn(Collections.singletonList(progAnswer));

        User author = new User();
        author.setUserId(2);
        author.setName("Test Author");
        when(userMapper.selectOne(any())).thenReturn(author);

        Map<String, String> result = problemSetService.getOne(1);

        assertNotNull(result.get("ps_actual_score"));
        assertEquals("50", result.get("ps_actual_score")); // 20 + 30 = 50

        assertEquals("Test Author", result.get("ps_author_name"));
    }


    @Test
    void testGetOne_State_Ended() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.minusHours(2);
        int duration = 60;

        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsStartTime(startTime.minusHours(1));
        problemSet.setPsEndTime(now.plusHours(1));
        problemSet.setDuration(duration);
        problemSet.setPsAuthorId(2);

        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(problemSet));

        StudentNPs studentNPs = new StudentNPs();
        studentNPs.setFirstStartTime(startTime);
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.singletonList(studentNPs));

        User author = new User();
        author.setUserId(2);
        author.setName("Test Author");
        when(userMapper.selectOne(any())).thenReturn(author);

        Map<String, String> result = problemSetService.getOne(1);

        assertEquals("ended", result.get("ps_status"));
    }

}
