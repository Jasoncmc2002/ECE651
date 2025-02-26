package com.yw.backend.service.user.account;

import java.util.List;
import java.util.Map;

public interface AdminService {
    Map<String, String> adminUpdateUserInfo(int userId, String username, String name, int permission);

    Map<String, String> adminUpdateUserPassword(int userId, String password, String confirmedPassword);

    List<Map<String, String>> adminSearchUser(String username, String name);

    Map<String, String> adminDeleteUser(int userId);

    Map<String, String> adminCreateUser(String username, String name, String password, int permission);
}
