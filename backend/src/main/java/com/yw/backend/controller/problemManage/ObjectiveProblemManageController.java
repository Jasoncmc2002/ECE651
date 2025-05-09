package com.yw.backend.controller.problemManage;

import com.yw.backend.service.problemManage.ObjectiveProblemManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ObjectiveProblemManageController {
    @Autowired
    private ObjectiveProblemManageService objectiveProblemManageService;
    @GetMapping("/problem_manage/objective_problem_manage/all/")
    public List<Map<String, String>> getAll() {
        return objectiveProblemManageService.getAll();
    }

    @GetMapping("/problem_manage/objective_problem_manage/")
    public Map<String, String> getOne(@RequestParam Map<String, String> data) {
        int objectiveProblemId;
        try {
            objectiveProblemId = Integer.parseInt(data.get("objectiveProblemId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid objective problem ID");
            return resp;
        }
        return objectiveProblemManageService.getOne(objectiveProblemId);
    }
    @PutMapping("/problem_manage/objective_problem_manage/")
    public Map<String, String> update(@RequestParam Map<String, String> data) {
        int objectiveProblemId;
        try {
            objectiveProblemId = Integer.parseInt(data.get("objectiveProblemId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid objective problem ID");
            return resp;
        }
        String opDescription = data.get("opDescription");
        int opTotalScore;
        try {
            opTotalScore = Integer.parseInt(data.get("opTotalScore"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Score must be a positive integer");
            return resp;
        }
        String opCorrectAnswer = data.get("opCorrectAnswer");
        String opTag = data.get("opTag");
        int opDifficulty;
        try {
            opDifficulty = Integer.parseInt(data.get("opDifficulty"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Difficulty must be a positive integer");
            return resp;
        }

        return objectiveProblemManageService.update(objectiveProblemId, opDescription, opTotalScore, opCorrectAnswer, opTag, opDifficulty);
    }
    @PostMapping("/problem_manage/objective_problem_manage/")
    public Map<String, String> create(@RequestParam Map<String, String> data) {
        System.out.println(data);
        String opDescription = data.get("opDescription");
        int opTotalScore;
        try {
            opTotalScore = Integer.parseInt(data.get("opTotalScore"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "the question score must be a positive integer");
            return resp;
        }
        String opCorrectAnswer = data.get("opCorrectAnswer");
        String opTag = data.get("opTag");
        int opDifficulty;
        try {
            opDifficulty = Integer.parseInt(data.get("opDifficulty"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "the difficulty coefficient must be a positive integer");
            return resp;
        }
        return objectiveProblemManageService.create(opDescription, opTotalScore, opCorrectAnswer, opTag, opDifficulty);
    }

    @DeleteMapping("/problem_manage/objective_problem_manage/")
    public Map<String, String> delete(@RequestParam Map<String, String> data) {
        // check and read id
        int objectiveProblemId;
        try {
            objectiveProblemId = Integer.parseInt(data.get("objectiveProblemId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "invalid objective problem ID");
            return resp;
        }
        return objectiveProblemManageService.delete(objectiveProblemId);
    }
}
