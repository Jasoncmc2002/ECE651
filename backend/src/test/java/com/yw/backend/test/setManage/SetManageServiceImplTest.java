package com.yw.backend.test.setManage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yw.backend.mapper.*;
import com.yw.backend.pojo.*;
import com.yw.backend.service.impl.problemManage.ProgrammingManageServiceImpl;
import com.yw.backend.service.impl.setManage.SetManageServiceImpl;
import com.yw.backend.service.impl.utils.UserDetailsImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class SetManageServiceImplTest {

    @Mock private ProblemSetMapper problemSetMapper;
    @InjectMocks private SetManageServiceImpl setManageService;

//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        User mockUser = new User();
//        mockUser.setUserId(1);
//        mockUser.setPermission(1);
//
//        UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);
//        UsernamePasswordAuthenticationToken authToken =
//                new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
//
//        SecurityContext securityContext = mock(SecurityContext.class);
//        when(securityContext.getAuthentication()).thenReturn(authToken);
//        SecurityContextHolder.setContext(securityContext);
//    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 初始化一个默认用户
        User mockUser = new User();
        mockUser.setUserId(1);
        mockUser.setPermission(1);

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

    private void setAuthenticationUser(int permission) {
        UserDetailsImpl loginUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = loginUser.getUser();

        user.setPermission(permission);

        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    @Test
    void testCreateProblemSet_Success() {
        when(problemSetMapper.selectList(null)).thenReturn(new ArrayList<>());

        Map<String, String> response = setManageService.create(
                "Sample Set", LocalDateTime.now(), LocalDateTime.now().plusHours(2), 60
        );

        assertEquals("success", response.get("error_message"));
        assertNotNull(response.get("problem_set_id"));

        verify(problemSetMapper, times(1)).insert(any(ProblemSet.class));
    }

    @Test
    void testCreateProblemSet_NoPermission() {
        setAuthenticationUser(0);

        Map<String, String> response = setManageService.create(
                "Sample Set", LocalDateTime.now(), LocalDateTime.now().plusHours(2), 60
        );

        assertEquals("no permission to create problem set", response.get("error_message"));
        verify(problemSetMapper, never()).insert(any());
    }

    @Test
    void testCreateProblemSet_EmptyName() {
        Map<String, String> response = setManageService.create(
                "", LocalDateTime.now(), LocalDateTime.now().plusHours(2), 60
        );

        assertEquals("the problem set name cannot be empty", response.get("error_message"));
    }

    @Test
    void testCreateProblemSet_NameTooLong() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            sb.append("A");
        }
        String longName = sb.toString();


        Map<String, String> response = setManageService.create(
                longName, LocalDateTime.now(), LocalDateTime.now().plusHours(2), 60
        );

        assertEquals("the problem set name cannot exceed 100 characters", response.get("error_message"));
    }

    @Test
    void testCreateProblemSet_StartTimeAfterEndTime() {
        Map<String, String> response = setManageService.create(
                "Valid Name", LocalDateTime.now().plusHours(2), LocalDateTime.now(), 60
        );

        assertEquals("the start time of the problem set cannot be after the end time", response.get("error_message"));
    }

    @Test
    void testCreateProblemSet_NegativeDuration() {
        Map<String, String> response = setManageService.create(
                "Valid Name", LocalDateTime.now(), LocalDateTime.now().plusHours(2), -10
        );

        assertEquals("the exam time cannot be a negative number", response.get("error_message"));
    }

    @Test
    void testCreateProblemSet_ExamTimeExceedsDuration() {
        Map<String, String> response = setManageService.create(
                "Valid Name", LocalDateTime.now(), LocalDateTime.now().plusMinutes(50), 60
        );

        assertEquals("the exam time exceed the duration of the problem set间", response.get("error_message"));
    }

    @Test
    void testCreateProblemSet_ValidDurationButInvalidTime() {
        Map<String, String> response = setManageService.create(
                "Valid Name", LocalDateTime.now().plusHours(2), LocalDateTime.now(), 60
        );

        assertEquals("the start time of the problem set cannot be after the end time", response.get("error_message"));
    }

    @Test
    void testCreateProblemSet_DatabaseError() {
        when(problemSetMapper.selectList(null)).thenReturn(new ArrayList<>());

        doThrow(new RuntimeException("Database error")).when(problemSetMapper).insert(any());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            setManageService.create("Sample Set", LocalDateTime.now(), LocalDateTime.now().plusHours(2), 60);
        });

        assertEquals("Database error", exception.getMessage());
    }


    @Test
    void testDeleteProblemSet_Success() {
        ProblemSet mockSet = new ProblemSet();
        mockSet.setProblemSetId(1);
        mockSet.setPsAuthorId(1);

        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(mockSet));

        Map<String, String> response = setManageService.delete(1);

        assertEquals("success", response.get("error_message"));
        verify(problemSetMapper, times(1)).delete(any(QueryWrapper.class));
    }

    @Test
    void testDeleteProblemSet_NoPermission() {
        setAuthenticationUser(0);

        Map<String, String> response = setManageService.delete(1);

        assertEquals("no operation permission to delete problem set", response.get("error_message"));
        verify(problemSetMapper, never()).delete(any());
    }

    @Test
    void testDeleteProblemSet_NotFound() {
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        Map<String, String> response = setManageService.delete(1);

        assertEquals("no problem set found with the given ID", response.get("error_message"));
    }

    @Test
    void testDeleteProblemSet_NotAuthor() {
        ProblemSet mockSet = new ProblemSet();
        mockSet.setProblemSetId(1);
        mockSet.setPsAuthorId(999);

        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(mockSet));

        Map<String, String> response = setManageService.delete(1);

        assertEquals("teachers cannot delete problem set created by others", response.get("error_message"));
        verify(problemSetMapper, never()).delete(any());
    }

    @Test
    void testDeleteProblemSet_DatabaseError() {
        ProblemSet mockSet = new ProblemSet();
        mockSet.setProblemSetId(1);
        mockSet.setPsAuthorId(1);

        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(mockSet));

        doThrow(new RuntimeException("Database error")).when(problemSetMapper).delete(any(QueryWrapper.class));

        assertThrows(RuntimeException.class, () -> setManageService.delete(1));
    }



    @Mock private ObjectiveProblemMapper objectiveProblemMapper;
    @Mock private OpNPsMapper opNPsMapper;
    @Mock private ObjectiveProblemAnswerMapper objectiveProblemAnswerMapper;


    @Test
    void testSearchObjectiveProblem_Success() {
        when(problemSetMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.singletonList(new ProblemSet()));

        ObjectiveProblem mockProblem = new ObjectiveProblem();
        mockProblem.setObjectiveProblemId(1);
        mockProblem.setOpDescription("Sample Question");
        mockProblem.setOpTag("tag1");
        mockProblem.setOpDifficulty(3);

        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(mockProblem));

        List<Map<String, String>> response = setManageService.searchObjectiveProblem(1, "Sample", "tag1", 1, 5);

        assertEquals(1, response.size());
        assertEquals("Sample Question...", response.get(0).get("op_description"));
    }

    @Test
    void testAddObjectiveProblem_Success() {
        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsAuthorId(1);

        ObjectiveProblem objectiveProblem = new ObjectiveProblem();
        objectiveProblem.setObjectiveProblemId(2);

        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(problemSet));
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(objectiveProblem));
        when(opNPsMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);

        Map<String, String> response = setManageService.addObjectiveProblem(1, 2);

        assertEquals("success", response.get("error_message"));
        verify(opNPsMapper, times(1)).insert(any());
    }



    @Test
    void testDeleteObjectiveProblem_Success() {
        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsAuthorId(1);

        ObjectiveProblem objectiveProblem = new ObjectiveProblem();
        objectiveProblem.setObjectiveProblemId(2);

        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(problemSet));

        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(objectiveProblem));

        when(opNPsMapper.selectCount(any(QueryWrapper.class)))
                .thenReturn(1L);

        when(opNPsMapper.delete(any(QueryWrapper.class)))
                .thenReturn(1);

        Map<String, String> response = setManageService.deleteObjectiveProblem(1, 2);

        assertEquals("success", response.get("error_message"));

        verify(opNPsMapper, times(1)).delete(any(QueryWrapper.class));
    }



    @Test
    void testGetAddedObjectiveProblem_Success() {
        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);

        OpNPs opNPs = new OpNPs(2, 1);
        ObjectiveProblem objectiveProblem = new ObjectiveProblem();
        objectiveProblem.setObjectiveProblemId(2);
        objectiveProblem.setOpDescription("Sample Question");
        objectiveProblem.setOpTag("tag1");
        objectiveProblem.setOpDifficulty(3);

        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(problemSet));
        when(opNPsMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(opNPs));
        when(objectiveProblemMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(objectiveProblem);

        List<Map<String, String>> response = setManageService.getAddedObjectiveProblem(1);

        assertEquals(1, response.size());
        assertEquals("Sample Question...", response.get(0).get("op_description"));
    }

    @Test
    void testSearchObjectiveProblem_NoProblemSetFound() {
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList()); // 模拟数据库无数据

        List<Map<String, String>> response = setManageService.searchObjectiveProblem(1, "Sample", "tag1", 1, 5);

        assertEquals(1, response.size());
        assertEquals("no problem set found with the given ID", response.get(0).get("error_message"));
    }


    @Test
    void testDeleteObjectiveProblem_DatabaseError() {
        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsAuthorId(1);

        ObjectiveProblem objectiveProblem = new ObjectiveProblem();
        objectiveProblem.setObjectiveProblemId(2);

        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(problemSet));
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(objectiveProblem));
        when(opNPsMapper.selectCount(any(QueryWrapper.class)))
                .thenReturn(1L);

        doThrow(new RuntimeException("Database error"))
                .when(opNPsMapper).delete(any(QueryWrapper.class));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            setManageService.deleteObjectiveProblem(1, 2);
        });

        assertEquals("Database error", exception.getMessage());
    }








    @Test
    void testCreateProblemSet_WithExistingProblemSets() {
        List<ProblemSet> existingSets = Arrays.asList(
                new ProblemSet(1, "Set 1", 1, LocalDateTime.now(), LocalDateTime.now().plusHours(2), 60),
                new ProblemSet(2, "Set 2", 1, LocalDateTime.now(), LocalDateTime.now().plusHours(2), 60),
                new ProblemSet(5, "Set 5", 1, LocalDateTime.now(), LocalDateTime.now().plusHours(2), 60) // 最大ID
        );

        when(problemSetMapper.selectList(null)).thenReturn(existingSets);

        Map<String, String> response = setManageService.create(
                "New Problem Set", LocalDateTime.now(), LocalDateTime.now().plusHours(2), 60
        );

        assertEquals("success", response.get("error_message"));
        assertEquals("6", response.get("problem_set_id"));

        verify(problemSetMapper, times(1)).insert(any(ProblemSet.class));
    }


    @Test
    void testSearchObjectiveProblem_NoPermission() {
        setAuthenticationUser(0);

        List<Map<String, String>> response = setManageService.searchObjectiveProblem(1, "desc", "tag", 1, 5);

        assertEquals(1, response.size());
        assertEquals("no permission to query objective problem in problem set", response.get(0).get("error_message"));
    }

    @Test
    void testAddObjectiveProblem_NoPermission() {
        setAuthenticationUser(0);

        Map<String, String> response = setManageService.addObjectiveProblem(1, 1);

        assertEquals("No permission to add objective problems to the problem set", response.get("error_message"));
    }

    @Test
    void testAddObjectiveProblem_ProblemSetNotFound() {
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        Map<String, String> response = setManageService.addObjectiveProblem(1, 1);

        assertEquals("No problem set found with the given ID", response.get("error_message"));
    }

    @Test
    void testAddObjectiveProblem_ObjectiveProblemNotFound() {
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(new ProblemSet()));

        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        Map<String, String> response = setManageService.addObjectiveProblem(1, 1);

        assertEquals("No objective problem found with the given ID", response.get("error_message"));
    }


    @Test
    void testAddObjectiveProblem_AlreadyExists() {
        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsAuthorId(1);

        ObjectiveProblem objectiveProblem = new ObjectiveProblem();
        objectiveProblem.setObjectiveProblemId(2);

        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(problemSet));
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(objectiveProblem));

        when(opNPsMapper.selectCount(any(QueryWrapper.class)))
                .thenReturn(1L);

        Map<String, String> response = setManageService.addObjectiveProblem(1, 2);

        assertEquals("The objective problem has already been added to the problem set",
                response.get("error_message"));

        verify(opNPsMapper, times(1)).selectCount(any(QueryWrapper.class));
    }

    @Test
    void testDeleteObjectiveProblem_NoPermission() {
        setAuthenticationUser(0);

        Map<String, String> response = setManageService.deleteObjectiveProblem(1, 1);

        assertEquals("No permission to delete objective problems from the problem set", response.get("error_message"));
    }

    @Test
    void testDeleteObjectiveProblem_ProblemSetNotFound() {
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        Map<String, String> response = setManageService.deleteObjectiveProblem(1, 1);

        assertEquals("No problem set found with the given ID", response.get("error_message"));
    }

    @Test
    void testDeleteObjectiveProblem_ObjectiveProblemNotFound() {
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(new ProblemSet()));

        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        Map<String, String> response = setManageService.deleteObjectiveProblem(1, 1);

        assertEquals("No objective problem found with the given ID", response.get("error_message"));
    }

    @Test
    void testGetAddedObjectiveProblem_NoPermission() {
        setAuthenticationUser(0);

        List<Map<String, String>> response = setManageService.getAddedObjectiveProblem(1);

        assertEquals(1, response.size());
        assertEquals("No permission to query objective problems in the problem set", response.get(0).get("error_message"));
    }

    @Test
    void testGetAddedObjectiveProblem_ProblemSetNotFound() {
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        List<Map<String, String>> response = setManageService.getAddedObjectiveProblem(1);

        assertEquals(1, response.size());
        assertEquals("No problem set found with the given ID", response.get(0).get("error_message"));
    }

    @Test
    void testDeleteObjectiveProblem_TeacherCannotModifyOthersProblemSet() {
        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsAuthorId(999);

        ObjectiveProblem objectiveProblem = new ObjectiveProblem();
        objectiveProblem.setObjectiveProblemId(2);

        setAuthenticationUser(1);

        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(problemSet));

        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(objectiveProblem));

        Map<String, String> response = setManageService.deleteObjectiveProblem(1, 2);

        assertEquals("Teachers are not allowed to delete objective problems from problem sets created by others",
                response.get("error_message"));
    }



    @Test
    void testDeleteObjectiveProblem_AlreadyDeleted() {
        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsAuthorId(1);

        ObjectiveProblem objectiveProblem = new ObjectiveProblem();
        objectiveProblem.setObjectiveProblemId(2);

        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(problemSet));
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(objectiveProblem));

        when(opNPsMapper.selectCount(any(QueryWrapper.class)))
                .thenReturn(0L);

        Map<String, String> response = setManageService.deleteObjectiveProblem(1, 2);

        assertEquals("The objective problem has been removed from the problem set",
                response.get("error_message"));

        verify(opNPsMapper, times(1)).selectCount(any(QueryWrapper.class));
    }

    @Test
    void testAddObjectiveProblem_TeacherCannotModifyOthersProblemSet() {
        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsAuthorId(999);

        ObjectiveProblem objectiveProblem = new ObjectiveProblem();
        objectiveProblem.setObjectiveProblemId(2);

        setAuthenticationUser(1);

        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(problemSet));

        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(objectiveProblem));

        Map<String, String> response = setManageService.addObjectiveProblem(1, 2);

        assertEquals("Teachers are not allowed to add objective problems to problem sets created by others",
                response.get("error_message"));
    }


    @Test
    void testSearchObjectiveProblem_AlreadyAdded() {
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(new ProblemSet()));

        ObjectiveProblem op1 = new ObjectiveProblem();
        op1.setObjectiveProblemId(1);
        op1.setOpDescription("Question 1");
        op1.setOpTag("Math");
        op1.setOpDifficulty(3);

        ObjectiveProblem op2 = new ObjectiveProblem();
        op2.setObjectiveProblemId(2);
        op2.setOpDescription("Question 2");
        op2.setOpTag("Physics");
        op2.setOpDifficulty(4);

        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(op1, op2));

        when(opNPsMapper.selectCount(any(QueryWrapper.class)))
                .thenReturn(1L, 0L);



        List<Map<String, String>> response = setManageService.searchObjectiveProblem(1, "Question", "Math", 1, 5);

        assertEquals(1, response.size());
        assertEquals("2", response.get(0).get("objective_problem_id"));

        verify(opNPsMapper, atLeast(2)).selectCount(any(QueryWrapper.class));

    }

    // below: cmc's code

