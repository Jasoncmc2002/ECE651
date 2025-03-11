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

    @GetMapping("/problem_set/active/")
    public List<Map<String, String>> getActiveProblemSet() {
        return problemSetService.getActiveProblemSet();
    }

    @GetMapping("/problem_set/all/")
    public List<Map<String, String>> getAllProblemSet() {
        return problemSetService.getAllProblemSet();
    }

    @GetMapping("/problem_set/")
    public Map<String, String> getOne(@RequestParam Map<String, String> data) {
        int problemSetId;
        try {
            problemSetId = Integer.parseInt(data.get("problemSetId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "无效题目集ID");
            return resp;
        }
        return problemSetService.getOne(problemSetId);
    }

    @PutMapping("/problem_set/start/")
    public Map<String, String> startProblemSet(@RequestParam Map<String, String> data) {
        int problemSetId;
        try {
            problemSetId = Integer.parseInt(data.get("problemSetId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "无效题目集ID");
            return resp;
        }
        return problemSetService.startProblemSet(problemSetId);
    }

}
