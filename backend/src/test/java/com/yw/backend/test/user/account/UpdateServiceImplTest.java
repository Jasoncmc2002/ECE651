package com.yw.backend.test.user.account;

//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
//import com.yw.backend.mapper.UserMapper;
//import com.yw.backend.pojo.User;
//import com.yw.backend.service.impl.utils.UserDetailsImpl;
//import com.yw.backend.service.impl.user.account.UpdateServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.mockito.ArgumentCaptor;

import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class UpdateServiceImplTest {
/**
    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UpdateServiceImpl updateService;

    private User mockUser;
    private UserDetailsImpl mockUserDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup mock user
        mockUser = new User(1, "oldUsername", "password123", "oldName", 0, "photo.jpg");
        mockUserDetails = new UserDetailsImpl(mockUser);

        // Setup security context
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mockUserDetails, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void updateUserInfo_SuccessfulUpdate() {
        // Arrange
        String newUsername = "newUsername";
        String newName = "newName";
        when(userMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());
        when(userMapper.update(any(User.class), any(UpdateWrapper.class))).thenReturn(1);

        // Act
        Map<String, String> result = updateService.updateUserInfo(newUsername, newName);

        // Assert
        assertEquals("success", result.get("error_message"));
        verify(userMapper).update(any(User.class), any(UpdateWrapper.class));
    }

    @Test
    void updateUserInfo_UsernameAlreadyExists() {
        // Arrange
        String newUsername = "existingUsername";
        String newName = "newName";
        when(userMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(new User()));

        // Act
        Map<String, String> result = updateService.updateUserInfo(newUsername, newName);

        // Assert
        assertEquals("Username already exists", result.get("error_message"));
        verify(userMapper, never()).update(any(User.class), any(UpdateWrapper.class));
    }

    @Test
    void updateUserInfo_NullUsername() {
        // Act
        Map<String, String> result = updateService.updateUserInfo(null, "validName");

        // Assert
        assertEquals("Username cannot be empty", result.get("error_message"));
        verify(userMapper, never()).update(any(User.class), any(UpdateWrapper.class));
    }

    @Test
    void updateUserInfo_NullName() {
        // Act
        Map<String, String> result = updateService.updateUserInfo("validUsername", null);

        // Assert
        assertEquals("Name cannot be empty", result.get("error_message"));
        verify(userMapper, never()).update(any(User.class), any(UpdateWrapper.class));
    }

    @Test
    void updateUserInfo_EmptyUsername() {
        // Act
        Map<String, String> result = updateService.updateUserInfo("   ", "validName");

        // Assert
        assertEquals("Username cannot be empty", result.get("error_message"));
        verify(userMapper, never()).update(any(User.class), any(UpdateWrapper.class));
    }

    @Test
    void updateUserInfo_EmptyName() {
        // Act
        Map<String, String> result = updateService.updateUserInfo("validUsername", "   ");

        // Assert
        assertEquals("Name cannot be empty", result.get("error_message"));
        verify(userMapper, never()).update(any(User.class), any(UpdateWrapper.class));
    }

    @Test
    void updateUserInfo_UsernameTooLong() {
        // Arrange
        StringBuilder longUsername = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            longUsername.append("a");
        }

        // Act
        Map<String, String> result = updateService.updateUserInfo(longUsername.toString(), "validName");

        // Assert
        assertEquals("Username length cannot exceed 100 characters", result.get("error_message"));
        verify(userMapper, never()).update(any(User.class), any(UpdateWrapper.class));
    }

    @Test
    void updateUserInfo_NameTooLong() {
        // Arrange
        StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            longName.append("a");
        }

        // Act
        Map<String, String> result = updateService.updateUserInfo("validUsername", longName.toString());

        // Assert
        assertEquals("Name length cannot exceed 100 characters", result.get("error_message"));
        verify(userMapper, never()).update(any(User.class), any(UpdateWrapper.class));
    }


    //API2: update password
    @Test
    void updatePassword_SuccessfulUpdate() {
        // Arrange
        String originalPassword = "oldPassword";
        String newPassword = "newPassword";
        String confirmedPassword = "newPassword";
        String encodedOldPassword = "encodedOldPassword";
        String encodedNewPassword = "encodedNewPassword";

        // Update mock user to have encoded password
        mockUser.setPassword(encodedOldPassword);

        // Setup password encoder behavior
        when(passwordEncoder.matches(originalPassword, encodedOldPassword)).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);
        when(userMapper.update(any(User.class), any(UpdateWrapper.class))).thenReturn(1);

        // Act
        Map<String, String> result = updateService.updatePassword(originalPassword, newPassword, confirmedPassword);

        // Assert
        assertEquals("success", result.get("error_message"));
        verify(userMapper).update(any(User.class), any(UpdateWrapper.class));
        verify(passwordEncoder).encode(newPassword);
    }

    @Test
    void updatePassword_IncorrectOriginalPassword() {
        // Arrange
        String originalPassword = "wrongPassword";
        String newPassword = "newPassword";
        String confirmedPassword = "newPassword";
        String encodedOldPassword = "encodedOldPassword";

        mockUser.setPassword(encodedOldPassword);
        when(passwordEncoder.matches(originalPassword, encodedOldPassword)).thenReturn(false);

        // Act
        Map<String, String> result = updateService.updatePassword(originalPassword, newPassword, confirmedPassword);

        // Assert
        assertEquals("Password incorrect", result.get("error_message"));
        verify(userMapper, never()).update(any(User.class), any(UpdateWrapper.class));
        verify(passwordEncoder, never()).encode(any(String.class));
    }

    @Test
    void updatePassword_EmptyNewPassword() {
        // Arrange
        String originalPassword = "oldPassword";
        String newPassword = "";
        String confirmedPassword = "";
        String encodedOldPassword = "encodedOldPassword";

        mockUser.setPassword(encodedOldPassword);
        when(passwordEncoder.matches(originalPassword, encodedOldPassword)).thenReturn(true);

        // Act
        Map<String, String> result = updateService.updatePassword(originalPassword, newPassword, confirmedPassword);

        // Assert
        assertEquals("Password cannot be empty", result.get("error_message"));
        verify(userMapper, never()).update(any(User.class), any(UpdateWrapper.class));
        verify(passwordEncoder, never()).encode(any(String.class));
    }

    @Test
    void updatePassword_PasswordMismatch() {
        // Arrange
        String originalPassword = "oldPassword";
        String newPassword = "newPassword";
        String confirmedPassword = "differentPassword";
        String encodedOldPassword = "encodedOldPassword";

        mockUser.setPassword(encodedOldPassword);
        when(passwordEncoder.matches(originalPassword, encodedOldPassword)).thenReturn(true);

        // Act
        Map<String, String> result = updateService.updatePassword(originalPassword, newPassword, confirmedPassword);

        // Assert
        assertEquals("Confirmed password did not match", result.get("error_message"));
        verify(userMapper, never()).update(any(User.class), any(UpdateWrapper.class));
        verify(passwordEncoder, never()).encode(any(String.class));
    }

    @Test
    void updatePhoto_SuccessfulUpdate() {
        // Arrange
        String newPhoto = "data:image/jpeg;base64,/9j/4AAQSkZJRg..."; // Valid base64 string under 50KB
        when(userMapper.update(any(User.class), any(UpdateWrapper.class))).thenReturn(1);

        // Act
        Map<String, String> result = updateService.updatePhoto(newPhoto);

        // Assert
        assertEquals("success", result.get("error_message"));
        verify(userMapper).update(any(User.class), any(UpdateWrapper.class));

        // Verify the user object was updated with new photo
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).update(userCaptor.capture(), any(UpdateWrapper.class));
        assertEquals(newPhoto, userCaptor.getValue().getPhoto());
    }

    @Test
    void updatePhoto_PhotoTooLarge() {
        // Arrange
        StringBuilder largePhoto = new StringBuilder();
        // Generate a string larger than 50KB (51200 characters)
        for (int i = 0; i < 51200; i++) {
            largePhoto.append("a");
        }

        // Act
        Map<String, String> result = updateService.updatePhoto(largePhoto.toString());

        // Assert
        assertEquals("Base64 encoding of the image larger than 50KB", result.get("error_message"));
        verify(userMapper, never()).update(any(User.class), any(UpdateWrapper.class));
    }

    @Test
    void updatePhoto_EmptyPhoto() {
        // Arrange
        String emptyPhoto = "";

        // Act
        Map<String, String> result = updateService.updatePhoto(emptyPhoto);

        // Assert
        assertEquals("success", result.get("error_message")); // Based on current implementation
        verify(userMapper).update(any(User.class), any(UpdateWrapper.class));
    }
**/
}