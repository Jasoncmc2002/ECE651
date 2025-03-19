package com.yw.backend.controller.problemManage;

import com.yw.backend.service.problemManage.ProgrammingManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ProgrammingManageController {
    @Autowired
    private ProgrammingManageService programmingManageService;

    @PutMapping("/problem_manage/programming_manage/")
    public Map<String, String> update(@RequestParam Map<String, String> data) {
        int programmingId;
        try {
            programmingId = Integer.parseInt(data.get("programmingId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid programming problem ID");
            return resp;
        }

        String pDescription = data.get("pDescription");

        int pTotalScore;
        try {
            pTotalScore = Integer.parseInt(data.get("pTotalScore"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Points must be a positive integer");
            return resp;
        }

        int timeLimit;
        try {
            timeLimit = Integer.parseInt(data.get("timeLimit"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Time limit must be a positive integer");
            return resp;
        }

        int codeSizeLimit;
        try {
            codeSizeLimit = Integer.parseInt(data.get("codeSizeLimit"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Code size limit must be a positive integer");
            return resp;
        }

        String pTag = data.get("pTag");
        String pTitle = data.get("pTitle");
        String pJudgeCode = data.get("pJudgeCode");

        int pDifficulty;
        try {
            pDifficulty = Integer.parseInt(data.get("pDifficulty"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Difficulty must be integer between 0 and 5");
            return resp;
        }

        return programmingManageService.update(programmingId, pDescription, pTotalScore, timeLimit, codeSizeLimit, pTag, pTitle, pJudgeCode, pDifficulty);
    }

    @GetMapping("/problem_manage/programming_manage/all/")
    public List<Map<String, String>> getAll() {
        return programmingManageService.getAll();
    }

    @GetMapping("/problem_manage/programming_manage/")
    public Map<String, String> getOne(@RequestParam Map<String, String> data) {
        int programmingId;
        try {
            programmingId = Integer.parseInt(data.get("programmingId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid programming problem ID");
            return resp;
        }
        return programmingManageService.getOne(programmingId);
    }
    @PostMapping("/problem_manage/programming_manage/")
    public Map<String, String> create(@RequestParam Map<String, String> data) {
        String pDescription = data.get("pDescription");

        int pTotalScore;
        try {
            pTotalScore = Integer.parseInt(data.get("pTotalScore"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "the question score must be a positive integer");
            return resp;
        }

        int timeLimit;
        try {
            timeLimit = Integer.parseInt(data.get("timeLimit"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "the time limit must be a positive integer");
            return resp;
        }

        int codeSizeLimit;
        try {
            codeSizeLimit = Integer.parseInt(data.get("codeSizeLimit"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "the code length limit must be a positive integer");
            return resp;
        }

        String pTag = data.get("pTag");
        String pTitle = data.get("pTitle");
        String pJudgeCode = data.get("pJudgeCode");

        int pDifficulty;
        try {
            pDifficulty = Integer.parseInt(data.get("pDifficulty"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "the difficulty coefficient must be a positive integer");
            return resp;
        }

        return programmingManageService.create(pDescription, pTotalScore, timeLimit, codeSizeLimit, pTag, pTitle, pJudgeCode, pDifficulty);
    }

    @DeleteMapping("/problem_manage/programming_manage/")
    public Map<String, String> delete(@RequestParam Map<String, String> data) {
        int programmingId;
        try {
            programmingId = Integer.parseInt(data.get("programmingId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "invalid programming question ID");
            return resp;
        }

        return programmingManageService.delete(programmingId);
    }
}
