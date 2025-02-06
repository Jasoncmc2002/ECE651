package com.yw.backend.test.problemManage;

import com.yw.backend.mapper.ObjectiveProblemMapper;
import com.yw.backend.pojo.ObjectiveProblem;
import com.yw.backend.pojo.User;
import com.yw.backend.service.impl.problemManage.ObjectiveProblemManageServiceImpl;
import com.yw.backend.service.impl.utils.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
    public void setUp() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authenticationToken);
        when(authenticationToken.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUser()).thenReturn(user);
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