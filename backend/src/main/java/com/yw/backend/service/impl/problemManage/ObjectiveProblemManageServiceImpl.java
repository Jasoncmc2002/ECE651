package com.yw.backend.service.impl.problemManage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yw.backend.mapper.ObjectiveProblemMapper;
import com.yw.backend.mapper.OpNPsMapper;
import com.yw.backend.mapper.UserMapper;
import com.yw.backend.pojo.ObjectiveProblem;
import com.yw.backend.pojo.OpNPs;
import com.yw.backend.pojo.User;
import com.yw.backend.service.impl.utils.UserDetailsImpl;
import com.yw.backend.service.problemManage.ObjectiveProblemManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ObjectiveProblemManageServiceImpl implements ObjectiveProblemManageService {
    @Autowired
    private ObjectiveProblemMapper objectiveProblemMapper;

    @Override
    public Map<String, String> update(int objectiveProblemId, String opDescription, int opTotalScore, String opCorrectAnswer, String opTag, int opDifficulty) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 1) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Do not have permission to modify problems");
            return resp;
        }

        QueryWrapper<ObjectiveProblem> objectiveProblemQueryWrapper = new QueryWrapper<>();
        objectiveProblemQueryWrapper.eq("objective_problem_id", objectiveProblemId);
        List<ObjectiveProblem> objectiveProblemList = objectiveProblemMapper.selectList(objectiveProblemQueryWrapper);
        if (objectiveProblemList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Objective problem does not exist");
            return resp;
        }

        ObjectiveProblem oldObjectiveProblem = objectiveProblemList.get(0);
        if (!Objects.equals(oldObjectiveProblem.getAuthorId(), user.getUserId()) && user.getPermission() < 2) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Teacher can only modify his own problems");
            return resp;
        }

        if (opDescription == null || opDescription.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Description cannot be empty");
            return resp;
        } else if (opDescription.length() > 10000) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Description cannot exceed 10000 characters");
            return resp;
        }

        if (opTotalScore <= 0) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Score must be a positive integer");
            return resp;
        }

        if (opCorrectAnswer == null || opCorrectAnswer.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Standard answer cannot be empty");
            return resp;
        } else if (opCorrectAnswer.length() > 1024) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Standard answer cannot exceed 1024 characters");
            return resp;
        }

        if (opTag == null || opTag.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Label cannot be empty");
            return resp;
        } else if (opTag.length() > 100) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Label cannot exceed 100 characters");
            return resp;
        }

        if (opDifficulty <= 0 || opDifficulty > 5) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Difficulty must be a positive integer between 1 and 5");
            return resp;
        }

        ObjectiveProblem newObjectiveProblem = new ObjectiveProblem(
                oldObjectiveProblem.getObjectiveProblemId(),
                oldObjectiveProblem.getAuthorId(),
                opDescription,
                opTotalScore,
                opCorrectAnswer,
                opTag,
                opDifficulty
        );
        UpdateWrapper<ObjectiveProblem> objectiveProblemUpdateWrapper = new UpdateWrapper<>();
        objectiveProblemUpdateWrapper.eq("objective_problem_id", oldObjectiveProblem.getObjectiveProblemId());
        objectiveProblemMapper.update(newObjectiveProblem, objectiveProblemUpdateWrapper);
        Map<String, String> resp = new HashMap<>();
        resp.put("error_message", "success");
        return resp;
    }


}
