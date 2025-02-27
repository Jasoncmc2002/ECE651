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

    @GetMapping("/set_manage/")
    public Map<String, String> getOne(@RequestParam Map<String, String> data) {
        int problemSetId;
        try {
            problemSetId = Integer.parseInt(data.get("problemSetId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid problem set ID");
            return resp;
        }
        return setManageService.getOne(problemSetId);
    }

    @GetMapping("/set_manage/assignment/")
    public List<Map<String, String>> getAssignmentList() {
        return setManageService.getAssignmentList();
    }

    @GetMapping("/set_manage/exam/")
    public List<Map<String, String>> getExamList() {
        return setManageService.getExamList();
    }


    @GetMapping("/set_manage/student/search/")
    public List<Map<String, String>> searchStudent(@RequestParam Map<String, String> data) {
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

        String username = data.get("username");
        String name = data.get("name");

        return setManageService.searchStudent(problemSetId, username, name);
    }

    @PostMapping("/set_manage/student/")
    public Map<String, String> addStudent(@RequestParam Map<String, String> data) {
        int problemSetId;
        try {
            problemSetId = Integer.parseInt(data.get("problemSetId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid problem set ID");
            return resp;
        }

        int userId;
        try {
            userId = Integer.parseInt(data.get("userId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid user ID");
            return resp;
        }

        return setManageService.addStudent(problemSetId, userId);
    }

    @DeleteMapping("/set_manage/student/")
    public Map<String, String> deleteStudent(@RequestParam Map<String, String> data) {
        int problemSetId;
        try {
            problemSetId = Integer.parseInt(data.get("problemSetId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid problem set ID");
            return resp;
        }

        int userId;
        try {
            userId = Integer.parseInt(data.get("userId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid user ID");
            return resp;
        }

        return setManageService.deleteStudent(problemSetId, userId);
    }

    @GetMapping("/set_manage/student/get_added/")
    public List<Map<String, String>> getAddedStudent(@RequestParam Map<String, String> data) {
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

        return setManageService.getAddedStudent(problemSetId);
    }
}
