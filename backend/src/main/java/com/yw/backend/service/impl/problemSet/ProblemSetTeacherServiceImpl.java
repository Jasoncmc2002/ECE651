package com.yw.backend.service.impl.problemSet;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yw.backend.mapper.*;
import com.yw.backend.pojo.*;
import com.yw.backend.service.impl.utils.UserDetailsImpl;
import com.yw.backend.service.problemSet.ProblemSetTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ProblemSetTeacherServiceImpl implements ProblemSetTeacherService {
    @Autowired
    private UserMapper userMapper;
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
    private ObjectiveProblemAnswerMapper objectiveProblemAnswerMapper;
    @Autowired
    private ProgrammingAnswerMapper programmingAnswerMapper;
    @Autowired
    private TestCaseMapper testCaseMapper;

    @Override
    public Map<String, String> getOneProblemSetInfo(int problemSetId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 1) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No operation permission for obtaining the integrated performance report of the problems");
            return resp;
        }

        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No such problem set found through ID query");
            return resp;
        }

        ProblemSet problemSet = problemSetList.get(0);
        if (!Objects.equals(problemSet.getPsAuthorId(), user.getUserId()) && user.getPermission() < 2) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Teachers are not allowed to search for transcripts created by others for problem sets");
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

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(problemSet.getPsStartTime())) {
            resp.put("ps_status_message", "The problem set has not started");
        } else if (now.isAfter(problemSet.getPsEndTime())) {
            resp.put("ps_status_message", "The problem set has ended");
        } else {
            resp.put("ps_status_message", "The problem set has started");
        }

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

        return resp;
    }

    @Override
    public List<Map<String, String>> getAllStudentRecord(int problemSetId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 1) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No operation permission for obtaining the integrated performance report of the problems");
            resp.add(map);
            return resp;
        }

        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No such problem set found through ID query");
            resp.add(map);
            return resp;
        }

        ProblemSet problemSet = problemSetList.get(0);
        if (!Objects.equals(problemSet.getPsAuthorId(), user.getUserId()) && user.getPermission() < 2) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "Teachers are not allowed to search for transcripts created by others for problem sets");
            resp.add(map);
            return resp;
        }

        List<Map<String, String>> resp = new ArrayList<>();
        QueryWrapper<StudentNPs> studentNPsQueryWrapper = new QueryWrapper<>();
        studentNPsQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        List<StudentNPs> studentNPsList = studentNPsMapper.selectList(studentNPsQueryWrapper);
        for (StudentNPs studentNPs : studentNPsList) {
            Map<String, String> map = new HashMap<>();
            Integer studentId = studentNPs.getStudentId();
            map.put("user_id", studentId.toString());

            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("user_id", studentId);
            User student = userMapper.selectOne(userQueryWrapper);
            map.put("name", student.getName());
            map.put("username", student.getUsername());
            map.put("permission", student.getPermission().toString());

            if (studentNPs.getFirstStartTime() == null) {
                map.put("first_start_time", "");
            } else {
                map.put("first_start_time", studentNPs.getFirstStartTime().toString());
            }

            int psActualScore = 0;
            QueryWrapper<ObjectiveProblemAnswer> objectiveProblemAnswerQueryWrapper = new QueryWrapper<>();
            objectiveProblemAnswerQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
            objectiveProblemAnswerQueryWrapper.eq("author_id", studentId);
            List<ObjectiveProblemAnswer> objectiveProblemAnswerList = objectiveProblemAnswerMapper.selectList(objectiveProblemAnswerQueryWrapper);
            for (ObjectiveProblemAnswer objectiveProblemAnswer : objectiveProblemAnswerList) {
                psActualScore += objectiveProblemAnswer.getOpaActualScore();
            }
            QueryWrapper<ProgrammingAnswer> programmingAnswerQueryWrapper = new QueryWrapper<>();
            programmingAnswerQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
            programmingAnswerQueryWrapper.eq("author_id", studentId);
            List<ProgrammingAnswer> programmingAnswerList = programmingAnswerMapper.selectList(programmingAnswerQueryWrapper);
            for (ProgrammingAnswer programmingAnswer : programmingAnswerList) {
                psActualScore += programmingAnswer.getPaActualScore();
            }
            map.put("ps_actual_score", String.valueOf(psActualScore));

            resp.add(map);
        }
        return resp;
    }

    @Override
    public List<Map<String, String>> getAllObjectiveProblemRecord(int problemSetId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 1) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No operation permission for obtaining the integrated performance report of the problems");
            resp.add(map);
            return resp;
        }

        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No such problem set found through ID query");
            resp.add(map);
            return resp;
        }

        ProblemSet problemSet = problemSetList.get(0);
        if (!Objects.equals(problemSet.getPsAuthorId(), user.getUserId()) && user.getPermission() < 2) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "Teachers are not allowed to search for transcripts created by others for problem sets");
            resp.add(map);
            return resp;
        }

        List<Map<String, String>> resp = new ArrayList<>();
        QueryWrapper<OpNPs> opNPsQueryWrapper = new QueryWrapper<>();
        opNPsQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        opNPsQueryWrapper.orderByAsc("objective_problem_id");
        List<OpNPs> opNPsList = opNPsMapper.selectList(opNPsQueryWrapper);
        for (OpNPs opNPs : opNPsList) {
            Map<String, String> map = new HashMap<>();
            QueryWrapper<ObjectiveProblem> objectiveProblemQueryWrapper = new QueryWrapper<>();
            objectiveProblemQueryWrapper.eq("objective_problem_id", opNPs.getObjectiveProblemId());
            ObjectiveProblem objectiveProblem = objectiveProblemMapper.selectOne(objectiveProblemQueryWrapper);
            map.put("objective_problem_id", objectiveProblem.getObjectiveProblemId().toString());

            String opDescription = objectiveProblem.getOpDescription();
            map.put("op_description", opDescription.substring(0, Math.min(opDescription.length(), 125)) + "...");

            Integer opTotalScore = objectiveProblem.getOpTotalScore();
            int opCorrectCount = 0;
            QueryWrapper<ObjectiveProblemAnswer> objectiveProblemAnswerQueryWrapper = new QueryWrapper<>();
            objectiveProblemAnswerQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
            objectiveProblemAnswerQueryWrapper.eq("objective_problem_id", objectiveProblem.getObjectiveProblemId());
            List<ObjectiveProblemAnswer> objectiveProblemAnswerList = objectiveProblemAnswerMapper.selectList(objectiveProblemAnswerQueryWrapper);
            int opAnswerCount = objectiveProblemAnswerList.size();
            for (ObjectiveProblemAnswer objectiveProblemAnswer : objectiveProblemAnswerList) {
                if (opTotalScore.equals(objectiveProblemAnswer.getOpaActualScore())) {
                    opCorrectCount += 1;
                }
            }
            map.put("op_correct_count", String.valueOf(opCorrectCount));
            map.put("op_answer_count", String.valueOf(opAnswerCount));
            resp.add(map);
        }

        return resp;
    }

    @Override
    public List<Map<String, String>> getOneStudentAllProgramming(int problemSetId, int studentId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        if (user.getPermission() < 1) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No permission to get one student all programming");
            resp.add(map);
            return resp;
        }
        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No such problem set");
            resp.add(map);
            return resp;
        }

        ProblemSet problemSet = problemSetList.get(0);
        if (!Objects.equals(problemSet.getPsAuthorId(), user.getUserId()) && user.getPermission() < 2) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No permission to get one student all programming");
            resp.add(map);
            return resp;
        }

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_id", studentId);
        List<User> userList = userMapper.selectList(userQueryWrapper);
        if (userList.isEmpty()) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No such student");
            resp.add(map);
            return resp;
        }

        User student = userList.get(0);
        QueryWrapper<StudentNPs> studentNPsQueryWrapper = new QueryWrapper<>();
        studentNPsQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        studentNPsQueryWrapper.eq("student_id", student.getUserId());
        List<StudentNPs> studentNPsList = studentNPsMapper.selectList(studentNPsQueryWrapper);
        if (studentNPsList.isEmpty()) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "Student not in this problem set");
            resp.add(map);
            return resp;
        }

        // { programming_id: '1', p_title: 'Programming 1', p_total_score: '25', pa_actual_score: '20', pa_status: '已作答' },
        QueryWrapper<PNPs> pnPsQueryWrapper = new QueryWrapper<>();
        pnPsQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        pnPsQueryWrapper.orderByAsc("programming_id");
        List<PNPs> pnPsList = pnPsMapper.selectList(pnPsQueryWrapper);
        List<Map<String, String>> resp = new ArrayList<>();
        for (PNPs pnPs : pnPsList) {
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
            programmingAnswerQueryWrapper.eq("author_id", student.getUserId());
            List<ProgrammingAnswer> programmingAnswerList = programmingAnswerMapper.selectList(programmingAnswerQueryWrapper);

            if (programmingAnswerList.isEmpty()) {
                map.put("pa_status", "Not Answered");
                map.put("pa_actual_score", "0");
            } else {
                ProgrammingAnswer programmingAnswer = programmingAnswerList.get(0);
                map.put("pa_status", "Answered");
                map.put("pa_actual_score", programmingAnswer.getPaActualScore().toString());
            }
            resp.add(map);
        }

        return resp;
    }

    @Override
    public Map<String, String> getOneStudentOneObjectiveProblem(int problemSetId, int studentId, int objectiveProblemId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 1) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No permission to get one student one objective problem");
            return resp;
        }

        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No such problem set");
            return resp;
        }

        ProblemSet problemSet = problemSetList.get(0);
        if (!Objects.equals(problemSet.getPsAuthorId(), user.getUserId()) && user.getPermission() < 2) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Teacher cannot get one student one objective problem from problem set created by others");
            return resp;
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_id", studentId);
        List<User> userList = userMapper.selectList(userQueryWrapper);
        if (userList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No such student");
            return resp;
        }

        User student = userList.get(0);
        QueryWrapper<StudentNPs> studentNPsQueryWrapper = new QueryWrapper<>();
        studentNPsQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        studentNPsQueryWrapper.eq("student_id", student.getUserId());
        List<StudentNPs> studentNPsList = studentNPsMapper.selectList(studentNPsQueryWrapper);
        if (studentNPsList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Student not in this problem set");
            return resp;
        }

        QueryWrapper<ObjectiveProblem> objectiveProblemQueryWrapper = new QueryWrapper<>();
        objectiveProblemQueryWrapper.eq("objective_problem_id", objectiveProblemId);
        List<ObjectiveProblem> objectiveProblemList = objectiveProblemMapper.selectList(objectiveProblemQueryWrapper);
        if (objectiveProblemList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No such objective problem");
            return resp;
        }

        ObjectiveProblem objectiveProblem = objectiveProblemList.get(0);
        QueryWrapper<OpNPs> opNPsQueryWrapper = new QueryWrapper<>();
        opNPsQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        opNPsQueryWrapper.eq("objective_problem_id", objectiveProblem.getObjectiveProblemId());
        List<OpNPs> opNPsList = opNPsMapper.selectList(opNPsQueryWrapper);
        if (opNPsList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Objective problem not in this problem set");
            return resp;
        }

        //         op_description: '',
        //        op_total_score: '--',
        //        opa_actual_score: '--',
        //        op_correct_answer: '--',
        //
        //        student_name: 'aa',
        //        student_username: 'bb',
        //
        //        opa_actual_answer: '',

        Map<String, String> resp = new HashMap<>();
        resp.put("error_message", "success");
        resp.put("objective_problem_id", objectiveProblem.getObjectiveProblemId().toString());
        resp.put("op_description", objectiveProblem.getOpDescription());
        resp.put("op_total_score", objectiveProblem.getOpTotalScore().toString());
        resp.put("op_correct_answer", objectiveProblem.getOpCorrectAnswer());

        resp.put("student_id", student.getUserId().toString());
        resp.put("student_name", student.getName());
        resp.put("student_username", student.getUsername());

        QueryWrapper<ObjectiveProblemAnswer> objectiveProblemAnswerQueryWrapper = new QueryWrapper<>();
        objectiveProblemAnswerQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        objectiveProblemAnswerQueryWrapper.eq("objective_problem_id", objectiveProblem.getObjectiveProblemId());
        objectiveProblemAnswerQueryWrapper.eq("author_id", student.getUserId());
        List<ObjectiveProblemAnswer> objectiveProblemAnswerList = objectiveProblemAnswerMapper.selectList(objectiveProblemAnswerQueryWrapper);
        if (objectiveProblemAnswerList.isEmpty()) {
            resp.put("opa_actual_answer", "");
            resp.put("opa_actual_score", "0");
        } else {
            ObjectiveProblemAnswer objectiveProblemAnswer = objectiveProblemAnswerList.get(0);
            resp.put("opa_actual_answer", objectiveProblemAnswer.getOpaActualAnswer());
            resp.put("opa_actual_score", objectiveProblemAnswer.getOpaActualScore().toString());
        }
        return resp;
    }

    @Override
    public Map<String, String> getOneStudentOneProgramming(int problemSetId, int studentId, int programmingId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 1) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No permission to get one student one objective problem");
            return resp;
        }

        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No such problem set");
            return resp;
        }

        ProblemSet problemSet = problemSetList.get(0);
        if (!Objects.equals(problemSet.getPsAuthorId(), user.getUserId()) && user.getPermission() < 2) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Teacher cannot get one student one objective problem from problem set created by others");
            return resp;
        }

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_id", studentId);
        List<User> userList = userMapper.selectList(userQueryWrapper);
        if (userList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No such student");
            return resp;
        }

        User student = userList.get(0);
        QueryWrapper<StudentNPs> studentNPsQueryWrapper = new QueryWrapper<>();
        studentNPsQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        studentNPsQueryWrapper.eq("student_id", student.getUserId());
        List<StudentNPs> studentNPsList = studentNPsMapper.selectList(studentNPsQueryWrapper);
        if (studentNPsList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Student not in this problem set");
            return resp;
        }

        QueryWrapper<Programming> programmingQueryWrapper = new QueryWrapper<>();
        programmingQueryWrapper.eq("programming_id", programmingId);
        List<Programming> programmingList = programmingMapper.selectList(programmingQueryWrapper);
        if (programmingList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No such programming");
            return resp;
        }

        Programming programming = programmingList.get(0);
        QueryWrapper<PNPs> pnPsQueryWrapper = new QueryWrapper<>();
        pnPsQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        pnPsQueryWrapper.eq("programming_id", programming.getProgrammingId());
        List<PNPs> pnPsList = pnPsMapper.selectList(pnPsQueryWrapper);
        if (pnPsList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Programming not in this problem set");
            return resp;
        }

        // p_title: '',
        //        p_description: '',
        //        p_total_score: '--',
        //        time_limit: '--',
        //        code_size_limit: '--',
        //
        //        student_name: 'aa',
        //        student_username: 'bb',
        //
        //        pa_code: '',
        //        pa_actual_score: '--',
        //        pass_count: '--',
        //        tc_count: '--',

        Map<String, String> resp = new HashMap<>();
        resp.put("error_message", "success");
        resp.put("programming_id", programming.getProgrammingId().toString());
        resp.put("p_title", programming.getPTitle());
        resp.put("p_description", programming.getPDescription());
        resp.put("p_total_score", programming.getPTotalScore().toString());
        resp.put("time_limit", programming.getTimeLimit().toString());
        resp.put("code_size_limit", programming.getCodeSizeLimit().toString());
        QueryWrapper<TestCase> testCaseQueryWrapper = new QueryWrapper<>();
        testCaseQueryWrapper.eq("programming_id", programming.getProgrammingId());
        int tcCount = Math.toIntExact(testCaseMapper.selectCount(testCaseQueryWrapper));
        resp.put("tc_count", String.valueOf(tcCount));

        resp.put("student_id", student.getUserId().toString());
        resp.put("student_name", student.getName());
        resp.put("student_username", student.getUsername());

        QueryWrapper<ProgrammingAnswer> programmingAnswerQueryWrapper = new QueryWrapper<>();
        programmingAnswerQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        programmingAnswerQueryWrapper.eq("programming_id", programming.getProgrammingId());
        programmingAnswerQueryWrapper.eq("author_id", student.getUserId());

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

        return resp;
    }
    @Override
    public List<Map<String, String>> getAllProgrammingRecord(int problemSetId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        // check permisssion
        if (user.getPermission() < 1) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No permission to obtain problem set record");
            resp.add(map);
            return resp;
        }

        // query problem set
        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No problem set under this ID");
            resp.add(map);
            return resp;
        }

        //
        ProblemSet problemSet = problemSetList.get(0);
        if (!Objects.equals(problemSet.getPsAuthorId(), user.getUserId()) && user.getPermission() < 2) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "You cannot query records of problem sets created by others");
            resp.add(map);
            return resp;
        }

        // return message
        // { programming_id: '4', p_title: 'Question 4', p_correct_count: '3', p_answer_count: '7' }
        List<Map<String, String>> resp = new ArrayList<>();
        QueryWrapper<PNPs> pnPsQueryWrapper = new QueryWrapper<>();
        pnPsQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        pnPsQueryWrapper.orderByAsc("programming_id");
        List<PNPs> pnPsList = pnPsMapper.selectList(pnPsQueryWrapper);
        for (PNPs pnPs : pnPsList) {
            Map<String, String> map = new HashMap<>();
            QueryWrapper<Programming> programmingQueryWrapper = new QueryWrapper<>();
            programmingQueryWrapper.eq("programming_id", pnPs.getProgrammingId());
            Programming programming = programmingMapper.selectOne(programmingQueryWrapper);
            map.put("programming_id", programming.getProgrammingId().toString());
            map.put("p_title", programming.getPTitle());

            // count how many people answered them and how many people get them correct
            Integer pTotalScore = programming.getPTotalScore();
            int pCorrectCount = 0;
            QueryWrapper<ProgrammingAnswer> programmingAnswerQueryWrapper = new QueryWrapper<>();
            programmingAnswerQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
            programmingAnswerQueryWrapper.eq("programming_id", programming.getProgrammingId());
            List<ProgrammingAnswer> programmingAnswerList = programmingAnswerMapper.selectList(programmingAnswerQueryWrapper);
            int pAnswerCount = programmingAnswerList.size();
            for (ProgrammingAnswer programmingAnswer : programmingAnswerList) {
                if (pTotalScore.equals(programmingAnswer.getPaActualScore())) {
                    pCorrectCount += 1;
                }
            }
            map.put("p_correct_count", String.valueOf(pCorrectCount));
            map.put("p_answer_count", String.valueOf(pAnswerCount));
            resp.add(map);
        }

        return resp;
    }

    @Override
    public Map<String, String> getOneStudentRecord(int problemSetId, int studentId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        // check permission
        if (user.getPermission() < 1) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No permission to obtain student record for this problem set");
            return resp;
        }

        // query problem set
        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No problem set under this ID");
            return resp;
        }

        // check if you have permission to this record
        ProblemSet problemSet = problemSetList.get(0);
        if (!Objects.equals(problemSet.getPsAuthorId(), user.getUserId()) && user.getPermission() < 2) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No permission to obtain student record from problem sets created by others");
            return resp;
        }

        // check if studnt exist
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_id", studentId);
        List<User> userList = userMapper.selectList(userQueryWrapper);
        if (userList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No student under this ID");
            return resp;
        }

        // check if student belong to this problem set
        User student = userList.get(0);
        QueryWrapper<StudentNPs> studentNPsQueryWrapper = new QueryWrapper<>();
        studentNPsQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        studentNPsQueryWrapper.eq("student_id", student.getUserId());
        List<StudentNPs> studentNPsList = studentNPsMapper.selectList(studentNPsQueryWrapper);
        if (studentNPsList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Student does not belong to this problem set");
            return resp;
        }

        // prepare to return info
        //         ps_name: 'test1',
        //        student_name: 'aa',
        //        student_username: 'bb',
        //        ps_start_time: '',
        //        ps_end_time: '',
        //        duration: '',
        //        ps_author_name: '',
        //
        //        ps_total_score: '--',
        //        ps_actual_score: '--',
        //
        //        first_start_time: '',
        //
        //        ps_status_message: '',
        Map<String, String> resp = new HashMap<>();
        resp.put("error_message", "success");
        resp.put("problem_set_id", problemSet.getProblemSetId().toString());
        resp.put("student_id", student.getUserId().toString());
        resp.put("ps_name", problemSet.getPsName());
        resp.put("student_name", student.getName());
        resp.put("student_username", student.getUsername());
        resp.put("ps_start_time", problemSet.getPsStartTime().toString());
        resp.put("ps_end_time", problemSet.getPsEndTime().toString());
        resp.put("duration", problemSet.getDuration().toString());

        Integer psAuthorId = problemSet.getPsAuthorId();
        QueryWrapper<User> psAuthorQueryWrapper = new QueryWrapper<>();
        psAuthorQueryWrapper.eq("user_id", psAuthorId);
        User psAuthor = userMapper.selectOne(psAuthorQueryWrapper);
        resp.put("ps_author_name", psAuthor.getName());

        // total score
        int psTotalScore = 0;
        // objective problems
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

        // programming problems
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

        // count the scores
        int psActualScore = 0;
        QueryWrapper<ObjectiveProblemAnswer> objectiveProblemAnswerQueryWrapper = new QueryWrapper<>();
        objectiveProblemAnswerQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        objectiveProblemAnswerQueryWrapper.eq("author_id", student.getUserId());
        List<ObjectiveProblemAnswer> objectiveProblemAnswerList = objectiveProblemAnswerMapper.selectList(objectiveProblemAnswerQueryWrapper);
        for (ObjectiveProblemAnswer objectiveProblemAnswer : objectiveProblemAnswerList) {
            psActualScore += objectiveProblemAnswer.getOpaActualScore();
        }
        // programming problems
        QueryWrapper<ProgrammingAnswer> programmingAnswerQueryWrapper = new QueryWrapper<>();
        programmingAnswerQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        programmingAnswerQueryWrapper.eq("author_id", student.getUserId());
        List<ProgrammingAnswer> programmingAnswerList = programmingAnswerMapper.selectList(programmingAnswerQueryWrapper);
        for (ProgrammingAnswer programmingAnswer : programmingAnswerList) {
            psActualScore += programmingAnswer.getPaActualScore();
        }

        resp.put("ps_actual_score", String.valueOf(psActualScore));

        StudentNPs studentNPs = studentNPsList.get(0);
        if (studentNPs.getFirstStartTime() == null) {
            resp.put("first_start_time", "");
        } else {
            resp.put("first_start_time", studentNPs.getFirstStartTime().toString());
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(problemSet.getPsStartTime())) {
            resp.put("ps_status_message", "The problem set has not started");
        } else if (now.isAfter(problemSet.getPsEndTime())) {
            resp.put("ps_status_message", "The problem set has ended");
        } else {
            resp.put("ps_status_message", "The problem set has started");
        }

        return resp;
    }

    @Override
    public List<Map<String, String>> getOneStudentAllObjectiveProblem(int problemSetId, int studentId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        // check permission
        if (user.getPermission() < 1) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No permission to obtain student record of objective problems for this problem set");
            resp.add(map);
            return resp;
        }

        // query problem set
        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No problem set under this ID");
            resp.add(map);
            return resp;
        }

        //
        ProblemSet problemSet = problemSetList.get(0);
        if (!Objects.equals(problemSet.getPsAuthorId(), user.getUserId()) && user.getPermission() < 2) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No permission to obtain student record of objective problems created by others");
            resp.add(map);
            return resp;
        }

        // check if student exist
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_id", studentId);
        List<User> userList = userMapper.selectList(userQueryWrapper);
        if (userList.isEmpty()) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No student under this ID");
            resp.add(map);
            return resp;
        }

        // check if student belong to this problem set
        User student = userList.get(0);
        QueryWrapper<StudentNPs> studentNPsQueryWrapper = new QueryWrapper<>();
        studentNPsQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        studentNPsQueryWrapper.eq("student_id", student.getUserId());
        List<StudentNPs> studentNPsList = studentNPsMapper.selectList(studentNPsQueryWrapper);
        if (studentNPsList.isEmpty()) {
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "Student does not belong to problem set");
            resp.add(map);
            return resp;
        }

        // prepare to return info
        // { objective_problem_id: '1', op_description: 'Question 1', op_total_score: '10', opa_actual_score: '--', opa_status: '已作答' },
        QueryWrapper<OpNPs> opNPsQueryWrapper = new QueryWrapper<>();
        opNPsQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        opNPsQueryWrapper.orderByAsc("objective_problem_id");
        List<OpNPs> opNPsList = opNPsMapper.selectList(opNPsQueryWrapper);
        List<Map<String, String>> resp = new ArrayList<>();
        for (OpNPs opNPs : opNPsList) {
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
            objectiveProblemAnswerQueryWrapper.eq("author_id", student.getUserId());
            List<ObjectiveProblemAnswer> objectiveProblemAnswerList = objectiveProblemAnswerMapper.selectList(objectiveProblemAnswerQueryWrapper);
            if (objectiveProblemAnswerList.isEmpty()) {
                map.put("opa_status", "Not Answered");
                map.put("opa_actual_score", "0");
            } else {
                ObjectiveProblemAnswer objectiveProblemAnswer = objectiveProblemAnswerList.get(0);
                map.put("opa_status", "Answered");
                map.put("opa_actual_score", objectiveProblemAnswer.getOpaActualScore().toString());
            }
            resp.add(map);
        }
        return resp;
    }
}
