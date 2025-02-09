package com.yw.backend.service.impl.problemManage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yw.backend.mapper.ObjectiveProblemMapper;
import com.yw.backend.pojo.ObjectiveProblem;
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
    public Map<String, String> create(String opDescription, int opTotalScore, String opCorrectAnswer, String opTag, int opDifficulty) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        Map<String, String> resp = new HashMap<>();
        // check permission
        if (user.getPermission() < 1) {
            resp.put("error_message", "no permission to create objective problem");
            return resp;
        }

        // check input
        if (opDescription == null || opDescription.length() == 0) {
            resp.put("error_message", "the question description cannot be empty");
            return resp;
        } else if (opDescription.length() > 10000) {
            resp.put("error_message", "the question description cannot exceed 1000 characters");
            return resp;
        }

        if (opTotalScore <= 0) {
            resp.put("error_message", "the question score must be a positive integer");
            return resp;
        }

        if (opCorrectAnswer == null || opCorrectAnswer.length() == 0) {
            resp.put("error_message", "the correct answer cannot be empty");
            return resp;
        } else if (opCorrectAnswer.length() > 1024) {
            resp.put("error_message", "the correct answer cannot exceed 1024 characters");
            return resp;
        }

        if (opTag == null || opTag.length() == 0) {
            resp.put("error_message", "the tag cannot be empty");
            return resp;
        } else if (opTag.length() > 100) {
            resp.put("error_message", "the tag cannot exceed 100 characters");
            return resp;
        }

        if (opDifficulty <= 0 || opDifficulty > 5) {
            resp.put("error_message", "the difficulty coefficient must be a positive integer between 1 and 5");
            return resp;
        }

        // calculate id
        int objectiveProblemMaxId = 0;
        List<ObjectiveProblem> objectiveProblemList = objectiveProblemMapper.selectList(null);
        if (!objectiveProblemList.isEmpty()) {
            for (ObjectiveProblem objectiveProblem : objectiveProblemList) {
                if (objectiveProblem.getObjectiveProblemId() > objectiveProblemMaxId)
                    objectiveProblemMaxId = objectiveProblem.getObjectiveProblemId();
            }
        }
        Integer objectiveProblemId = objectiveProblemMaxId + 1;

        // create instance
        ObjectiveProblem objectiveProblem = new ObjectiveProblem(
                objectiveProblemId,
                user.getUserId(),
                opDescription,
                opTotalScore,
                opCorrectAnswer,
                opTag,
                opDifficulty
        );
        objectiveProblemMapper.insert(objectiveProblem);

        // return id
        resp.put("error_message", "success");
        resp.put("objective_problem_id", objectiveProblemId.toString());
        return resp;
    }

    @Override
    public Map<String, String> delete(int objectiveProblemId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        // check permission
        if (user.getPermission() < 1) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "no permission to delete objective problems");
            return resp;
        }

        // check if the question exists
        QueryWrapper<ObjectiveProblem> objectiveProblemQueryWrapper = new QueryWrapper<>();
        objectiveProblemQueryWrapper.eq("objective_problem_id", objectiveProblemId);
        List<ObjectiveProblem> objectiveProblemList = objectiveProblemMapper.selectList(objectiveProblemQueryWrapper);
        if (objectiveProblemList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "no objective problem found via ID query");
            return resp;
        }

        // check if this question can be deleted
        ObjectiveProblem oldObjectiveProblem = objectiveProblemList.get(0);

        if (!Objects.equals(oldObjectiveProblem.getAuthorId(), user.getUserId()) && user.getPermission() < 2) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "teachers cannot delete questions created by others");
            return resp;
        }

        // delete
        objectiveProblemMapper.delete(objectiveProblemQueryWrapper);
        Map<String, String> resp = new HashMap<>();
        resp.put("error_message", "success");
        return resp;
    }
}
