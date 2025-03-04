package com.yw.backend.test.setManage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yw.backend.mapper.ProblemSetMapper;
import com.yw.backend.mapper.UserMapper;
import com.yw.backend.pojo.ProblemSet;
import com.yw.backend.pojo.User;
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
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class SetManageServiceGetOneTest {
    @Mock
    private ProblemSetMapper problemSetMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private SetManageServiceImpl setManageService;

    private User mockTeacher;
    private User mockStudent;
    private UserDetailsImpl mockTeacherDetails;
    private UserDetailsImpl mockStudentDetails;
    private ProblemSet mockProblemSet;
    private LocalDateTime defaultStartTime;
    private LocalDateTime defaultEndTime;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup common test times
        defaultStartTime = LocalDateTime.of(2023, 5, 1, 10, 0);
        defaultEndTime = LocalDateTime.of(2023, 5, 1, 12, 0);

        // Setup mock teacher (permission level 1)
        mockTeacher = new User(1, "teacher", "password", "张三(teacher)", 1, "photo.jpg");
        mockTeacherDetails = new UserDetailsImpl(mockTeacher);

        // Setup mock student (permission level 0)
        mockStudent = new User(3, "student", "password", "王五(student)", 0, "photo.jpg");
        mockStudentDetails = new UserDetailsImpl(mockStudent);

        // Setup mock problem set
        mockProblemSet = new ProblemSet();
        mockProblemSet.setProblemSetId(1);
        mockProblemSet.setPsName("Test Problem Set");
        mockProblemSet.setPsAuthorId(1); // Created by teacher
        mockProblemSet.setPsStartTime(defaultStartTime);
        mockProblemSet.setPsEndTime(defaultEndTime);
        mockProblemSet.setDuration(60); // 60 minutes
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setupAuthentication(UserDetailsImpl userDetails) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void getOne_NoPermission() {
        // Setup student authentication (no permission)
        setupAuthentication(mockStudentDetails);

        Map<String, String> response = setManageService.getOne(1);

        assertEquals(1, response.size());
        assertEquals("No permission to obtain the problem set", response.get("error_message"));
        verify(problemSetMapper, never()).selectList(any(QueryWrapper.class));
    }

    @Test
    void getOne_NonExistentProblemSet() {
        // Setup teacher authentication
        setupAuthentication(mockTeacherDetails);

        // Configure mapper to return empty list for non-existent problem set
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(new ArrayList<>());

        Map<String, String> response = setManageService.getOne(999);

        assertEquals(1, response.size());
        assertEquals("No problem set with this ID", response.get("error_message"));
        verify(userMapper, never()).selectOne(any(QueryWrapper.class));
    }

    @Test
    void getOne_SuccessfulRetrieval() {
        // Setup teacher authentication
        setupAuthentication(mockTeacherDetails);

        // Setup problem set query result
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(problemSetList);

        // Setup author query result
        when(userMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(mockTeacher);

        Map<String, String> response = setManageService.getOne(1);

        // Verify all fields in response
        assertEquals("success", response.get("error_message"));
        assertEquals("1", response.get("problem_set_id"));
        assertEquals("Test Problem Set", response.get("ps_name"));
        assertEquals("1", response.get("ps_author_id"));
        assertEquals("张三(teacher)", response.get("ps_author_name"));
        assertEquals(defaultStartTime.toString(), response.get("ps_start_time"));
        assertEquals(defaultEndTime.toString(), response.get("ps_end_time"));
        assertEquals("60", response.get("duration"));

        // Verify the sequence of operations
        verify(problemSetMapper, times(1)).selectList(any(QueryWrapper.class));
        verify(userMapper, times(1)).selectOne(any(QueryWrapper.class));
    }
}
