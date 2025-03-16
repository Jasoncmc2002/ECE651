package com.yw.backend.test.problemSet;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yw.backend.mapper.*;
import com.yw.backend.pojo.*;
import com.yw.backend.service.impl.problemSet.ProblemSetTeacherServiceImpl;
import com.yw.backend.service.impl.utils.UserDetailsImpl;
import com.yw.backend.service.problemSet.ProblemSetTeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProblemSetTeacherServiceImplTest {

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
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    private void mockLoginUser(int userId, int permission) {
        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setPermission(permission);
        UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Test
    void testGetOneStudentAllProgramming_noPermission() {
        mockLoginUser(100, 0);
        List<Map<String, String>> result =
                problemSetTeacherService.getOneStudentAllProgramming(1, 1);
        assertEquals(1, result.size());
        assertEquals("No permission to get one student all programming",
                result.get(0).get("error_message"));
    }

    @Test
    void testGetOneStudentAllProgramming_problemSetNotExist() {
        mockLoginUser(100, 1);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        List<Map<String, String>> result =
                problemSetTeacherService.getOneStudentAllProgramming(999, 1);
        assertEquals(1, result.size());
        assertEquals("No such problem set", result.get(0).get("error_message"));
    }

    @Test
    void testGetOneStudentAllProgramming_noPermissionNotAuthor() {
        mockLoginUser(100, 1);
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsAuthorId(200);
        List<ProblemSet> psList = new ArrayList<>();
        psList.add(ps);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(psList);
        List<Map<String, String>> result =
                problemSetTeacherService.getOneStudentAllProgramming(1, 1);
        assertEquals(1, result.size());
        assertEquals("No permission to get one student all programming",
                result.get(0).get("error_message"));
    }

    @Test
    void testGetOneStudentAllProgramming_studentNotExist() {
        mockLoginUser(100, 2);
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsAuthorId(200);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(ps));
        when(userMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        List<Map<String, String>> result =
                problemSetTeacherService.getOneStudentAllProgramming(1, 999);
        assertEquals(1, result.size());
        assertEquals("No such student", result.get(0).get("error_message"));
    }

    @Test
    void testGetOneStudentAllProgramming_studentNotInPs() {
        mockLoginUser(100, 1);
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsAuthorId(100);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(ps));
        User student = new User();
        student.setUserId(999);
        when(userMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(student));
        when(studentNPsMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        List<Map<String, String>> result =
                problemSetTeacherService.getOneStudentAllProgramming(1, 999);
        assertEquals(1, result.size());
        assertEquals("Student not in this problem set", result.get(0).get("error_message"));
    }

    @Test
    void testGetOneStudentAllProgramming_noProgrammingAnswer() {
        mockLoginUser(100, 1);
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsAuthorId(100);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(ps));
        User student = new User();
        student.setUserId(999);
        when(userMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(student));
        StudentNPs snps = new StudentNPs();
        snps.setStudentId(999);
        snps.setProblemSetId(1);
        when(studentNPsMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(snps));
        PNPs pnp = new PNPs();
        pnp.setProblemSetId(1);
        pnp.setProgrammingId(10);
        when(pnPsMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(pnp));
        Programming pg = new Programming();
        pg.setProgrammingId(10);
        pg.setPTitle("Test Programming");
        pg.setPTotalScore(50);
        when(programmingMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(pg);
        when(programmingAnswerMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        List<Map<String, String>> result =
                problemSetTeacherService.getOneStudentAllProgramming(1, 999);
        assertEquals(1, result.size());
        Map<String, String> one = result.get(0);
        assertEquals("10", one.get("programming_id"));
        assertEquals("Test Programming", one.get("p_title"));
        assertEquals("50", one.get("p_total_score"));
        assertEquals("Unanswered", one.get("pa_status"));
        assertEquals("0", one.get("pa_actual_score"));
    }

    @Test
    void testGetOneStudentAllProgramming_withProgrammingAnswer() {
        mockLoginUser(100, 1);
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsAuthorId(100);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(ps));
        User student = new User();
        student.setUserId(999);
        when(userMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(student));
        StudentNPs snps = new StudentNPs();
        snps.setStudentId(999);
        snps.setProblemSetId(1);
        when(studentNPsMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(snps));
        PNPs pnp = new PNPs();
        pnp.setProblemSetId(1);
        pnp.setProgrammingId(10);
        when(pnPsMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(pnp));
        Programming pg = new Programming();
        pg.setProgrammingId(10);
        pg.setPTitle("Test Programming");
        pg.setPTotalScore(50);
        when(programmingMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(pg);
        ProgrammingAnswer pa = new ProgrammingAnswer();
        pa.setPaActualScore(40);
        when(programmingAnswerMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(pa));
        List<Map<String, String>> result =
                problemSetTeacherService.getOneStudentAllProgramming(1, 999);
        assertEquals(1, result.size());
        Map<String, String> one = result.get(0);
        assertEquals("Answered", one.get("pa_status"));
        assertEquals("40", one.get("pa_actual_score"));
    }

    @Test
    void testGetOneStudentOneObjectiveProblem_noPermission() {
        mockLoginUser(100, 0);
        Map<String, String> resp =
                problemSetTeacherService.getOneStudentOneObjectiveProblem(1, 2, 3);
        assertEquals("No permission to get one student one objective problem",
                resp.get("error_message"));
    }

    @Test
    void testGetOneStudentOneObjectiveProblem_problemSetNotExist() {
        mockLoginUser(100, 1);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        Map<String, String> resp =
                problemSetTeacherService.getOneStudentOneObjectiveProblem(1, 2, 3);
        assertEquals("No such problem set", resp.get("error_message"));
    }

    @Test
    void testGetOneStudentOneObjectiveProblem_notAuthorNoPermission() {
        mockLoginUser(100, 1);
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsAuthorId(999);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(ps));
        Map<String, String> resp =
                problemSetTeacherService.getOneStudentOneObjectiveProblem(1, 2, 3);
        assertTrue(resp.get("error_message").contains("Teacher cannot get one student"));
    }

    @Test
    void testGetOneStudentOneObjectiveProblem_studentNotExist() {
        mockLoginUser(100, 2);
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsAuthorId(999);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(ps));
        when(userMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        Map<String, String> resp =
                problemSetTeacherService.getOneStudentOneObjectiveProblem(1, 2, 3);
        assertEquals("No such student", resp.get("error_message"));
    }

    @Test
    void testGetOneStudentOneObjectiveProblem_studentNotInProblemSet() {
        mockLoginUser(100, 2);
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsAuthorId(999);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(ps));
        User student = new User();
        student.setUserId(2);
        when(userMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(student));
        when(studentNPsMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        Map<String, String> resp =
                problemSetTeacherService.getOneStudentOneObjectiveProblem(1, 2, 3);
        assertEquals("Student not in this problem set", resp.get("error_message"));
    }

    @Test
    void testGetOneStudentOneObjectiveProblem_objectiveProblemNotExist() {
        mockLoginUser(100, 2);
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsAuthorId(100);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(ps));
        User student = new User();
        student.setUserId(2);
        when(userMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(student));
        StudentNPs snps = new StudentNPs();
        snps.setStudentId(2);
        snps.setProblemSetId(1);
        when(studentNPsMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(snps));
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        Map<String, String> resp =
                problemSetTeacherService.getOneStudentOneObjectiveProblem(1, 2, 10);
        assertEquals("No such objective problem", resp.get("error_message"));
    }

    @Test
    void testGetOneStudentOneObjectiveProblem_problemNotInPs() {
        mockLoginUser(100, 2);
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsAuthorId(100);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(ps));
        User student = new User();
        student.setUserId(2);
        when(userMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(student));
        StudentNPs snps = new StudentNPs();
        snps.setStudentId(2);
        snps.setProblemSetId(1);
        when(studentNPsMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(snps));
        ObjectiveProblem op = new ObjectiveProblem();
        op.setObjectiveProblemId(10);
        op.setOpDescription("Test");
        op.setOpTotalScore(10);
        op.setOpCorrectAnswer("B");
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(op));
        when(opNPsMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        Map<String, String> resp =
                problemSetTeacherService.getOneStudentOneObjectiveProblem(1, 2, 10);
        assertEquals("Objective problem not in this problem set", resp.get("error_message"));
    }

    @Test
    void testGetOneStudentOneObjectiveProblem_noAnswer() {
        mockLoginUser(100, 2);
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsAuthorId(100);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(ps));
        User student = new User();
        student.setUserId(2);
        student.setName("Alice");
        student.setUsername("alice123");
        when(userMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(student));
        StudentNPs snps = new StudentNPs();
        snps.setStudentId(2);
        snps.setProblemSetId(1);
        when(studentNPsMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(snps));
        ObjectiveProblem op = new ObjectiveProblem();
        op.setObjectiveProblemId(10);
        op.setOpDescription("Test OP");
        op.setOpTotalScore(10);
        op.setOpCorrectAnswer("C");
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(op));
        when(opNPsMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(new OpNPs()));
        when(objectiveProblemAnswerMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        Map<String, String> resp =
                problemSetTeacherService.getOneStudentOneObjectiveProblem(1, 2, 10);
        assertEquals("success", resp.get("error_message"));
        assertEquals("10", resp.get("objective_problem_id"));
        assertEquals("Test OP", resp.get("op_description"));
        assertEquals("10", resp.get("op_total_score"));
        assertEquals("C", resp.get("op_correct_answer"));
        assertEquals("2", resp.get("student_id"));
        assertEquals("Alice", resp.get("student_name"));
        assertEquals("alice123", resp.get("student_username"));
        assertEquals("", resp.get("opa_actual_answer"));
        assertEquals("0", resp.get("opa_actual_score"));
    }

    @Test
    void testGetOneStudentOneObjectiveProblem_withAnswer() {
        mockLoginUser(100, 2);
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsAuthorId(100);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(ps));
        User student = new User();
        student.setUserId(2);
        student.setName("Bob");
        student.setUsername("bob123");
        when(userMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(student));
        StudentNPs snps = new StudentNPs();
        snps.setStudentId(2);
        snps.setProblemSetId(1);
        when(studentNPsMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(snps));
        ObjectiveProblem op = new ObjectiveProblem();
        op.setObjectiveProblemId(10);
        op.setOpDescription("Test OP");
        op.setOpTotalScore(10);
        op.setOpCorrectAnswer("A");
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(op));
        when(opNPsMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(new OpNPs()));
        ObjectiveProblemAnswer opa = new ObjectiveProblemAnswer();
        opa.setOpaActualAnswer("B");
        opa.setOpaActualScore(6);
        when(objectiveProblemAnswerMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(opa));
        Map<String, String> resp =
                problemSetTeacherService.getOneStudentOneObjectiveProblem(1, 2, 10);
        assertEquals("success", resp.get("error_message"));
        assertEquals("B", resp.get("opa_actual_answer"));
        assertEquals("6", resp.get("opa_actual_score"));
    }

    @Test
    void testGetOneStudentOneProgramming_noPermission() {
        mockLoginUser(100, 0);
        Map<String, String> resp =
                problemSetTeacherService.getOneStudentOneProgramming(1, 2, 3);
        assertEquals("No permission to get one student one objective problem",
                resp.get("error_message"));
    }

    @Test
    void testGetOneStudentOneProgramming_problemSetNotExist() {
        mockLoginUser(100, 1);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        Map<String, String> resp =
                problemSetTeacherService.getOneStudentOneProgramming(1, 2, 3);
        assertEquals("No such problem set", resp.get("error_message"));
    }

    @Test
    void testGetOneStudentOneProgramming_notAuthorNoPermission() {
        mockLoginUser(100, 1);
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsAuthorId(999);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(ps));
        Map<String, String> resp =
                problemSetTeacherService.getOneStudentOneProgramming(1, 2, 3);
        assertTrue(resp.get("error_message").contains("Teacher cannot get one student"));
    }

    @Test
    void testGetOneStudentOneProgramming_studentNotExist() {
        mockLoginUser(100, 2);
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsAuthorId(999);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(ps));
        when(userMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        Map<String, String> resp =
                problemSetTeacherService.getOneStudentOneProgramming(1, 2, 3);
        assertEquals("No such student", resp.get("error_message"));
    }

    @Test
    void testGetOneStudentOneProgramming_studentNotInPs() {
        mockLoginUser(100, 1);
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsAuthorId(100);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(ps));
        User student = new User();
        student.setUserId(2);
        when(userMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(student));
        when(studentNPsMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        Map<String, String> resp =
                problemSetTeacherService.getOneStudentOneProgramming(1, 2, 10);
        assertEquals("Student not in this problem set", resp.get("error_message"));
    }

    @Test
    void testGetOneStudentOneProgramming_noSuchProgramming() {
        mockLoginUser(100, 1);
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsAuthorId(100);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(ps));
        User student = new User();
        student.setUserId(2);
        when(userMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(student));
        StudentNPs snps = new StudentNPs();
        snps.setStudentId(2);
        snps.setProblemSetId(1);
        when(studentNPsMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(snps));
        when(programmingMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        Map<String, String> resp =
                problemSetTeacherService.getOneStudentOneProgramming(1, 2, 1000);
        assertEquals("No such programming", resp.get("error_message"));
    }

    @Test
    void testGetOneStudentOneProgramming_notInProblemSet() {
        mockLoginUser(100, 1);
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsAuthorId(100);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(ps));
        User student = new User();
        student.setUserId(2);
        when(userMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(student));
        StudentNPs snps = new StudentNPs();
        snps.setStudentId(2);
        snps.setProblemSetId(1);
        when(studentNPsMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(snps));
        Programming pg = new Programming();
        pg.setProgrammingId(10);
        pg.setPTitle("Programming test");
        pg.setPTotalScore(100);
        when(programmingMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(pg));
        when(pnPsMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        Map<String, String> resp =
                problemSetTeacherService.getOneStudentOneProgramming(1, 2, 10);
        assertEquals("Programming not in this problem set", resp.get("error_message"));
    }

    @Test
    void testGetOneStudentOneProgramming_noAnswer() {
        mockLoginUser(100, 1);
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsAuthorId(100);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(ps));
        User student = new User();
        student.setUserId(2);
        student.setName("Tom");
        student.setUsername("tomcat");
        when(userMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(student));
        StudentNPs snps = new StudentNPs();
        snps.setStudentId(2);
        snps.setProblemSetId(1);
        when(studentNPsMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(snps));
        Programming pg = new Programming();
        pg.setProgrammingId(10);
        pg.setPTitle("Programming test");
        pg.setPDescription("Description here...");
        pg.setPTotalScore(100);
        pg.setTimeLimit(2);
        pg.setCodeSizeLimit(65536);
        when(programmingMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(pg));
        PNPs pnp = new PNPs();
        pnp.setProblemSetId(1);
        pnp.setProgrammingId(10);
        when(pnPsMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(pnp));
        when(testCaseMapper.selectCount(any(QueryWrapper.class))).thenReturn(5L);
        when(programmingAnswerMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        Map<String, String> resp =
                problemSetTeacherService.getOneStudentOneProgramming(1, 2, 10);
        assertEquals("success", resp.get("error_message"));
        assertEquals("Programming test", resp.get("p_title"));
        assertEquals("Description here...", resp.get("p_description"));
        assertEquals("100", resp.get("p_total_score"));
        assertEquals("2", resp.get("time_limit"));
        assertEquals("65536", resp.get("code_size_limit"));
        assertEquals("5", resp.get("tc_count"));
        assertEquals("", resp.get("pa_code"));
        assertEquals("0", resp.get("pa_actual_score"));
        assertEquals("0", resp.get("pass_count"));
    }

    @Test
    void testGetOneStudentOneProgramming_withAnswer() {
        mockLoginUser(100, 1);
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsAuthorId(100);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(ps));
        User student = new User();
        student.setUserId(2);
        student.setName("Tom");
        student.setUsername("tomcat");
        when(userMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(student));
        StudentNPs snps = new StudentNPs();
        snps.setStudentId(2);
        snps.setProblemSetId(1);
        when(studentNPsMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(snps));
        Programming pg = new Programming();
        pg.setProgrammingId(10);
        pg.setPTitle("Programming test");
        pg.setPDescription("Description here...");
        pg.setPTotalScore(100);
        pg.setTimeLimit(2);
        pg.setCodeSizeLimit(65536);
        when(programmingMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(pg));
        PNPs pnp = new PNPs();
        pnp.setProblemSetId(1);
        pnp.setProgrammingId(10);
        when(pnPsMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(pnp));
        when(testCaseMapper.selectCount(any(QueryWrapper.class))).thenReturn(5L);
        ProgrammingAnswer pa = new ProgrammingAnswer();
        pa.setPaCode("print('Hello')");
        pa.setPaActualScore(80);
        pa.setPassCount(3);
        when(programmingAnswerMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(pa));
        Map<String, String> resp =
                problemSetTeacherService.getOneStudentOneProgramming(1, 2, 10);
        assertEquals("success", resp.get("error_message"));
        assertEquals("print('Hello')", resp.get("pa_code"));
        assertEquals("80", resp.get("pa_actual_score"));
        assertEquals("3", resp.get("pass_count"));
    }
}
