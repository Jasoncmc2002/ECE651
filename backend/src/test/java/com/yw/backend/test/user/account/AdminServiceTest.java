package com.yw.backend.test.user.account;

import com.yw.backend.pojo.User;
import com.yw.backend.service.impl.utils.UserDetailsImpl;
import com.yw.backend.service.user.account.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback
@Transactional
public class AdminServiceTest {
    @Autowired
    private AdminService adminService;

    private void setAuthenticationToken(User user) {
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
    private final User adminUser = new User(2, "admin", "password", "Admin", 2, "");

    @Test
    public void testAdminUpdateUserInfo() {
        setAuthenticationToken(adminUser);
        Map<String, String> result = adminService.adminUpdateUserInfo(1, "newUsername", "newName", 1);
        assertEquals("success", result.get("error_message"));
    }

    @Test
    public void testAdminUpdateUserInfo_UserNotExist() {
        setAuthenticationToken(adminUser);
        Map<String, String> result = adminService.adminUpdateUserInfo(999, "newUsername", "newName", 1);
        assertEquals("User does not exist", result.get("error_message"));
    }

    @Test
    public void testAdminUpdateUserInfo_InvalidPermission() {
        setAuthenticationToken(adminUser);
        Map<String, String> result = adminService.adminUpdateUserInfo(1, "newUsername", "newName", 3);
        assertEquals("Invalid permission value", result.get("error_message"));
    }

    @Test
    public void testAdminUpdateUserInfo_UsernameExists() {
        setAuthenticationToken(adminUser);
        Map<String, String> result = adminService.adminUpdateUserInfo(1, "abc", "newName", 1);
        assertEquals("Username already exists", result.get("error_message"));
    }

    @Test
    public void testAdminUpdateUserInfo_EmptyUsername() {
        setAuthenticationToken(adminUser);
        Map<String, String> result = adminService.adminUpdateUserInfo(1, "", "newName", 1);
        assertEquals("Username cannot be empty", result.get("error_message"));
    }

    @Test
    public void testAdminUpdateUserInfo_EmptyName() {
        setAuthenticationToken(adminUser);
        Map<String, String> result = adminService.adminUpdateUserInfo(1, "newUsername", "", 1);
        assertEquals("Name cannot be empty", result.get("error_message"));
    }

    @Test
    public void testAdminUpdateUserPassword() {
        setAuthenticationToken(adminUser);
        Map<String, String> result = adminService.adminUpdateUserPassword(1, "newPassword", "newPassword");
        assertEquals("success", result.get("error_message"));
    }

    @Test
    public void testAdminUpdateUserPassword_UserNotExist() {
        setAuthenticationToken(adminUser);
        Map<String, String> result = adminService.adminUpdateUserPassword(999, "newPassword", "newPassword");
        assertEquals("Cannot find user by ID", result.get("error_message"));
    }

    @Test
    public void testAdminUpdateUserPassword_EmptyPassword() {
        setAuthenticationToken(adminUser);
        Map<String, String> result = adminService.adminUpdateUserPassword(1, "", "");
        assertEquals("Password cannot be empty", result.get("error_message"));
    }

    @Test
    public void testAdminUpdateUserPassword_PasswordMismatch() {
        setAuthenticationToken(adminUser);
        Map<String, String> result = adminService.adminUpdateUserPassword(1, "password1", "password2");
        assertEquals("Two passwords are not the same", result.get("error_message"));
    }

    @Test
    public void testAdminUpdateUserPassword_PasswordTooLong() {
        setAuthenticationToken(adminUser);
        String longPassword = new String(new char[101]).replace('\0', 'a');
        Map<String, String> result = adminService.adminUpdateUserPassword(1, longPassword, longPassword);
        assertEquals("Password length cannot be greater than 100", result.get("error_message"));
    }

    @Test
    public void testAdminSearchUser() {
        setAuthenticationToken(adminUser);
        List<Map<String, String>> result = adminService.adminSearchUser("abcd", "ccc");
        assertEquals(1, result.size());
        assertEquals("abcd", result.get(0).get("username"));
        assertEquals("ccc", result.get(0).get("name"));
    }

    @Test
    public void testAdminSearchUser_NoPermission() {
        User normalUser = new User(3, "user", "password", "User", 1, "");
        setAuthenticationToken(normalUser);
        List<Map<String, String>> result = adminService.adminSearchUser("testUsername", "testName");
        assertEquals(1, result.size());
        assertEquals("Admin cannot search user", result.get(0).get("error_message"));
    }

    @Test
    public void testAdminDeleteUser() {
        setAuthenticationToken(adminUser);
        Map<String, String> result = adminService.adminDeleteUser(1);
        assertEquals("success", result.get("error_message"));
    }

    @Test
    public void testAdminDeleteUser_UserNotExist() {
        setAuthenticationToken(adminUser);
        Map<String, String> result = adminService.adminDeleteUser(999);
        assertEquals("Cannot find user by ID", result.get("error_message"));
    }

    @Test
    public void testAdminDeleteUser_InvalidPermission() {
        User normalUser = new User(3, "user", "password", "User", 1, "");
        setAuthenticationToken(normalUser);
        Map<String, String> result = adminService.adminDeleteUser(1);
        assertEquals("Admin cannot delete user", result.get("error_message"));
    }

    @Test
    public void testAdminDeleteUser_DeleteSelf() {
        setAuthenticationToken(adminUser);
        Map<String, String> result = adminService.adminDeleteUser(2);
        assertEquals("Admin cannot delete self", result.get("error_message"));
    }

    @Test
    public void testAdminCreateUser() {
        setAuthenticationToken(adminUser);
        Map<String, String> result = adminService.adminCreateUser("newUser", "newName", "newPassword", 1);
        assertEquals("success", result.get("error_message"));
    }
    @Test
    public void testAdminCreateUser_UsernameExists() {
        setAuthenticationToken(adminUser);
        Map<String, String> result = adminService.adminCreateUser("abc", "newName", "newPassword", 1);
        assertEquals("Username already exists", result.get("error_message"));
    }

    @Test
    public void testAdminCreateUser_EmptyUsername() {
        setAuthenticationToken(adminUser);
        Map<String, String> result = adminService.adminCreateUser("", "newName", "newPassword", 1);
        assertEquals("Username cannot be empty", result.get("error_message"));
    }

    @Test
    public void testAdminCreateUser_EmptyName() {
        setAuthenticationToken(adminUser);
        Map<String, String> result = adminService.adminCreateUser("newUser", "", "newPassword", 1);
        assertEquals("Name cannot be empty", result.get("error_message"));
    }

    @Test
    public void testAdminCreateUser_InvalidPermission() {
        setAuthenticationToken(adminUser);
        Map<String, String> result = adminService.adminCreateUser("newUser", "newName", "newPassword", 3);
        assertEquals("Permission value is invalid", result.get("error_message"));
    }

    @Test
    public void testAdminCreateUser_EmptyPassword() {
        setAuthenticationToken(adminUser);
        Map<String, String> result = adminService.adminCreateUser("newUser", "newName", "", 1);
        assertEquals("Password cannot be empty", result.get("error_message"));
    }

    @Test
    public void testAdminCreateUser_PasswordTooLong() {
        setAuthenticationToken(adminUser);
        String longPassword = new String(new char[101]).replace('\0', 'a');
        Map<String, String> result = adminService.adminCreateUser("newUser", "newName", longPassword, 1);
        assertEquals("Password length cannot be greater than 100", result.get("error_message"));
    }

}
