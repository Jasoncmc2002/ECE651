package com.yw.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yw.backend.mapper.UserMapper;
import com.yw.backend.pojo.User;
import com.yw.backend.service.impl.utils.UserDetailsImpl;
import com.yw.backend.service.user.account.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UpdateServiceImpl implements UpdateService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, String> updateUserInfo(String username, String name) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        int user_id = user.getUserId();
        Map<String, String> map = new HashMap<>();

        if (username == null) {
            map.put("error_message", "Username cannot be empty");
            return map;
        }
        if (name == null) {
            map.put("error_message", "Name cannot be empty");
            return map;
        }

        username = username.trim();
        name = name.trim();
        if (username.length() == 0) {
            map.put("error_message", "Username cannot be empty");
            return map;
        }
        if (username.length() > 100) {
            map.put("error_message", "Username length cannot exceed 100 characters");
            return map;
        }
        if (name.length() == 0) {
            map.put("error_message", "Name cannot be empty");
            return map;
        }
        if (name.length() > 100) {
            map.put("error_message", "Name length cannot exceed 100 characters");
            return map;
        }

        QueryWrapper<User> checkUsernameExixtsQueryWrapper = new QueryWrapper<>();
        checkUsernameExixtsQueryWrapper.eq("username", username).ne("user_id", user_id);
        List<User> userList = userMapper.selectList(checkUsernameExixtsQueryWrapper);
        if (!userList.isEmpty()) {
            map.put("error_message", "Username already exists");
            return map;
        }

        User new_user = new User(
                user.getUserId(),
                username,
                user.getPassword(),
                name,
                user.getPermission(),
                user.getPhoto()
        );
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.eq("user_id", user_id);
        userMapper.update(new_user, userUpdateWrapper);
        map.put("error_message", "success");
        return map;
    }

    @Override
    public Map<String, String> updatePassword(String originalPassword, String password, String confirmedPassword) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        Map<String, String> map = new HashMap<>();
        String encodedOriginalPassword = user.getPassword();
        if (!passwordEncoder.matches(originalPassword, encodedOriginalPassword)) {
            map.put("error_message", "Password incorrect");
            return map;
        }

        if (password.length() == 0 || confirmedPassword.length() == 0) {
            map.put("error_message", "Password cannot be empty");
            return map;
        }
        if (!password.equals(confirmedPassword)) {
            map.put("error_message", "Confirmed password did not match");
            return map;
        }
        if (password.length() > 100 || confirmedPassword.length() > 100) {
            map.put("error_message", "Password length cannot exceed 100 characters");
            return map;
        }

        String encodedPassword = passwordEncoder.encode(password);
        User new_user = new User(
                user.getUserId(),
                user.getUsername(),
                encodedPassword,
                user.getName(),
                user.getPermission(),
                user.getPhoto()
        );
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.eq("user_id", user.getUserId());
        userMapper.update(new_user, userUpdateWrapper);
        map.put("error_message", "success");
        return map;
    }

    @Override
    public Map<String, String> updatePhoto(String photo) {
        if (photo.length() > 50000) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Base64 encoding of the image larger than 50KB");
            return resp;
        }
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        user.setPhoto(photo);
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.eq("user_id", user.getUserId());
        userMapper.update(user, userUpdateWrapper);
        Map<String, String> resp = new HashMap<>();
        resp.put("error_message", "success");
        return resp;
    }
}
