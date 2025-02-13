package com.yw.backend.controller.problemManage;

import com.yw.backend.service.problemManage.TestCaseManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class TestCaseManageController {
    @Autowired
    private TestCaseManageService testCaseManageService;

    @PostMapping("/problem_manage/test_case_manage/")
    public Map<String, String> create(@RequestParam Map<String, String> data) {
        int programmingId;
        try {
            programmingId = Integer.parseInt(data.get("programmingId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid programming ID");
            return resp;
        }

        String tcInput = data.get("tcInput");
        String tcOutput = data.get("tcOutput");

        boolean respId = data.get("respId") != null && Objects.equals(data.get("respId"), "yes");

        return testCaseManageService.create(programmingId, tcInput, tcOutput, respId);
    }

    @DeleteMapping("/problem_manage/test_case_manage/")
    public Map<String, String> delete(@RequestParam Map<String, String> data) {
        int testCaseId;
        try {
            testCaseId = Integer.parseInt(data.get("testCaseId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid test case ID");
            return resp;
        }
        return testCaseManageService.delete(testCaseId);
    }

    @GetMapping("/problem_manage/test_case_manage/by_programming_id/")
    public List<Map<String, String>> getByProgrammingId(@RequestParam Map<String, String> data) {
        int programmingId;
        try {
            programmingId = Integer.parseInt(data.get("programmingId"));
        } catch (NumberFormatException e) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "Invalid programming ID");
            resp.add(map);
            return resp;
        }
        return testCaseManageService.getByProgrammingId(programmingId);
    }
}
