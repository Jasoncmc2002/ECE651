package com.yw.backend;

import org.junit.jupiter.api.Test;
import org.python.core.PyException;
import org.python.util.PythonInterpreter;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@SpringBootTest
class BackendApplicationTests {

    @Test
    void contextLoads() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode("pyw"));
        System.out.println(passwordEncoder.encode("pyxc"));
        System.out.println(passwordEncoder.matches("pyw", "$2a$10$Cl6M6DSEeTHhEF7BXuB6ous7Tv9.GSfIacXnfS93yrrJWCP/7FBe6"));
        try (PythonInterpreter pythonInterpreter = new PythonInterpreter()) {
            StringReader stringReader = new StringReader("1 2");
            StringWriter stringWriter = new StringWriter();
            pythonInterpreter.setIn(stringReader);
            pythonInterpreter.setOut(stringWriter);
            pythonInterpreter.exec("a, b = map(int, raw_input('').split())\n" +
                    "print(a + b)");
            System.out.println(stringWriter);
        } catch (PyException pyException) {
            System.out.println(pyException.toString());
        }

        int res = -1;
        try {
            res = Integer.parseInt("");
        } catch (NumberFormatException e) {
            res = 0;
        } finally {
            System.out.println("" + res);
        }

        String alphabet = "abcdefg";
        System.out.println(alphabet.substring(0, Math.min(alphabet.length(), 100)));

        String psStartTime = "2024-03-29T20:08";
        LocalDateTime date = LocalDateTime.parse(psStartTime);
        System.out.println(date.getSecond());

        int paActualScore = Math.round((float) (25 * 3) / 7);
        System.out.println(paActualScore);
    }

}
