package com.yw.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yw.backend.mapper.UserMapper;
import com.yw.backend.pojo.User;
import com.yw.backend.service.impl.utils.UserDetailsImpl;
import com.yw.backend.service.user.account.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, String> adminUpdateUserInfo(int userId, String username, String name, int permission) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();


        if (user.getPermission() < 2) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Your permission cannot update user");
            return resp;
        }

        if (user.getUserId() == userId) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Admin cannot update self");
            return resp;
        }


        Map<String, String> resp = new HashMap<>();

        QueryWrapper<User> checkUserExistsQueryWrapper = new QueryWrapper<>();
        checkUserExistsQueryWrapper.eq("user_id", userId);
        List<User> checkUserExistsList = userMapper.selectList(checkUserExistsQueryWrapper);
        if (checkUserExistsList.isEmpty()) {
            resp.put("error_message", "User does not exist");
            return resp;
        }


        if (username == null) {
            resp.put("error_message", "Username cannot be empty");
            return resp;
        }
        if (name == null) {
            resp.put("error_message", "Name cannot be empty");
            return resp;
        }

        username = username.trim();
        name = name.trim();
        if (username.length() == 0) {
            resp.put("error_message", "Username cannot be empty");
            return resp;
        }
        if (username.length() > 100) {
            resp.put("error_message", "Username length cannot be greater than 100");
            return resp;
        }
        if (name.length() == 0) {
            resp.put("error_message", "Name cannot be empty");
            return resp;
        }
        if (name.length() > 100) {
            resp.put("error_message", "Name length cannot be greater than 100");
            return resp;
        }

        if (permission < 0 || permission > 2) {
            resp.put("error_message", "Invalid permission value");
            return resp;
        }


        QueryWrapper<User> checkUsernameExixtsQueryWrapper = new QueryWrapper<>();
        checkUsernameExixtsQueryWrapper.eq("username", username).ne("user_id", userId);
        List<User> checkUsernameExixtsList = userMapper.selectList(checkUsernameExixtsQueryWrapper);
        if (!checkUsernameExixtsList.isEmpty()) {
            resp.put("error_message", "Username already exists");
            return resp;
        }


        User target = checkUserExistsList.get(0);
        target.setUsername(username);
        target.setName(name);
        target.setPermission(permission);
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.eq("user_id", target.getUserId());
        userMapper.update(target, userUpdateWrapper);
        resp.put("error_message", "success");
        return resp;
    }

    @Override
    public Map<String, String> adminUpdateUserPassword(int userId, String password, String confirmedPassword) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 2) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Admin cannot update user");
            return resp;
        }

        if (user.getUserId() == userId) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Admin cannot update self");
            return resp;
        }


        Map<String, String> resp = new HashMap<>();
        QueryWrapper<User> checkUserExistsQueryWrapper = new QueryWrapper<>();
        checkUserExistsQueryWrapper.eq("user_id", userId);
        List<User> checkUserExistsList = userMapper.selectList(checkUserExistsQueryWrapper);
        if (checkUserExistsList.isEmpty()) {
            resp.put("error_message", "Cannot find user by ID");
            return resp;
        }

        if (password.length() == 0 || confirmedPassword.length() == 0) {
            resp.put("error_message", "Password cannot be empty");
            return resp;
        }
        if (!password.equals(confirmedPassword)) {
            resp.put("error_message", "Two passwords are not the same");
            return resp;
        }
        if (password.length() > 100 || confirmedPassword.length() > 100) {
            resp.put("error_message", "Password length cannot be greater than 100");
            return resp;
        }

        User target = checkUserExistsList.get(0);
        String encodedPassword = passwordEncoder.encode(password);
        target.setPassword(encodedPassword);
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.eq("user_id", target.getUserId());
        userMapper.update(target, userUpdateWrapper);
        resp.put("error_message", "success");
        return resp;
    }

    @Override
    public List<Map<String, String>> adminSearchUser(String username, String name) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 2) {
            //System.out.println("Admin cannot search user");
            List<Map<String, String>> resp = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "Admin cannot search user");
            resp.add(map);
            return resp;
        }

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.like("username", username);
        userQueryWrapper.like("name", name);
        List<User> userList = userMapper.selectList(userQueryWrapper);
        List<Map<String, String>> resp = new ArrayList<>();
        for (User target : userList) {
            Map<String, String> map = new HashMap<>();
            map.put("user_id", target.getUserId().toString());
            map.put("username", target.getUsername());
            map.put("name", target.getName());
            map.put("permission", target.getPermission().toString());

            resp.add(map);
        }

        return resp;
    }

    @Override
    public Map<String, String> adminDeleteUser(int userId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 2) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Admin cannot delete user");
            return resp;
        }

        if (user.getUserId() == userId) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Admin cannot delete self");
            return resp;
        }

        Map<String, String> resp = new HashMap<>();
        QueryWrapper<User> checkUserExistsQueryWrapper = new QueryWrapper<>();
        checkUserExistsQueryWrapper.eq("user_id", userId);
        List<User> checkUserExistsList = userMapper.selectList(checkUserExistsQueryWrapper);
        if (checkUserExistsList.isEmpty()) {
            resp.put("error_message", "Cannot find user by ID");
            return resp;
        }

        userMapper.delete(checkUserExistsQueryWrapper);
        resp.put("error_message", "success");
        return resp;
    }

    @Override
    public Map<String, String> adminCreateUser(String username, String name, String password, int permission) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPermission() < 2) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Admin cannot create user");
            return resp;
        }

        Map<String, String> resp = new HashMap<>();
        if (username == null) {
            resp.put("error_message", "Username cannot be empty");
            return resp;
        }
        if (name == null) {
            resp.put("error_message", "User name cannot be empty");
            return resp;
        }

        username = username.trim();
        name = name.trim();
        if (username.isEmpty()) {
            resp.put("error_message", "Username cannot be empty");
            return resp;
        }
        if (username.length() > 100) {
            resp.put("error_message", "Length of username cannot be greater than 100");
            return resp;
        }
        QueryWrapper<User> checkUsernameExixtsQueryWrapper = new QueryWrapper<>();
        checkUsernameExixtsQueryWrapper.eq("username", username);
        int usernameCount = Math.toIntExact(userMapper.selectCount(checkUsernameExixtsQueryWrapper));
        if (usernameCount != 0) {
            resp.put("error_message", "Username already exists");
            return resp;
        }

        if (name.isEmpty()) {
            resp.put("error_message", "Name cannot be empty");
            return resp;
        }
        if (name.length() > 100) {
            resp.put("error_message", "Name length cannot be greater than 100");
            return resp;
        }

        if (permission < 0 || permission > 2) {
            resp.put("error_message", "Permission value is invalid");
            return resp;
        }

        if (password.isEmpty()) {
            resp.put("error_message", "Password cannot be empty");
            return resp;
        }
        if (password.length() > 100) {
            resp.put("error_message", "Password length cannot be greater than 100");
            return resp;
        }

        String encodedPassword = passwordEncoder.encode(password);
        int userMaxId = 0;
        List<User> userList = userMapper.selectList(null);
        if (!userList.isEmpty()) {
            for (User u : userList) {
                if (u.getUserId() > userMaxId) {
                    userMaxId = u.getUserId();
                }
            }
        }
        Integer userId = userMaxId + 1;
        User new_user = new User(
                userId,
                username,
                encodedPassword,
                name,
                permission,
                ""
        );
        userMapper.insert(new_user);
        resp.put("error_message", "success");
        return resp;
    }
}
