package com.example.airassist.service;

import com.example.airassist.common.dto.LoginRequest;
import com.example.airassist.common.dto.LoginResponse;
import com.example.airassist.common.dto.SignupRequest;
import com.example.airassist.common.dto.SignupResponse;
import com.example.airassist.common.exceptions.UserAlreadyExistsException;
import com.example.airassist.common.exceptions.UserSaveFailedException;
import com.example.airassist.jwt.JwtTokenProvider;
import com.example.airassist.persistence.dao.UserRepository;
import com.example.airassist.persistence.model.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public LoginResponse login(LoginRequest loginRequest) {
      log.info("Log in request received: {}", loginRequest);
      Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
              loginRequest.getEmail(),
              loginRequest.getPassword()
      ));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      String token = jwtTokenProvider.generateToken(authentication);
      log.info("Generated token: {}", token);
      return new LoginResponse(token);
    }


    private void checkUserExists(String email) throws UserAlreadyExistsException {
        if(userRepository.findByEmail(email).isPresent()
        ){
            log.warn("User with email {}  already exists in DB", email);
            throw new UserAlreadyExistsException("User with email "+
                    email + " already exists in DB", HttpStatus.CONFLICT);        }
    }

    @Override
    public void signup(SignupRequest signupRequest) {

        log.info("Signup request received: {}", signupRequest);

        checkUserExists(signupRequest.getEmail());

        User user = createUserWithGeneratedPassword(signupRequest.getEmail(), signupRequest.getFirstName(), signupRequest.getLastName());

        user =  Optional.ofNullable(userRepository.save(user)).orElseThrow(() ->{
            log.error("An unexpected error occurred while saving user");
            return new UserSaveFailedException("An unexpected error occurred while saving user", HttpStatus.INTERNAL_SERVER_ERROR);
        });

        log.info("User {} saved successfully", user.getEmail());
    }

    private User createUserWithGeneratedPassword(String email, String firstName, String lastName) {
        String generatedPassword = UUID.randomUUID().toString().substring(0, 6);
        return User.builder()
                .email(email)
                .password(passwordEncoder.encode(generatedPassword))
                .firstName(firstName)
                .lastName(lastName)
                .isFirstLogin(true)
                .role(new HashSet<>())
                .build();
    }
}
