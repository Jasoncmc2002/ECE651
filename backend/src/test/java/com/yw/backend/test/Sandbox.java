package com.yw.backend.test;

import org.python.core.PyException;
import org.python.util.PythonInterpreter;

import java.io.StringReader;
import java.io.StringWriter;

class CodeRunner extends Thread {
    String code;
    PyException pyException = null;
    boolean isEndedInTime = false;
    PythonInterpreter pythonInterpreter = null;

    @Override
    public void run() throws PyException {
        System.out.println("Start code runner");  // 断点用于检查运行时间
        try {
            pythonInterpreter.exec(this.code);
        } catch (PyException pyException) {
//            System.out.println(pyException);
            this.pyException = pyException;
        } finally {
            this.isEndedInTime = true;
        }
    }
}

public class Sandbox {

    private String code;
    private String testIn;
    private String testOut;
    private int excTime;
    private boolean isEndedInTime = false;
    private boolean isEndedNormally = true;

    public Sandbox(String code, String testIn, int excTime) {
        this.code = code;
        this.testIn = testIn;
        this.excTime = excTime;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setTestIn(String testIn) {
        this.testIn = testIn;
    }

    public void setExcTime(int excTime) {
        this.excTime = excTime;
    }

    public boolean isEndedInTime() {
        return isEndedInTime;
    }

    public String getTestOut() {
        return testOut;
    }

    public boolean isEndedNormally() {
        return isEndedNormally;
    }

    public void run() throws InterruptedException {
//        System.out.println(this.code);
//        System.out.println(this.testIn);
        CodeRunner codeRunner = new CodeRunner();
        codeRunner.setDaemon(true);  // 主进程退出，守护进程退出，在start之前调用
        codeRunner.code = this.code;


        try (PythonInterpreter pythonInterpreter = new PythonInterpreter()) {
            // 连接输入输出
            StringReader stringReader = new StringReader(this.testIn);
            StringWriter stringWriter = new StringWriter();
            pythonInterpreter.setIn(stringReader);
            pythonInterpreter.setOut(stringWriter);

            // 赋值解释器并执行
            codeRunner.pythonInterpreter = pythonInterpreter;
            codeRunner.start();
            Thread.sleep(excTime);

            // 复制是否执行完毕
            this.isEndedInTime = codeRunner.isEndedInTime;

            // 检查解释器是否有错误
            if (codeRunner.pyException != null) {
                throw codeRunner.pyException;
                // 接下来的代码看catch部分
            }

//            System.out.println(stringWriter);
            // 复制输出结果
            this.testOut = stringWriter.toString();


        } catch (PyException pyException) {
//            System.out.println(pyException);
            this.testOut = pyException.toString();
            this.isEndedInTime = true;
            this.isEndedNormally = false;
        }
    }
}
