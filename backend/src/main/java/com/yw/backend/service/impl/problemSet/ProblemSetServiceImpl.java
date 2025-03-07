package com.yw.backend.service.impl.problemSet;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yw.backend.mapper.*;
import com.yw.backend.pojo.*;
import com.yw.backend.service.impl.judge.Sandbox;
import com.yw.backend.service.impl.utils.UserDetailsImpl;
import com.yw.backend.service.problemSet.ProblemSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ProblemSetServiceImpl implements ProblemSetService {
    @Autowired
    private StudentNPsMapper studentNPsMapper;
    @Autowired
    private ProblemSetMapper problemSetMapper;
    @Autowired
    private ProgrammingMapper programmingMapper;
    @Autowired
    private PNPsMapper pnPsMapper;
    @Autowired
    private ProgrammingAnswerMapper programmingAnswerMapper;
    @Autowired
    private TestCaseMapper testCaseMapper;



    @Override
    public Map<String, String> getOneProgramming(int problemSetId, int programmingId) {
        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No Such Problem Set");
            return resp;
        }

        QueryWrapper<Programming> programmingQueryWrapper = new QueryWrapper<>();
        programmingQueryWrapper.eq("programming_id", programmingId);
        List<Programming> programmingList = programmingMapper.selectList(programmingQueryWrapper);
        if (programmingList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No Such Programming Problem");
            return resp;
        }

        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        QueryWrapper<StudentNPs> studentNPsQueryWrapper = new QueryWrapper<>();
        studentNPsQueryWrapper.eq("student_id", user.getUserId());
        studentNPsQueryWrapper.eq("problem_set_id", problemSetId);
        List<StudentNPs> studentNPsList = studentNPsMapper.selectList(studentNPsQueryWrapper);
        if (studentNPsList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "User Not In This Problem Set");
            return resp;
        }

        ProblemSet problemSet = problemSetList.get(0);
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(problemSet.getPsStartTime())) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Problem Set Not Started");
            return resp;
        }

        StudentNPs studentNPs = studentNPsList.get(0);
        if (studentNPs.getFirstStartTime() == null && now.isBefore(problemSet.getPsEndTime())) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Answer Not Started");
            return resp;
        }

        Programming programming = programmingList.get(0);
        QueryWrapper<PNPs> pnPsQueryWrapper = new QueryWrapper<>();
        pnPsQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        pnPsQueryWrapper.eq("programming_id", programming.getProgrammingId());
        List<PNPs> pnPsList = pnPsMapper.selectList(pnPsQueryWrapper);
        if (pnPsList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Programming Problem Not Belong To This Problem Set");
            return resp;
        }

        Map<String, String> resp = new HashMap<>();
        resp.put("error_message", "success");
        resp.put("p_title", programming.getPTitle());
        resp.put("p_description", programming.getPDescription());
        resp.put("p_total_score", programming.getPTotalScore().toString());
        resp.put("time_limit", programming.getTimeLimit().toString());
        resp.put("code_size_limit", programming.getCodeSizeLimit().toString());
        QueryWrapper<TestCase> testCaseQueryWrapper = new QueryWrapper<>();
        testCaseQueryWrapper.eq("programming_id", programming.getProgrammingId());
        int tcCount = Math.toIntExact(testCaseMapper.selectCount(testCaseQueryWrapper));
        resp.put("tc_count", String.valueOf(tcCount));

        if (studentNPs.getFirstStartTime() == null) {
            resp.put("first_start_time", "");
        } else {
            resp.put("first_start_time", studentNPs.getFirstStartTime().toString());
        }

        resp.put("duration", problemSet.getDuration().toString());
        resp.put("ps_end_time", problemSet.getPsEndTime().toString());

        QueryWrapper<ProgrammingAnswer> programmingAnswerQueryWrapper = new QueryWrapper<>();
        programmingAnswerQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        programmingAnswerQueryWrapper.eq("programming_id", programming.getProgrammingId());
        programmingAnswerQueryWrapper.eq("author_id", user.getUserId());
        List<ProgrammingAnswer> programmingAnswerList = programmingAnswerMapper.selectList(programmingAnswerQueryWrapper);
        if (programmingAnswerList.isEmpty()) {
            resp.put("pa_code", "");
            resp.put("pa_actual_score", "0");
            resp.put("pass_count", "0");
        } else {
            ProgrammingAnswer programmingAnswer = programmingAnswerList.get(0);
            resp.put("pa_code", programmingAnswer.getPaCode());
            resp.put("pa_actual_score", programmingAnswer.getPaActualScore().toString());
            resp.put("pass_count", programmingAnswer.getPassCount().toString());
        }
        if (now.isAfter(problemSet.getPsEndTime())) {
            resp.put("ps_status", "closed");
        } else if (studentNPs.getFirstStartTime() == null) {
            resp.put("ps_status", "not_started");
        } else if (problemSet.getDuration() == 0) {
            resp.put("ps_status", "started");
        } else if (now.isAfter(studentNPs.getFirstStartTime().plusMinutes(problemSet.getDuration()))) {
            resp.put("ps_status", "ended");
        } else {
            resp.put("ps_status", "started");
        }

        return resp;
    }

    @Override
    public Map<String, String> submitProgramming(int problemSetId, int programmingId, String paCode) {
        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No Such Problem Set");
            return resp;
        }

        QueryWrapper<Programming> programmingQueryWrapper = new QueryWrapper<>();
        programmingQueryWrapper.eq("programming_id", programmingId);
        List<Programming> programmingList = programmingMapper.selectList(programmingQueryWrapper);
        if (programmingList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No Such Programming Problem");
            return resp;
        }

        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        QueryWrapper<StudentNPs> studentNPsQueryWrapper = new QueryWrapper<>();
        studentNPsQueryWrapper.eq("student_id", user.getUserId());
        studentNPsQueryWrapper.eq("problem_set_id", problemSetId);
        List<StudentNPs> studentNPsList = studentNPsMapper.selectList(studentNPsQueryWrapper);
        if (studentNPsList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "User Not In This Problem Set");
            return resp;
        }

        ProblemSet problemSet = problemSetList.get(0);
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(problemSet.getPsStartTime())) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Problem Set Not Started");
            return resp;
        }

        StudentNPs studentNPs = studentNPsList.get(0);
        if (studentNPs.getFirstStartTime() == null && now.isBefore(problemSet.getPsEndTime())) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Answer Not Started");
            return resp;
        }

        Programming programming = programmingList.get(0);
        QueryWrapper<PNPs> pnPsQueryWrapper = new QueryWrapper<>();
        pnPsQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        pnPsQueryWrapper.eq("programming_id", programming.getProgrammingId());
        List<PNPs> pnPsList = pnPsMapper.selectList(pnPsQueryWrapper);
        if (pnPsList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Programming Problem Not Belong To This Problem Set");
            return resp;
        }


        int state;
        if (now.isAfter(problemSet.getPsEndTime())) {
            state = 4;
        } else if (studentNPs.getFirstStartTime() == null) {
            state = 1;
        } else if (problemSet.getDuration() == 0) {
            state = 2;
        } else if (now.isAfter(studentNPs.getFirstStartTime().plusMinutes(problemSet.getDuration()))) {
            state = 3;
        } else {
            state = 2;
        }

        switch (state) {
            case 1: {
                Map<String, String> resp = new HashMap<>();
                resp.put("error_message", "Answer Not Started");
                return resp;
            }
            case 2: {
                Integer codeSizeLimit = programming.getCodeSizeLimit();
                if (paCode == null || paCode.isEmpty()) {
                    Map<String, String> resp = new HashMap<>();
                    resp.put("error_message", "My Code Cannot Be Empty");
                    return resp;
                } else if (paCode.length() > codeSizeLimit * 1000) {
                    Map<String, String> resp = new HashMap<>();
                    resp.put("error_message", "My Code Length Exceeds Limit");
                    return resp;
                }

                QueryWrapper<TestCase> testCaseQueryWrapper = new QueryWrapper<>();
                testCaseQueryWrapper.eq("programming_id", programming.getProgrammingId());
                List<TestCase> testCaseList = testCaseMapper.selectList(testCaseQueryWrapper);
                Integer timeLimit = programming.getTimeLimit();

                String combinedCode = paCode + "\n\n" + programming.getPJudgeCode();

                int passCount = 0;
                Map<String, String> resp = new HashMap<>();
                for (TestCase testCase : testCaseList) {
                    String testInput = testCase.getTcInput();
                    String testOutput = testCase.getTcOutput();

                    Sandbox sandbox = new Sandbox(combinedCode, testInput, timeLimit);
                    try {
                        sandbox.run();
                        if (sandbox.isEndedInTime()) {
                            System.out.println("Sandbox Ended In Time");
                            if (!sandbox.isEndedNormally()) {
                                System.out.println("Python Error in Sandbox");
                                System.out.println(sandbox.getTestOut() + "/");
                                resp.put("test_input", testInput);
                                resp.put("test_output", "Runtime error：" + "\n" + sandbox.getTestOut());
                            } else {
                                if (testOutput.equals(sandbox.getTestOut())) {
                                    passCount += 1;
                                } else {
                                    System.out.println("Expected output: \n" + testOutput + "/");
                                    System.out.println("Actual output: \n" + sandbox.getTestOut() + "/");
                                    resp.put("test_input", testInput);
                                    resp.put("test_output", "Expected output: \n" + testOutput + "/" + '\n' + "Actual output: \n" + sandbox.getTestOut() + "/");
                                }
                            }
                        } else {
                            System.out.println("Sandbox Timeout");
                            resp.put("test_input", testInput);
                            resp.put("test_output", "Runtime error: Execution Timeout");
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                int paActualScore = Math.round((float) (programming.getPTotalScore() * passCount) / testCaseList.size());

                QueryWrapper<ProgrammingAnswer> programmingAnswerQueryWrapper = new QueryWrapper<>();
                programmingAnswerQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
                programmingAnswerQueryWrapper.eq("programming_id", programming.getProgrammingId());
                programmingAnswerQueryWrapper.eq("author_id", user.getUserId());
                List<ProgrammingAnswer> programmingAnswerList = programmingAnswerMapper.selectList(programmingAnswerQueryWrapper);
                if (programmingAnswerList.isEmpty()) {
                    ProgrammingAnswer programmingAnswer = new ProgrammingAnswer(
                            null,
                            user.getUserId(),
                            problemSet.getProblemSetId(),
                            programming.getProgrammingId(),
                            paCode,
                            paActualScore,
                            passCount
                    );
                    programmingAnswerMapper.insert(programmingAnswer);
                } else {
                    ProgrammingAnswer programmingAnswer = programmingAnswerList.get(0);
                    programmingAnswer.setPaCode(paCode);
                    programmingAnswer.setPaActualScore(paActualScore);
                    programmingAnswer.setPassCount(passCount);
                    programmingAnswerMapper.update(programmingAnswer, programmingAnswerQueryWrapper);
                }

                resp.put("pa_actual_score", String.valueOf(paActualScore));
                resp.put("pass_count", String.valueOf(passCount));
                resp.put("tc_count", String.valueOf(testCaseList.size()));
                if (passCount == 0) {
                    resp.put("res_message", "Answer Incorrect");
                } else if (passCount == testCaseList.size()) {
                    resp.put("res_message", "Answer Correct");
                } else {
                    resp.put("res_message", "Partial Answer Correct");
                }
                resp.put("error_message", "success");
                return resp;
            }
            case 3: {
                Map<String, String> resp = new HashMap<>();
                resp.put("error_message", "Answer Time Ended");
                return resp;
            }
            case 4: {
                Map<String, String> resp = new HashMap<>();
                resp.put("error_message", "Problem Set Ended");
                return resp;
            }
            default: {
                Map<String, String> resp = new HashMap<>();
                resp.put("error_message", "Inner server error, wrong state number, state=" + state);
                return resp;
            }
        }
    }

    @Override
    public Map<String, String> submitSpecialJudge(int problemSetId, int programmingId, String paCode, String testInput) {

        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Problem Set Not Found");
            return resp;
        }

        QueryWrapper<Programming> programmingQueryWrapper = new QueryWrapper<>();
        programmingQueryWrapper.eq("programming_id", programmingId);
        List<Programming> programmingList = programmingMapper.selectList(programmingQueryWrapper);
        if (programmingList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Programming Problem Not Found");
            return resp;
        }

        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        QueryWrapper<StudentNPs> studentNPsQueryWrapper = new QueryWrapper<>();
        studentNPsQueryWrapper.eq("student_id", user.getUserId());
        studentNPsQueryWrapper.eq("problem_set_id", problemSetId);
        List<StudentNPs> studentNPsList = studentNPsMapper.selectList(studentNPsQueryWrapper);
        if (studentNPsList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "User Not In This Problem Set");
            return resp;
        }

        ProblemSet problemSet = problemSetList.get(0);
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(problemSet.getPsStartTime())) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Problem Set Not Started");
            return resp;
        }
        StudentNPs studentNPs = studentNPsList.get(0);
        if (studentNPs.getFirstStartTime() == null && now.isBefore(problemSet.getPsEndTime())) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Answer Not Started");
            return resp;
        }

        Programming programming = programmingList.get(0);
        QueryWrapper<PNPs> pnPsQueryWrapper = new QueryWrapper<>();
        pnPsQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        pnPsQueryWrapper.eq("programming_id", programming.getProgrammingId());
        List<PNPs> pnPsList = pnPsMapper.selectList(pnPsQueryWrapper);
        if (pnPsList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Problem Not Belong To Problem Set");
            return resp;
        }

        int state;
        if (now.isAfter(problemSet.getPsEndTime())) {
            state = 4;
        } else if (studentNPs.getFirstStartTime() == null) {
            state = 1;
        } else if (problemSet.getDuration() == 0) {
            state = 2;
        } else if (now.isAfter(studentNPs.getFirstStartTime().plusMinutes(problemSet.getDuration()))) {
            state = 3;
        } else {
            state = 2;
        }

        switch (state) {
            case 1: {
                Map<String, String> resp = new HashMap<>();
                resp.put("error_message", "Answer Not Started");
                return resp;
            }
            case 2: {
                Integer codeSizeLimit = programming.getCodeSizeLimit();
                if (paCode == null || paCode.isEmpty()) {
                    Map<String, String> resp = new HashMap<>();
                    resp.put("error_message", "My Code Cannot Be Empty");
                    return resp;
                } else if (paCode.length() > codeSizeLimit * 1000) {
                    Map<String, String> resp = new HashMap<>();
                    resp.put("error_message", "My Code Length Exceeds Limit");
                    return resp;
                }

                Integer timeLimit = programming.getTimeLimit();

                String combinedCode = paCode + "\n\n" + programming.getPJudgeCode();

                Sandbox sandbox = new Sandbox(combinedCode, testInput, timeLimit);
                Map<String, String> resp = new HashMap<>();
                try {
                    sandbox.run();
                    if (sandbox.isEndedInTime()) {
                        System.out.println("Sandbox Ended In Time");
                        System.out.println(sandbox.getTestOut() + "/");

                        if (!sandbox.isEndedNormally()) {
                            System.out.println("Python Error in Sandbox");
                            resp.put("error_message", "success");
                            resp.put("test_output", "Run error：" + "\n" + sandbox.getTestOut());
                            return resp;
                        }

                        resp.put("error_message", "success");
                        resp.put("test_output", sandbox.getTestOut());
                        return resp;
                    } else {
                        System.out.println("Sandbox Timeout");

                        resp.put("error_message", "success");
                        resp.put("test_output", "Execution Timeout");
                    }
                    return resp;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            case 3: {
                Map<String, String> resp = new HashMap<>();
                resp.put("error_message", "Time To Answer Ended");
                return resp;
            }
            case 4: {
                Map<String, String> resp = new HashMap<>();
                resp.put("error_message", "Problem Set Ended");
                return resp;
            }
            default: {
                Map<String, String> resp = new HashMap<>();
                resp.put("error_message", "Inner server error, wrong state number, state=" + state);
                return resp;
            }
        }
    }
}
