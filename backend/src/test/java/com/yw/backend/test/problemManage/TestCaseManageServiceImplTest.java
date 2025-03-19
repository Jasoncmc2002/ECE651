package com.yw.backend.test.problemManage;

import com.yw.backend.mapper.ProgrammingMapper;
import com.yw.backend.mapper.TestCaseMapper;
import com.yw.backend.pojo.Programming;
import com.yw.backend.pojo.TestCase;
import com.yw.backend.pojo.User;
import com.yw.backend.service.impl.problemManage.TestCaseManageServiceImpl;
import com.yw.backend.service.impl.utils.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestCaseManageServiceImplTest {

    @InjectMocks
    private TestCaseManageServiceImpl testCaseManageService;

    @Mock
    private TestCaseMapper testCaseMapper;

    @Mock
    private ProgrammingMapper programmingMapper;

    private User normalUser;
    private User teacherUser;
    private User adminUser;
    private UserDetailsImpl userDetails;
    private Programming programming;
    private TestCase testCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        normalUser = new User();
        normalUser.setUserId(1);
        normalUser.setPermission(0);

        teacherUser = new User();
        teacherUser.setUserId(2);
        teacherUser.setPermission(1);

        adminUser = new User();
        adminUser.setUserId(3);
        adminUser.setPermission(2);
        programming = new Programming();
        programming.setProgrammingId(1);
        programming.setPAuthorId(2);

        testCase = new TestCase();
        testCase.setTestCaseId(1);
        testCase.setProgrammingId(1);
        testCase.setTcInput("test input");
        testCase.setTcOutput("test output");
    }

    private void setAuthenticationUser(User user) {
        userDetails = new UserDetailsImpl(user);
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    @Test
    void testCreateTestCase_NoPermission() {
        setAuthenticationUser(normalUser);
        
        Map<String, String> result = testCaseManageService.create(1, "input", "output", false);
        
        assertEquals("Permission denied", result.get("error_message"));
    }

    @Test
    void testCreateTestCase_NoProgramming() {
        setAuthenticationUser(teacherUser);
        when(programmingMapper.selectList(any())).thenReturn(Collections.emptyList());
        
        Map<String, String> result = testCaseManageService.create(1, "input", "output", false);
        
        assertEquals("No such programming problem", result.get("error_message"));
    }

    @Test
    void testCreateTestCase_NotAuthorAndNotAdmin() {
        setAuthenticationUser(teacherUser);
        when(programmingMapper.selectList(any())).thenReturn(Collections.singletonList(programming));
        programming.setPAuthorId(3); // 设置不同的作者ID
        
        Map<String, String> result = testCaseManageService.create(1, "input", "output", false);
        
        assertEquals("Teacher cannot create test case for others' programming problem", 
                    result.get("error_message"));
    }

    @Test
    void testCreateTestCase_InputTooLong() {
        setAuthenticationUser(teacherUser);
        when(programmingMapper.selectList(any())).thenReturn(Collections.singletonList(programming));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10001; i++) {
            sb.append("A");
        }
        String longInput = sb.toString();
        
        Map<String, String> result = testCaseManageService.create(1, longInput, "output", false);
        
        assertEquals("Test case input too long", result.get("error_message"));
    }

    @Test
    void testCreateTestCase_EmptyOutput() {
        setAuthenticationUser(teacherUser);
        when(programmingMapper.selectList(any())).thenReturn(Collections.singletonList(programming));
        
        Map<String, String> result = testCaseManageService.create(1, "input", "", false);
        
        assertEquals("Question output cannot be empty", result.get("error_message"));
    }

    @Test
    void testCreateTestCase_OutputTooLong() {
        setAuthenticationUser(teacherUser);
        when(programmingMapper.selectList(any())).thenReturn(Collections.singletonList(programming));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10001; i++) {
            sb.append("A");
        }
        String longOutput = sb.toString();
        
        Map<String, String> result = testCaseManageService.create(1, "input", longOutput, false);
        
        assertEquals("Test case output too long", result.get("error_message"));
    }

    @Test
    void testCreateTestCase_SuccessWithId() {
        setAuthenticationUser(teacherUser);
        when(programmingMapper.selectList(any())).thenReturn(Collections.singletonList(programming));
        when(testCaseMapper.selectList(any())).thenReturn(
            Collections.singletonList(testCase));
        
        Map<String, String> result = testCaseManageService.create(1, "input", "output", true);
        
        assertEquals("success", result.get("error_message"));
        assertNotNull(result.get("test_case_id"));
    }

    @Test
    void testCreateTestCase_SuccessWithoutId() {
        setAuthenticationUser(teacherUser);
        when(programmingMapper.selectList(any())).thenReturn(Collections.singletonList(programming));
        
        Map<String, String> result = testCaseManageService.create(1, "input", "output", false);
        
        assertEquals("success", result.get("error_message"));
    }

    // 测试删除测试用例的各种场景
    @Test
    void testDeleteTestCase_NoPermission() {
        setAuthenticationUser(normalUser);
        
        Map<String, String> result = testCaseManageService.delete(1);
        
        assertEquals("Permission denied", result.get("error_message"));
    }

    @Test
    void testDeleteTestCase_NoSuchTestCase() {
        setAuthenticationUser(teacherUser);
        when(testCaseMapper.selectList(any())).thenReturn(Collections.emptyList());
        
        Map<String, String> result = testCaseManageService.delete(1);
        
        assertEquals("No such test case", result.get("error_message"));
    }

    @Test
    void testDeleteTestCase_NotAuthorAndNotAdmin() {
        setAuthenticationUser(teacherUser);
        when(testCaseMapper.selectList(any())).thenReturn(Collections.singletonList(testCase));
        when(programmingMapper.selectOne(any())).thenReturn(programming);
        programming.setPAuthorId(3); // 设置不同的作者ID
        
        Map<String, String> result = testCaseManageService.delete(1);
        
        assertEquals("Teacher cannot delete others' test case", result.get("error_message"));
    }

    @Test
    void testDeleteTestCase_Success() {
        setAuthenticationUser(teacherUser);
        when(testCaseMapper.selectList(any())).thenReturn(Collections.singletonList(testCase));
        when(programmingMapper.selectOne(any())).thenReturn(programming);
        
        Map<String, String> result = testCaseManageService.delete(1);
        
        assertEquals("success", result.get("error_message"));
    }

    // 测试获取测试用例列表的各种场景
    @Test
    void testGetByProgrammingId_NoPermission() {
        setAuthenticationUser(normalUser);
        
        List<Map<String, String>> result = testCaseManageService.getByProgrammingId(1);
        
        assertEquals("Permission denied", result.get(0).get("error_message"));
    }

    @Test
    void testGetByProgrammingId_Success() {
        setAuthenticationUser(teacherUser);
        when(testCaseMapper.selectList(any())).thenReturn(Collections.singletonList(testCase));
        
        List<Map<String, String>> result = testCaseManageService.getByProgrammingId(1);
        
        assertFalse(result.isEmpty());
        assertEquals("1", result.get(0).get("test_case_id"));
        assertEquals("1", result.get(0).get("programming_id"));
        assertEquals("test input", result.get(0).get("tc_input"));
        assertEquals("test output", result.get(0).get("tc_output"));
    }
}