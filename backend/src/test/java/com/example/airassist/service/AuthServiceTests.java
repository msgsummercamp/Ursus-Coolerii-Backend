package com.example.airassist.service;

import com.example.airassist.common.dto.LoginRequest;
import com.example.airassist.jwt.JwtTokenProvider;
import com.example.airassist.persistence.dao.UserRepository;
import com.example.airassist.persistence.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

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

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        authService = new AuthServiceImpl(authenticationManager, jwtTokenProvider, userRepository, passwordEncoder);
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

    @Test
    void testRegisterUserWithGeneratedPassword_passwordIsEncoded() {
        String email = "user@example.com";
        String encodedPassword = "encoded";
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        authService.registerUserWithGeneratedPassword(email);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(encodedPassword, savedUser.getPassword());
    }

    @Test
    void testRegisterUserWithGeneratedPassword_callsSave() {
        String email = "user@example.com";
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        authService.registerUserWithGeneratedPassword(email);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUserWithGeneratedPassword_isFirstLoginTrue() {
        String email = "user@example.com";
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        authService.registerUserWithGeneratedPassword(email);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertTrue(savedUser.isFirstLogin());
    }

}