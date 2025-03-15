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
    //note that all APIs are GET
    @Autowired
    private ProblemSetTeacherService problemSetTeacherService;

    @GetMapping("/problem_set/teacher/all_programming_record/")
    public List<Map<String, String>> getAllProgrammingRecord(@RequestParam Map<String, String> data) {
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
        return problemSetTeacherService.getAllProgrammingRecord(problemSetId);
    }

    @GetMapping("/problem_set/teacher/one_student_record/")
    public Map<String, String> getOneStudentRecord(@RequestParam Map<String, String> data) {
        int problemSetId;
        try {
            problemSetId = Integer.parseInt(data.get("problemSetId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid problem set ID");
            return resp;
        }

        int studentId;
        try {
            studentId = Integer.parseInt(data.get("studentId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid student ID");
            return resp;
        }

        return problemSetTeacherService.getOneStudentRecord(problemSetId, studentId);
    }

    @GetMapping("/problem_set/teacher/one_student_all_objective_problem/")
    public List<Map<String, String>> getOneStudentAllObjectiveProblem(@RequestParam Map<String, String> data) {
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

        int studentId;
        try {
            studentId = Integer.parseInt(data.get("studentId"));
        } catch (NumberFormatException e) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "Invalid student ID");
            resp.add(map);
            return resp;
        }

        return problemSetTeacherService.getOneStudentAllObjectiveProblem(problemSetId, studentId);
    }

}
