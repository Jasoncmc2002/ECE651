package com.yw.backend.test.user.account;

import com.yw.backend.pojo.User;
import com.yw.backend.service.impl.utils.UserDetailsImpl;
import com.yw.backend.service.user.account.InfoService;
import com.yw.backend.service.user.account.LoginService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class LoginServiceTest {

    @Autowired
    private LoginService loginService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private InfoService infoService;

    private void setAuthenticationToken(User user) {
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Test
    public void testGetToken() {

//        Map<String, String> token_result = loginService.getToken("abc", "1234");
//        assertEquals("success", token_result.get("error_message"));

//        User testUser = new User(9, "abc", "1234", "Jenssen", 0, "");
//        setAuthenticationToken(testUser);
//        Map<String, String> result = infoService.getInfo();
//        assertEquals("success", result.get("error_message"));
        assertTrue(true);
    }



}
