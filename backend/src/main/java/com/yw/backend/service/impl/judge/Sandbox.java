package com.yw.backend.service.impl.judge;

import lombok.Data;
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
        System.out.println("Start code runner");
        try {
            pythonInterpreter.exec(this.code);
        } catch (PyException pyException) {
            this.pyException = pyException;
        } finally {
            this.isEndedInTime = true;
        }
    }
}

@Data
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


    public void run() throws InterruptedException, PyException {
        CodeRunner codeRunner = new CodeRunner();
        codeRunner.setDaemon(true);
        codeRunner.code = this.code;
        System.setProperty("python.home", "/home/ec2-user/ec2-user/jython");
        PythonInterpreter.initialize(System.getProperties(), null, new String[0]);

        try (PythonInterpreter pythonInterpreter = new PythonInterpreter()) {
            pythonInterpreter.exec("from __future__ import print_function");
            pythonInterpreter.exec("def input(prompt=''):\n" +
                    "    return raw_input(prompt)");

            StringReader stringReader = new StringReader(this.testIn);
            StringWriter stringWriter = new StringWriter();
            pythonInterpreter.setIn(stringReader);
            pythonInterpreter.setOut(stringWriter);
            codeRunner.pythonInterpreter = pythonInterpreter;
            codeRunner.start();
            Thread.sleep(excTime);
            pythonInterpreter.close();
            this.isEndedInTime = codeRunner.isEndedInTime;
            if (codeRunner.pyException != null) {
                throw codeRunner.pyException;
            }
            this.testOut = stringWriter.toString();


        } catch (PyException pyException) {
            this.testOut = pyException.toString();
            this.isEndedInTime = true;
            this.isEndedNormally = false;
        }
    }
}
