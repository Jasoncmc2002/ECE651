package com.yw.backend.test.setManage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yw.backend.mapper.ObjectiveProblemAnswerMapper;
import com.yw.backend.mapper.ObjectiveProblemMapper;
import com.yw.backend.mapper.OpNPsMapper;
import com.yw.backend.mapper.ProblemSetMapper;
import com.yw.backend.pojo.*;
import com.yw.backend.service.impl.setManage.SetManageServiceImpl;
import com.yw.backend.service.impl.utils.UserDetailsImpl;

import com.yw.backend.mapper.*;
import com.yw.backend.service.impl.utils.UserDetailsImpl;
import com.yw.backend.service.setManage.SetManageService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContext;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@Transactional
public class SetManageServiceImplTest {

    @Mock private ProblemSetMapper problemSetMapper;
    @InjectMocks private SetManageServiceImpl setManageService;


    @Mock
    private ProblemSetMapper problemSetMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private StudentNPsMapper studentNPsMapper;
    @Mock
    private ObjectiveProblemAnswerMapper objectiveProblemAnswerMapper;
    @Mock
    private ProgrammingAnswerMapper programmingAnswerMapper;

    @InjectMocks
    private SetManageServiceImpl setManageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        User mockUser = new User();
        mockUser.setUserId(1);
        mockUser.setPermission(1);  // 默认有权限

        UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authToken);
        SecurityContextHolder.setContext(securityContext);
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        User user = new User();
        user.setPermission(1);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
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

    // ===================== 测试 create 方法 =====================

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
        setAuthenticationUser(0); // 无权限

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




    // ===================== 测试 delete 方法 =====================

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
        setAuthenticationUser(0); // 无权限

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
        mockSet.setPsAuthorId(999); // 其他人创建的

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
        // 1️⃣ 模拟一个 ProblemSet（题集）
        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsAuthorId(1);  // 当前用户 ID = 1

        // 2️⃣ 模拟一个 ObjectiveProblem（客观题）
        ObjectiveProblem objectiveProblem = new ObjectiveProblem();
        objectiveProblem.setObjectiveProblemId(2);

        // 3️⃣ 确保 problemSetMapper.selectList() 返回数据
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(problemSet));

        // 4️⃣ 确保 objectiveProblemMapper.selectList() 返回数据
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(objectiveProblem));

        // 5️⃣ 确保 opNPsMapper.selectCount() 返回 1（题目确实存在）
        when(opNPsMapper.selectCount(any(QueryWrapper.class)))
                .thenReturn(1L);  // selectCount 返回 long

        // 6️⃣ 确保 delete() 正常执行
        when(opNPsMapper.delete(any(QueryWrapper.class)))
                .thenReturn(1); // 假设删除 1 行数据

        // 7️⃣ 调用方法
        Map<String, String> response = setManageService.deleteObjectiveProblem(1, 2);

        // 8️⃣ 断言返回结果
        assertEquals("success", response.get("error_message"));

        // 9️⃣ 验证 delete() 只调用了一次
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
        // 模拟数据库已有 3 个 ProblemSet，ID 分别是 1, 2, 5
        List<ProblemSet> existingSets = Arrays.asList(
                new ProblemSet(1, "Set 1", 1, LocalDateTime.now(), LocalDateTime.now().plusHours(2), 60),
                new ProblemSet(2, "Set 2", 1, LocalDateTime.now(), LocalDateTime.now().plusHours(2), 60),
                new ProblemSet(5, "Set 5", 1, LocalDateTime.now(), LocalDateTime.now().plusHours(2), 60) // 最大ID
        );

        // 让 problemSetMapper.selectList(null) 返回已有的 ProblemSet
        when(problemSetMapper.selectList(null)).thenReturn(existingSets);

        // 调用 create 方法
        Map<String, String> response = setManageService.create(
                "New Problem Set", LocalDateTime.now(), LocalDateTime.now().plusHours(2), 60
        );

        // 断言：新的 ProblemSet ID 应该是最大 ID + 1，即 6
        assertEquals("success", response.get("error_message"));
        assertEquals("6", response.get("problem_set_id")); // 确保 ID 计算正确

        // 验证 insert() 被调用
        verify(problemSetMapper, times(1)).insert(any(ProblemSet.class));
    }


    @Test
    void testSearchObjectiveProblem_NoPermission() {
        // 设置用户权限为 0（没有权限）
        setAuthenticationUser(0);

        // 执行方法
        List<Map<String, String>> response = setManageService.searchObjectiveProblem(1, "desc", "tag", 1, 5);

        // 断言返回的错误消息
        assertEquals(1, response.size());
        assertEquals("no permission to query objective problem in problem set", response.get(0).get("error_message"));
    }

    @Test
    void testAddObjectiveProblem_NoPermission() {
        // 设置用户权限为 0（没有权限）
        setAuthenticationUser(0);

        // 执行方法
        Map<String, String> response = setManageService.addObjectiveProblem(1, 1);

        // 断言返回的错误消息
        assertEquals("No permission to add objective problems to the problem set", response.get("error_message"));
    }

    @Test
    void testAddObjectiveProblem_ProblemSetNotFound() {
        // 模拟 problemSetMapper.selectList() 返回空列表
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        // 执行方法
        Map<String, String> response = setManageService.addObjectiveProblem(1, 1);

        // 断言返回的错误消息
        assertEquals("No problem set found with the given ID", response.get("error_message"));
    }

    @Test
    void testAddObjectiveProblem_ObjectiveProblemNotFound() {
        // 模拟 problemSetMapper.selectList() 返回正常数据
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(new ProblemSet()));

        // 模拟 objectiveProblemMapper.selectList() 返回空列表
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        // 执行方法
        Map<String, String> response = setManageService.addObjectiveProblem(1, 1);

        // 断言返回的错误消息
        assertEquals("No objective problem found with the given ID", response.get("error_message"));
    }


    @Test
    void testAddObjectiveProblem_AlreadyExists() {
        // 模拟 ProblemSet 存在
        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsAuthorId(1);

        // 模拟 ObjectiveProblem 存在
        ObjectiveProblem objectiveProblem = new ObjectiveProblem();
        objectiveProblem.setObjectiveProblemId(2);

        // 确保 problemSetMapper 和 objectiveProblemMapper 查询返回数据
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(problemSet));
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(objectiveProblem));

        // **模拟题目已经被添加**
        when(opNPsMapper.selectCount(any(QueryWrapper.class)))
                .thenReturn(1L); // 返回 1，表示题目已经存在

        // 执行方法
        Map<String, String> response = setManageService.addObjectiveProblem(1, 2);

        // 断言返回的错误消息
        assertEquals("The objective problem has already been added to the problem set",
                response.get("error_message"));

        // 确保 selectCount() 被调用
        verify(opNPsMapper, times(1)).selectCount(any(QueryWrapper.class));
    }

    @Test
    void testDeleteObjectiveProblem_NoPermission() {
        // 设置用户权限为 0（无权限）
        setAuthenticationUser(0);

        // 执行方法
        Map<String, String> response = setManageService.deleteObjectiveProblem(1, 1);

        // 断言返回的错误消息
        assertEquals("No permission to delete objective problems from the problem set", response.get("error_message"));
    }

    @Test
    void testDeleteObjectiveProblem_ProblemSetNotFound() {
        // 模拟 problemSetMapper.selectList() 返回空列表
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        // 执行方法
        Map<String, String> response = setManageService.deleteObjectiveProblem(1, 1);

        // 断言返回的错误消息
        assertEquals("No problem set found with the given ID", response.get("error_message"));
    }

    @Test
    void testDeleteObjectiveProblem_ObjectiveProblemNotFound() {
        // 模拟 problemSetMapper.selectList() 返回正常数据
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(new ProblemSet()));

        // 模拟 objectiveProblemMapper.selectList() 返回空列表
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        // 执行方法
        Map<String, String> response = setManageService.deleteObjectiveProblem(1, 1);

        // 断言返回的错误消息
        assertEquals("No objective problem found with the given ID", response.get("error_message"));
    }

    @Test
    void testGetAddedObjectiveProblem_NoPermission() {
        // 设置用户权限为 0（无权限）
        setAuthenticationUser(0);

        // 执行方法
        List<Map<String, String>> response = setManageService.getAddedObjectiveProblem(1);

        // 断言返回的错误消息
        assertEquals(1, response.size());
        assertEquals("No permission to query objective problems in the problem set", response.get(0).get("error_message"));
    }

    @Test
    void testGetAddedObjectiveProblem_ProblemSetNotFound() {
        // 模拟 problemSetMapper.selectList() 返回空列表
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        // 执行方法
        List<Map<String, String>> response = setManageService.getAddedObjectiveProblem(1);

        // 断言返回的错误消息
        assertEquals(1, response.size());
        assertEquals("No problem set found with the given ID", response.get(0).get("error_message"));
    }

    @Test
    void testDeleteObjectiveProblem_TeacherCannotModifyOthersProblemSet() {
        // 模拟 ProblemSet，作者 ID 为 999，而当前用户 ID 为 1
        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsAuthorId(999); // 其他人创建的

        // 模拟 ObjectiveProblem 存在
        ObjectiveProblem objectiveProblem = new ObjectiveProblem();
        objectiveProblem.setObjectiveProblemId(2);

        // 模拟用户是普通教师，权限 < 2
        setAuthenticationUser(1);

        // 确保 problemSetMapper.selectList() 返回该问题集
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(problemSet));

        // **确保 objectiveProblemMapper.selectList() 返回该题目**
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(objectiveProblem));

        // 执行方法
        Map<String, String> response = setManageService.deleteObjectiveProblem(1, 2);

        // 断言返回的错误消息
        assertEquals("Teachers are not allowed to delete objective problems from problem sets created by others",
                response.get("error_message"));
    }



    @Test
    void testDeleteObjectiveProblem_AlreadyDeleted() {
        // 模拟 ProblemSet 存在
        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsAuthorId(1);

        // 模拟 ObjectiveProblem 存在
        ObjectiveProblem objectiveProblem = new ObjectiveProblem();
        objectiveProblem.setObjectiveProblemId(2);

        // 确保 problemSetMapper 和 objectiveProblemMapper 查询返回数据
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(problemSet));
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(objectiveProblem));

        // **模拟题目已经被删除**
        when(opNPsMapper.selectCount(any(QueryWrapper.class)))
                .thenReturn(0L); // 返回 0，表示题目已经不存在

        // 执行方法
        Map<String, String> response = setManageService.deleteObjectiveProblem(1, 2);

        // 断言返回的错误消息
        assertEquals("The objective problem has been removed from the problem set",
                response.get("error_message"));

        // 确保 selectCount() 被调用
        verify(opNPsMapper, times(1)).selectCount(any(QueryWrapper.class));
    }

    @Test
    void testAddObjectiveProblem_TeacherCannotModifyOthersProblemSet() {
        // 模拟一个 ProblemSet，作者 ID 为 999，而当前用户 ID 为 1
        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsAuthorId(999); // 其他人创建的

        // 模拟 ObjectiveProblem 存在
        ObjectiveProblem objectiveProblem = new ObjectiveProblem();
        objectiveProblem.setObjectiveProblemId(2);

        // 模拟用户是普通教师，权限 < 2
        setAuthenticationUser(1);

        // 确保 problemSetMapper.selectList() 返回该问题集
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(problemSet));

        // **确保 objectiveProblemMapper.selectList() 返回该题目**
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(objectiveProblem));

        // 执行方法
        Map<String, String> response = setManageService.addObjectiveProblem(1, 2);

        // 断言返回的错误消息
        assertEquals("Teachers are not allowed to add objective problems to problem sets created by others",
                response.get("error_message"));
    }


    @Test
    void testSearchObjectiveProblem_AlreadyAdded() {
        // 模拟问题集存在
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(new ProblemSet()));

        // 模拟查询到两个问题
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
                .thenReturn(1L, 0L); // **依次返回 1（op1 已添加）和 0（op2 未添加）**



        // 执行方法
        List<Map<String, String>> response = setManageService.searchObjectiveProblem(1, "Question", "Math", 1, 5);

        // 断言：op1 被过滤掉，所以结果应该只有 op2
        assertEquals(1, response.size());
        assertEquals("2", response.get(0).get("objective_problem_id")); // 只剩下 op2

        // 确保 `opNPsMapper.selectCount()` 至少被调用 2 次（每个问题都会查询）
        verify(opNPsMapper, atLeast(2)).selectCount(any(QueryWrapper.class));

    }




}

    @Test
    public void testGetOne() {
        // Test permission denied
        User user = new User();
        user.setPermission(0);
        setAuthentication(user);
        Map<String, String> result = setManageService.getOne(1);
        assertEquals("No permission to obtain the problem set", result.get("error_message"));

        // Test no such problem set
        user.setPermission(1);
        setAuthentication(user);
        when(problemSetMapper.selectList(any())).thenReturn(Collections.emptyList());
        result = setManageService.getOne(1);
        assertEquals("No problem set with this ID", result.get("error_message"));

        // Test success
        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsName("Test");
        problemSet.setPsAuthorId(1);
        problemSet.setPsStartTime(LocalDateTime.now());
        problemSet.setPsEndTime(LocalDateTime.now().plusHours(1));
        problemSet.setDuration(60);
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(problemSet));
        User author = new User();
        author.setName("Author");
        when(userMapper.selectOne(any())).thenReturn(author);
        result = setManageService.getOne(1);
        assertEquals("success", result.get("error_message"));
    }

    @Test
    public void testGetAssignmentList() {
        // Test permission denied
        User user = new User();
        user.setPermission(0);
        setAuthentication(user);
        List<Map<String, String>> result = setManageService.getAssignmentList();
        assertEquals("Problem set get list permission denied", result.get(0).get("error_message"));

        // Test success
        user.setPermission(1);
        setAuthentication(user);
        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsName("Test");
        problemSet.setPsAuthorId(1);
        problemSet.setPsStartTime(LocalDateTime.now());
        problemSet.setPsEndTime(LocalDateTime.now().plusHours(1));
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(problemSet));
        User author = new User();
        author.setName("Author");
        when(userMapper.selectOne(any())).thenReturn(author);
        result = setManageService.getAssignmentList();
        assertEquals("Test", result.get(0).get("ps_name"));
    }

    @Test
    public void testGetExamList() {
        // Test permission denied
        User user = new User();
        user.setPermission(0);
        setAuthentication(user);
        List<Map<String, String>> result = setManageService.getExamList();
        assertEquals("Problem set get list permission denied", result.get(0).get("error_message"));

        // Test success
        user.setPermission(1);
        setAuthentication(user);
        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsName("Test");
        problemSet.setPsAuthorId(1);
        problemSet.setPsStartTime(LocalDateTime.now());
        problemSet.setPsEndTime(LocalDateTime.now().plusHours(1));
        problemSet.setDuration(60);
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(problemSet));
        User author = new User();
        author.setName("Author");
        when(userMapper.selectOne(any())).thenReturn(author);
        result = setManageService.getExamList();
        assertEquals("Test", result.get(0).get("ps_name"));
    }

    @Test
    public void testSearchStudent() {
        // Test permission denied
        User user = new User();
        user.setPermission(0);
        setAuthentication(user);
        List<Map<String, String>> result = setManageService.searchStudent(2, "02", null);
        assertEquals("Problem set search student permission denied", result.get(0).get("error_message"));

        // Test no such problem set
        user.setPermission(1);
        setAuthentication(user);
        when(problemSetMapper.selectList(any())).thenReturn(Collections.emptyList());
        result = setManageService.searchStudent(20000, "02", null);
        assertEquals("No such problem set", result.get(0).get("error_message"));

        // Test success
        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(problemSet));
        User student = new User();
        student.setUserId(1);
        student.setUsername("username");
        student.setName("name");
        student.setPermission(2);
        when(userMapper.selectList(any())).thenReturn(Collections.singletonList(student));
        when(studentNPsMapper.selectCount(any())).thenReturn(0L);
        result = setManageService.searchStudent(1, "username", "name");
        assertEquals("username", result.get(0).get("username"));
    }

    @Test
    public void testAddStudent() {
        // Test permission denied
        User user = new User();
        user.setPermission(0);
        setAuthentication(user);
        Map<String, String> result = setManageService.addStudent(1, 1);
        assertEquals("Problem set add student permission denied", result.get("error_message"));

        // Test no such problem set
        user.setPermission(1);
        setAuthentication(user);
        when(problemSetMapper.selectList(any())).thenReturn(Collections.emptyList());
        result = setManageService.addStudent(1, 1);
        assertEquals("No such problem set", result.get("error_message"));

        // Test no such user
        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsAuthorId(1);
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(problemSet));
        when(userMapper.selectList(any())).thenReturn(Collections.emptyList());
        result = setManageService.addStudent(1, 1);
        assertEquals("No such user", result.get("error_message"));

        // Test success
        User student = new User();
        student.setUserId(1);
        when(userMapper.selectList(any())).thenReturn(Collections.singletonList(student));
        when(studentNPsMapper.selectCount(any())).thenReturn(0L);
        result = setManageService.addStudent(1, 1);
        assertEquals("Teacher cannot add student to others' problem set", result.get("error_message"));
    }

    @Test
    public void testDeleteStudent() {
        // Test permission denied
        User user = new User();
        user.setPermission(0);
        setAuthentication(user);
        Map<String, String> result = setManageService.deleteStudent(1, 1);
        assertEquals("Problem set delete student permission denied", result.get("error_message"));

        // Test no such problem set
        user.setPermission(1);
        setAuthentication(user);
        when(problemSetMapper.selectList(any())).thenReturn(Collections.emptyList());
        result = setManageService.deleteStudent(1, 1);
        assertEquals("No such problem set", result.get("error_message"));

        // Test no such user
        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        problemSet.setPsAuthorId(1);
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(problemSet));
        when(userMapper.selectList(any())).thenReturn(Collections.emptyList());
        result = setManageService.deleteStudent(1, 1);
        assertEquals("No such user", result.get("error_message"));

        // Test success
        User student = new User();
        student.setUserId(1);
        when(userMapper.selectList(any())).thenReturn(Collections.singletonList(student));
        when(studentNPsMapper.selectCount(any())).thenReturn(1L);
        result = setManageService.deleteStudent(1, 1);
        assertEquals("Teacher cannot delete student from others' problem set", result.get("error_message"));
    }

    @Test
    public void testGetAddedStudent() {
        // Test permission denied
        User user = new User();
        user.setPermission(0);
        setAuthentication(user);
        List<Map<String, String>> result = setManageService.getAddedStudent(1);
        assertEquals("Problem set get added student permission denied", result.get(0).get("error_message"));

        // Test no such problem set
        user.setPermission(1);
        setAuthentication(user);
        when(problemSetMapper.selectList(any())).thenReturn(Collections.emptyList());
        result = setManageService.getAddedStudent(1);
        assertEquals("No such problem set", result.get(0).get("error_message"));

        // Test success
        ProblemSet problemSet = new ProblemSet();
        problemSet.setProblemSetId(1);
        when(problemSetMapper.selectList(any())).thenReturn(Collections.singletonList(problemSet));
        StudentNPs studentNPs = new StudentNPs();
        studentNPs.setStudentId(1);
        when(studentNPsMapper.selectList(any())).thenReturn(Collections.singletonList(studentNPs));
        User student = new User();
        student.setUserId(1);
        student.setUsername("username");
        student.setName("name");
        student.setPermission(2);
        when(userMapper.selectOne(any())).thenReturn(student);
        result = setManageService.getAddedStudent(1);
        assertEquals("username", result.get(0).get("username"));
    }

    private void setAuthentication(User user) {
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}