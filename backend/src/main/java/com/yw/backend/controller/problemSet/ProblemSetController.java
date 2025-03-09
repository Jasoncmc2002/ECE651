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

    @GetMapping("/problem_set/objective_problem/all/")
    public List<Map<String, String>> getAllObjectiveProblem(@RequestParam Map<String, String> data) {
        int problemSetId;
        try {
            problemSetId = Integer.parseInt(data.get("problemSetId"));
        } catch (NumberFormatException e) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "Invalid problem set ID");
            resp.add(map);
            return resp;
        }
        return problemSetService.getAllObjectiveProblem(problemSetId);
    }

    @GetMapping("/problem_set/programming/all/")
    public List<Map<String, String>> getAllProgramming(@RequestParam Map<String, String> data) {
        int problemSetId;
        try {
            problemSetId = Integer.parseInt(data.get("problemSetId"));
        } catch (NumberFormatException e) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "Invalid problem set ID");
            resp.add(map);
            return resp;
        }
        return problemSetService.getAllProgramming(problemSetId);
    }

    @GetMapping("/problem_set/objective_problem/one/")
    public Map<String, String> getOneObjectiveProblem(@RequestParam Map<String, String> data) {
        int problemSetId;
        try {
            problemSetId = Integer.parseInt(data.get("problemSetId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "IPnvalid problem set ID");
            return resp;
        }

        int objectiveProblemId;
        try {
            objectiveProblemId = Integer.parseInt(data.get("objectiveProblemId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid problem ID");
            return resp;
        }

        return problemSetService.getOneObjectiveProblem(problemSetId, objectiveProblemId);
    }

    @PostMapping("/problem_set/objective_problem/submit/")
    public Map<String, String> submitObjectiveProblemAnswer(@RequestParam Map<String, String> data) {
        int problemSetId;
        try {
            problemSetId = Integer.parseInt(data.get("problemSetId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid problem set ID");
            return resp;
        }

        int objectiveProblemId;
        try {
            objectiveProblemId = Integer.parseInt(data.get("objectiveProblemId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid problem ID");
            return resp;
        }

        String opaActualAnswer = data.get("opaActualAnswer");

        return problemSetService.submitObjectiveProblemAnswer(problemSetId, objectiveProblemId, opaActualAnswer);
    }

}
