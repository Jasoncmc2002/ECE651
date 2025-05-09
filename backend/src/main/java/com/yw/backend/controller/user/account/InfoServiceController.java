package com.yw.backend.controller.user.account;

import com.yw.backend.service.user.account.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class InfoServiceController {
    @Autowired
    private InfoService infoService;

    @GetMapping("/user/account/info/")
    public Map<String, String> getInfo() {
        return infoService.getInfo();
    }

    @GetMapping("/user/account/photo/")
    public Map<String, String> getPhoto() {
        return infoService.getPhoto();
    }
}
