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
                map.put("pa_status", "Unanswered");
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
}
