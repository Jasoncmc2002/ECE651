package com.yw.backend.test.user.account;

//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.yw.backend.mapper.UserMapper;
//import com.yw.backend.pojo.User;
//import com.yw.backend.service.impl.user.account.RegisterServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RegisterServiceImplTest {
/**
    @InjectMocks
    private RegisterServiceImpl registerService; // The service under test

    @Mock
    private UserMapper userMapper; // Mocking the UserMapper dependency

    @Mock
    private PasswordEncoder passwordEncoder; // Mocking the PasswordEncoder dependency

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testRegister_UsernameNull() {
        Map<String, String> result = registerService.register(null, "password", "password");
        assertEquals("Username cannot be empty", result.get("error_message"));
    }

    @Test
    void testRegister_UsernameEmpty() {
        Map<String, String> result = registerService.register("   ", "password", "password");
        assertEquals("Username cannot be empty", result.get("error_message"));
    }


    @Test
    void testRegister_PasswordNull() {
        Map<String, String> result = registerService.register("username", null, null);
        assertEquals("Password cannot be empty", result.get("error_message"));
    }

    @Test
    void testRegister_PasswordMismatch() {
        Map<String, String> result = registerService.register("username", "password1", "password2");
        assertEquals("Confirmed password did not match", result.get("error_message"));
    }



    @Test
    void testRegister_UsernameExists() {
        // Mock existing user in the database
        User existingUser = new User();
        existingUser.setUsername("existingUser");
        when(userMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.singletonList(existingUser));

        Map<String, String> result = registerService.register("existingUser", "password", "password");
        assertEquals("Username already exists", result.get("error_message"));
    }

    @Test
    void testRegister_Success() {
        // Mock no existing user
        when(userMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());

        // Mock password encoding
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        // Mock user insertion
        doAnswer(invocation -> null).when(userMapper).insert(any(User.class));

        // Call the method
        Map<String, String> result = registerService.register("newUser", "password", "password");

        // Assert the result
        assertEquals("success", result.get("error_message"));

        // Verify interactions
        verify(userMapper, times(1)).selectList(any(QueryWrapper.class));
        verify(userMapper, times(1)).insert(any(User.class));
        verify(passwordEncoder, times(1)).encode("password");
    }
    **/
    }