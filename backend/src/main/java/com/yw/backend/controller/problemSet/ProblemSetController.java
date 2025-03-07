package com.yw.backend.controller.problemSet;

import com.yw.backend.service.problemSet.ProblemSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ProblemSetController {
    @Autowired
    private ProblemSetService problemSetService;

    @GetMapping("/problem_set/programming/one/")
    public Map<String, String> getOneProgramming(@RequestParam Map<String, String> data) {
        int problemSetId;
        try {
            problemSetId = Integer.parseInt(data.get("problemSetId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid problem set ID");
            return resp;
        }

        int programmingId;
        try {
            programmingId = Integer.parseInt(data.get("programmingId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid programming ID");
            return resp;
        }

        return problemSetService.getOneProgramming(problemSetId, programmingId);
    }

    @PostMapping("/problem_set/programming/submit/")
    public Map<String, String> submitProgramming(@RequestParam Map<String, String> data) {
        int problemSetId;
        try {
            problemSetId = Integer.parseInt(data.get("problemSetId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid problem set ID");
            return resp;
        }

        int programmingId;
        try {
            programmingId = Integer.parseInt(data.get("programmingId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid programming ID");
            return resp;
        }

        String paCode = data.get("paCode");

        return problemSetService.submitProgramming(problemSetId, programmingId, paCode);
    }

    @PostMapping("/problem_set/programming/special_judge/")
    public Map<String, String> submitSpecialJudge(@RequestParam Map<String, String> data) {
        int problemSetId;
        try {
            problemSetId = Integer.parseInt(data.get("problemSetId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid problem set ID");
            return resp;
        }

        int programmingId;
        try {
            programmingId = Integer.parseInt(data.get("programmingId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid programming ID");
            return resp;
        }

        String paCode = data.get("paCode");
        String testInput = data.get("testInput");

        return problemSetService.submitSpecialJudge(problemSetId, programmingId, paCode, testInput);
    }
}
