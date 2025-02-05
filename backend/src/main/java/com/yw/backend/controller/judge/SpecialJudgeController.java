package com.yw.backend.controller.judge;

import com.yw.backend.service.judge.SpecialJudgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class SpecialJudgeController {
    //
    @Autowired
    private SpecialJudgeService specialJudgeService;

    /**
     * Controller method to handle special judge requests.
     * This method receives data containing code, test input, and a time limit,
     * then calls the special judge service to evaluate the code.
     *
     * @param data A map containing the request parameters: "code", "testInput", and "timeLimit".
     * @return A map containing the evaluation result or an error message.
     */
    @PostMapping("/judge/special_judge/")
    public Map<String, String> specialJudge(@RequestParam Map<String, String> data) {
        String code = data.get("code");
        String testInput = data.get("testInput");

        int timeLimit;
        try {
            timeLimit = Integer.parseInt(data.get("timeLimit"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid time limit");
            return resp;
        }

        if (code == null) {
            Map<String, String> res = new HashMap<>();
            res.put("error_message", "success");
            res.put("test_output", "");
            return res;
        }
        return specialJudgeService.specialJudge(code, testInput, timeLimit);
    }
}
