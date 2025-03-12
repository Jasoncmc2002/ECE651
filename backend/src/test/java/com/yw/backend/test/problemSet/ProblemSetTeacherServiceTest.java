package com.yw.backend.test.problemSet;

import com.yw.backend.service.impl.problemSet.ProblemSetTeacherServiceImpl;
import com.yw.backend.mapper.*;
import com.yw.backend.pojo.*;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ProblemSetTeacherServiceTest {

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
    @Mock
    private TestCaseMapper testCaseMapper;

    @InjectMocks
    private ProblemSetTeacherServiceImpl problemSetTeacherService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        User user = new User();
        user.setUserId(1);
        user.setPermission(2);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testGetOneStudentAllProgramming() {
        // Mock data
        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsAuthorId(1);

        User student = new User();
        student.setUserId(2);

        PNPs pnPs = new PNPs();
        pnPs.setProgrammingId(1);

        Programming programming = new Programming();
        programming.setProgrammingId(1);
        programming.setPTitle("Programming 1");
        programming.setPTotalScore(25);

        ProgrammingAnswer programmingAnswer = new ProgrammingAnswer();
        programmingAnswer.setProgrammingId(1);
        programmingAnswer.setPaActualScore(20);

        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(problemSet));
        when(userMapper.selectList(any())).thenReturn(Collections.singletonList(student));
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.singletonList(new StudentNPs()));
        when(pnPsMapper.selectList(any())).thenReturn(Collections.singletonList(pnPs));
        when(programmingMapper.selectOne(any())).thenReturn(programming);
        when(programmingAnswerMapper.selectList(any())).thenReturn(Collections.singletonList(programmingAnswer));

        Map<String, String> data = new HashMap<>();
        data.put("problemSetId", "1");
        data.put("studentId", "2");

        List<Map<String, String>> result = problemSetTeacherService.getOneStudentAllProgramming(1, 2);

        assertEquals(1, result.size());
        assertEquals("Programming 1", result.get(0).get("p_title"));
        assertEquals("20", result.get(0).get("pa_actual_score"));
    }

    @Test
    public void testGetOneStudentOneObjectiveProblem() {
        // Mock data
        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsAuthorId(1);

        User student = new User();
        student.setUserId(2);

        ObjectiveProblem objectiveProblem = new ObjectiveProblem();
        objectiveProblem.setObjectiveProblemId(1);
        objectiveProblem.setOpDescription("Description");
        objectiveProblem.setOpTotalScore(10);
        objectiveProblem.setOpCorrectAnswer("Answer");

        ObjectiveProblemAnswer objectiveProblemAnswer = new ObjectiveProblemAnswer();
        objectiveProblemAnswer.setOpaActualAnswer("Student Answer");
        objectiveProblemAnswer.setOpaActualScore(8);

        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(problemSet));
        when(userMapper.selectList(any())).thenReturn(Collections.singletonList(student));
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.singletonList(new StudentNPs()));
        when(objectiveProblemMapper.selectList(any())).thenReturn(Collections.singletonList(objectiveProblem));
        when(opNPsMapper.selectList(any())).thenReturn(Collections.singletonList(new OpNPs()));
        when(objectiveProblemAnswerMapper.selectList(any())).thenReturn(Collections.singletonList(objectiveProblemAnswer));

        Map<String, String> data = new HashMap<>();
        data.put("problemSetId", "1");
        data.put("studentId", "2");
        data.put("objectiveProblemId", "1");

        Map<String, String> result = problemSetTeacherService.getOneStudentOneObjectiveProblem(1, 2, 1);

        assertEquals("success", result.get("error_message"));
        assertEquals("Description", result.get("op_description"));
        assertEquals("8", result.get("opa_actual_score"));
    }

    @Test
    public void testGetOneStudentOneProgramming() {
        // Mock data
        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsAuthorId(1);

        User student = new User();
        student.setUserId(2);

        Programming programming = new Programming();
        programming.setProgrammingId(1);
        programming.setPTitle("Programming 1");
        programming.setPDescription("Description");
        programming.setPTotalScore(25);
        programming.setTimeLimit(100);
        programming.setCodeSizeLimit(1000);

        ProgrammingAnswer programmingAnswer = new ProgrammingAnswer();
        programmingAnswer.setPaCode("Code");
        programmingAnswer.setPaActualScore(20);
        programmingAnswer.setPassCount(5);

        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(problemSet));
        when(userMapper.selectList(any())).thenReturn(Collections.singletonList(student));
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.singletonList(new StudentNPs()));
        when(programmingMapper.selectList(any())).thenReturn(Collections.singletonList(programming));
        when(pnPsMapper.selectList(any())).thenReturn(Collections.singletonList(new PNPs()));
        when(programmingAnswerMapper.selectList(any())).thenReturn(Collections.singletonList(programmingAnswer));
        when(testCaseMapper.selectCount(any())).thenReturn(10L);

        Map<String, String> data = new HashMap<>();
        data.put("problemSetId", "1");
        data.put("studentId", "2");
        data.put("programmingId", "1");

        Map<String, String> result = problemSetTeacherService.getOneStudentOneProgramming(1, 2, 1);

        assertEquals("success", result.get("error_message"));
        assertEquals("Programming 1", result.get("p_title"));
        assertEquals("20", result.get("pa_actual_score"));
        assertEquals("5", result.get("pass_count"));
    }
}