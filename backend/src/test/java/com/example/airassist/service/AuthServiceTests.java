package com.example.airassist.service;

import com.example.airassist.common.dto.LoginRequest;
import com.example.airassist.jwt.JwtTokenProvider;
import com.example.airassist.persistence.dao.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = com.example.airassist.Application.class)
public class AuthServiceTests {
    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @Mock
    private MailSenderService mailSenderService;

    @Mock
    private RedisTemplate redisTemplate;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        authService = new AuthServiceImpl(authenticationManager, jwtTokenProvider, userRepository, passwordEncoder, redisTemplate, mailSenderService);
    }

    @Test
    void testLogin_Ok() {
        Authentication fakeAuthentication = mock(Authentication.class);
        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("user", "password")))
                .thenReturn(fakeAuthentication);
        when(jwtTokenProvider.generateToken(fakeAuthentication)).thenReturn("MockToken");
        LoginRequest request = new LoginRequest("user", "password");
        assertEquals("MockToken", authService.login(request).getToken());
    }

    @Test
    void testLogin_BadCredentials() {
        Authentication fakeAuthentication = mock(Authentication.class);
        fakeAuthentication.setAuthenticated(false);
        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("user", "password"))).thenReturn(fakeAuthentication);
        when(jwtTokenProvider.generateToken(fakeAuthentication)).thenReturn(null);
        LoginRequest request = new LoginRequest("user", "password");
        assertNull(authService.login(request).getToken());
    }


}