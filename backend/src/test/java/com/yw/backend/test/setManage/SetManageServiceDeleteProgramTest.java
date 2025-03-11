package com.yw.backend.test.setManage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yw.backend.mapper.PNPsMapper;
import com.yw.backend.mapper.ProblemSetMapper;
import com.yw.backend.mapper.ProgrammingAnswerMapper;
import com.yw.backend.mapper.ProgrammingMapper;
import com.yw.backend.pojo.ProblemSet;
import com.yw.backend.pojo.Programming;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class SetManageServiceDeleteProgramTest {
    @Mock
    private ProblemSetMapper problemSetMapper;

    @Mock
    private ProgrammingMapper programmingMapper;

    @Mock
    private PNPsMapper pnPsMapper;

    @Mock
    private ProgrammingAnswerMapper programmingAnswerMapper;

    @InjectMocks
    private SetManageServiceImpl setManageService;

    private User mockTeacher;
    private User mockAdmin;
    private User mockOtherTeacher;
    private User mockStudent;
    private UserDetailsImpl mockTeacherDetails;
    private UserDetailsImpl mockAdminDetails;
    private UserDetailsImpl mockOtherTeacherDetails;
    private UserDetailsImpl mockStudentDetails;
    private ProblemSet mockProblemSet;
    private ProblemSet mockOtherProblemSet;
    private Programming mockProgramming;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup mock users with different permission levels
        mockTeacher = new User(1, "teacher", "password", "张三(teacher)", 1, "photo.jpg");
        mockTeacherDetails = new UserDetailsImpl(mockTeacher);

        mockAdmin = new User(2, "admin", "password", "李四(admin)", 2, "photo.jpg");
        mockAdminDetails = new UserDetailsImpl(mockAdmin);

        mockOtherTeacher = new User(3, "other_teacher", "password", "王五(other_teacher)", 1, "photo.jpg");
        mockOtherTeacherDetails = new UserDetailsImpl(mockOtherTeacher);

        mockStudent = new User(4, "student", "password", "赵六(student)", 0, "photo.jpg");
        mockStudentDetails = new UserDetailsImpl(mockStudent);

        // Setup mock problem sets
        mockProblemSet = new ProblemSet();
        mockProblemSet.setProblemSetId(1);
        mockProblemSet.setPsName("Test Problem Set");
        mockProblemSet.setPsAuthorId(1); // Created by teacher (user_id = 1)

        mockOtherProblemSet = new ProblemSet();
        mockOtherProblemSet.setProblemSetId(2);
        mockOtherProblemSet.setPsName("Other Problem Set");
        mockOtherProblemSet.setPsAuthorId(3); // Created by other teacher (user_id = 3)

        // Setup mock programming problem
        mockProgramming = new Programming();
        mockProgramming.setProgrammingId(1);
        mockProgramming.setPTitle("L1-010 比较大小");
        mockProgramming.setPTag("函数");
        mockProgramming.setPDifficulty(5);
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
    void deleteProgramming_NoPermission() {
        // Setup student authentication (no permission)
        setupAuthentication(mockStudentDetails);

        Map<String, String> response = setManageService.deleteProgramming(1, 1);

        assertEquals(1, response.size());
        assertEquals("No permission to delete problems from problem set",
                response.get("error_message"));
    }

    @Test
    void deleteProgramming_NonExistentProblemSet() {
        // Setup teacher authentication
        setupAuthentication(mockTeacherDetails);

        // Configure mapper to return empty list for non-existent problem set
        doReturn(new ArrayList<>()).when(problemSetMapper).selectList(any());

        Map<String, String> response = setManageService.deleteProgramming(999, 1);

        assertEquals(1, response.size());
        assertEquals("No problem set with this ID",
                response.get("error_message"));
    }

    @Test
    void deleteProgramming_NonExistentProgramming() {
        // Setup teacher authentication
        setupAuthentication(mockTeacherDetails);

        // Configure problem set query result
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        doReturn(problemSetList).when(problemSetMapper).selectList(any());

        // Configure mapper to return empty list for non-existent programming problem
        doReturn(new ArrayList<>()).when(programmingMapper).selectList(any());

        Map<String, String> response = setManageService.deleteProgramming(1, 999);

        assertEquals(1, response.size());
        assertEquals("No problem with this ID",
                response.get("error_message"));
    }

    @Test
    void deleteProgramming_TeacherCannotDeleteFromOthersProblemSet() {
        // Setup teacher authentication
        setupAuthentication(mockTeacherDetails);

        // Configure problem set query result (other teacher's problem set)
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockOtherProblemSet);
        doReturn(problemSetList).when(problemSetMapper).selectList(any());

        // Configure programming problem query result
        List<Programming> programmingList = new ArrayList<>();
        programmingList.add(mockProgramming);
        doReturn(programmingList).when(programmingMapper).selectList(any());

        Map<String, String> response = setManageService.deleteProgramming(2, 1);

        assertEquals(1, response.size());
        assertEquals("No permission to delete problems from problem set created by others",
                response.get("error_message"));
    }

    @Test
    void deleteProgramming_ProblemAlreadyDeleted() {
        // Setup teacher authentication
        setupAuthentication(mockTeacherDetails);

        // Configure problem set query result
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        doReturn(problemSetList).when(problemSetMapper).selectList(any());

        // Configure programming problem query result
        List<Programming> programmingList = new ArrayList<>();
        programmingList.add(mockProgramming);
        doReturn(programmingList).when(programmingMapper).selectList(any());

        // Problem is already deleted (count = 0)
        doReturn(0L).when(pnPsMapper).selectCount(any());

        Map<String, String> response = setManageService.deleteProgramming(1, 1);

        assertEquals(1, response.size());
        assertEquals("Problem deleted from problem set",
                response.get("error_message"));
    }

    @Test
    void deleteProgramming_SuccessfulDeletion() {
        // Setup teacher authentication
        setupAuthentication(mockTeacherDetails);

        // Configure problem set query result
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockProblemSet);
        doReturn(problemSetList).when(problemSetMapper).selectList(any());

        // Configure programming problem query result
        List<Programming> programmingList = new ArrayList<>();
        programmingList.add(mockProgramming);
        doReturn(programmingList).when(programmingMapper).selectList(any());

        // Problem exists in problem set (count = 1)
        doReturn(1L).when(pnPsMapper).selectCount(any());

        // Mock delete operations
        doReturn(1).when(pnPsMapper).delete(any());
        doReturn(2).when(programmingAnswerMapper).delete(any());

        Map<String, String> response = setManageService.deleteProgramming(1, 1);

        assertEquals(1, response.size());
        assertEquals("success", response.get("error_message"));
    }

    @Test
    void deleteProgramming_AdminCanDeleteFromOthersProblemSet() {
        // Setup admin authentication
        setupAuthentication(mockAdminDetails);

        // Configure problem set query result (other teacher's problem set)
        List<ProblemSet> problemSetList = new ArrayList<>();
        problemSetList.add(mockOtherProblemSet);
        doReturn(problemSetList).when(problemSetMapper).selectList(any());

        // Configure programming problem query result
        List<Programming> programmingList = new ArrayList<>();
        programmingList.add(mockProgramming);
        doReturn(programmingList).when(programmingMapper).selectList(any());

        // Problem exists in problem set (count = 1)
        doReturn(1L).when(pnPsMapper).selectCount(any());

        // Mock delete operations
        doReturn(1).when(pnPsMapper).delete(any());
        doReturn(2).when(programmingAnswerMapper).delete(any());

        Map<String, String> response = setManageService.deleteProgramming(2, 1);

        assertEquals(1, response.size());
        assertEquals("success", response.get("error_message"));
    }
}
