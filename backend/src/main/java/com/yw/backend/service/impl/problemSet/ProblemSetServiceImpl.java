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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProblemSetServiceImpl implements ProblemSetService {
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
