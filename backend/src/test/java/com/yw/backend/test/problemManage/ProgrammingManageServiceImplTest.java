package com.yw.backend.test.problemManage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yw.backend.mapper.ProgrammingMapper;
import com.yw.backend.pojo.Programming;
import com.yw.backend.pojo.User;
import com.yw.backend.service.impl.problemManage.ProgrammingManageServiceImpl;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ProgrammingManageServiceImplTest {

    @Mock
    private ProgrammingMapper programmingMapper;

    @InjectMocks
    private ProgrammingManageServiceImpl programmingManageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
