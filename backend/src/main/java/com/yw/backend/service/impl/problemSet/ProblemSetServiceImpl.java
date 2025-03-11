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
    private ObjectiveProblemMapper objectiveProblemMapper;
    @Autowired
    private OpNPsMapper opNPsMapper;
    @Autowired
    private ProgrammingMapper programmingMapper;
    @Autowired
    private PNPsMapper pnPsMapper;
    @Autowired
    private ProgrammingAnswerMapper programmingAnswerMapper;
    @Autowired
    private TestCaseMapper testCaseMapper;
    @Autowired
    private ObjectiveProblemAnswerMapper objectiveProblemAnswerMapper;

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



    @Override
    public List<Map<String, String>> getAllObjectiveProblem(int problemSetId) {
        // check if problem set exist
        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No problem set with this ID");
            resp.add(map);
            return resp;
        }

        // check if user belong to this problem set
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        QueryWrapper<StudentNPs> studentNPsQueryWrapper = new QueryWrapper<>();
        studentNPsQueryWrapper.eq("student_id", user.getUserId());
        studentNPsQueryWrapper.eq("problem_set_id", problemSetId);
        List<StudentNPs> studentNPsList = studentNPsMapper.selectList(studentNPsQueryWrapper);
        if (studentNPsList.isEmpty()) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "User does not belong to this problem set");
            resp.add(map);
            return resp;
        }

        // check if problem set has already started
        ProblemSet problemSet = problemSetList.get(0);
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(problemSet.getPsStartTime())) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "Problem set not started yet");
            resp.add(map);
            return resp;
        }

        // Check if you have started answering questions
        // If the questions have not started and the exam has not ended, the list will not be displayed
        StudentNPs studentNPs = studentNPsList.get(0);
        if (studentNPs.getFirstStartTime() == null && now.isBefore(problemSet.getPsEndTime())) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "Not started yet");
            resp.add(map);
            return resp;
        }

        // return the problems in a list
        QueryWrapper<OpNPs> opNPsQueryWrapper = new QueryWrapper<>();
        opNPsQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        opNPsQueryWrapper.orderByAsc("objective_problem_id");
        List<OpNPs> opNPsList = opNPsMapper.selectList(opNPsQueryWrapper);
        List<Map<String, String>> resp = new ArrayList<>();
        for (OpNPs opNPs : opNPsList) {
//            objective_problem_id: '1',
//            op_description: 'Question 1',
//            op_total_score: '10',
//            opa_actual_score: '--',
//            opa_status: '已作答'
            QueryWrapper<ObjectiveProblem> objectiveProblemQueryWrapper = new QueryWrapper<>();
            objectiveProblemQueryWrapper.eq("objective_problem_id", opNPs.getObjectiveProblemId());
            ObjectiveProblem objectiveProblem = objectiveProblemMapper.selectOne(objectiveProblemQueryWrapper);
            Map<String, String> map = new HashMap<>();
            map.put("objective_problem_id", objectiveProblem.getObjectiveProblemId().toString());

            String opDescription = objectiveProblem.getOpDescription();
            map.put("op_description", opDescription.substring(0, Math.min(opDescription.length(), 60)) + "...");

            map.put("op_total_score", objectiveProblem.getOpTotalScore().toString());

            QueryWrapper<ObjectiveProblemAnswer> objectiveProblemAnswerQueryWrapper = new QueryWrapper<>();
            objectiveProblemAnswerQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
            objectiveProblemAnswerQueryWrapper.eq("objective_problem_id", objectiveProblem.getObjectiveProblemId());
            objectiveProblemAnswerQueryWrapper.eq("author_id", user.getUserId());
            List<ObjectiveProblemAnswer> objectiveProblemAnswerList = objectiveProblemAnswerMapper.selectList(objectiveProblemAnswerQueryWrapper);
            if (objectiveProblemAnswerList.isEmpty()) {
                map.put("opa_status", "Questions not answered");
                map.put("opa_actual_score", "0");
            } else {
                ObjectiveProblemAnswer objectiveProblemAnswer = objectiveProblemAnswerList.get(0);
                map.put("opa_status", "Questions answered");
                if (now.isAfter(problemSet.getPsEndTime())) {
                    map.put("opa_actual_score", objectiveProblemAnswer.getOpaActualScore().toString());
                } else {
                    map.put("opa_actual_score", "--");
                }
            }
            resp.add(map);
        }
        return resp;
    }

    @Override
    public List<Map<String, String>> getAllProgramming(int problemSetId) {
        // check if problem set exist
        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No problem set with this ID");
            resp.add(map);
            return resp;
        }

        // check if user belong to problem set
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        QueryWrapper<StudentNPs> studentNPsQueryWrapper = new QueryWrapper<>();
        studentNPsQueryWrapper.eq("student_id", user.getUserId());
        studentNPsQueryWrapper.eq("problem_set_id", problemSetId);
        List<StudentNPs> studentNPsList = studentNPsMapper.selectList(studentNPsQueryWrapper);
        if (studentNPsList.isEmpty()) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "User does not belong to this problem set");
            resp.add(map);
            return resp;
        }

        // check if problem set has started
        ProblemSet problemSet = problemSetList.get(0);
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(problemSet.getPsStartTime())) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "Problem set not started yet");
            resp.add(map);
            return resp;
        }

        // Check if you have started answering questions
        // If the questions have not started and the exam has not ended, the list will not be displayed
        StudentNPs studentNPs = studentNPsList.get(0);
        if (studentNPs.getFirstStartTime() == null && now.isBefore(problemSet.getPsEndTime())) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "未开始作答");
            resp.add(map);
            return resp;
        }

        // return questions in lists
        QueryWrapper<PNPs> pnPsQueryWrapper = new QueryWrapper<>();
        pnPsQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        pnPsQueryWrapper.orderByAsc("programming_id");
        List<PNPs> pnPsList = pnPsMapper.selectList(pnPsQueryWrapper);
        List<Map<String, String>> resp = new ArrayList<>();
        for (PNPs pnPs : pnPsList) {
            // programming_id: '1',
            // p_title: 'Programming 1',
            // p_total_score: '25',
            // pa_actual_score: '20',
            // pa_status: '已作答'
            QueryWrapper<Programming> programmingQueryWrapper = new QueryWrapper<>();
            programmingQueryWrapper.eq("programming_id", pnPs.getProgrammingId());
            Programming programming = programmingMapper.selectOne(programmingQueryWrapper);
            Map<String, String> map = new HashMap<>();
            map.put("programming_id", programming.getProgrammingId().toString());
            map.put("p_title", programming.getPTitle());
            map.put("p_total_score", programming.getPTotalScore().toString());

            QueryWrapper<ProgrammingAnswer> programmingAnswerQueryWrapper = new QueryWrapper<>();
            programmingAnswerQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
            programmingAnswerQueryWrapper.eq("programming_id", programming.getProgrammingId());
            programmingAnswerQueryWrapper.eq("author_id", user.getUserId());
            List<ProgrammingAnswer> programmingAnswerList = programmingAnswerMapper.selectList(programmingAnswerQueryWrapper);
            if (programmingAnswerList.isEmpty()) {
                map.put("pa_status", "Questions not answered");
                map.put("pa_actual_score", "0");
            } else {
                ProgrammingAnswer programmingAnswer = programmingAnswerList.get(0);
                map.put("pa_status", "Questions already answered");
                map.put("pa_actual_score", programmingAnswer.getPaActualScore().toString());
            }
            resp.add(map);
        }
        return resp;
    }

    @Override
    public Map<String, String> getOneObjectiveProblem(int problemSetId, int objectiveProblemId) {
        // check if problem set exist
        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No problem set with this ID");
            return resp;
        }

        // check if objective problem exist
        QueryWrapper<ObjectiveProblem> objectiveProblemQueryWrapper = new QueryWrapper<>();
        objectiveProblemQueryWrapper.eq("objective_problem_id", objectiveProblemId);
        List<ObjectiveProblem> objectiveProblemList = objectiveProblemMapper.selectList(objectiveProblemQueryWrapper);
        if (objectiveProblemList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No problem with this ID");
            return resp;
        }

        //
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        QueryWrapper<StudentNPs> studentNPsQueryWrapper = new QueryWrapper<>();
        studentNPsQueryWrapper.eq("student_id", user.getUserId());
        studentNPsQueryWrapper.eq("problem_set_id", problemSetId);
        List<StudentNPs> studentNPsList = studentNPsMapper.selectList(studentNPsQueryWrapper);
        if (studentNPsList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "User does not belong to this problem set");
            return resp;
        }

        //
        ProblemSet problemSet = problemSetList.get(0);
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(problemSet.getPsStartTime())) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Problem set not started yet");
            return resp;
        }

        // check if student started the problem set yet
        StudentNPs studentNPs = studentNPsList.get(0);
        if (studentNPs.getFirstStartTime() == null && now.isBefore(problemSet.getPsEndTime())) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Problem set not started yet");
            return resp;
        }

        //check if objective problem belong to problem set
        ObjectiveProblem objectiveProblem = objectiveProblemList.get(0);
        QueryWrapper<OpNPs> opNPsQueryWrapper = new QueryWrapper<>();
        opNPsQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        opNPsQueryWrapper.eq("objective_problem_id", objectiveProblem.getObjectiveProblemId());
        List<OpNPs> opNPsList = opNPsMapper.selectList(opNPsQueryWrapper);
        if (opNPsList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Problem not belong to problem set");
            return resp;
        }

        // add objective problem info into dictionary
        Map<String, String> resp = new HashMap<>();
        resp.put("error_message", "success");
        resp.put("op_description", objectiveProblem.getOpDescription());
        resp.put("op_total_score", objectiveProblem.getOpTotalScore().toString());

        //count down
        if (studentNPs.getFirstStartTime() == null) {
            resp.put("first_start_time", "");
        } else {
            resp.put("first_start_time", studentNPs.getFirstStartTime().toString());
        }

        resp.put("duration", problemSet.getDuration().toString());
        resp.put("ps_end_time", problemSet.getPsEndTime().toString());

        //status signal
        if (now.isAfter(problemSet.getPsEndTime())) {
            // exam ended
            resp.put("ps_status", "closed");
        } else if (studentNPs.getFirstStartTime() == null) {
            // not yet answered
            resp.put("ps_status", "not_started");
        } else if (problemSet.getDuration() == 0) {
            // assignment
            resp.put("ps_status", "started");
        } else if (now.isAfter(studentNPs.getFirstStartTime().plusMinutes(problemSet.getDuration()))) {
            // time ended
            resp.put("ps_status", "ended");
        } else {
            resp.put("ps_status", "started");
        }

        // return different info based on whether exam is ended
        if (now.isAfter(problemSet.getPsEndTime())) {
            resp.put("op_correct_answer", objectiveProblem.getOpCorrectAnswer());
        } else {
            resp.put("op_correct_answer", "");
        }

        //obtain info of one's answer
        QueryWrapper<ObjectiveProblemAnswer> objectiveProblemAnswerQueryWrapper = new QueryWrapper<>();
        objectiveProblemAnswerQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        objectiveProblemAnswerQueryWrapper.eq("objective_problem_id", objectiveProblem.getObjectiveProblemId());
        objectiveProblemAnswerQueryWrapper.eq("author_id", user.getUserId());
        List<ObjectiveProblemAnswer> objectiveProblemAnswerList = objectiveProblemAnswerMapper.selectList(objectiveProblemAnswerQueryWrapper);
        if (objectiveProblemAnswerList.isEmpty()) {
            resp.put("opa_actual_answer", "");
            resp.put("opa_actual_score", "0");
        } else {
            ObjectiveProblemAnswer objectiveProblemAnswer = objectiveProblemAnswerList.get(0);
            resp.put("opa_actual_answer", objectiveProblemAnswer.getOpaActualAnswer());
            // whether return actual score is depended on whether the problem set is ended
            if (now.isAfter(problemSet.getPsEndTime())) {
                resp.put("opa_actual_score", objectiveProblemAnswer.getOpaActualScore().toString());
            } else {
                resp.put("opa_actual_score", "--");
            }
        }

        return resp;
    }

    @Override
    public Map<String, String> submitObjectiveProblemAnswer(int problemSetId, int objectiveProblemId, String opaActualAnswer) {
        //
        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No problem set with this ID");
            return resp;
        }

        //
        ProblemSet problemSet = problemSetList.get(0);
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(problemSet.getPsStartTime())) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Problem set not started yet");
            return resp;
        }

        //
        QueryWrapper<ObjectiveProblem> objectiveProblemQueryWrapper = new QueryWrapper<>();
        objectiveProblemQueryWrapper.eq("objective_problem_id", objectiveProblemId);
        List<ObjectiveProblem> objectiveProblemList = objectiveProblemMapper.selectList(objectiveProblemQueryWrapper);
        if (objectiveProblemList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No problem with this ID");
            return resp;
        }

        //
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        QueryWrapper<StudentNPs> studentNPsQueryWrapper = new QueryWrapper<>();
        studentNPsQueryWrapper.eq("student_id", user.getUserId());
        studentNPsQueryWrapper.eq("problem_set_id", problemSetId);
        List<StudentNPs> studentNPsList = studentNPsMapper.selectList(studentNPsQueryWrapper);
        if (studentNPsList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "User does not belong to this problem set");
            return resp;
        }

        //
        StudentNPs studentNPs = studentNPsList.get(0);
        if (studentNPs.getFirstStartTime() == null && now.isBefore(problemSet.getPsEndTime())) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Not answered yet");
            return resp;
        }

        //
        ObjectiveProblem objectiveProblem = objectiveProblemList.get(0);
        QueryWrapper<OpNPs> opNPsQueryWrapper = new QueryWrapper<>();
        opNPsQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        opNPsQueryWrapper.eq("objective_problem_id", objectiveProblem.getObjectiveProblemId());
        List<OpNPs> opNPsList = opNPsMapper.selectList(opNPsQueryWrapper);
        if (opNPsList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Problem does not belong to this problem set");
            return resp;
        }

        // check input
        if (opaActualAnswer == null || opaActualAnswer.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Answer cannot be empty");
            return resp;
        } else if (opaActualAnswer.length() > 1024) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Answer cannot exceed 1024 characters");
            return resp;
        }

        // Determine the return information based on whether the answering has started and whether the exam has ended
        // First determine whether the exam has ended
        // If it has not ended, you also need to determine whether to start answering
        // If you start answering, you also need to determine whether it is homework
        // If it is an exam, you also need to determine whether the answering time has ended
        int state;
        if (now.isAfter(problemSet.getPsEndTime())) {
            // exam ended
            state = 4;
        } else if (studentNPs.getFirstStartTime() == null) {
            // answer not started
            state = 1;
        } else if (problemSet.getDuration() == 0) {
            // assignment
            state = 2;
        } else if (now.isAfter(studentNPs.getFirstStartTime().plusMinutes(problemSet.getDuration()))) {
            // time ended
            state = 3;
        } else {
            state = 2;
        }

        switch (state) {
            case 1: {
                Map<String, String> resp = new HashMap<>();
                resp.put("error_message", "Not answered yet");
                return resp;
            }
            case 2: {
                // can answer question; can judge
                int opaActualScore;
                if (opaActualAnswer.equals(objectiveProblem.getOpCorrectAnswer())) {
                    opaActualScore = objectiveProblem.getOpTotalScore();
                } else {
                    opaActualScore = 0;
                }
                // firstly check if already answered
                QueryWrapper<ObjectiveProblemAnswer> objectiveProblemAnswerQueryWrapper = new QueryWrapper<>();
                objectiveProblemAnswerQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
                objectiveProblemAnswerQueryWrapper.eq("objective_problem_id", objectiveProblem.getObjectiveProblemId());
                objectiveProblemAnswerQueryWrapper.eq("author_id", user.getUserId());
                List<ObjectiveProblemAnswer> objectiveProblemAnswerList = objectiveProblemAnswerMapper.selectList(objectiveProblemAnswerQueryWrapper);
                if (objectiveProblemAnswerList.isEmpty()) {
                    ObjectiveProblemAnswer objectiveProblemAnswer = new ObjectiveProblemAnswer(
                            null,
                            user.getUserId(),
                            objectiveProblem.getObjectiveProblemId(),
                            problemSet.getProblemSetId(),
                            opaActualScore,
                            opaActualAnswer
                    );
                    objectiveProblemAnswerMapper.insert(objectiveProblemAnswer);
                } else {
                    ObjectiveProblemAnswer objectiveProblemAnswer = objectiveProblemAnswerList.get(0);
                    objectiveProblemAnswer.setOpaActualAnswer(opaActualAnswer);
                    objectiveProblemAnswer.setOpaActualScore(opaActualScore);
                    objectiveProblemAnswerMapper.update(objectiveProblemAnswer, objectiveProblemAnswerQueryWrapper);
                }
                Map<String, String> resp = new HashMap<>();
                resp.put("error_message", "success");
                return resp;
            }
            case 3: {
                Map<String, String> resp = new HashMap<>();
                resp.put("error_message", "Time ended");
                return resp;
            }
            case 4: {
                Map<String, String> resp = new HashMap<>();
                resp.put("error_message", "Problem set finished");
                return resp;
            }
            default: {
                Map<String, String> resp = new HashMap<>();
                resp.put("error_message", "Server internal error, state value error, state=" + state);
                return resp;
            }
        }
    @Autowired
    private UserMapper userMapper;
    }

    @Override
    public List<Map<String, String>> getActiveProblemSet() {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        // Obtain all question sets in descending order according to problems_set_id
        Integer studentId = user.getUserId();
        QueryWrapper<StudentNPs> studentNPsQueryWrapper = new QueryWrapper<>();
        studentNPsQueryWrapper.eq("student_id", studentId);
        studentNPsQueryWrapper.orderByDesc("problem_set_id");
        List<StudentNPs> studentNPsList = studentNPsMapper.selectList(studentNPsQueryWrapper);

        // Search for specific information in the question set, convert it into a dictionary and return it, pay attention to checking the time
        LocalDateTime now = LocalDateTime.now();
        List<Map<String, String>> resp = new ArrayList<>();
        for (StudentNPs studentNPs : studentNPsList) {
            Integer problemSetId = studentNPs.getProblemSetId();
            QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
            problemSetQueryWrapper.eq("problem_set_id", problemSetId);
            ProblemSet problemSet = problemSetMapper.selectOne(problemSetQueryWrapper);

            if (problemSet.getPsEndTime().isBefore(now) || now.isBefore(problemSet.getPsStartTime())) {
                continue;
            }

            Map<String, String> map = new HashMap<>();
            map.put("problem_set_id", problemSet.getProblemSetId().toString());
            map.put("ps_name", problemSet.getPsName());
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("user_id", problemSet.getPsAuthorId());
            User author = userMapper.selectOne(userQueryWrapper);
            map.put("ps_author_name", author.getName());
            map.put("ps_start_time", problemSet.getPsStartTime().toString());
            map.put("ps_end_time", problemSet.getPsEndTime().toString());
            map.put("duration", problemSet.getDuration().toString());
            resp.add(map);
        }

        return resp;
    }

    @Override
    public List<Map<String, String>> getAllProblemSet() {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        // Obtain all question sets in descending order according to problems_set_id
        Integer studentId = user.getUserId();
        QueryWrapper<StudentNPs> studentNPsQueryWrapper = new QueryWrapper<>();
        studentNPsQueryWrapper.eq("student_id", studentId);
        studentNPsQueryWrapper.orderByDesc("problem_set_id");
        List<StudentNPs> studentNPsList = studentNPsMapper.selectList(studentNPsQueryWrapper);

        // Search for specific information in the question set, convert it into a dictionary and return it, pay attention to checking the time
        LocalDateTime now = LocalDateTime.now();
        List<Map<String, String>> resp = new ArrayList<>();
        for (StudentNPs studentNPs : studentNPsList) {
            Integer problemSetId = studentNPs.getProblemSetId();
            QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
            problemSetQueryWrapper.eq("problem_set_id", problemSetId);
            ProblemSet problemSet = problemSetMapper.selectOne(problemSetQueryWrapper);

            if (now.isBefore(problemSet.getPsStartTime())) {
                continue;
            }

            Map<String, String> map = new HashMap<>();
            map.put("problem_set_id", problemSet.getProblemSetId().toString());
            map.put("ps_name", problemSet.getPsName());
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("user_id", problemSet.getPsAuthorId());
            User author = userMapper.selectOne(userQueryWrapper);
            map.put("ps_author_name", author.getName());
            map.put("ps_start_time", problemSet.getPsStartTime().toString());
            map.put("ps_end_time", problemSet.getPsEndTime().toString());
            map.put("duration", problemSet.getDuration().toString());
            resp.add(map);
        }

        return resp;
    }

    @Override
    public Map<String, String> getOne(int problemSetId) {
        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No such problem set found through ID query");
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
            resp.put("error_message", "The user does not belong to this problem set");
            return resp;
        }

        ProblemSet problemSet = problemSetList.get(0);
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(problemSet.getPsStartTime())) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "The problem set has not started yet");
            return resp;
        }

        Map<String, String> resp = new HashMap<>();
        resp.put("error_message", "success");
        resp.put("problem_set_id", problemSet.getProblemSetId().toString());
        resp.put("ps_name", problemSet.getPsName());
        Integer authorId = problemSet.getPsAuthorId();
        resp.put("ps_author_id", authorId.toString());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_id", authorId);
        User author = userMapper.selectOne(userQueryWrapper);
        resp.put("ps_author_name", author.getName());
        resp.put("ps_start_time", problemSet.getPsStartTime().toString());
        resp.put("ps_end_time", problemSet.getPsEndTime().toString());
        resp.put("duration", problemSet.getDuration().toString());

        StudentNPs studentNPs = studentNPsList.get(0);
        if (studentNPs.getFirstStartTime() == null) {
            resp.put("first_start_time", "");
        } else {
            resp.put("first_start_time", studentNPs.getFirstStartTime().toString());
        }

        int state;
        if (now.isAfter(problemSet.getPsEndTime())) {
            resp.put("ps_status", "closed");
            state = 4;
        } else if (studentNPs.getFirstStartTime() == null) {
            resp.put("ps_status", "not_started");
            state = 1;
        } else if (problemSet.getDuration() == 0) {
            resp.put("ps_status", "started");
            state = 2;
        } else if (now.isAfter(studentNPs.getFirstStartTime().plusMinutes(problemSet.getDuration()))) {
            resp.put("ps_status", "ended");
            state = 3;
        } else {
            resp.put("ps_status", "started");
            state = 2;
        }

        switch (state) {
            case 1: {
                resp.put("ps_total_score", "--");
                resp.put("ps_actual_score", "--");
                break;
            }
            case 2:
            case 3: {
                int psTotalScore = 0;
                QueryWrapper<OpNPs> opNPsQueryWrapper = new QueryWrapper<>();
                opNPsQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
                List<OpNPs> opNPsList = opNPsMapper.selectList(opNPsQueryWrapper);
                for (OpNPs opNPs : opNPsList) {
                    Integer objectiveProblemId = opNPs.getObjectiveProblemId();
                    QueryWrapper<ObjectiveProblem> objectiveProblemQueryWrapper = new QueryWrapper<>();
                    objectiveProblemQueryWrapper.eq("objective_problem_id", objectiveProblemId);
                    ObjectiveProblem objectiveProblem = objectiveProblemMapper.selectOne(objectiveProblemQueryWrapper);
                    psTotalScore += objectiveProblem.getOpTotalScore();
                }

                QueryWrapper<PNPs> pnPsQueryWrapper = new QueryWrapper<>();
                pnPsQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
                List<PNPs> pnPsList = pnPsMapper.selectList(pnPsQueryWrapper);
                for (PNPs pnPs : pnPsList) {
                    Integer programmingId = pnPs.getProgrammingId();
                    QueryWrapper<Programming> programmingQueryWrapper = new QueryWrapper<>();
                    programmingQueryWrapper.eq("programming_id", programmingId);
                    Programming programming = programmingMapper.selectOne(programmingQueryWrapper);
                    psTotalScore += programming.getPTotalScore();
                }

                resp.put("ps_total_score", String.valueOf(psTotalScore));

                resp.put("ps_actual_score", "--");
                break;
            }
            case 4: {
                int psTotalScore = 0;
                QueryWrapper<OpNPs> opNPsQueryWrapper = new QueryWrapper<>();
                opNPsQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
                List<OpNPs> opNPsList = opNPsMapper.selectList(opNPsQueryWrapper);
                for (OpNPs opNPs : opNPsList) {
                    Integer objectiveProblemId = opNPs.getObjectiveProblemId();
                    QueryWrapper<ObjectiveProblem> objectiveProblemQueryWrapper = new QueryWrapper<>();
                    objectiveProblemQueryWrapper.eq("objective_problem_id", objectiveProblemId);
                    ObjectiveProblem objectiveProblem = objectiveProblemMapper.selectOne(objectiveProblemQueryWrapper);
                    psTotalScore += objectiveProblem.getOpTotalScore();
                }

                QueryWrapper<PNPs> pnPsQueryWrapper = new QueryWrapper<>();
                pnPsQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
                List<PNPs> pnPsList = pnPsMapper.selectList(pnPsQueryWrapper);
                for (PNPs pnPs : pnPsList) {
                    Integer programmingId = pnPs.getProgrammingId();
                    QueryWrapper<Programming> programmingQueryWrapper = new QueryWrapper<>();
                    programmingQueryWrapper.eq("programming_id", programmingId);
                    Programming programming = programmingMapper.selectOne(programmingQueryWrapper);
                    psTotalScore += programming.getPTotalScore();
                }

                resp.put("ps_total_score", String.valueOf(psTotalScore));

                int psActualScore = 0;
                QueryWrapper<ObjectiveProblemAnswer> objectiveProblemAnswerQueryWrapper = new QueryWrapper<>();
                objectiveProblemAnswerQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
                objectiveProblemAnswerQueryWrapper.eq("author_id", user.getUserId());
                List<ObjectiveProblemAnswer> objectiveProblemAnswerList = objectiveProblemAnswerMapper.selectList(objectiveProblemAnswerQueryWrapper);
                for (ObjectiveProblemAnswer objectiveProblemAnswer : objectiveProblemAnswerList) {
                    psActualScore += objectiveProblemAnswer.getOpaActualScore();
                }
                QueryWrapper<ProgrammingAnswer> programmingAnswerQueryWrapper = new QueryWrapper<>();
                programmingAnswerQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
                programmingAnswerQueryWrapper.eq("author_id", user.getUserId());
                List<ProgrammingAnswer> programmingAnswerList = programmingAnswerMapper.selectList(programmingAnswerQueryWrapper);
                for (ProgrammingAnswer programmingAnswer : programmingAnswerList) {
                    psActualScore += programmingAnswer.getPaActualScore();
                }

                resp.put("ps_actual_score", String.valueOf(psActualScore));
            }
        }
        return resp;
    }

    @Override
    public Map<String, String> startProblemSet(int problemSetId) {
        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No such problem set found through ID query");
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
            resp.put("error_message", "The user does not belong to this problem set");
            return resp;
        }

        ProblemSet problemSet = problemSetList.get(0);
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(problemSet.getPsStartTime())) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "The problem set has not started yet");
            return resp;
        }

        if (now.isAfter(problemSet.getPsEndTime())) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "The prblem set has ended");
            return resp;
        }

        StudentNPs studentNPs = studentNPsList.get(0);
        if (studentNPs.getFirstStartTime() != null) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "The answer to the problem set has started");
            return resp;
        }

        studentNPs.setFirstStartTime(LocalDateTime.now());
        studentNPsMapper.update(studentNPs, studentNPsQueryWrapper);
        Map<String, String> resp = new HashMap<>();
        resp.put("error_message", "success");
        return resp;
    }

}
