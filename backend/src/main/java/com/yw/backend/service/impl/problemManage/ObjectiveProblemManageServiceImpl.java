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
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OpNPsMapper opNPsMapper;

    @Override
    public List<Map<String, String>> getAll() {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        // check permission level
        if (user.getPermission() < 1) {
            System.out.println("No permission to retrieve objective problems list");
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No permission to retrieve objective problems list");
            resp.add(map);
            return resp;
        }

        List<ObjectiveProblem> objectiveProblemList = objectiveProblemMapper.selectList(null);
        List<Map<String, String>> resp = new ArrayList<>();
        for (ObjectiveProblem objectiveProblem : objectiveProblemList) {
            Map<String, String> map = new HashMap<>();
            Integer objectiveProblemId = objectiveProblem.getObjectiveProblemId();
            map.put("objective_problem_id", objectiveProblemId.toString());
            QueryWrapper<OpNPs> opNPsQueryWrapper = new QueryWrapper<>();
            opNPsQueryWrapper.eq("objective_problem_id", objectiveProblemId);
            int opUseCount = Math.toIntExact(opNPsMapper.selectCount(opNPsQueryWrapper));
            map.put("op_use_count", String.valueOf(opUseCount));

            String opDescription = objectiveProblem.getOpDescription();
            map.put("op_description", opDescription.substring(0, Math.min(opDescription.length(), 125)) + "...");

            map.put("op_total_score", objectiveProblem.getOpTotalScore().toString());
            map.put("op_tag", objectiveProblem.getOpTag());
            map.put("op_difficulty", objectiveProblem.getOpDifficulty().toString());

            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", objectiveProblem.getAuthorId());
            User author = userMapper.selectOne(queryWrapper);
            map.put("op_author_name", author.getName());

            resp.add(map);
        }
        return resp;
    }

    @Override
    public Map<String, String> getOne(int objectiveProblemId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 1) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No permission to retrieve objective problem");
            return resp;
        }

        QueryWrapper<ObjectiveProblem> objectiveProblemQueryWrapper = new QueryWrapper<>();
        objectiveProblemQueryWrapper.eq("objective_problem_id", objectiveProblemId);
        List<ObjectiveProblem> objectiveProblemList = objectiveProblemMapper.selectList(objectiveProblemQueryWrapper);
        if (objectiveProblemList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No objective problem correspond to this ID");
            return resp;
        }
        ObjectiveProblem objectiveProblem = objectiveProblemList.get(0);
        Map<String, String> resp = new HashMap<>();
        resp.put("error_message", "success");
        resp.put("objective_problem_id", objectiveProblem.getObjectiveProblemId().toString());
        QueryWrapper<OpNPs> opNPsQueryWrapper = new QueryWrapper<>();
        opNPsQueryWrapper.eq("objective_problem_id", objectiveProblem.getObjectiveProblemId());
//        List<OpNPs> opNPsList = opNPsMapper.selectList(opNPsQueryWrapper);
        int opUseCount = Math.toIntExact(opNPsMapper.selectCount(opNPsQueryWrapper));
        resp.put("op_use_count", String.valueOf(opUseCount));

        Integer authorId = objectiveProblem.getAuthorId();
        resp.put("op_author_id", authorId.toString());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_id", authorId);
        User author = userMapper.selectOne(userQueryWrapper);
        resp.put("op_author_name", author.getName());

        resp.put("op_description", objectiveProblem.getOpDescription());
        resp.put("op_total_score", objectiveProblem.getOpTotalScore().toString());
        resp.put("op_correct_answer", objectiveProblem.getOpCorrectAnswer());
        resp.put("op_tag", objectiveProblem.getOpTag());
        resp.put("op_difficulty", objectiveProblem.getOpDifficulty().toString());

        return resp;
    }
}
