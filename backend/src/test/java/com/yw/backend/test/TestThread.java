package com.yw.backend.test;

import org.python.core.PyException;

class Worker extends Thread {
    private String code;
    private String testIn;
    private String testOut;
    private PyException pyException;
    private boolean isEndedCorrectly = false;

    public void setCode(String code) {
        this.code = code;
    }

    public void setTestIn(String testIn) {
        this.testIn = testIn;
    }

    public boolean isEndedCorrectly() {
        return isEndedCorrectly;
    }

    public String getTestOut() {
        return testOut;
    }

    public PyException getPyException() {
        return pyException;
    }

    @Override
    public void run() {
//        super.run();  // 不用，自己写逻辑
//        try(PythonInterpreter pythonInterpreter = new PythonInterpreter()) {
//            StringReader stringReader = new StringReader("1 2");
//            StringWriter stringWriter = new StringWriter();
//            pythonInterpreter.setIn(stringReader);
//            pythonInterpreter.setOut(stringWriter);
//            pythonInterpreter.exec("a, b = map(int, raw_input('').split())\n" +
//                    "print(a + b)");
//            System.out.println(stringWriter);
//        } catch (PyException pyException) {
//            System.out.println(pyException);
//            this.pyException = pyException;
//        }
        for (int i = 0; i < 10; i++) {
            int s = 0;
//            for (int j = 0; j < 1000; j++)
//                for (int k = 0; k < 1000; k++)
//                    for (int l = 0; l < 1000; l++) {
//                        s = (s + j * k * l);
//                    }
            System.out.println("Thread: " + this.getName() + "i = " + i);
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
        }
        this.isEndedCorrectly = true;
    }
}

public class TestThread {

    public static void useStop(String[] args) throws InterruptedException {
        Worker worker = new Worker();
        worker.setName("Worker 1");
        worker.start();
        Thread.sleep(200);
        worker.stop();
        if (worker.isEndedCorrectly()) {
            System.out.println("Worker Ended Correctly");
        } else {
            System.out.println("Worker Timeout");
        }
    }

    public static void useDaemon(String[] args) throws InterruptedException {
        Worker worker = new Worker();
        worker.setName("Worker 1");
        worker.setDaemon(true);
        worker.start();
//        worker.join(2000);  // 不可以使用join，2000不够还会继续等
        Thread.sleep(200);
        System.out.println("Main thread finished");
        if (worker.isEndedCorrectly()) {
            System.out.println("Worker Ended Correctly");
        } else {
            System.out.println("Worker Timeout");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String code = "a, b = map(int, raw_input('').split())\n" +
                "print(a + b)";
        String TLECode = "s = 0\n" +
                "for i in range(0, 1000):\n" +
                "    for j in range(0, 1000):\n" +
                "        for k in range(0, 1000):\n" +
                "            s = s + i * j * k\n" +
                "print(s)";
        String testIn = "1 2";
        Sandbox sandbox = new Sandbox(TLECode, testIn, 400);
        sandbox.run();
        if (sandbox.isEndedInTime()) {
            System.out.println("Sandbox Ended In Time");
            if (!sandbox.isEndedNormally()) {
                System.out.println("Python Error in Sandbox");
            }
            System.out.println(sandbox.getTestOut());
        } else {
            System.out.println("Sandbox Timeout");
        }
    }
}
