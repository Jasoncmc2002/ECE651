package com.yw.backend.test.problemManage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yw.backend.mapper.ObjectiveProblemMapper;
import com.yw.backend.pojo.ObjectiveProblem;
import com.yw.backend.pojo.User;
import com.yw.backend.service.impl.problemManage.ObjectiveProblemManageServiceImpl;
import com.yw.backend.service.impl.utils.UserDetailsImpl;
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
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ObjectiveProblemManageServiceImplTest {


    @Mock
    private ObjectiveProblemMapper objectiveProblemMapper;

    @InjectMocks
    private ObjectiveProblemManageServiceImpl objectiveProblemManageService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private UsernamePasswordAuthenticationToken authenticationToken;

    @Mock
    private UserDetailsImpl userDetails;

    @Mock
    private User user;

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

    private void setAuthenticationUser(int permission) {
        //        ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).setPermission(0);
        UserDetailsImpl loginUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = loginUser.getUser();

        user.setPermission(permission);

        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

    }


    @Test
    void testCreateObjectiveProblem_Success() {
        // Mock returning empty (not questions in the database)
        when(objectiveProblemMapper.selectList(null)).thenReturn(new ArrayList<>());

        Map<String, String> response = objectiveProblemManageService.create(
                "Sample Question", 5, "A", "time complexity", 3
        );

        assertEquals("success", response.get("error_message"));
        assertNotNull(response.get("objective_problem_id"));

        verify(objectiveProblemMapper, times(1)).insert(any(ObjectiveProblem.class));
    }

    @Test
    void testCreateObjectiveProblem_NoPermission() {
        setAuthenticationUser(0);

        Map<String, String> response = objectiveProblemManageService.create(
                "Sample Question", 5, "A", "time complexity", 3
        );

        assertEquals("no permission to create objective problem", response.get("error_message"));
        verify(objectiveProblemMapper, never()).insert(any());
    }

    @Test
    void testCreateObjectiveProblem_EmptyDescription() {
        Map<String, String> response = objectiveProblemManageService.create(
                "", 5, "A", "time complexity", 3
        );

        assertEquals("the question description cannot be empty", response.get("error_message"));
    }

    @Test
    void testDeleteObjectiveProblem_Success() {
        // simulate questions in the database
        ObjectiveProblem mockProblem = new ObjectiveProblem();
        mockProblem.setObjectiveProblemId(1);
        mockProblem.setAuthorId(1);

        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(mockProblem));

        Map<String, String> response = objectiveProblemManageService.delete(1);

        assertEquals("success", response.get("error_message"));
        verify(objectiveProblemMapper, times(1)).delete(any(QueryWrapper.class));
    }

    @Test
    void testDeleteObjectiveProblem_NoPermission() {
//        ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).setPermission(0);
        setAuthenticationUser(0);

        Map<String, String> response = objectiveProblemManageService.delete(1);

        assertEquals("no permission to delete objective problems", response.get("error_message"));
        verify(objectiveProblemMapper, never()).delete(any());
    }

    @Test
    void testDeleteObjectiveProblem_NotFound() {
        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        Map<String, String> response = objectiveProblemManageService.delete(1);

        assertEquals("no objective problem found via ID query", response.get("error_message"));
    }

    @Test
    void testDeleteObjectiveProblem_NotAuthor() {
        // simulate data with questions but the author not the current user
        ObjectiveProblem mockProblem = new ObjectiveProblem();
        mockProblem.setObjectiveProblemId(1);
        mockProblem.setAuthorId(999); // created by another user

        when(objectiveProblemMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(mockProblem));

        Map<String, String> response = objectiveProblemManageService.delete(1);

        assertEquals("teachers cannot delete questions created by others", response.get("error_message"));
        verify(objectiveProblemMapper, never()).delete(any());
    }
    @Test
    public void testUpdate_NoPermission() {
        when(user.getPermission()).thenReturn(0);

        Map<String, String> response = objectiveProblemManageService.update(1, "description", 10, "answer", "tag", 3);

        assertEquals("Do not have permission to modify problems", response.get("error_message"));
    }

    @Test
    public void testUpdate_AnswerTooLong() {
        when(user.getPermission()).thenReturn(2);
        ObjectiveProblem objectiveProblem = new ObjectiveProblem();
        objectiveProblem.setObjectiveProblemId(1);
        objectiveProblem.setAuthorId(1);
        when(objectiveProblemMapper.selectList(any())).thenReturn(Collections.singletonList(objectiveProblem));

        StringBuilder longTagBuilder = new StringBuilder();
        for (int i = 0; i < 1025; i++) {
            longTagBuilder.append("a");
        }
        String longAnswer = longTagBuilder.toString();
        Map<String, String> response = objectiveProblemManageService.update(1, "description", 10, longAnswer, "tag", 3);

        assertEquals("Standard answer cannot exceed 1024 characters", response.get("error_message"));
    }

    @Test
    public void testUpdate_TagEmpty() {
        when(user.getPermission()).thenReturn(2);
        ObjectiveProblem objectiveProblem = new ObjectiveProblem();
        objectiveProblem.setObjectiveProblemId(1);
        objectiveProblem.setAuthorId(1);
        when(objectiveProblemMapper.selectList(any())).thenReturn(Collections.singletonList(objectiveProblem));

        Map<String, String> response = objectiveProblemManageService.update(1, "description", 10, "answer", "", 3);

        assertEquals("Label cannot be empty", response.get("error_message"));
    }

    @Test
    public void testUpdate_TagTooLong() {
        when(user.getPermission()).thenReturn(2);
        ObjectiveProblem objectiveProblem = new ObjectiveProblem();
        objectiveProblem.setObjectiveProblemId(1);
        objectiveProblem.setAuthorId(1);
        when(objectiveProblemMapper.selectList(any())).thenReturn(Collections.singletonList(objectiveProblem));

        StringBuilder longTagBuilder = new StringBuilder();
        for (int i = 0; i < 1025; i++) {
            longTagBuilder.append("a");
        }
        String longTag = longTagBuilder.toString();
        Map<String, String> response = objectiveProblemManageService.update(1, "description", 10, "answer", longTag, 3);

        assertEquals("Label cannot exceed 100 characters", response.get("error_message"));
    }

    @Test
    public void testUpdate_DifficultyOutOfRange() {
        when(user.getPermission()).thenReturn(2);
        ObjectiveProblem objectiveProblem = new ObjectiveProblem();
        objectiveProblem.setObjectiveProblemId(1);
        objectiveProblem.setAuthorId(1);
        when(objectiveProblemMapper.selectList(any())).thenReturn(Collections.singletonList(objectiveProblem));

        Map<String, String> response = objectiveProblemManageService.update(1, "description", 10, "answer", "tag", 6);

        assertEquals("Difficulty must be a positive integer between 1 and 5", response.get("error_message"));
    }

    @Test
    public void testUpdate_ObjectiveProblemNotExist() {
        when(user.getPermission()).thenReturn(1);
        when(objectiveProblemMapper.selectList(any())).thenReturn(Collections.emptyList());

        Map<String, String> response = objectiveProblemManageService.update(1, "description", 10, "answer", "tag", 3);

        assertEquals("Objective problem does not exist", response.get("error_message"));
    }

    @Test
    public void testUpdate_Success() {
        when(user.getPermission()).thenReturn(2);
        ObjectiveProblem objectiveProblem = new ObjectiveProblem();
        objectiveProblem.setObjectiveProblemId(1);
        objectiveProblem.setAuthorId(1);
        when(objectiveProblemMapper.selectList(any())).thenReturn(Collections.singletonList(objectiveProblem));

        Map<String, String> response = objectiveProblemManageService.update(1, "description", 10, "answer", "tag", 3);

        assertEquals("success", response.get("error_message"));
        verify(objectiveProblemMapper, times(1)).update(any(), any());
    }
}