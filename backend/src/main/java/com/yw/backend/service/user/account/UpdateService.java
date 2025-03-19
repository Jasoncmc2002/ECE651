package com.yw.backend.service.user.account;

import java.util.Map;

public interface UpdateService {
    Map<String, String> updateUserInfo(String username, String name);

    Map<String, String> updatePassword(String originalPassword, String password, String confirmedPassword);

    Map<String, String> updatePhoto(String photo);
}
