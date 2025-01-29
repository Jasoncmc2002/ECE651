package com.yw.backend.controller.user.account;

import com.yw.backend.service.user.account.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UpdateController {
    @Autowired
    private UpdateService updateService;

    @PostMapping("/user/account/update_user_info/")
    public Map<String, String> updateUserInfo(@RequestParam Map<String, String> map) {
        String username = map.get("username");
        String name = map.get("name");
        return updateService.updateUserInfo(username, name);
    }

    @PostMapping("/user/account/update_password")
    public Map<String, String> updatePassword(@RequestParam Map<String, String> map) {
        String originalPassword = map.get("originalPassword");
        String password = map.get("password");
        String confirmedPassword = map.get("confirmedPassword");
        return updateService.updatePassword(originalPassword, password, confirmedPassword);
    }

    @PutMapping("/user/account/update_photo")
    public Map<String, String> updatePhoto(@RequestParam Map<String, String> data) {
        String photo = data.get("photo");
        return updateService.updatePhoto(photo);
    }
}
