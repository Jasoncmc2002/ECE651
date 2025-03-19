package com.yw.backend.test.judge;

import com.yw.backend.service.impl.judge.Sandbox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.python.core.PyException;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class SandBoxTest {
    @Test
    public void testSandBoxRunSuccess() throws InterruptedException {
        String code = "print('Hello, World!')";
        String testIn = "";
        int excTime = 1000;

        Sandbox sandbox = new Sandbox(code, testIn, excTime);
        sandbox.run();

        assertTrue(sandbox.isEndedInTime());
        assertTrue(sandbox.isEndedNormally());
        assertEquals("Hello, World!\n", sandbox.getTestOut());
    }


    @Test
    public void testSandBoxRunException() throws InterruptedException {
        String code = "raise Exception('Test Exception')";
        String testIn = "";
        int excTime = 1000;
        Sandbox sandbox = new Sandbox(code, testIn, excTime);
        sandbox.run();
        assertTrue(sandbox.isEndedInTime());
        assertFalse(sandbox.isEndedNormally());
        assertTrue(sandbox.getTestOut().contains("Test Exception"));
    }

    @Test
    public void testSandBoxRunTimeout() throws InterruptedException {
        String code = "import time\ntime.sleep(2)";
        String testIn = "";
        int excTime = 1000;

        Sandbox sandbox = new Sandbox(code, testIn, excTime);
        sandbox.run();

        assertFalse(sandbox.isEndedInTime());
        assertTrue(sandbox.isEndedNormally());
    }

    private Sandbox sandbox;
    @BeforeEach
    void setUp() {
        sandbox = new Sandbox("", "", 1000);
    }


    @Test
    void testCodeThrowsPyException() throws InterruptedException {
        // Mock the case where a Python exception is thrown
        sandbox.setCode("raise Exception('Error')");
        sandbox.run();
        assertFalse(sandbox.isEndedNormally());
        assertTrue(sandbox.getTestOut().contains("Error"));
    }

    @Test
    void testCodeReturnsOutput() throws InterruptedException {
        // Mock the case where the code produces output
        sandbox.setCode("print('Hello, World!')");
        sandbox.run();
        assertEquals("Hello, World!", sandbox.getTestOut().trim());
    }

    @Test
    void testCodeHandlesEmptyInput() throws InterruptedException {
        // Mock the case where input is empty
        sandbox.setTestIn("");
        sandbox.run();
        assertTrue(sandbox.isEndedInTime());
    }

    @Test
    void testCodeWithLargeInput() throws InterruptedException {
        // Mock the case where input is very large
        StringBuilder largeInput = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeInput.append("data ");
        }
        sandbox.setTestIn(largeInput.toString());
        sandbox.run();
        assertTrue(sandbox.isEndedInTime());
    }

    @Test
    void testCodeWithComplexLogic() throws InterruptedException {
        // Mock the case where code has a complex logic path
        sandbox.setCode("for i in range(10):\n" +
                "    if i % 2 == 0:\n" +
                "        print(i)");
        sandbox.run();
        assertTrue(sandbox.isEndedInTime());
        assertTrue(sandbox.getTestOut().contains("0"));
        assertTrue(sandbox.getTestOut().contains("2"));
    }

    @Test
    void testCodeWithMultipleBranches() throws InterruptedException {
        // Mock a case with multiple branches
        sandbox.setCode("x = 5\n" +
                "if x > 10:\n" +
                "    print('greater')\n" +
                "else:\n" +
                "    print('lesser')");
        sandbox.run();
        assertEquals("lesser", sandbox.getTestOut().trim());
    }

    @Test
    void testCodeWithLongExecutionTime() throws InterruptedException {
        // Mock the case where code takes a long time to execute
        sandbox.setExcTime(5000); // Long execution time (5 seconds)
        sandbox.run();
        assertTrue(sandbox.isEndedInTime());
    }

    @Test
    void testCodeWithEdgeCaseInput() throws InterruptedException {
        // Mock an edge case input
        sandbox.setTestIn("0"); // Edge case input
        sandbox.run();
        assertTrue(sandbox.isEndedInTime());
    }

    @Test
    void testCodeWithInvalidSyntax() throws InterruptedException {
        // Mock the case where the code has invalid syntax
        sandbox.setCode("if x = 5"); // Invalid syntax
        sandbox.run();
        assertFalse(sandbox.isEndedNormally());
        assertTrue(sandbox.getTestOut().contains("SyntaxError"));
    }


}



