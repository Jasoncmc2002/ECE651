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
    public Map<String, String> create(String pDescription, int pTotalScore, int timeLimit, int codeSizeLimit, String pTag, String pTitle, String pJudgeCode, int pDifficulty) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        // check permission
        if (user.getPermission() < 1) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "no permission to create programming questions");
            return resp;
        }

        // check input
        if (pDescription == null || pDescription.length() == 0) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "the question description cannot be empty");
            return resp;
        } else if (pDescription.length() > 10000) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "the question description cannot exceed 10000 characters");
            return resp;
        }

        if (pTotalScore <= 0) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "the question score must be a positive integer");
            return resp;
        }

        if (pDifficulty <= 0 || pDifficulty > 5) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "the difficulty coefficient must be a positive integer between 1 and 5");
            return resp;
        }

        if (timeLimit <= 0) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "the time limit must be a positive integer");
            return resp;
        }

        if (codeSizeLimit <= 0) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "the code length limit must be a positive integer");
            return resp;
        }

        if (pTag == null || pTag.length() == 0) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "tags cannot be empty");
            return resp;
        } else if (pTag.length() > 100) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "tags cannot exceed 100 characters");
            return resp;
        }

        if (pTitle == null || pTitle.length() == 0) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "the title cannot be empty");
            return resp;
        } else if (pTitle.length() > 100) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "the title cannot exceed 100 characters");
            return resp;
        }

        if (pJudgeCode.length() > 16000) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "the evaluation program code length for function-based questions cannot exceed 16kB");
            return resp;
        }

        // calculate id
        int programmingMaxId = 0;
        List<Programming> programmingList = programmingMapper.selectList(null);
        if (!programmingList.isEmpty()) {
            for (Programming programming : programmingList) {
                if (programming.getProgrammingId() > programmingMaxId)
                    programmingMaxId = programming.getProgrammingId();
            }
        }
        Integer programmingId = programmingMaxId + 1;

        // create instance
        Programming programming = new Programming(
                programmingId,
                pDescription,
                pTotalScore,
                timeLimit,
                codeSizeLimit,
                pTag,
                user.getUserId(),
                pTitle,
                pJudgeCode,
                pDifficulty
        );
        programmingMapper.insert(programming);
        // return id
        Map<String, String> resp = new HashMap<>();
        resp.put("error_message", "success");
        resp.put("programming_id", programmingId.toString());
        return resp;
    }

    @Override
    public Map<String, String> delete(int programmingId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        // check permission
        if (user.getPermission() < 1) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "no permission to delete programming questions");
            return resp;
        }

        // check if question exists
        QueryWrapper<Programming> programmingQueryWrapper = new QueryWrapper<>();
        programmingQueryWrapper.eq("programming_id", programmingId);
        List<Programming> programmingList = programmingMapper.selectList(programmingQueryWrapper);
        if (programmingList.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "no programming question found with the provided ID");
            return resp;
        }

        // check if this question can be deleted
        Programming oldProgramming = programmingList.get(0);
        if (!Objects.equals(oldProgramming.getPAuthorId(), user.getUserId()) && user.getPermission() < 2) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "teachers cannot delete programming questions created by others");
            return resp;
        }

        // delete
        programmingMapper.delete(programmingQueryWrapper);
        Map<String, String> resp = new HashMap<>();
        resp.put("error_message", "success");
        return resp;
    }
}
