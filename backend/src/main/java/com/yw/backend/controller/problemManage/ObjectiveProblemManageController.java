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
}
