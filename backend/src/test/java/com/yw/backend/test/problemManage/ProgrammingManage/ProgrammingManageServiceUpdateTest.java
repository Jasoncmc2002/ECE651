package com.yw.backend.test.problemManage.ProgrammingManage;

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
import org.springframework.security.core.context.SecurityContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProgrammingManageServiceUpdateTest {

    @Mock
    private ProgrammingMapper programmingMapper;

    @InjectMocks
    private ProgrammingManageServiceImpl programmingManageService;

    private User mockUser;
    private UserDetailsImpl mockUserDetails;
    private Programming mockProgramming;

    //a valid "programming problem"
    private final int validProgrammingId = 1;
    private final String validDescription = "Valid description";
    private final int validTotalScore = 100;
    private final int validTimeLimit = 1000;
    private final int validCodeSizeLimit = 64;
    private final String validTag = "算法";
    private final String validTitle = "Valid Title";
    private final String validJudgeCode = "valid judge code";
    private final int validDifficulty = 3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        //set a user with default permission 0
        mockUser = new User(1, "testUser", "password", "Test User", 0, "photo.jpg");
        mockUserDetails = new UserDetailsImpl(mockUser);

        //set a mock programming problem
        mockProgramming = new Programming();
        mockProgramming.setProgrammingId(validProgrammingId);
        mockProgramming.setPAuthorId(2);
        mockProgramming.setPDescription(validDescription);
        mockProgramming.setPTotalScore(validTotalScore);
        mockProgramming.setTimeLimit(validTimeLimit);
        mockProgramming.setCodeSizeLimit(validCodeSizeLimit);
        mockProgramming.setPTag(validTag);
        mockProgramming.setPTitle(validTitle);
        mockProgramming.setPJudgeCode(validJudgeCode);
        mockProgramming.setPDifficulty(validDifficulty);

        //token
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mockUserDetails, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void update_NoPermission() {
        //if permission = 0
        Map<String, String> response = programmingManageService.update(
                validProgrammingId, validDescription, validTotalScore, validTimeLimit,
                validCodeSizeLimit, validTag, validTitle, validJudgeCode, validDifficulty);

        assertEquals("No permission in updating programming problems", response.get("error_message"));
    }

    @Test
    void update_ProblemNotFound() {
        mockUser.setPermission(1);

        when(programmingMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(new ArrayList<>());

        Map<String, String> response = programmingManageService.update(
                validProgrammingId, validDescription, validTotalScore, validTimeLimit,
                validCodeSizeLimit, validTag, validTitle, validJudgeCode, validDifficulty);

        assertEquals("No programming problem with this ID", response.get("error_message"));
    }

    @Test
    void update_NoPermissionToModifyOthersProblem() {
        mockUser.setPermission(1);

        List<Programming> problemList = new ArrayList<>();
        problemList.add(mockProgramming);
        when(programmingMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(problemList);

        Map<String, String> response = programmingManageService.update(
                validProgrammingId, validDescription, validTotalScore, validTimeLimit,
                validCodeSizeLimit, validTag, validTitle, validJudgeCode, validDifficulty);

        assertEquals("You cannot modify programming problems created by others",
                response.get("error_message"));
    }

    @Test
    void update_EmptyDescription() {
        mockUser.setPermission(2);

        List<Programming> problemList = new ArrayList<>();
        problemList.add(mockProgramming);
        when(programmingMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(problemList);

        Map<String, String> response = programmingManageService.update(
                validProgrammingId, "", validTotalScore, validTimeLimit,
                validCodeSizeLimit, validTag, validTitle, validJudgeCode, validDifficulty);

        assertEquals("Problem Description cannot be empty", response.get("error_message"));
    }

    @Test
    void update_SuccessfulUpdate() {
        mockUser.setPermission(2);

        List<Programming> problemList = new ArrayList<>();
        problemList.add(mockProgramming);
        when(programmingMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(problemList);
        when(programmingMapper.update(any(Programming.class), any(UpdateWrapper.class)))
                .thenReturn(1);

        Map<String, String> response = programmingManageService.update(
                validProgrammingId, validDescription, validTotalScore, validTimeLimit,
                validCodeSizeLimit, validTag, validTitle, validJudgeCode, validDifficulty);

        assertEquals("success", response.get("error_message"));
    }
}