//    @Mock private ProblemSetMapper problemSetMapper;
    @Mock private ProgrammingMapper programmingMapper;
    @InjectMocks
    private ProgrammingManageServiceImpl programmingManageService;

    private void setAuthenticationUser(int permission, int userId) {
        UserDetailsImpl loginUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = loginUser.getUser();
        user.setPermission(permission);
        user.setUserId(userId);
    }

    @Test
    void testCreate_Success() {
        when(programmingMapper.selectList(null)).thenReturn(new ArrayList<>());
        Map<String, String> response = programmingManageService.create(
                "Valid description", 10, 1000, 65536, "Algorithm", "Title", "judgeCode", 3);
        assertEquals("success", response.get("error_message"));
        assertEquals("1", response.get("programming_id"));
        verify(programmingMapper, times(1)).insert(any(Programming.class));
    }

    @Test
    void testCreate_NoPermission() {
        setAuthenticationUser(0, 1);
        Map<String, String> response = programmingManageService.create(
                "Valid description", 10, 1000, 65536, "Algorithm", "Title", "judgeCode", 3);
        assertEquals("no permission to create programming questions", response.get("error_message"));
        verify(programmingMapper, never()).insert(any());
    }

    @Test
    void testCreate_EmptyDescription() {
        Map<String, String> response = programmingManageService.create(
                "", 10, 1000, 65536, "Algorithm", "Title", "judgeCode", 3);
        assertEquals("the question description cannot be empty", response.get("error_message"));
    }

    @Test
    void testCreate_DescriptionTooLong() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10001; i++) sb.append("A");
        Map<String, String> response = programmingManageService.create(
                sb.toString(), 10, 1000, 65536, "Algorithm", "Title", "judgeCode", 3);
        assertEquals("the question description cannot exceed 10000 characters", response.get("error_message"));
    }

    @Test
    void testCreate_InvalidScore() {
        Map<String, String> response = programmingManageService.create(
                "Valid description", 0, 1000, 65536, "Algorithm", "Title", "judgeCode", 3);
        assertEquals("the question score must be a positive integer", response.get("error_message"));
    }

    @Test
    void testCreate_InvalidDifficulty() {
        Map<String, String> response = programmingManageService.create(
                "Valid description", 10, 1000, 65536, "Algorithm", "Title", "judgeCode", 6);
        assertEquals("the difficulty coefficient must be a positive integer between 1 and 5", response.get("error_message"));
    }

    @Test
    void testCreate_InvalidTimeLimit() {
        Map<String, String> response = programmingManageService.create(
                "Valid description", 10, 0, 65536, "Algorithm", "Title", "judgeCode", 3);
        assertEquals("the time limit must be a positive integer", response.get("error_message"));
    }

    @Test
    void testCreate_InvalidCodeSizeLimit() {
        Map<String, String> response = programmingManageService.create(
                "Valid description", 10, 1000, 0, "Algorithm", "Title", "judgeCode", 3);
        assertEquals("the code length limit must be a positive integer", response.get("error_message"));
    }

    @Test
    void testCreate_EmptyTag() {
        Map<String, String> response = programmingManageService.create(
                "Valid description", 10, 1000, 65536, "", "Title", "judgeCode", 3);
        assertEquals("tags cannot be empty", response.get("error_message"));
    }

    @Test
    void testCreate_TagTooLong() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 101; i++) sb.append("A");
        Map<String, String> response = programmingManageService.create(
                "Valid description", 10, 1000, 65536, sb.toString(), "Title", "judgeCode", 3);
        assertEquals("tags cannot exceed 100 characters", response.get("error_message"));
    }

    @Test
    void testCreate_EmptyTitle() {
        Map<String, String> response = programmingManageService.create(
                "Valid description", 10, 1000, 65536, "Algorithm", "", "judgeCode", 3);
        assertEquals("the title cannot be empty", response.get("error_message"));
    }

    @Test
    void testCreate_TitleTooLong() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 101; i++) sb.append("A");
        Map<String, String> response = programmingManageService.create(
                "Valid description", 10, 1000, 65536, "Algorithm", sb.toString(), "judgeCode", 3);
        assertEquals("the title cannot exceed 100 characters", response.get("error_message"));
    }

    @Test
    void testCreate_JudgeCodeTooLong() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16001; i++) sb.append("A");
        Map<String, String> response = programmingManageService.create(
                "Valid description", 10, 1000, 65536, "Algorithm", "Title", sb.toString(), 3);
        assertEquals("the evaluation program code length for function-based questions cannot exceed 16kB",
                response.get("error_message"));
    }

    @Test
    void testCreate_CalculateIDWhenDataExists() {
        Programming existing1 = new Programming();
        existing1.setProgrammingId(3);
        Programming existing2 = new Programming();
        existing2.setProgrammingId(7);
        Programming existing3 = new Programming();
        existing3.setProgrammingId(5);

        when(programmingMapper.selectList(null))
                .thenReturn(Arrays.asList(existing1, existing2, existing3));

        Map<String, String> response = programmingManageService.create(
                "Valid description", 10, 1000, 65536, "Algorithm", "Title", "judgeCode", 3);

        assertEquals("success", response.get("error_message"));
        assertEquals("8", response.get("programming_id"));
        verify(programmingMapper, times(1)).insert(any(Programming.class));
    }


    @Test
    void testDelete_Success_OwnQuestion() {
        Programming existing = new Programming();
        existing.setProgrammingId(1);
        existing.setPAuthorId(1);
        when(programmingMapper.selectList(any())).thenReturn(Collections.singletonList(existing));
        Map<String, String> response = programmingManageService.delete(1);
        assertEquals("success", response.get("error_message"));
        verify(programmingMapper, times(1)).delete(any(QueryWrapper.class));
    }

    @Test
    void testDelete_Success_Admin() {
        setAuthenticationUser(2, 99); // Admin user
        Programming existing = new Programming();
        existing.setProgrammingId(1);
        existing.setPAuthorId(88);
        when(programmingMapper.selectList(any())).thenReturn(Collections.singletonList(existing));
        Map<String, String> response = programmingManageService.delete(1);
        assertEquals("success", response.get("error_message"));
        verify(programmingMapper, times(1)).delete(any(QueryWrapper.class));
    }

    @Test
    void testDelete_NoPermission() {
        setAuthenticationUser(0, 1);
        Map<String, String> response = programmingManageService.delete(1);
        assertEquals("no permission to delete programming questions", response.get("error_message"));
        verify(programmingMapper, never()).delete(any());
    }

    @Test
    void testDelete_QuestionNotFound() {
        when(programmingMapper.selectList(any())).thenReturn(Collections.emptyList());
        Map<String, String> response = programmingManageService.delete(1);
        assertEquals("no programming question found with the provided ID", response.get("error_message"));
    }

    @Test
    void testDelete_NotAuthorNorAdmin() {
        setAuthenticationUser(1, 2);
        Programming existing = new Programming();
        existing.setProgrammingId(1);
        existing.setPAuthorId(88);
        when(programmingMapper.selectList(any())).thenReturn(Collections.singletonList(existing));
        Map<String, String> response = programmingManageService.delete(1);
        assertEquals("teachers cannot delete programming questions created by others", response.get("error_message"));
        verify(programmingMapper, never()).delete(any());
    }

}
