package com.example.airassist.auth.service;


import com.example.airassist.dto.LoginRequest;
import com.example.airassist.dto.LoginResponse;
import com.example.airassist.dto.SignupRequest;
import com.example.airassist.dto.SignupResponse;
import com.example.airassist.exceptions.UserAlreadyExistsException;
import com.example.airassist.exceptions.UserSaveFailedException;
import com.example.airassist.jwt.JwtTokenProvider;
import com.example.airassist.persistence.dao.UserRepository;
import com.example.airassist.persistence.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserRepository userRepository,  PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        log.info("AuthServiceImpl initialized");
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
      log.info("Log in request received: {}", loginRequest);

      Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
              loginRequest.getUsername(),
              loginRequest.getPassword()
      ));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      String token = jwtTokenProvider.generateToken(authentication);
      log.info("Generated token: {}", token);
      return new LoginResponse(token);
    }


    private void checkUserExists(String username, String email) throws UserAlreadyExistsException {
        if(userRepository.findByUsername(username).isPresent() ||
            userRepository.findByEmail(email).isPresent()
        ){
            log.warn("User with username {} or email {}  already exists in DB", username, email);
            throw new UserAlreadyExistsException("User with username " +  username + "or email "+
                    email + " already exists in DB", HttpStatus.CONFLICT);        }
    }

    @Override
    public SignupResponse signup(SignupRequest signupRequest) {

        log.info("Signup request received: {}", signupRequest);

        checkUserExists(signupRequest.getUsername(), signupRequest.getEmail());

        User user = User.builder()
                .username(signupRequest.getUsername())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .email(signupRequest.getEmail())
                .firstName(signupRequest.getFirstName())
                .lastName(signupRequest.getLastName())
                .role(new HashSet<>())
                .build();

        user =  Optional.ofNullable(userRepository.save(user)).orElseThrow(() ->{
            log.error("An unexpected error occurred while saving user");
            return new UserSaveFailedException("An unexpected error occurred while saving user", HttpStatus.INTERNAL_SERVER_ERROR);
        });

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signupRequest.getUsername(),
                        signupRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        log.info("User {} saved and authenticated successfully", user.getUsername());
        return new SignupResponse(token);
    }
}
