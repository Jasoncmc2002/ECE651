package com.yw.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yw.backend.mapper.UserMapper;
import com.yw.backend.pojo.User;
import com.yw.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, String> register(String username, String password, String confirmedPassword) {
        Map<String, String> map = new HashMap<>();
        if (username == null) {
            map.put("error_message", "username cannot be empty");
            return map;
        }
        if (password == null || confirmedPassword == null) {
            map.put("error_message", "password cannot be empty");
            return map;
        }

        username = username.trim();
//        password = password.trim();
//        confirmedPassword = confirmedPassword.trim();
        if (username.length() == 0) {
            map.put("error_message", "username cannot be empty");
            return map;
        }

        if (username.length() > 100) {
            map.put("error_message", "Username length cannot exceed 100");
            return map;
        }

        if (password.length() == 0 || confirmedPassword.length() == 0) {
            map.put("error_message", "password cannot be empty");
            return map;
        }

        if (!password.equals(confirmedPassword)) {
            map.put("error_message", "The password entered twice is inconsistent");
            return map;
        }

        if (password.length() > 100 || confirmedPassword.length() > 100) {
            map.put("error_message", "Password length cannot exceed 100");
            return map;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        List<User> users = userMapper.selectList(queryWrapper);
        if (!users.isEmpty()) {
            map.put("error_message", "The username already exists");
            return map;
        }

        String encodedPassword = passwordEncoder.encode(password);
        String name = "Default User";
        // 计算id
        int userMaxId = 0;
        List<User> userList = userMapper.selectList(null);
        if (!userList.isEmpty()) {
            for (User user : userList) {
                if (user.getUserId() > userMaxId) {
                    userMaxId = user.getUserId();
                }
            }
        }
        Integer userId = userMaxId + 1;
        User user = new User(userId, username, encodedPassword, name, 0, "");
        userMapper.insert(user);

        map.put("error_message", "success");
        return map;
    }
}
