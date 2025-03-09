package com.yw.backend.service.impl.setManage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yw.backend.mapper.*;
import com.yw.backend.pojo.*;
import com.yw.backend.service.impl.utils.UserDetailsImpl;
import com.yw.backend.service.setManage.SetManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class SetManageServiceImpl implements SetManageService {
    @Autowired
    private ProblemSetMapper problemSetMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProgrammingMapper programmingMapper;
    private StudentNPsMapper studentNPsMapper;
    @Autowired
    private PNPsMapper pnPsMapper;
    private ObjectiveProblemAnswerMapper objectiveProblemAnswerMapper;
    @Autowired
    private ProgrammingAnswerMapper programmingAnswerMapper;
    @Autowired
    private ObjectiveProblemMapper objectiveProblemMapper;
    @Autowired
    private OpNPsMapper opNPsMapper;

    @Override
    public Map<String, String> create(String psName, LocalDateTime psStartTime, LocalDateTime psEndTime, int duration) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        // check permission
        if (user.getPermission() < 1) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "no permission to create problem set");
            return resp;
        }

        // check input
        if (psName == null || psName.length() == 0) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "the problem set name cannot be empty");
            return resp;
        } else if (psName.length() > 100) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "the problem set name cannot exceed 100 characters");
            return resp;
        }

        if (psStartTime.isAfter(psEndTime)) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "the start time of the problem set cannot be after the end time");
            return resp;
        }

        if (duration < 0) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "the exam time cannot be a negative number");
            return resp;
        } else if (psStartTime.plusMinutes(duration).isAfter(psEndTime)) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "the exam time exceed the duration of the problem set间");
            return resp;
        }
        // calculate id
        int problemSetMaxId = 0;
        List<ProblemSet> problemSetList = problemSetMapper.selectList(null);
        if (!problemSetList.isEmpty())
            for (ProblemSet problemSet : problemSetList)
                if (problemSet.getProblemSetId() > problemSetMaxId)
                    problemSetMaxId = problemSet.getProblemSetId();
        Integer problemSetId = problemSetMaxId + 1;

        // create instance
        ProblemSet problemSet = new ProblemSet(
                problemSetId,
                psName,
                user.getUserId(),
                psStartTime,
                psEndTime,
                duration
        );
        problemSetMapper.insert(problemSet);
        // return id
        Map<String, String> resp = new HashMap<>();
        resp.put("error_message", "success");
        resp.put("problem_set_id", problemSetId.toString());
        return resp;
    }

    @Override
    public Map<String, String> delete(int problemSetId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        // check permission
        if (user.getPermission() < 1) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "no operation permission to delete problem set");
            return resp;
        }

        // check if this problem exists
        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "no problem set found with the given ID");
            return resp;
        }

        // check if this problem can be deleted
        ProblemSet problemSet = problemSetList.get(0);
        if (!Objects.equals(problemSet.getPsAuthorId(), user.getUserId()) && user.getPermission() < 2) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "teachers cannot delete problem set created by others");
            return resp;
        }

        // delete
        problemSetMapper.delete(problemSetQueryWrapper);
        Map<String, String> resp = new HashMap<>();
        resp.put("error_message", "success");
        return resp;
    }

    @Override
    public List<Map<String, String>> searchObjectiveProblem(int problemSetId, String opDescription, String opTag, int opDifficultyMin, int opDifficultyMax) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        // check permission
        if (user.getPermission() < 1) {
            System.out.println("no permission to query objective problem in problem set");
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "no permission to query objective problem in problem set");
            resp.add(map);
            return resp;
        }

        // check if the problem set exist
        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            System.out.println("no problem set found with the given ID");
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "no problem set found with the given ID");
            resp.add(map);
            return resp;
        }

        // query questions, making sure to remove those that have been added
        QueryWrapper<ObjectiveProblem> objectiveProblemQueryWrapper = new QueryWrapper<>();
        objectiveProblemQueryWrapper.like("op_description", opDescription);
        objectiveProblemQueryWrapper.like("op_tag", opTag);
        objectiveProblemQueryWrapper.ge("op_difficulty", opDifficultyMin);
        objectiveProblemQueryWrapper.le("op_difficulty", opDifficultyMax);
        List<ObjectiveProblem> objectiveProblemList = objectiveProblemMapper.selectList(objectiveProblemQueryWrapper);
        List<Map<String, String>> resp = new ArrayList<>();
        for (ObjectiveProblem objectiveProblem : objectiveProblemList) {
            // check if this problem has been added to this problem set
            QueryWrapper<OpNPs> checkAddedQueryWrapper = new QueryWrapper<>();
            checkAddedQueryWrapper.eq("problem_set_id", problemSetId).eq("objective_problem_id", objectiveProblem.getObjectiveProblemId());
            int addCount = Math.toIntExact(opNPsMapper.selectCount(checkAddedQueryWrapper));
            if (addCount != 0) {
                continue;
            }

            // input the problem information to resp
            Map<String, String> map = new HashMap<>();
            map.put("objective_problem_id", objectiveProblem.getObjectiveProblemId().toString());
            String searchedOpDescription = objectiveProblem.getOpDescription();
            map.put("op_description", searchedOpDescription.substring(0, Math.min(searchedOpDescription.length(), 60)) + "...");
            map.put("op_tag", objectiveProblem.getOpTag());
            map.put("op_difficulty", objectiveProblem.getOpDifficulty().toString());

            QueryWrapper<OpNPs> getUseCountQueryWrapper = new QueryWrapper<>();
            getUseCountQueryWrapper.eq("objective_problem_id", objectiveProblem.getObjectiveProblemId());
            int opUseCount = Math.toIntExact(opNPsMapper.selectCount(getUseCountQueryWrapper));
            map.put("op_use_count", String.valueOf(opUseCount));
            resp.add(map);
        }
        return resp;
    }

    @Override
    public Map<String, String> addObjectiveProblem(int problemSetId, int objectiveProblemId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        // check permission
        if (user.getPermission() < 1) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No permission to add objective problems to the problem set");
            return resp;
        }

        // Check if both IDs exist
        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No problem set found with the given ID");
            return resp;
        }

        QueryWrapper<ObjectiveProblem> objectiveProblemQueryWrapper = new QueryWrapper<>();
        objectiveProblemQueryWrapper.eq("objective_problem_id", objectiveProblemId);
        List<ObjectiveProblem> objectiveProblemList = objectiveProblemMapper.selectList(objectiveProblemQueryWrapper);
        if (objectiveProblemList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No objective problem found with the given ID");
            return resp;
        }

        // Check if this question set can be modified
        ProblemSet problemSet = problemSetList.get(0);
        if (!Objects.equals(problemSet.getPsAuthorId(), user.getUserId()) && user.getPermission() < 2) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Teachers are not allowed to add objective problems to problem sets created by others");
            return resp;
        }

        // Check if the question has already been added
        ObjectiveProblem objectiveProblem = objectiveProblemList.get(0);
        QueryWrapper<OpNPs> opNPsQueryWrapper = new QueryWrapper<>();
        opNPsQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        opNPsQueryWrapper.eq("objective_problem_id", objectiveProblem.getObjectiveProblemId());
        int count = Math.toIntExact(opNPsMapper.selectCount(opNPsQueryWrapper));
        if (count != 0) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "The objective problem has already been added to the problem set");
            return resp;
        }

        // add
        OpNPs opNPs = new OpNPs(
                objectiveProblem.getObjectiveProblemId(),
                problemSet.getProblemSetId()
        );
        opNPsMapper.insert(opNPs);
        Map<String, String> resp = new HashMap<>();
        resp.put("error_message", "success");
        return resp;
    }

    @Override
    public Map<String, String> deleteObjectiveProblem(int problemSetId, int objectiveProblemId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        // check permission
        if (user.getPermission() < 1) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No permission to delete objective problems from the problem set");
            return resp;
        }

        // Check if both IDs exist
        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No problem set found with the given ID");
            return resp;
        }

        QueryWrapper<ObjectiveProblem> objectiveProblemQueryWrapper = new QueryWrapper<>();
        objectiveProblemQueryWrapper.eq("objective_problem_id", objectiveProblemId);
        List<ObjectiveProblem> objectiveProblemList = objectiveProblemMapper.selectList(objectiveProblemQueryWrapper);
        if (objectiveProblemList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No objective problem found with the given ID");
            return resp;
        }

        // Check if this problem set can be modified
        ProblemSet problemSet = problemSetList.get(0);
        if (!Objects.equals(problemSet.getPsAuthorId(), user.getUserId()) && user.getPermission() < 2) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Teachers are not allowed to delete objective problems from problem sets created by others");
            return resp;
        }

        // Check if the problem has already been deleted
        ObjectiveProblem objectiveProblem = objectiveProblemList.get(0);
        QueryWrapper<OpNPs> deleteQueryWrapper = new QueryWrapper<>();
        deleteQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        deleteQueryWrapper.eq("objective_problem_id", objectiveProblem.getObjectiveProblemId());
        int count = Math.toIntExact(opNPsMapper.selectCount(deleteQueryWrapper));
        if (count == 0) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "The objective problem has been removed from the problem set");
            return resp;
        }

        // delete
        opNPsMapper.delete(deleteQueryWrapper);

        // Delete all answer records related to this problem in this exam
        QueryWrapper<ObjectiveProblemAnswer> objectiveProblemAnswerQueryWrapper = new QueryWrapper<>();
        objectiveProblemAnswerQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        objectiveProblemAnswerQueryWrapper.eq("objective_problem_id", objectiveProblem.getObjectiveProblemId());
        objectiveProblemAnswerMapper.delete(objectiveProblemAnswerQueryWrapper);

        Map<String, String> resp = new HashMap<>();
        resp.put("error_message", "success");
        return resp;
    }

    @Override
    public List<Map<String, String>> getAddedObjectiveProblem(int problemSetId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        // check permission
        if (user.getPermission() < 1) {
            System.out.println("No permission to query objective problems in the problem set");
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No permission to query objective problems in the problem set");
            resp.add(map);
            return resp;
        }

        // Check if the problem exists
        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            System.out.println("No problem set found with the given ID");
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No problem set found with the given ID");
            resp.add(map);
            return resp;
        }

        // Query the objective problems associated with this problem set
        QueryWrapper<OpNPs> opNPsQueryWrapper = new QueryWrapper<>();
        opNPsQueryWrapper.eq("problem_set_id", problemSetId);
        opNPsQueryWrapper.orderByAsc("objective_problem_id");
        List<OpNPs> opNPsList = opNPsMapper.selectList(opNPsQueryWrapper);

        // Convert the objective problem information into a list and return it
        List<Map<String, String>> resp = new ArrayList<>();
        for (OpNPs opNPs : opNPsList) {
            Map<String, String> map = new HashMap<>();

            Integer objectiveProblemId = opNPs.getObjectiveProblemId();

            QueryWrapper<ObjectiveProblem> objectiveProblemQueryWrapper = new QueryWrapper<>();
            objectiveProblemQueryWrapper.eq("objective_problem_id", objectiveProblemId);
            ObjectiveProblem objectiveProblem = objectiveProblemMapper.selectOne(objectiveProblemQueryWrapper);

            map.put("objective_problem_id", objectiveProblem.getObjectiveProblemId().toString());
            String searchedOpDescription = objectiveProblem.getOpDescription();
            map.put("op_description", searchedOpDescription.substring(0, Math.min(searchedOpDescription.length(), 60)) + "...");
            map.put("op_tag", objectiveProblem.getOpTag());
            map.put("op_difficulty", objectiveProblem.getOpDifficulty().toString());

            QueryWrapper<OpNPs> getUseCountQueryWrapper = new QueryWrapper<>();
            getUseCountQueryWrapper.eq("objective_problem_id", objectiveProblem.getObjectiveProblemId());
            int opUseCount = Math.toIntExact(opNPsMapper.selectCount(getUseCountQueryWrapper));
            map.put("op_use_count", String.valueOf(opUseCount));
            resp.add(map);
        }
        return resp;
    }
    @Override
    public Map<String, String> update(int problemSetId, String psName, LocalDateTime psStartTime, LocalDateTime psEndTime, int duration) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 1) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No permission in updating the problem set");
            return resp;
        }

        // search for the problem set
        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No problem set with this ID");
            return resp;
        }

        //check user permission
        ProblemSet oldProblemSet = problemSetList.get(0);
        if (!Objects.equals(oldProblemSet.getPsAuthorId(), user.getUserId()) && user.getPermission() < 2) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "You cannot modify problem sets created by others");
            return resp;
        }

        if (psName == null || psName.length() == 0) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Problem set name cannot be empty");
            return resp;
        } else if (psName.length() > 100) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Problem set name cannot exceed 100 characters");
            return resp;
        }

        if (psStartTime.isAfter(psEndTime)) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Erroneous problem set start or finish time");
            return resp;
        }

        if (duration < 0) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Test time cannot be negative");
            return resp;
        } else if (psStartTime.plusMinutes(duration).isAfter(psEndTime)) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Test time exceeds problem set time");
            return resp;
        }

        ProblemSet newProblemSet = new ProblemSet(
                oldProblemSet.getProblemSetId(),
                psName,
                oldProblemSet.getPsAuthorId(),
                psStartTime,
                psEndTime,
                duration
        );
        UpdateWrapper<ProblemSet> problemSetUpdateWrapper = new UpdateWrapper<>();
        problemSetUpdateWrapper.eq("problem_set_id", oldProblemSet.getProblemSetId());
        problemSetMapper.update(newProblemSet, problemSetUpdateWrapper);
        Map<String, String> resp = new HashMap<>();
        resp.put("error_message", "success");
        return resp;
    }

    @Override
    public Map<String, String> getOne(int problemSetId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 1) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No permission to obtain the problem set");
            return resp;
        }

        // query question
        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No problem set with this ID");
            return resp;
        }
        ProblemSet problemSet = problemSetList.get(0);
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
        return resp;
    }


    @Override
    public List<Map<String, String>> searchProgramming(int problemSetId, String pTitle, String pTag, int pDifficultyMin, int pDifficultyMax) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 1) {
            System.out.println("No permission to search for the programming problems");
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No permission to search for the programming problems");
            resp.add(map);
            return resp;
        }

        // check if problem exists
        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            System.out.println("No problem set with this ID");
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No problem set with this ID");
            resp.add(map);
            return resp;
        }

        // query problem. remember to remove problems that are already added
        QueryWrapper<Programming> programmingQueryWrapper = new QueryWrapper<>();
        programmingQueryWrapper.like("p_title", pTitle);
        programmingQueryWrapper.like("p_tag", pTag);
        programmingQueryWrapper.ge("p_difficulty", pDifficultyMin);
        programmingQueryWrapper.le("p_difficulty", pDifficultyMax);
        List<Programming> programmingList = programmingMapper.selectList(programmingQueryWrapper);
        List<Map<String, String>> resp = new ArrayList<>();
        for (Programming programming : programmingList) {
            // check if this problem is already added to the problem set
            QueryWrapper<PNPs> checkAddedQueryWrapper = new QueryWrapper<>();
            checkAddedQueryWrapper.eq("problem_set_id", problemSetId);
            checkAddedQueryWrapper.eq("programming_id", programming.getProgrammingId());
            int addCount = Math.toIntExact(pnPsMapper.selectCount(checkAddedQueryWrapper));
            if (addCount != 0) {
                continue;
            }

            // put problem info into resp
            Map<String, String> map = new HashMap<>();
            map.put("programming_id", programming.getProgrammingId().toString());
            map.put("p_title", programming.getPTitle());
            map.put("p_tag", programming.getPTag());
            map.put("p_difficulty", programming.getPDifficulty().toString());

            QueryWrapper<PNPs> getUseCountQueryWrapper = new QueryWrapper<>();
            getUseCountQueryWrapper.eq("programming_id", programming.getProgrammingId());
            int pUseCount = Math.toIntExact(pnPsMapper.selectCount(getUseCountQueryWrapper));
            map.put("p_use_count", String.valueOf(pUseCount));
            resp.add(map);
        }
        return resp;
    }

    @Override
    public Map<String, String> addProgramming(int problemSetId, int programmingId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 1) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No permission to add problems to problem set");
            return resp;
        }

        //check if two id exists
        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No problem set with this ID");
            return resp;
        }

        QueryWrapper<Programming> programmingQueryWrapper = new QueryWrapper<>();
        programmingQueryWrapper.eq("programming_id", programmingId);
        List<Programming> programmingList = programmingMapper.selectList(programmingQueryWrapper);
        if (programmingList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No problem with this ID");
            return resp;
        }

        //check if user can edit this problem set
        ProblemSet problemSet = problemSetList.get(0);
        if (!Objects.equals(problemSet.getPsAuthorId(), user.getUserId()) && user.getPermission() < 2) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No permission to add problems to problem sets created by others");
            return resp;
        }

        //check if problem is already added
        Programming programming = programmingList.get(0);
        QueryWrapper<PNPs> pnPsQueryWrapper = new QueryWrapper<>();
        pnPsQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        pnPsQueryWrapper.eq("programming_id", programming.getProgrammingId());
        int count = Math.toIntExact(pnPsMapper.selectCount(pnPsQueryWrapper));
        if (count != 0) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Problem already added to problem set");
            return resp;
        }

        PNPs pnPs = new PNPs(
                problemSet.getProblemSetId(),
                programming.getProgrammingId()
        );
        pnPsMapper.insert(pnPs);
        Map<String, String> resp = new HashMap<>();
        resp.put("error_message", "success");
        return resp;
    }

    @Override
    public Map<String, String> deleteProgramming(int problemSetId, int programmingId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 1) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No permission to delete problems from problem set");
            return resp;
        }

        //check if two id exist
        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No problem set with this ID");
            return resp;
        }

        QueryWrapper<Programming> programmingQueryWrapper = new QueryWrapper<>();
        programmingQueryWrapper.eq("programming_id", programmingId);
        List<Programming> programmingList = programmingMapper.selectList(programmingQueryWrapper);
        if (programmingList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No problem with this ID");
            return resp;
        }

        //check user permission
        ProblemSet problemSet = problemSetList.get(0);
        if (!Objects.equals(problemSet.getPsAuthorId(), user.getUserId()) && user.getPermission() < 2) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No permission to delete problems from problem set created by others");
            return resp;
        }

        //check if the problem is already deleted
        Programming programming = programmingList.get(0);
        QueryWrapper<PNPs> deletQueryWrapper = new QueryWrapper<>();
        deletQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        deletQueryWrapper.eq("programming_id", programming.getProgrammingId());
        int count = Math.toIntExact(pnPsMapper.selectCount(deletQueryWrapper));
        if (count == 0) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Problem deleted from problem set");
            return resp;
        }

        //delete
        pnPsMapper.delete(deletQueryWrapper);

        //delete record in the exam
        QueryWrapper<ProgrammingAnswer> programmingAnswerQueryWrapper = new QueryWrapper<>();
        programmingAnswerQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        programmingAnswerQueryWrapper.eq("programming_id", programming.getProgrammingId());
        programmingAnswerMapper.delete(programmingAnswerQueryWrapper);

        Map<String, String> resp = new HashMap<>();
        resp.put("error_message", "success");
        return resp;
    }

    @Override
    public List<Map<String, String>> getAddedProgramming(int problemSetId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 1) {
            System.out.println("No permission to query problems from problem set");
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No permission to query problems from problem set");
            resp.add(map);
            return resp;
        }

        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            System.out.println("No problem set with this ID");
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No problem set with this ID");
            resp.add(map);
            return resp;
        }

        // check programming problems link to this set
        QueryWrapper<PNPs> pnPsQueryWrapper = new QueryWrapper<>();
        pnPsQueryWrapper.eq("problem_set_id", problemSetId);
        pnPsQueryWrapper.orderByAsc("programming_id");
        List<PNPs> pnPsList = pnPsMapper.selectList(pnPsQueryWrapper);

        //return the programming problems as a list
        List<Map<String, String>> resp = new ArrayList<>();
        for (PNPs pnPs : pnPsList) {
            Map<String, String> map = new HashMap<>();

            Integer programmingId = pnPs.getProgrammingId();

            QueryWrapper<Programming> programmingQueryWrapper = new QueryWrapper<>();
            programmingQueryWrapper.eq("programming_id", programmingId);
            Programming programming = programmingMapper.selectOne(programmingQueryWrapper);

            if (programming == null) {
                continue; // possible nullpointer exception?
            }
            map.put("programming_id", programming.getProgrammingId().toString());
            map.put("p_title", programming.getPTitle());
            map.put("p_tag", programming.getPTag());
            map.put("p_difficulty", programming.getPDifficulty().toString());

            QueryWrapper<PNPs> getUseCountQueryWrapper = new QueryWrapper<>();
            getUseCountQueryWrapper.eq("programming_id", programming.getProgrammingId());
            int pUseCount = Math.toIntExact(pnPsMapper.selectCount(getUseCountQueryWrapper));
            map.put("p_use_count", String.valueOf(pUseCount));
            resp.add(map);
        }
        return resp;
    }

    @Override
    public List<Map<String, String>> getAssignmentList() {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 1) {
            System.out.println("Problem set get list permission denied");
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "Problem set get list permission denied");
            resp.add(map);
            return resp;
        }
        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("duration", 0);
        problemSetQueryWrapper.orderByDesc("problem_set_id");
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        List<Map<String, String>> resp = new ArrayList<>();
        for (ProblemSet problemSet : problemSetList) {
            Map<String, String> map = new HashMap<>();
            map.put("problem_set_id", problemSet.getProblemSetId().toString());
            map.put("ps_name", problemSet.getPsName());
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("user_id", problemSet.getPsAuthorId());
            User author = userMapper.selectOne(userQueryWrapper);
            map.put("ps_author_name", author.getName());
            map.put("ps_start_time", problemSet.getPsStartTime().toString());
            map.put("ps_end_time", problemSet.getPsEndTime().toString());
            resp.add(map);
        }
        return resp;
    }

    @Override
    public List<Map<String, String>> getExamList() {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 1) {
            System.out.println("Problem set get list permission denied");
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "Problem set get list permission denied");
            resp.add(map);
            return resp;
        }
        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.ne("duration", 0);
        problemSetQueryWrapper.orderByDesc("problem_set_id");
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        List<Map<String, String>> resp = new ArrayList<>();
        for (ProblemSet problemSet : problemSetList) {
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
    public List<Map<String, String>> searchStudent(int problemSetId, String username, String name) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 1) {
            System.out.println("Problem set search student permission denied");
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "Problem set search student permission denied");
            resp.add(map);
            return resp;
        }

        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            System.out.println("No such problem set");
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No such problem set");
            resp.add(map);
            return resp;
        }

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.like("username", username);
        userQueryWrapper.like("name", name);
        List<User> studentList = userMapper.selectList(userQueryWrapper);
        List<Map<String, String>> resp = new ArrayList<>();
        for (User student : studentList) {
            QueryWrapper<StudentNPs> checkAddedQueryWrapper = new QueryWrapper<>();
            checkAddedQueryWrapper.eq("problem_set_id", problemSetId);
            checkAddedQueryWrapper.eq("student_id", student.getUserId());
            int addCount = Math.toIntExact(studentNPsMapper.selectCount(checkAddedQueryWrapper));
            if (addCount != 0) {
                continue;
            }

            Map<String, String> map = new HashMap<>();
            map.put("user_id", student.getUserId().toString());
            map.put("username", student.getUsername());
            map.put("name", student.getName());
            map.put("permission", student.getPermission().toString());

            resp.add(map);
        }
        return resp;
    }

    @Override
    public Map<String, String> addStudent(int problemSetId, int userId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 1) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Problem set add student permission denied");
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

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_id", userId);
        List<User> userList = userMapper.selectList(userQueryWrapper);
        if (userList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No such user");
            return resp;
        }

        ProblemSet problemSet = problemSetList.get(0);
        if (!Objects.equals(problemSet.getPsAuthorId(), user.getUserId()) && user.getPermission() < 2) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Teacher cannot add student to others' problem set");
            return resp;
        }

        User student = userList.get(0);
        QueryWrapper<StudentNPs> studentNPsQueryWrapper = new QueryWrapper<>();
        studentNPsQueryWrapper.eq("problem_set_id", problemSetId);
        studentNPsQueryWrapper.eq("student_id", student.getUserId());
        int count = Math.toIntExact(studentNPsMapper.selectCount(studentNPsQueryWrapper));
        if (count != 0) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Student already added to problem set");
            return resp;
        }

        StudentNPs studentNPs = new StudentNPs(
                student.getUserId(),
                problemSet.getProblemSetId(),
                null
        );
        studentNPsMapper.insert(studentNPs);
        Map<String, String> resp = new HashMap<>();
        resp.put("error_message", "success");
        return resp;
    }

    @Override
    public Map<String, String> deleteStudent(int problemSetId, int userId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 1) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Problem set delete student permission denied");
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

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_id", userId);
        List<User> userList = userMapper.selectList(userQueryWrapper);
        if (userList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No such user");
            return resp;
        }

        ProblemSet problemSet = problemSetList.get(0);
        if (!Objects.equals(problemSet.getPsAuthorId(), user.getUserId()) && user.getPermission() < 2) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Teacher cannot delete student from others' problem set");
            return resp;
        }

        User student = userList.get(0);
        QueryWrapper<StudentNPs> deleteQueryWrapper = new QueryWrapper<>();
        deleteQueryWrapper.eq("problem_set_id", problemSetId);
        deleteQueryWrapper.eq("student_id", student.getUserId());
        int count = Math.toIntExact(studentNPsMapper.selectCount(deleteQueryWrapper));
        if (count == 0) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Student not added to problem set");
            return resp;
        }

        studentNPsMapper.delete(deleteQueryWrapper);
        QueryWrapper<ObjectiveProblemAnswer> objectiveProblemAnswerQueryWrapper = new QueryWrapper<>();
        objectiveProblemAnswerQueryWrapper.eq("author_id", student.getUserId());
        objectiveProblemAnswerQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        objectiveProblemAnswerMapper.delete(objectiveProblemAnswerQueryWrapper);

        QueryWrapper<ProgrammingAnswer> programmingAnswerQueryWrapper = new QueryWrapper<>();
        programmingAnswerQueryWrapper.eq("author_id", student.getUserId());
        programmingAnswerQueryWrapper.eq("problem_set_id", problemSet.getProblemSetId());
        programmingAnswerMapper.delete(programmingAnswerQueryWrapper);

        Map<String, String> resp = new HashMap<>();
        resp.put("error_message", "success");
        return resp;
    }

    @Override
    public List<Map<String, String>> getAddedStudent(int problemSetId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 1) {
            System.out.println("Problem set get added student permission denied");
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "Problem set get added student permission denied");
            resp.add(map);
            return resp;
        }

        QueryWrapper<ProblemSet> problemSetQueryWrapper = new QueryWrapper<>();
        problemSetQueryWrapper.eq("problem_set_id", problemSetId);
        List<ProblemSet> problemSetList = problemSetMapper.selectList(problemSetQueryWrapper);
        if (problemSetList.isEmpty()) {
            System.out.println("No such problem set");
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No such problem set");
            resp.add(map);
            return resp;
        }

        QueryWrapper<StudentNPs> studentNPsQueryWrapper = new QueryWrapper<>();
        studentNPsQueryWrapper.eq("problem_set_id", problemSetId);
        studentNPsQueryWrapper.orderByAsc("student_id");
        List<StudentNPs> studentNPsList = studentNPsMapper.selectList(studentNPsQueryWrapper);

        List<Map<String, String>> resp = new ArrayList<>();
        for (StudentNPs studentNPs : studentNPsList) {
            Map<String, String> map = new HashMap<>();

            Integer userId = studentNPs.getStudentId();

            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("user_id", userId);
            User student = userMapper.selectOne(userQueryWrapper);

            map.put("user_id", student.getUserId().toString());
            map.put("username", student.getUsername());
            map.put("name", student.getName());
            map.put("permission", student.getPermission().toString());
            resp.add(map);
        }
        return resp;
    }
}
