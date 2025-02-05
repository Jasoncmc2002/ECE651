package com.yw.backend.service.impl.judge;

import com.yw.backend.pojo.User;
import com.yw.backend.service.impl.utils.UserDetailsImpl;
import com.yw.backend.service.judge.SpecialJudgeService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SpecialJudgeServiceImpl implements SpecialJudgeService {
    @Override
    public Map<String, String> specialJudge(String code, String testInput, int timeLimit) {

        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 1) {
            System.out.println("No permission to use program debugging tools");
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No permission to use program debugging tools");
            return resp;
        }

        Sandbox sandbox = new Sandbox(code, testInput, timeLimit);
        Map<String, String> resp = new HashMap<>();
        try {
            sandbox.run();
            if (sandbox.isEndedInTime()) {
                System.out.println("Sandbox Ended In Time");
                System.out.println(sandbox.getTestOut());

                if (!sandbox.isEndedNormally()) {
                    System.out.println("Python Error in Sandbox");
                    resp.put("error_message", "success");
                    resp.put("test_output", "Error during code execution: " + "\n" + sandbox.getTestOut());
                    return resp;
                }

                resp.put("error_message", "success");
                resp.put("test_output", sandbox.getTestOut());
                return resp;
            } else {
                System.out.println("Sandbox Timeout");

                resp.put("error_message", "success");
                resp.put("test_output", "Code Execution Timeout");
            }
            return resp;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
