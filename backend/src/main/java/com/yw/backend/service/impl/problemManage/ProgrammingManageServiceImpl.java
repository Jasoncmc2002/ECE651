package com.yw.backend.service.impl.problemManage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yw.backend.mapper.PNPsMapper;
import com.yw.backend.mapper.ProgrammingMapper;
import com.yw.backend.mapper.UserMapper;
import com.yw.backend.pojo.PNPs;
import com.yw.backend.pojo.Programming;
import com.yw.backend.pojo.User;
import com.yw.backend.service.impl.utils.UserDetailsImpl;
import com.yw.backend.service.problemManage.ProgrammingManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProgrammingManageServiceImpl implements ProgrammingManageService {
    @Autowired
    private ProgrammingMapper programmingMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PNPsMapper pnPsMapper;

    @Override
    public Map<String, String> update(int programmingId, String pDescription, int pTotalScore, int timeLimit, int codeSizeLimit, String pTag, String pTitle, String pJudgeCode, int pDifficulty) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        //Permission
        if (user.getPermission() < 1) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No permission in updating programming problems");
            return resp;
        }

        //check if problem exists
        QueryWrapper<Programming> programmingQueryWrapper = new QueryWrapper<>();
        programmingQueryWrapper.eq("programming_id", programmingId);
        List<Programming> programmingList = programmingMapper.selectList(programmingQueryWrapper);
        if (programmingList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No programming problem with this ID");
            return resp;
        }

        // check permission whether you can modify this problem
        Programming oldProgramming = programmingList.get(0);
        if (!Objects.equals(oldProgramming.getPAuthorId(), user.getUserId()) && user.getPermission() < 2) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "You cannot modify programming problems created by others");
            return resp;
        }

        // Check input parameters
        if (pDescription == null || pDescription.length() == 0) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Problem Description cannot be empty");
            return resp;
        } else if (pDescription.length() > 10000) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Problem Description cannot exceeds 10000 characters");
            return resp;
        }

        if (pTotalScore <= 0) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Points must be a positive integer");
            return resp;
        }

        if (pDifficulty <= 0 || pDifficulty > 5) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Difficulty must be integer between 0 and 5");
            return resp;
        }

        if (timeLimit <= 0) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Time limit must be a positive integer");
            return resp;
        }

        if (codeSizeLimit <= 0) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Code size limit must be a positive integer");
            return resp;
        }

        if (pTag == null || pTag.length() == 0) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Tag cannot be empty");
            return resp;
        } else if (pTag.length() > 100) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Tag cannot exceed 100 characters");
            return resp;
        }

        if (pTitle == null || pTitle.length() == 0) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Title cannot be empty");
            return resp;
        } else if (pTitle.length() > 100) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Title cannot exceed 100 characters");
            return resp;
        }

        if (pJudgeCode == null) {
            pJudgeCode = "";    //avoid NullPointerException
        }
        else if (pJudgeCode.length() > 16000) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Code cannot exceed 16000 characters (16k)");
            return resp;
        }

        Programming newProgramming = new Programming(
                oldProgramming.getProgrammingId(),
                pDescription,
                pTotalScore,
                timeLimit,
                codeSizeLimit,
                pTag,
                oldProgramming.getPAuthorId(),
                pTitle,
                pJudgeCode,
                pDifficulty
        );
        UpdateWrapper<Programming> programmingUpdateWrapper = new UpdateWrapper<>();
        programmingUpdateWrapper.eq("programming_id", oldProgramming.getProgrammingId());
        programmingMapper.update(newProgramming, programmingUpdateWrapper);
        Map<String, String> resp = new HashMap<>();
        resp.put("error_message", "success");
        return resp;
    }

    @Override
    public List<Map<String, String>> getAll() {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 1) {
            System.out.println("No permission to obtain programming problems");
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "No permission to obtain programming problems");
            resp.add(map);
            return resp;
        }

        //turn all outcome to list Map
        List<Programming> programmingList = programmingMapper.selectList(null);
        List<Map<String, String>> resp = new ArrayList<>();
        for (Programming programming : programmingList) {
            Map<String, String> map = new HashMap<>();
            Integer programmingId = programming.getProgrammingId();
            map.put("programming_id", programmingId.toString());

            QueryWrapper<PNPs> pnPsQueryWrapper = new QueryWrapper<>();
            pnPsQueryWrapper.eq("programming_id", programmingId);
            int pUseCount = Math.toIntExact(pnPsMapper.selectCount(pnPsQueryWrapper));
            map.put("p_use_count", String.valueOf(pUseCount));

            map.put("p_title", programming.getPTitle());
            map.put("p_total_score", programming.getPTotalScore().toString());
            map.put("p_tag", programming.getPTag());
            map.put("p_difficulty", programming.getPDifficulty().toString());

            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("user_id", programming.getPAuthorId());
            User author = userMapper.selectOne(userQueryWrapper);
            map.put("p_author_name", author.getName());

            resp.add(map);
        }
        return resp;
    }

    @Override
    public Map<String, String> getOne(int programmingId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 1) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No permission to obtain programming problems");
            return resp;
        }

        //query a problem by ID
        QueryWrapper<Programming> programmingQueryWrapper = new QueryWrapper<>();
        programmingQueryWrapper.eq("programming_id", programmingId);
        List<Programming> programmingList = programmingMapper.selectList(programmingQueryWrapper);
        if (programmingList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "No programming problem with this ID");
            return resp;
        }
        Programming programming = programmingList.get(0);
        Map<String, String> resp = new HashMap<>();
        resp.put("error_message", "success");
        resp.put("programming_id", programming.getProgrammingId().toString());
        QueryWrapper<PNPs> pnPsQueryWrapper = new QueryWrapper<>();
        pnPsQueryWrapper.eq("programming_id", programming.getProgrammingId());
        int pUseCount = Math.toIntExact(pnPsMapper.selectCount(pnPsQueryWrapper));
        resp.put("p_use_count", String.valueOf(pUseCount));

        resp.put("p_description", programming.getPDescription());
        resp.put("p_total_score", programming.getPTotalScore().toString());
        resp.put("time_limit", programming.getTimeLimit().toString());
        resp.put("code_size_limit", programming.getCodeSizeLimit().toString());
        resp.put("p_tag", programming.getPTag());

        Integer authorId = programming.getPAuthorId();
        resp.put("p_author_id", authorId.toString());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_id", authorId);
        User author = userMapper.selectOne(userQueryWrapper);
        resp.put("p_author_name", author.getName());

        resp.put("p_title", programming.getPTitle());
        resp.put("p_judge_code", programming.getPJudgeCode());
        resp.put("p_difficulty", programming.getPDifficulty().toString());
        return resp;
    }
}
