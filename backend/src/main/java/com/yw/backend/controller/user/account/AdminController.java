package com.yw.backend.controller.user.account;

import com.yw.backend.service.user.account.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PutMapping("/user/account/admin/user_info/")
    public Map<String, String> adminUpdateUserInfo(@RequestParam Map<String, String> data) {
        int userId;
        try {
            userId = Integer.parseInt(data.get("userId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid user ID");
            return resp;
        }

        String username = data.get("username");
        String name = data.get("name");

        int permission;
        try {
            permission = Integer.parseInt(data.get("permission"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid permission value");
            return resp;
        }

        return adminService.adminUpdateUserInfo(userId, username, name, permission);
    }

    @PutMapping("/user/account/admin/password/")
    public Map<String, String> adminUpdateUserPassword(@RequestParam Map<String, String> data) {
        int userId;
        try {
            userId = Integer.parseInt(data.get("userId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid user ID");
            return resp;
        }

        String password = data.get("password");
        String confirmedPassword = data.get("confirmedPassword");

        return adminService.adminUpdateUserPassword(userId, password, confirmedPassword);
    }

    @GetMapping("/user/account/admin/search")
    public List<Map<String, String>> adminSearchUser(@RequestParam Map<String, String> data) {
        String username = data.get("username");
        String name = data.get("name");

        return adminService.adminSearchUser(username, name);
    }

    @DeleteMapping("/user/account/admin/delete/")
    public Map<String, String> adminDeleteUser(@RequestParam Map<String, String> data) {
        int userId;
        try {
            userId = Integer.parseInt(data.get("userId"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid user ID");
            return resp;
        }
        return adminService.adminDeleteUser(userId);
    }

    @PostMapping("/user/account/admin/batch_create/")
    public Map<String, String> adminCreateUser(@RequestParam Map<String, String> data) {
        String username = data.get("username");
        String name = data.get("name");
        String password = data.get("password");
        int permission;
        try {
            permission = Integer.parseInt(data.get("permission"));
        } catch (NumberFormatException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error_message", "Invalid permission value");
            return resp;
        }

        return adminService.adminCreateUser(username, name, password, permission);
    }
}
