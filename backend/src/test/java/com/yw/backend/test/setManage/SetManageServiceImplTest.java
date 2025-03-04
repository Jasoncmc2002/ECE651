
package com.yw.backend.test.setManage;
import com.yw.backend.mapper.*;
import com.yw.backend.pojo.*;
import com.yw.backend.service.impl.setManage.SetManageServiceImpl;
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

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
public class SetManageServiceImplTest {

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

    @Test
    public void testGetOne() {
        // Test permission denied
        User user = new User();
        user.setPermission(0);
        setAuthentication(user);
        Map<String, String> result = setManageService.getOne(1);
        assertEquals("Permission denied", result.get("error_message"));

        // Test no such problem set
        user.setPermission(1);
        setAuthentication(user);
        when(problemSetMapper.selectList(any())).thenReturn(Collections.emptyList());
        result = setManageService.getOne(1);
        assertEquals("No such problem set", result.get("error_message"));

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