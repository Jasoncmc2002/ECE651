package com.yw.backend.controller.setManage;

import com.yw.backend.service.setManage.SetManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SetManageController {
    @Autowired
    private SetManageService setManageService;

    @PostMapping("/set_manage/")
    public Map<String, String> create(@RequestParam Map<String, String> data) {
        String psName = data.get("psName");

        LocalDateTime psStartTime;
        try {
            psStartTime = LocalDateTime.parse(data.get("psStartTime"));
        } catch (DateTimeParseException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "the start time of problem set is invalid");
            return resp;
        }

        LocalDateTime psEndTime;
        try {
            psEndTime = LocalDateTime.parse(data.get("psEndTime"));
        } catch (DateTimeParseException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "the end time of problem set is invalid");
            return resp;
        }

        int duration;
        try {
            duration = Integer.parseInt(data.get("duration"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "the exam time set of problem is invalid");
            return resp;
        }

        return setManageService.create(psName, psStartTime, psEndTime, duration);
    }

    @DeleteMapping("/set_manage/")
    public Map<String, String> delete(@RequestParam Map<String, String> data) {
        int problemSetId;
        try {
            problemSetId = Integer.parseInt(data.get("problemSetId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "invalid problem set ID");
            return resp;
        }
        return setManageService.delete(problemSetId);
    }

    @GetMapping("/set_manage/objective_problem/search/")
    public List<Map<String, String>> searchObjectiveProblem(@RequestParam Map<String, String> data) {
        int problemSetId;
        try {
            problemSetId = Integer.parseInt(data.get("problemSetId"));
        } catch (NumberFormatException e) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "invalid problem set ID");
            resp.add(map);
            return resp;
        }

        String opDescription = data.get("opDescription");
        String opTag = data.get("opTag");

        int opDifficultyMin;
        try {
            opDifficultyMin = Integer.parseInt(data.get("opDifficultyMin"));
        } catch (NumberFormatException e) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "invalid lower limit of difficulty efficiency");
            resp.add(map);
            return resp;
        }

        int opDifficultyMax;
        try {
            opDifficultyMax = Integer.parseInt(data.get("opDifficultyMax"));
        } catch (NumberFormatException e) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "invalid higher limit of difficulty efficiency");
            resp.add(map);
            return resp;
        }

        return setManageService.searchObjectiveProblem(problemSetId, opDescription, opTag, opDifficultyMin, opDifficultyMax);
    }

    @PostMapping("/set_manage/objective_problem/")
    public Map<String, String> addObjectiveProblem(@RequestParam Map<String, String> data) {
        int problemSetId;
        try {
            problemSetId = Integer.parseInt(data.get("problemSetId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "invalid problem set ID");
            return resp;
        }

        int objectiveProblemId;
        try {
            objectiveProblemId = Integer.parseInt(data.get("objectiveProblemId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "invalid objective problem ID");
            return resp;
        }

        return setManageService.addObjectiveProblem(problemSetId, objectiveProblemId);
    }

    @DeleteMapping("/set_manage/objective_problem/")
    public Map<String, String> deleteObjectiveProblem(@RequestParam Map<String, String> data) {
        int problemSetId;
        try {
            problemSetId = Integer.parseInt(data.get("problemSetId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "invalid problem set ID");
            return resp;
        }

        int objectiveProblemId;
        try {
            objectiveProblemId = Integer.parseInt(data.get("objectiveProblemId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "invalid objective problem ID");
            return resp;
        }

        return setManageService.deleteObjectiveProblem(problemSetId, objectiveProblemId);
    }

    @GetMapping("/set_manage/objective_problem/get_added/")
    public List<Map<String, String>> getAddedObjectiveProblem(@RequestParam Map<String, String> data) {
        int problemSetId;
        try {
            problemSetId = Integer.parseInt(data.get("problemSetId"));
        } catch (NumberFormatException e) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "invalid problem set ID");
            resp.add(map);
            return resp;
        }

        return setManageService.getAddedObjectiveProblem(problemSetId);
    }

}
