package com.example.airassist.service;

import com.example.airassist.persistence.dao.UserRepository;
import com.example.airassist.persistence.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@SpringBootTest(classes = com.example.airassist.Application.class)
public class UserServiceTests {
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @BeforeEach
    void setUp(){
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        userService = new UserServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    void testFindByEmail_returnsUser(){
        User user = new User();
        user.setEmail("mail");
        when(userRepository.findByEmail("mail")).thenReturn(Optional.of(user));
        assertEquals(Optional.of(user), userService.findByEmail("mail"));
    }

    @Test
    void testFindByEmail_returnsEmpty(){
        User user =new User();
        user.setEmail("mail");
        when(userRepository.findByEmail("mail")).thenReturn(Optional.empty());
        assertEquals(Optional.empty(), userService.findByEmail("mail"));
    }

    @Test
    void testFindById_returnsUser(){
        User user = new User();
        UUID id = UUID.fromString("e4b0b82f-23ac-4b57-9645-b8cc8fdf71c2");
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        assertEquals(Optional.of(user), userService.findById(id));
    }

    @Test
    void testFindById_returnsEmpty(){
        UUID id = UUID.fromString("e4b0b82f-23ac-4b57-9645-b8cc8fdf71c2");
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertEquals(Optional.empty(), userService.findById(id));
    }

    @Test
    void testCreateUserWithRandomPassword_createsUserCorrectly() {
        String email = "test@example.com";
        String password = "password";
        String encodedPassword = "encodedPassword";
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);

        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn(encodedPassword);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserService userService = new UserServiceImpl(userRepository, passwordEncoder);
        User user = userService.createUserWithRandomPassword(email);

        assertEquals(email, user.getEmail());
        assertEquals(encodedPassword, user.getPassword());
        assertTrue(user.isFirstLogin());
    }

    @Test
    void testCreateUserWithRandomPassword_passwordIsEncoded() {
        String email = "user@example.com";
        String encodedPassword = "encoded";
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);

        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn(encodedPassword);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserService userService = new UserServiceImpl(userRepository, passwordEncoder);
        User user = userService.createUserWithRandomPassword(email);

        assertNotEquals(user.getPassword(), null);
        assertEquals(encodedPassword, user.getPassword());
    }

    @Test
    void testCreateUserWithRandomPassword_callsSave() {
        String email = "user@example.com";
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);

        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encoded");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserService userService = new UserServiceImpl(userRepository, passwordEncoder);
        userService.createUserWithRandomPassword(email);

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }
}
