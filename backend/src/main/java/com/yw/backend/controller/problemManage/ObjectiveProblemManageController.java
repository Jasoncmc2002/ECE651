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

}
