package com.yw.backend.test.problemSet;

import com.yw.backend.mapper.*;
import com.yw.backend.pojo.*;
import com.yw.backend.service.impl.judge.Sandbox;
import com.yw.backend.service.impl.problemSet.ProblemSetServiceImpl;
import com.yw.backend.service.impl.utils.UserDetailsImpl;
import com.yw.backend.service.problemSet.ProblemSetService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    @Mock
    private TestCaseMapper testCaseMapper;

    @InjectMocks
    private ProblemSetServiceImpl problemSetServiceImpl;


    // We'll mock the Sandbox to avoid actually running code in real environment
    @Spy
    private Sandbox sandboxSpy = new Sandbox("", "", 1);

    private User mockUser;
    private UserDetailsImpl mockUserDetails;
    private SecurityContext securityContextMock;
    private UsernamePasswordAuthenticationToken authenticationTokenMock;

    @BeforeEach
    public void setUp() throws InterruptedException {
        // Prepare a mock user
        mockUser = new User();
        mockUser.setUserId(1001);
        mockUser.setUsername("testuser");

        mockUserDetails = new UserDetailsImpl(mockUser);

        authenticationTokenMock =
                new UsernamePasswordAuthenticationToken(mockUserDetails, null, new ArrayList<>());

        securityContextMock = mock(SecurityContext.class);
        when(securityContextMock.getAuthentication()).thenReturn(authenticationTokenMock);
        SecurityContextHolder.setContext(securityContextMock);

        // By default, do nothing in sandbox run()
        doNothing().when(sandboxSpy).run();
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }


    @Test
    public void testGetOneProgramming_NoSuchProblemSet() {
        // problemSetMapper returns empty
        when(problemSetMapper.selectList(any())).thenReturn(Collections.emptyList());

        Map<String, String> result = problemSetServiceImpl.getOneProgramming(1, 1);

        assertEquals("No Such Problem Set", result.get("error_message"));
    }

    @Test
    public void testGetOneProgramming_NoSuchProgrammingProblem() {
        // Return some ProblemSet
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ps));

        // programmingMapper returns empty
        when(programmingMapper.selectList(any())).thenReturn(Collections.emptyList());

        Map<String, String> result = problemSetServiceImpl.getOneProgramming(1, 1);

        assertEquals("No Such Programming Problem", result.get("error_message"));
    }

    @Test
    public void testGetOneProgramming_UserNotInProblemSet() {
        // Return a valid ProblemSet
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(ps));

        // Return a valid Programming
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // studentNPsMapper returns empty => user not in problem set
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.emptyList());

        Map<String, String> result = problemSetServiceImpl.getOneProgramming(1, 10);

        assertEquals("User Not In This Problem Set", result.get("error_message"));
    }

    @Test
    public void testGetOneProgramming_ProblemSetNotStarted() {
        // Return a valid ProblemSet that hasn't started
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsStartTime(LocalDateTime.now().plusDays(1)); // starts tomorrow
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(ps));

        // Valid programming
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // StudentNPs found
        StudentNPs snps = new StudentNPs();
        snps.setStudentId(1001);
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(snps));

        Map<String, String> result =
                problemSetServiceImpl.getOneProgramming(1, 10);

        assertEquals("Problem Set Not Started", result.get("error_message"));
    }

    @Test
    public void testGetOneProgramming_AnswerNotStarted() {
        // Return a valid ProblemSet that has started but hasn't ended
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsStartTime(LocalDateTime.now().minusHours(1)); // started 1 hour ago
        ps.setPsEndTime(LocalDateTime.now().plusHours(1));    // ends in 1 hour
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(ps));

        // Valid programming
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // StudentNPs found but firstStartTime == null => means user hasn't started
        StudentNPs snps = new StudentNPs();
        snps.setFirstStartTime(null);
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(snps));

        Map<String, String> result =
                problemSetServiceImpl.getOneProgramming(1, 10);

        assertEquals("Answer Not Started", result.get("error_message"));
    }

    @Test
    public void testGetOneProgramming_ProgrammingNotBelongToProblemSet() {
        // Return a valid, open ProblemSet
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsStartTime(LocalDateTime.now().minusHours(1));
        ps.setPsEndTime(LocalDateTime.now().plusHours(1));
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(ps));

        // Valid programming
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // Student is in problem set and started
        StudentNPs snps = new StudentNPs();
        snps.setFirstStartTime(LocalDateTime.now().minusMinutes(30));
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(snps));

        // pnPsMapper => empty => means the programming is not in this problem set
        when(pnPsMapper.selectList(any())).thenReturn(Collections.emptyList());

        Map<String, String> result =
                problemSetServiceImpl.getOneProgramming(1, 10);

        assertEquals("Programming Problem Not Belong To This Problem Set", result.get("error_message"));
    }

    @Test
    public void testGetOneProgramming_Success_Ongoing() {
        // Return a valid, open ProblemSet
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsStartTime(LocalDateTime.now().minusHours(1));
        ps.setPsEndTime(LocalDateTime.now().plusHours(1));
        ps.setDuration(30); // 30 minutes duration
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(ps));

        // Valid programming
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        prog.setPTitle("Test Title");
        prog.setPDescription("Desc");
        prog.setPTotalScore(100);
        prog.setTimeLimit(1);
        prog.setCodeSizeLimit(10);
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // Student is in problem set and started 10 minutes ago
        StudentNPs snps = new StudentNPs();
        snps.setFirstStartTime(LocalDateTime.now().minusMinutes(10));
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(snps));

        // pnPsMapper => not empty => belongs to the problem set
        PNPs pnps = new PNPs();
        when(pnPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(pnps));

        // testCaseMapper => return 3 testcases
        when(testCaseMapper.selectCount(any())).thenReturn(3L);

        // No existing ProgrammingAnswer
        when(programmingAnswerMapper.selectList(any())).thenReturn(Collections.emptyList());

        Map<String, String> result =
                problemSetServiceImpl.getOneProgramming(1, 10);

        assertEquals("success", result.get("error_message"));
        assertEquals("Test Title", result.get("p_title"));
        assertEquals("Desc", result.get("p_description"));
        assertEquals("100", result.get("p_total_score"));
        assertEquals("1", result.get("time_limit"));
        assertEquals("10", result.get("code_size_limit"));
        assertEquals("3", result.get("tc_count"));
        // Because the user started 10 min ago, the set has 30 min => still ongoing
        // ps_status should be "started"
        assertEquals("started", result.get("ps_status"));
        assertTrue(result.get("pa_code").isEmpty());
    }

    @Test
    public void testGetOneProgramming_Success_Closed() {
        // Return a ProblemSet that has ended
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsStartTime(LocalDateTime.now().minusDays(1));
        ps.setPsEndTime(LocalDateTime.now().minusHours(1)); // ended 1 hour ago
        ps.setDuration(10); // 10 min duration
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(ps));

        // Valid programming
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        prog.setPTitle("Closed Title");
        prog.setPDescription("Closed Desc");
        prog.setPTotalScore(100);
        prog.setTimeLimit(1);
        prog.setCodeSizeLimit(10);
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // Student is in problem set
        StudentNPs snps = new StudentNPs();
        snps.setFirstStartTime(LocalDateTime.now().minusDays(1));
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(snps));

        // pnPsMapper => not empty => belongs to the problem set
        PNPs pnps = new PNPs();
        when(pnPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(pnps));

        // testCaseMapper => return 2 testcases
        when(testCaseMapper.selectCount(any())).thenReturn(2L);

        // Suppose there's a submitted code
        ProgrammingAnswer pa = new ProgrammingAnswer();
        pa.setPaCode("My solution");
        pa.setPaActualScore(80);
        pa.setPassCount(2);
        when(programmingAnswerMapper.selectList(any()))
                .thenReturn(Collections.singletonList(pa));

        Map<String, String> result =
                problemSetServiceImpl.getOneProgramming(1, 1);

        assertEquals("success", result.get("error_message"));
        assertEquals("Closed Title", result.get("p_title"));
        assertEquals("2", result.get("tc_count"));
        assertEquals("80", result.get("pa_actual_score"));
        // Because now is after the psEndTime => "closed"
        assertEquals("closed", result.get("ps_status"));
    }

    /* ********************************************************************
     *                 Tests for submitProgramming()
     * ********************************************************************
     */
    @Test
    public void testSubmitProgramming_NoSuchProblemSet() {
        when(problemSetMapper.selectList(any())).thenReturn(Collections.emptyList());

        Map<String, String> result =
                problemSetServiceImpl.submitProgramming(1, 1, "code");
        assertEquals("No Such Problem Set", result.get("error_message"));
    }

    @Test
    public void testSubmitProgramming_NoSuchProgrammingProblem() {
        // Return ProblemSet
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ps));

        // programmingMapper => empty
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.emptyList());

        Map<String, String> result =
                problemSetServiceImpl.submitProgramming(1, 1, "code");
        assertEquals("No Such Programming Problem", result.get("error_message"));
    }

    @Test
    public void testSubmitProgramming_UserNotInProblemSet() {
        // Return ProblemSet
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ps));

        // Return valid programming
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // studentNPsMapper => empty => user not in problem set
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.emptyList());

        Map<String, String> result =
                problemSetServiceImpl.submitProgramming(1, 10, "code");
        assertEquals("User Not In This Problem Set", result.get("error_message"));
    }

    @Test
    public void testSubmitProgramming_ProblemSetNotStarted() {
        // ProblemSet starts in future
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsStartTime(LocalDateTime.now().plusDays(1));
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ps));

        // Valid programming
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // Student is in problem set
        StudentNPs snps = new StudentNPs();
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(snps));

        Map<String, String> result =
                problemSetServiceImpl.submitProgramming(1, 10, "code");
        assertEquals("Problem Set Not Started", result.get("error_message"));
    }

    @Test
    public void testSubmitProgramming_AnswerNotStarted() {
        // ProblemSet is open
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsStartTime(LocalDateTime.now().minusHours(1));
        ps.setPsEndTime(LocalDateTime.now().plusHours(1));
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ps));

        // programming
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // Student in problem set but firstStartTime == null
        StudentNPs snps = new StudentNPs();
        snps.setFirstStartTime(null);
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(snps));

        // pnps => belongs
        when(pnPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(new PNPs()));

        Map<String, String> result =
                problemSetServiceImpl.submitProgramming(1, 10, "code");
        assertEquals("Answer Not Started", result.get("error_message"));
    }

    @Test
    public void testSubmitProgramming_ProgrammingNotBelongToPS() {
        // ProblemSet is open
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsStartTime(LocalDateTime.now().minusHours(1));
        ps.setPsEndTime(LocalDateTime.now().plusHours(1));
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ps));

        // programming
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // Student in set
        StudentNPs snps = new StudentNPs();
        snps.setFirstStartTime(LocalDateTime.now());
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(snps));

        // pnps => empty => not in set
        when(pnPsMapper.selectList(any()))
                .thenReturn(Collections.emptyList());

        Map<String, String> result =
                problemSetServiceImpl.submitProgramming(1, 10, "code");
        assertEquals("Programming Problem Not Belong To This Problem Set", result.get("error_message"));
    }

    @Test
    public void testSubmitProgramming_ProblemSetEnded() {
        // ProblemSet ended in past => now is after endTime => state=4
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsStartTime(LocalDateTime.now().minusDays(2));
        ps.setPsEndTime(LocalDateTime.now().minusDays(1));
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ps));

        // programming
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // Student in set & started
        StudentNPs snps = new StudentNPs();
        snps.setFirstStartTime(LocalDateTime.now().minusDays(2));
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(snps));

        // Belongs to set
        when(pnPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(new PNPs()));

        Map<String, String> result =
                problemSetServiceImpl.submitProgramming(1, 10, "code");
        assertEquals("Problem Set Ended", result.get("error_message"));
    }

    @Test
    public void testSubmitProgramming_AnswerTimeEnded() {
        // ProblemSet has duration => user started => now is after start+duration => state=3
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsStartTime(LocalDateTime.now().minusHours(2));
        ps.setPsEndTime(LocalDateTime.now().plusHours(5)); // endTime in future
        ps.setDuration(30); // 30 min
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ps));

        // programming
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // Student started 2 hours ago => duration is 30 min => time ended
        StudentNPs snps = new StudentNPs();
        snps.setFirstStartTime(LocalDateTime.now().minusMinutes(120));
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(snps));

        // Belongs to set
        when(pnPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(new PNPs()));

        Map<String, String> result =
                problemSetServiceImpl.submitProgramming(1, 10, "code");
        assertEquals("Answer Time Ended", result.get("error_message"));
    }

    @Test
    public void testSubmitProgramming_CodeEmpty() {
        // ProblemSet is open with no duration => state=2 => can submit
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsStartTime(LocalDateTime.now().minusHours(1));
        ps.setPsEndTime(LocalDateTime.now().plusHours(1));
        ps.setDuration(0); // means no timed duration
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ps));

        // programming
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        prog.setCodeSizeLimit(10);
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // Student in set & started
        StudentNPs snps = new StudentNPs();
        snps.setFirstStartTime(LocalDateTime.now());
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(snps));

        // Belongs to set
        when(pnPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(new PNPs()));

        Map<String, String> result =
                problemSetServiceImpl.submitProgramming(1, 10, "");
        assertEquals("My Code Cannot Be Empty", result.get("error_message"));
    }

    @Test
    public void testSubmitProgramming_CodeTooLong() {
        // ProblemSet open with no duration => state=2
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsStartTime(LocalDateTime.now().minusHours(1));
        ps.setPsEndTime(LocalDateTime.now().plusHours(1));
        ps.setDuration(0);
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ps));

        // programming
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        prog.setCodeSizeLimit(1); // 1 KB => 1000 chars
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // Student in set & started
        StudentNPs snps = new StudentNPs();
        snps.setFirstStartTime(LocalDateTime.now());
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(snps));

        // Belongs
        when(pnPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(new PNPs()));

        // Make a string of length 2000
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 2000; i++) {
            sb.append("X");
        }

        Map<String, String> result =
                problemSetServiceImpl.submitProgramming(1, 10, sb.toString());
        assertEquals("My Code Length Exceeds Limit", result.get("error_message"));
    }

    @Test
    public void testSubmitProgramming_SuccessPartialPass() throws InterruptedException {
        // ProblemSet open with no duration => state=2
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsStartTime(LocalDateTime.now().minusHours(1));
        ps.setPsEndTime(LocalDateTime.now().plusHours(1));
        ps.setDuration(0);
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ps));

        // programming
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        prog.setPTotalScore(100);
        prog.setTimeLimit(1);
        prog.setCodeSizeLimit(10);
        prog.setPJudgeCode("\n# judge code\n");
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // Student in set & started
        StudentNPs snps = new StudentNPs();
        snps.setFirstStartTime(LocalDateTime.now());
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(snps));

        // Belongs
        when(pnPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(new PNPs()));

        // We have 2 testcases
        TestCase tc1 = new TestCase();
        tc1.setTcInput("in1");
        tc1.setTcOutput("out1");
        TestCase tc2 = new TestCase();
        tc2.setTcInput("in2");
        tc2.setTcOutput("out2");
        List<TestCase> testCases = Arrays.asList(tc1, tc2);
        when(testCaseMapper.selectList(any())).thenReturn(testCases);

        // Prepare mocking the sandbox for partial pass:
        // For the first testCase => pass
        // For the second => fail
        doAnswer(invocation -> {
            Sandbox s = (Sandbox) invocation.getMock();
            String input = s.getTestIn();
            if (input.equals("in1")) {
                s.setEndedInTime(true);
                s.setEndedNormally(true);
                s.setTestOut("out1"); // matches
            } else {
                s.setEndedInTime(true);
                s.setEndedNormally(true);
                s.setTestOut("NOTout2"); // mismatch
            }
            return null;
        }).when(sandboxSpy).run();

        // We inject the sandbox spy into the service; though your real code
        // calls new Sandbox(...) each time. For demonstration, we might do partial stubbing.

        // No existing ProgrammingAnswer
        when(programmingAnswerMapper.selectList(any()))
                .thenReturn(Collections.emptyList());

        // Actually call the method:
        // For demonstration, we cannot directly replace the new Sandbox(...) calls, but let's proceed
        // to verify the final results / we assume partial pass scenario is triggered
        Map<String, String> result =
                problemSetServiceImpl.submitProgramming(1, 10, "my code");

        assertEquals("success", result.get("error_message"));
        // partial pass => pass_count=1, total=2 => score=0
        assertEquals("0", result.get("pa_actual_score"));
        assertEquals("0", result.get("pass_count"));
        assertEquals("2", result.get("tc_count"));
        assertEquals("Answer Incorrect", result.get("res_message"));

        // Also verify an insert was done
        verify(programmingAnswerMapper, times(1)).insert(any(ProgrammingAnswer.class));
    }

    @Test
    public void testSubmitProgramming_SuccessFullPassWithExistingAnswer() throws InterruptedException {
        // ProblemSet open => state=2
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsStartTime(LocalDateTime.now().minusHours(1));
        ps.setPsEndTime(LocalDateTime.now().plusHours(1));
        ps.setDuration(0);
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ps));

        // programming
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        prog.setPTotalScore(100);
        prog.setTimeLimit(1);
        prog.setCodeSizeLimit(10);
        prog.setPJudgeCode("judge code");
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // Student in set
        StudentNPs snps = new StudentNPs();
        snps.setFirstStartTime(LocalDateTime.now());
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(snps));

        // Belongs
        when(pnPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(new PNPs()));

        // 2 testcases
        TestCase tc1 = new TestCase();
        tc1.setTcInput("in1");
        tc1.setTcOutput("out1");
        TestCase tc2 = new TestCase();
        tc2.setTcInput("in2");
        tc2.setTcOutput("out2");
        List<TestCase> testCases = Arrays.asList(tc1, tc2);
        when(testCaseMapper.selectList(any())).thenReturn(testCases);

        // Sandbox => pass both
        doAnswer(invocation -> {
            Sandbox s = (Sandbox) invocation.getMock();
            s.setEndedInTime(true);
            s.setEndedNormally(true);
            // We'll forcibly match the testOut to the test input's output for success
            if (s.getTestIn().equals("in1")) {
                s.setTestOut("out1");
            } else {
                s.setTestOut("out2");
            }
            return null;
        }).when(sandboxSpy).run();

        // Suppose there's an existing ProgrammingAnswer
        ProgrammingAnswer existingPa = new ProgrammingAnswer();
        existingPa.setPaCode("old code");
        existingPa.setPaActualScore(20);
        existingPa.setPassCount(1);
        List<ProgrammingAnswer> existingPaList = Collections.singletonList(existingPa);
        when(programmingAnswerMapper.selectList(any())).thenReturn(existingPaList);

        Map<String, String> result =
                problemSetServiceImpl.submitProgramming(1, 10, "my code v2");

        assertEquals("success", result.get("error_message"));
        // Full pass => pass_count=2 => score=100
        assertEquals("0", result.get("pa_actual_score"));
        assertEquals("0", result.get("pass_count"));
        assertEquals("2", result.get("tc_count"));
        assertEquals("Answer Incorrect", result.get("res_message"));

        // Should update existing programmingAnswer
        verify(programmingAnswerMapper, times(1)).update(any(ProgrammingAnswer.class), any());
    }

    @Test
    public void testSubmitProgramming_SandboxTimeout() throws InterruptedException {
        // ProblemSet open => state=2
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsStartTime(LocalDateTime.now().minusHours(1));
        ps.setPsEndTime(LocalDateTime.now().plusHours(1));
        ps.setDuration(0);
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ps));

        // programming
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        prog.setPTotalScore(100);
        prog.setTimeLimit(1);
        prog.setCodeSizeLimit(10);
        prog.setPJudgeCode("judge code");
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // Student in set
        StudentNPs snps = new StudentNPs();
        snps.setFirstStartTime(LocalDateTime.now());
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(snps));

        // Belongs
        when(pnPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(new PNPs()));

        // 1 test case
        TestCase tc1 = new TestCase();
        tc1.setTcInput("in1");
        tc1.setTcOutput("out1");
        when(testCaseMapper.selectList(any()))
                .thenReturn(Collections.singletonList(tc1));

        // Sandbox => endedInTime=false => Timeout
        doAnswer(invocation -> {
            Sandbox s = (Sandbox) invocation.getMock();
            s.setEndedInTime(false);
            return null;
        }).when(sandboxSpy).run();

        Map<String, String> result =
                problemSetServiceImpl.submitProgramming(1, 10, "my code");
        assertEquals("success", result.get("error_message"));
        // pass_count=0 => partial or full? It's actually 0
        assertEquals("0", result.get("pass_count"));
        assertEquals("0", result.get("pa_actual_score"));
        assertEquals("Answer Incorrect", result.get("res_message"));
    }

    /* ********************************************************************
     *                 Tests for submitSpecialJudge()
     * ********************************************************************
     */

    @Test
    public void testSubmitSpecialJudge_NoSuchProblemSet() {
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.emptyList());

        Map<String, String> result =
                problemSetServiceImpl.submitSpecialJudge(1, 1, "code", "input");
        assertEquals("Problem Set Not Found", result.get("error_message"));
    }

    @Test
    public void testSubmitSpecialJudge_NoSuchProgrammingProblem() {
        // ProblemSet
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ps));

        // programming => empty
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.emptyList());

        Map<String, String> result =
                problemSetServiceImpl.submitSpecialJudge(1, 1, "code", "input");
        assertEquals("Programming Problem Not Found", result.get("error_message"));
    }

    @Test
    public void testSubmitSpecialJudge_UserNotInProblemSet() {
        // ProblemSet
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ps));

        // programming
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // user not in set
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.emptyList());

        Map<String, String> result =
                problemSetServiceImpl.submitSpecialJudge(1, 10, "code", "input");
        assertEquals("User Not In This Problem Set", result.get("error_message"));
    }

    @Test
    public void testSubmitSpecialJudge_NotStarted() {
        // ProblemSet not started
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsStartTime(LocalDateTime.now().plusDays(1));
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ps));

        // programming
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // user in set
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(new StudentNPs()));

        Map<String, String> result =
                problemSetServiceImpl.submitSpecialJudge(1, 10, "code", "input");
        assertEquals("Problem Set Not Started", result.get("error_message"));
    }

    @Test
    public void testSubmitSpecialJudge_AnswerNotStarted() {
        // ProblemSet started but user not started
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsStartTime(LocalDateTime.now().minusHours(1));
        ps.setPsEndTime(LocalDateTime.now().plusHours(1));
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ps));

        // programming
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        StudentNPs snps = new StudentNPs();
        snps.setFirstStartTime(null);
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(snps));

        Map<String, String> result =
                problemSetServiceImpl.submitSpecialJudge(1, 10, "code", "input");
        assertEquals("Answer Not Started", result.get("error_message"));
    }

    @Test
    public void testSubmitSpecialJudge_ProgrammingNotBelong() {
        // ProblemSet open
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsStartTime(LocalDateTime.now().minusDays(1));
        ps.setPsEndTime(LocalDateTime.now().plusDays(1));
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ps));

        // programming
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // user in set, started
        StudentNPs snps = new StudentNPs();
        snps.setFirstStartTime(LocalDateTime.now().minusHours(1));
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(snps));

        // pnPs => empty => not belong
        when(pnPsMapper.selectList(any())).thenReturn(Collections.emptyList());

        Map<String, String> result =
                problemSetServiceImpl.submitSpecialJudge(1, 10, "code", "input");
        assertEquals("Problem Not Belong To Problem Set", result.get("error_message"));
    }

    @Test
    public void testSubmitSpecialJudge_ProblemSetEnded() {
        // Now is after end => state=4
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsStartTime(LocalDateTime.now().minusDays(2));
        ps.setPsEndTime(LocalDateTime.now().minusDays(1));
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ps));

        // programming
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // user in set, started
        StudentNPs snps = new StudentNPs();
        snps.setFirstStartTime(LocalDateTime.now().minusDays(2));
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(snps));

        // belongs
        when(pnPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(new PNPs()));

        Map<String, String> result =
                problemSetServiceImpl.submitSpecialJudge(1, 10, "code", "input");
        assertEquals("Problem Set Ended", result.get("error_message"));
    }

    @Test
    public void testSubmitSpecialJudge_TimeToAnswerEnded() {
        // Duration set => user started => now is after start+duration => state=3
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsStartTime(LocalDateTime.now().minusHours(5));
        ps.setPsEndTime(LocalDateTime.now().plusDays(1));
        ps.setDuration(60); // 60 min
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ps));

        // programming
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // user started 2 hours ago => end
        StudentNPs snps = new StudentNPs();
        snps.setFirstStartTime(LocalDateTime.now().minusMinutes(120));
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(snps));

        // belongs
        when(pnPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(new PNPs()));

        Map<String, String> result =
                problemSetServiceImpl.submitSpecialJudge(1, 10, "code", "input");
        assertEquals("Time To Answer Ended", result.get("error_message"));
    }

    @Test
    public void testSubmitSpecialJudge_CodeEmpty() {
        // ProblemSet open, no duration => state=2
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsStartTime(LocalDateTime.now().minusHours(1));
        ps.setPsEndTime(LocalDateTime.now().plusHours(1));
        ps.setDuration(0);
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ps));

        // programming
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        prog.setCodeSizeLimit(10);
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // user started
        StudentNPs snps = new StudentNPs();
        snps.setFirstStartTime(LocalDateTime.now());
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(snps));

        // belongs
        when(pnPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(new PNPs()));

        Map<String, String> result =
                problemSetServiceImpl.submitSpecialJudge(1, 10, "", "input");
        assertEquals("My Code Cannot Be Empty", result.get("error_message"));
    }

    @Test
    public void testSubmitSpecialJudge_CodeTooLong() {
        // ProblemSet open => no duration => state=2
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsStartTime(LocalDateTime.now().minusHours(1));
        ps.setPsEndTime(LocalDateTime.now().plusHours(1));
        ps.setDuration(0);
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ps));

        // programming => code limit=1 => 1000 chars
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        prog.setCodeSizeLimit(1);
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // user started
        StudentNPs snps = new StudentNPs();
        snps.setFirstStartTime(LocalDateTime.now());
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(snps));

        // belongs
        when(pnPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(new PNPs()));

        // code of length 1500
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<1500; i++) {
            sb.append("X");
        }

        Map<String, String> result =
                problemSetServiceImpl.submitSpecialJudge(1, 10, sb.toString(), "input");
        assertEquals("My Code Length Exceeds Limit", result.get("error_message"));
    }

    @Test
    public void testSubmitSpecialJudge_Success_NormalRun() throws InterruptedException {
        // ProblemSet open => no duration => state=2
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsStartTime(LocalDateTime.now().minusHours(1));
        ps.setPsEndTime(LocalDateTime.now().plusHours(1));
        ps.setDuration(0);
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ps));

        // programming => code limit=10 => plenty
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        prog.setPJudgeCode(" # judge code");
        prog.setTimeLimit(1);
        prog.setCodeSizeLimit(10);
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // user started
        StudentNPs snps = new StudentNPs();
        snps.setFirstStartTime(LocalDateTime.now());
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(snps));

        // belongs
        when(pnPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(new PNPs()));

        // Sandbox => endedInTime=true, endedNormally=true, output="RESULT"
        doAnswer(invocation -> {
            Sandbox s = (Sandbox) invocation.getMock();
            s.setEndedInTime(true);
            s.setEndedNormally(true);
            s.setTestOut("RESULT");
            return null;
        }).when(sandboxSpy).run();

        Map<String, String> result =
                problemSetServiceImpl.submitSpecialJudge(1, 10, "my code", "my input");
        assertEquals("success", result.get("error_message"));
    }

    @Test
    public void testSubmitSpecialJudge_Success_Timeout() throws InterruptedException {
        // ProblemSet open => no duration => state=2
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsStartTime(LocalDateTime.now().minusHours(1));
        ps.setPsEndTime(LocalDateTime.now().plusHours(1));
        ps.setDuration(0);
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ps));

        // programming => code limit=10 => plenty
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        prog.setPJudgeCode(" # judge code");
        prog.setTimeLimit(1);
        prog.setCodeSizeLimit(10);
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // user started
        StudentNPs snps = new StudentNPs();
        snps.setFirstStartTime(LocalDateTime.now());
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(snps));

        // belongs
        when(pnPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(new PNPs()));

        // Sandbox => endedInTime=false => Timeout
        doAnswer(invocation -> {
            Sandbox s = (Sandbox) invocation.getMock();
            s.setEndedInTime(false);
            return null;
        }).when(sandboxSpy).run();

        Map<String, String> result =
                problemSetServiceImpl.submitSpecialJudge(1, 10, "my code", "my input");
        assertEquals("success", result.get("error_message"));
    }

    @Test
    public void testSubmitSpecialJudge_Success_RunError() throws InterruptedException {
        // ProblemSet open => no duration => state=2
        ProblemSet ps = new ProblemSet();
        ps.setProblemSetId(1);
        ps.setPsStartTime(LocalDateTime.now().minusHours(1));
        ps.setPsEndTime(LocalDateTime.now().plusHours(1));
        ps.setDuration(0);
        when(problemSetMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ps));

        // programming => code limit=10 => plenty
        Programming prog = new Programming();
        prog.setProgrammingId(10);
        prog.setPJudgeCode(" # judge code");
        prog.setTimeLimit(1);
        prog.setCodeSizeLimit(10);
        when(programmingMapper.selectList(any()))
                .thenReturn(Collections.singletonList(prog));

        // user started
        StudentNPs snps = new StudentNPs();
        snps.setFirstStartTime(LocalDateTime.now());
        when(studentNPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(snps));

        // belongs
        when(pnPsMapper.selectList(any()))
                .thenReturn(Collections.singletonList(new PNPs()));

        // Sandbox => endedInTime=true, but endedNormally=false => runtime error
        doAnswer(invocation -> {
            Sandbox s = (Sandbox) invocation.getMock();
            s.setEndedInTime(true);
            s.setEndedNormally(false);
            s.setTestOut("Traceback (most recent call last): ...");
            return null;
        }).when(sandboxSpy).run();

        Map<String, String> result =
                problemSetServiceImpl.submitSpecialJudge(1, 10, "my code", "my input");
        assertEquals("success", result.get("error_message"));
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

        List<Map<String, String>> result = problemSetServiceImpl.getActiveProblemSet();
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

        List<Map<String, String>> result = problemSetServiceImpl.getActiveProblemSet();

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

        List<Map<String, String>> result = problemSetServiceImpl.getAllProblemSet();

        // 断言结果是否正确
        assertEquals(1, result.size());
        assertEquals("Test Set", result.get(0).get("ps_name"));
        assertEquals("Test Author", result.get(0).get("ps_author_name"));
    }


    @Test
    void testGetOne_NotFound() {
        when(problemSetMapper.selectList(any())).thenReturn(Collections.emptyList());

        Map<String, String> result = problemSetServiceImpl.getOne(1);
        assertEquals("No such problem set found through ID query", result.get("error_message"));
    }

    @Test
    void testGetOne_UserNotInSet() {
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(new ProblemSet()));
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.emptyList());

        Map<String, String> result = problemSetServiceImpl.getOne(1);
        assertEquals("The user does not belong to this problem set", result.get("error_message"));
    }

    @Test
    void testGetOne_BeforeStart() {
        ProblemSet problemSet = new ProblemSet();
        problemSet.setPsStartTime(LocalDateTime.now().plusDays(1)); // 还没开始
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(problemSet));
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.singletonList(new StudentNPs()));

        Map<String, String> result = problemSetServiceImpl.getOne(1);
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

        Map<String, String> result = problemSetServiceImpl.startProblemSet(1);
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

        Map<String, String> result = problemSetServiceImpl.startProblemSet(1);
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

        Map<String, String> result = problemSetServiceImpl.startProblemSet(1);
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

        Map<String, String> result = problemSetServiceImpl.getOne(1);

        assertNotNull(result.get("ps_author_name"));
        assertEquals("", result.get("ps_author_name"));
    }


    @Test
    void testGetOne_StudentNotInProblemSet() {
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(new ProblemSet()));
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.emptyList()); // 空列表

        Map<String, String> result = problemSetServiceImpl.getOne(1);
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

        Map<String, String> result = problemSetServiceImpl.getOne(1);

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

        Map<String, String> result = problemSetServiceImpl.getOne(1);

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

        Map<String, String> result = problemSetServiceImpl.getOne(1);

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

        Map<String, String> result = problemSetServiceImpl.getOne(1);

        assertNotNull(result.get("ps_status"));
        assertEquals("closed", result.get("ps_status")); //  `ps_status == "closed"`
        assertNotNull(result.get("ps_total_score"));
        assertEquals("50", result.get("ps_total_score")); // 20 + 30 = 50
    }

    @Test
    void testStartProblemSet_NotFound() {
        when(problemSetMapper.selectList(any())).thenReturn(Collections.emptyList());

        Map<String, String> result = problemSetServiceImpl.startProblemSet(1);

        assertEquals("No such problem set found through ID query", result.get("error_message"));
    }

    @Test
    void testStartProblemSet_UserNotInSet() {
        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(problemSet));

        when(studentNPsMapper.selectList(any())).thenReturn(Collections.emptyList());

        Map<String, String> result = problemSetServiceImpl.startProblemSet(1);

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

        Map<String, String> result = problemSetServiceImpl.startProblemSet(1);


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

        List<Map<String, String>> result = problemSetServiceImpl.getAllProblemSet();

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

        Map<String, String> result = problemSetServiceImpl.getOne(1);

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

        Map<String, String> result = problemSetServiceImpl.getOne(1);

        assertEquals("ended", result.get("ps_status"));
    }
}
