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

    @PutMapping("/set_manage/")
    public Map<String, String> update(@RequestParam Map<String, String> data) {
        int problemSetId;
        try {
            problemSetId = Integer.parseInt(data.get("problemSetId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid probelm set ID");
            return resp;
        }

        String psName = data.get("psName");

        LocalDateTime psStartTime;
        try {
            psStartTime = LocalDateTime.parse(data.get("psStartTime"));
        } catch (DateTimeParseException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid problem set start time");
            return resp;
        }

        LocalDateTime psEndTime;
        try {
            psEndTime = LocalDateTime.parse(data.get("psEndTime"));
        } catch (DateTimeParseException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid problem set end time");
            return resp;
        }

        int duration;
        try {
            duration = Integer.parseInt(data.get("duration"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid problem set test duration");
            return resp;
        }

        return setManageService.update(problemSetId, psName, psStartTime, psEndTime, duration);
    }

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


    @GetMapping("/set_manage/programming/search/")
    public List<Map<String, String>> searchProgramming(@RequestParam Map<String, String> data) {
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

        String pTitle = data.get("pTitle");
        String pTag = data.get("pTag");

        int pDifficultyMin;
        try {
            pDifficultyMin = Integer.parseInt(data.get("pDifficultyMin"));
        } catch (NumberFormatException e) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "Invalid min difficulty");
            resp.add(map);
            return resp;
        }

        int pDifficultyMax;
        try {
            pDifficultyMax = Integer.parseInt(data.get("pDifficultyMax"));
        } catch (NumberFormatException e) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "Inval max difficulty");
            resp.add(map);
            return resp;
        }

        return setManageService.searchProgramming(problemSetId, pTitle, pTag, pDifficultyMin, pDifficultyMax);
    }

    @PostMapping("/set_manage/programming/")
    public Map<String, String> addProgramming(@RequestParam Map<String, String> data) {
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
            resp.put("error_message", "Invalid problem ID");
            return resp;
        }

        return setManageService.addProgramming(problemSetId, programmingId);
    }

    @DeleteMapping("/set_manage/programming/")
    public Map<String, String> deleteProgramming(@RequestParam Map<String, String> data) {
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
            resp.put("error_message", "Invalid problem ID");
            return resp;
        }

        return setManageService.deleteProgramming(problemSetId, programmingId);
    }

    @GetMapping("/set_manage/programming/get_added/")
    public List<Map<String, String>> getAddedProgramming(@RequestParam Map<String, String> data) {
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
        return setManageService.getAddedProgramming(problemSetId);
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
