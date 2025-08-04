package com.example.airassist.service;

import com.example.airassist.persistence.dao.UserRepository;
import com.example.airassist.persistence.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
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
        userService = new UserServiceImpl(userRepository);
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


}
