package com.yw.backend.service.impl.user.account;

import com.yw.backend.pojo.User;
import com.yw.backend.service.impl.utils.UserDetailsImpl;
import com.yw.backend.service.user.account.InfoService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class InfoServiceImpl implements InfoService {
    @Override
    public Map<String, String> getInfo() {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        Map<String, String> map = new HashMap<>();
        map.put("error_message", "success");
        map.put("user_id", user.getUserId().toString());
        map.put("name", user.getName());
        map.put("username", user.getUsername());
        map.put("permission", user.getPermission().toString());

        return map;
    }

    @Override
    public Map<String, String> getPhoto() {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (user.getPhoto() == null) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "success");
            resp.put("photo", "");
            return resp;
        } else {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "success");
            resp.put("photo", user.getPhoto());
            return resp;
        }
    }
}
