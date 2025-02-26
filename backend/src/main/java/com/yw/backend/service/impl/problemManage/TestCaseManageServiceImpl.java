package com.yw.backend.service.impl.problemManage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yw.backend.mapper.ProgrammingMapper;
import com.yw.backend.mapper.TestCaseMapper;
import com.yw.backend.pojo.Programming;
import com.yw.backend.pojo.TestCase;
import com.yw.backend.pojo.User;
import com.yw.backend.service.impl.utils.UserDetailsImpl;
import com.yw.backend.service.problemManage.TestCaseManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TestCaseManageServiceImpl implements TestCaseManageService {
    @Autowired
    private TestCaseMapper testCaseMapper;
    @Autowired
    private ProgrammingMapper programmingMapper;

    @Override
    public Map<String, String> create(int programmingId, String tcInput, String tcOutput, boolean respId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 1) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Permission denied");
            return resp;
        }

        QueryWrapper<Programming> programmingQueryWrapper = new QueryWrapper<>();
        programmingQueryWrapper.eq("programming_id", programmingId);
        List<Programming> programmingList = programmingMapper.selectList(programmingQueryWrapper);
        if (programmingList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No such programming problem");
            return resp;
        }

        Programming programming = programmingList.get(0);
        if (!Objects.equals(programming.getPAuthorId(), user.getUserId()) && user.getPermission() < 2) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Teacher cannot create test case for others' programming problem");
            return resp;
        }

        if (tcInput != null && tcInput.length() > 1024) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Test case input too long");
            return resp;
        }

        if (tcOutput == null || tcOutput.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Question output cannot be empty");
            return resp;
        } else if (tcOutput.length() > 1024) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Test case output too long");
            return resp;
        }

        if (respId) {
            int testCaseMaxId = 0;
            List<TestCase> testCaseList = testCaseMapper.selectList(null);
            if (!testCaseList.isEmpty()) {
                for (TestCase testCase : testCaseList) {
                    if (testCase.getTestCaseId() > testCaseMaxId)
                        testCaseMaxId = testCase.getTestCaseId();
                }
            }
            Integer testCaseId = testCaseMaxId + 1;
            TestCase testCase = new TestCase(
                    testCaseId,
                    programmingId,
                    tcInput,
                    tcOutput
            );
            testCaseMapper.insert(testCase);
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "success");
            resp.put("test_case_id", testCaseId.toString());
            return resp;
        } else {
            TestCase testCase = new TestCase(
                    null,
                    programmingId,
                    tcInput,
                    tcOutput
            );
            testCaseMapper.insert(testCase);
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "success");
            return resp;
        }

    }

    @Override
    public Map<String, String> delete(int testCaseId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 1) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Permission denied");
            return resp;
        }

        QueryWrapper<TestCase> testCaseQueryWrapper = new QueryWrapper<>();
        testCaseQueryWrapper.eq("test_case_id", testCaseId);
        List<TestCase> testCaseList = testCaseMapper.selectList(testCaseQueryWrapper);
        if (testCaseList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No such test case");
            return resp;
        }

        TestCase testCase = testCaseList.get(0);
        Integer programmingId = testCase.getProgrammingId();
        QueryWrapper<Programming> programmingQueryWrapper = new QueryWrapper<>();
        programmingQueryWrapper.eq("programming_id", programmingId);
        Programming programming = programmingMapper.selectOne(programmingQueryWrapper);
        if (!Objects.equals(programming.getPAuthorId(), user.getUserId()) && user.getPermission() < 2) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Teacher cannot delete others' test case");
            return resp;
        }

        testCaseMapper.delete(testCaseQueryWrapper);
        Map<String, String> resp = new HashMap<>();
        resp.put("error_message", "success");
        return resp;
    }

    @Override
    public List<Map<String, String>> getByProgrammingId(int programmingId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 1) {
            System.out.println("Permission denied");
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "Permission denied");
            resp.add(map);
            return resp;
        }

        QueryWrapper<TestCase> testCaseQueryWrapper = new QueryWrapper<>();
        testCaseQueryWrapper.eq("programming_id", programmingId);
        List<TestCase> testCaseList = testCaseMapper.selectList(testCaseQueryWrapper);
        List<Map<String, String>> resp = new ArrayList<>();
        for (TestCase testCase : testCaseList) {
            Map<String, String> map = new HashMap<>();
            map.put("test_case_id", testCase.getTestCaseId().toString());
            map.put("programming_id", testCase.getProgrammingId().toString());
            map.put("tc_input", testCase.getTcInput());
            map.put("tc_output", testCase.getTcOutput());
            resp.add(map);
        }
        return resp;
    }
}
