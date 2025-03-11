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
