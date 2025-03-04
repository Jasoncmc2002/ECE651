package com.yw.backend.test.setManage;
import com.yw.backend.mapper.ProblemSetMapper;
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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class SetManageServiceUpdateTest {

    @Mock
    private ProblemSetMapper problemSetMapper;

    @InjectMocks
    private SetManageServiceImpl setManageService;

    private User mockTeacher;
    private User mockAdmin;
    private UserDetailsImpl mockTeacherDetails;
    private UserDetailsImpl mockAdminDetails;
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

        // Setup mock admin (permission level 2)
        mockAdmin = new User(2, "admin", "password", "李四(admin)", 2, "photo.jpg");
        mockAdminDetails = new UserDetailsImpl(mockAdmin);

        // Setup mock problem set
        mockProblemSet = new ProblemSet();
        mockProblemSet.setProblemSetId(1);
        mockProblemSet.setPsName("Original Problem Set");
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

    private void setupProblemSetQuery(ProblemSet problemSet) {
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(problemSet);
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(problemSetList);
    }

    @Test
    void update_NoPermission() {
        // Setup user with no permission (student)
        User mockStudent = new User(3, "student", "password", "Student", 0, "photo.jpg");
        UserDetailsImpl mockStudentDetails = new UserDetailsImpl(mockStudent);
        setupAuthentication(mockStudentDetails);

        Map<String, String> response = setManageService.update(
                1, "New Name", defaultStartTime, defaultEndTime, 60);

        assertEquals(1, response.size());
        assertEquals("No permission in updating the problem set", response.get("error_message"));
        verify(problemSetMapper, never()).update(any(), any());
    }

    @Test
    void update_NonExistentProblemSet() {
        // Setup teacher authentication
        setupAuthentication(mockTeacherDetails);

        // Configure mapper to return empty list for non-existent problem set
        when(problemSetMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(new ArrayList<>());

        Map<String, String> response = setManageService.update(
                999, "New Name", defaultStartTime, defaultEndTime, 60);

        assertEquals(1, response.size());
        assertEquals("No problem set with this ID", response.get("error_message"));
        verify(problemSetMapper, never()).update(any(), any());
    }

    @Test
    void update_TeacherCannotModifyOthersProblemSet() {
        // Setup teacher authentication
        setupAuthentication(mockTeacherDetails);

        // Setup problem set created by someone else
        ProblemSet othersProblemSet = new ProblemSet();
        othersProblemSet.setProblemSetId(2);
        othersProblemSet.setPsName("Others Problem Set");
        othersProblemSet.setPsAuthorId(4); // Created by another teacher
        othersProblemSet.setPsStartTime(defaultStartTime);
        othersProblemSet.setPsEndTime(defaultEndTime);
        othersProblemSet.setDuration(60);

        setupProblemSetQuery(othersProblemSet);

        Map<String, String> response = setManageService.update(
                2, "New Name", defaultStartTime, defaultEndTime, 60);

        assertEquals(1, response.size());
        assertEquals("You cannot modify problem sets created by others", response.get("error_message"));
        verify(problemSetMapper, never()).update(any(), any());
    }

    @Test
    void update_EmptyProblemSetName() {
        // Setup teacher authentication
        setupAuthentication(mockTeacherDetails);
        setupProblemSetQuery(mockProblemSet);

        Map<String, String> response = setManageService.update(
                1, "", defaultStartTime, defaultEndTime, 60);

        assertEquals(1, response.size());
        assertEquals("Problem set name cannot be empty", response.get("error_message"));
        verify(problemSetMapper, never()).update(any(), any());
    }

    @Test
    void update_TooLongProblemSetName() {
        // Setup teacher authentication
        setupAuthentication(mockTeacherDetails);
        setupProblemSetQuery(mockProblemSet);

        // Create a string longer than 100 characters
        String longName = new String(new char[101]).replace("\0", "A");

        Map<String, String> response = setManageService.update(
                1, longName, defaultStartTime, defaultEndTime, 60);

        assertEquals(1, response.size());
        assertEquals("Problem set name cannot exceed 100 characters", response.get("error_message"));
        verify(problemSetMapper, never()).update(any(), any());
    }

    @Test
    void update_InvalidTimeRange() {
        // Setup teacher authentication
        setupAuthentication(mockTeacherDetails);
        setupProblemSetQuery(mockProblemSet);

        // Set end time before start time
        LocalDateTime invalidEndTime = defaultStartTime.minusHours(1);

        Map<String, String> response = setManageService.update(
                1, "New Name", defaultStartTime, invalidEndTime, 60);

        assertEquals(1, response.size());
        assertEquals("Erroneous problem set start or finish time", response.get("error_message"));
        verify(problemSetMapper, never()).update(any(), any());
    }

    @Test
    void update_NegativeDuration() {
        // Setup teacher authentication
        setupAuthentication(mockTeacherDetails);
        setupProblemSetQuery(mockProblemSet);

        Map<String, String> response = setManageService.update(
                1, "New Name", defaultStartTime, defaultEndTime, -10);

        assertEquals(1, response.size());
        assertEquals("Test time cannot be negative", response.get("error_message"));
        verify(problemSetMapper, never()).update(any(), any());
    }

    @Test
    void update_DurationExceedsTimeRange() {
        // Setup teacher authentication
        setupAuthentication(mockTeacherDetails);
        setupProblemSetQuery(mockProblemSet);

        // Duration (180 minutes) exceeds time range (120 minutes)
        int excessiveDuration = 180;

        Map<String, String> response = setManageService.update(
                1, "New Name", defaultStartTime, defaultEndTime, excessiveDuration);

        assertEquals(1, response.size());
        assertEquals("Test time exceeds problem set time", response.get("error_message"));
        verify(problemSetMapper, never()).update(any(), any());
    }

    @Test
    void update_TeacherSuccessfulUpdate() {
        // Setup teacher authentication
        setupAuthentication(mockTeacherDetails);
        setupProblemSetQuery(mockProblemSet);

        String newName = "Updated Problem Set";
        LocalDateTime newStartTime = defaultStartTime.plusHours(1);
        LocalDateTime newEndTime = defaultEndTime.plusHours(1);
        int newDuration = 90;

        Map<String, String> response = setManageService.update(
                1, newName, newStartTime, newEndTime, newDuration);

        assertEquals(1, response.size());
        assertEquals("success", response.get("error_message"));

        // Verify that update was called with correct parameters
        verify(problemSetMapper, times(1)).update(any(ProblemSet.class), any(UpdateWrapper.class));
    }

    @Test
    void update_AdminCanUpdateOthersProblemSet() {
        // Setup admin authentication
        setupAuthentication(mockAdminDetails);
        setupProblemSetQuery(mockProblemSet);

        String newName = "Admin Updated Problem Set";

        Map<String, String> response = setManageService.update(
                1, newName, defaultStartTime, defaultEndTime, 60);

        assertEquals(1, response.size());
        assertEquals("success", response.get("error_message"));

        // Verify that update was called with correct parameters
        verify(problemSetMapper, times(1)).update(any(ProblemSet.class), any(UpdateWrapper.class));
    }
}
