package com.yw.backend.controller.problemSet;

import com.yw.backend.service.problemSet.ProblemSetTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ProblemSetTeacherController {
    @Autowired
    private ProblemSetTeacherService problemSetTeacherService;

    @GetMapping("/problem_set/teacher/one_problem_set_info/")
    public Map<String, String> getOneProblemSetInfo(@RequestParam Map<String, String> data) {
        int problemSetId;
        try {
            problemSetId = Integer.parseInt(data.get("problemSetId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid problem set ID");
            return resp;
        }
        return problemSetTeacherService.getOneProblemSetInfo(problemSetId);
    }

    @GetMapping("/problem_set/teacher/all_student_record/")
    public List<Map<String, String>> getAllStudentRecord(@RequestParam Map<String, String> data) {
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
        return problemSetTeacherService.getAllStudentRecord(problemSetId);
    }

    @GetMapping("/problem_set/teacher/all_objective_problem_record/")
    public List<Map<String, String>> getAllObjectiveProblemRecord(@RequestParam Map<String, String> data) {
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
        return problemSetTeacherService.getAllObjectiveProblemRecord(problemSetId);
    }

}
