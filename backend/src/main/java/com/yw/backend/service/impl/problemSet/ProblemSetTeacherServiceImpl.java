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
            resp.put("ps_status_message", "The problem set has not started yet");
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

}
