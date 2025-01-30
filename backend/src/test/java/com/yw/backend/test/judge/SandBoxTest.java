package com.yw.backend.test.judge;

import com.yw.backend.service.impl.judge.Sandbox;
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


}



