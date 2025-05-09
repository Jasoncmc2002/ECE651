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

    @GetMapping("/problem_set/teacher/one_student_all_programming/")
    public List<Map<String, String>> getOneStudentAllProgramming(@RequestParam Map<String, String> data) {
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

        return problemSetTeacherService.getOneStudentAllProgramming(problemSetId, studentId);
    }

    @GetMapping("/problem_set/teacher/one_student_one_objective_problem/")
    public Map<String, String> getOneStudentOneObjectiveProblem(@RequestParam Map<String, String> data) {
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

        int objectiveProblemId;
        try {
            objectiveProblemId = Integer.parseInt(data.get("objectiveProblemId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid objective problem ID");
            return resp;
        }

        return problemSetTeacherService.getOneStudentOneObjectiveProblem(problemSetId, studentId, objectiveProblemId);
    }

    @GetMapping("/problem_set/teacher/one_student_one_programming/")
    public Map<String, String> getOneStudentOneProgramming(@RequestParam Map<String, String> data) {
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

        int programmingId;
        try {
            programmingId = Integer.parseInt(data.get("programmingId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid programming ID");
            return resp;
        }

        return problemSetTeacherService.getOneStudentOneProgramming(problemSetId, studentId, programmingId);
    }
}
