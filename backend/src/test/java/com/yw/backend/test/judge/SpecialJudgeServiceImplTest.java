package com.yw.backend.test.judge;

import com.yw.backend.pojo.User;
import com.yw.backend.service.impl.utils.UserDetailsImpl;
import com.yw.backend.service.judge.SpecialJudgeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

//Test for SpecialJudgeServiceImplTest
@SpringBootTest
public class SpecialJudgeServiceImplTest {

    @Autowired
    private SpecialJudgeService specialJudgeService;

    @Mock
    private User mockUser;
    private UserDetailsImpl mockUserDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        //Create user
        mockUser = new User(1, "testUser", "password123", "Test User", 0, "photo.jpg");
        mockUserDetails = new UserDetailsImpl(mockUser);
    }

    private void setAuthenticationUser(int permission) {
        // use to change user permission level
        mockUser.setPermission(permission);
        mockUserDetails = new UserDetailsImpl(mockUser);

        // authentication
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mockUserDetails, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void testNoPermission() {
        setAuthenticationUser(0);  //permission = 0

        Map<String, String> result = specialJudgeService.specialJudge(
                "print('test')", "", 1000);

        assertEquals("No permission to use program debugging tools", result.get("error_message"));
    }

    @Test
    void testNormalExecution() {
        setAuthenticationUser(1);  //permission = 1

        Map<String, String> result = specialJudgeService.specialJudge(
                "print('Hello, World!')", "", 1000);

        assertEquals("success", result.get("error_message"));
        assertEquals("Hello, World!\n", result.get("test_output"));
    }

    @Test
    void testTimeoutExecution() {
        setAuthenticationUser(1);

        Map<String, String> result = specialJudgeService.specialJudge(
                "while True: pass", "", 100);  // 100ms timeout

        assertEquals("success", result.get("error_message"));
        assertEquals("Code Execution Timeout", result.get("test_output"));
    }

    @Test
    void testRuntimeError() {
        setAuthenticationUser(1);

        Map<String, String> result = specialJudgeService.specialJudge(
                "print(1/0)", "", 1000);

        assertEquals("success", result.get("error_message"));
        assertTrue(result.get("test_output").startsWith("Error during code execution: "));
    }

    @Test
    void testWithInput() {
        setAuthenticationUser(1);

        String code = "a = int(input())\nb = int(input())\nprint(a + b)";
        String input = "2\n3\n";

        Map<String, String> result = specialJudgeService.specialJudge(
                code, input, 1000);

        assertEquals("success", result.get("error_message"));
        assertEquals("5\n", result.get("test_output"));
    }

    @Test
    void testLongOutput() {
        setAuthenticationUser(1);

        String code = "for i in range(100): print(i)";

        Map<String, String> result = specialJudgeService.specialJudge(
                code, "", 1000);

        assertEquals("success", result.get("error_message"));
        assertEquals(100, result.get("test_output").split("\n").length);
    }

    @Test
    void testComplexComputation() {
        setAuthenticationUser(1);

        // a more complicated calculation
        String code = "def fibonacci(n):\n" +
                "    if n <= 1:\n" +
                "        return n\n" +
                "    return fibonacci(n-1) + fibonacci(n-2)\n" +
                "\n" +
                "print(fibonacci(10))";

        Map<String, String> result = specialJudgeService.specialJudge(
                code, "", 2000);  // More time

        assertEquals("success", result.get("error_message"));
        assertEquals("55\n", result.get("test_output"));
    }
}