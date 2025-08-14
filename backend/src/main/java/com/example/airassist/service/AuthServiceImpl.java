package com.example.airassist.service;

import com.example.airassist.common.dto.*;
import com.example.airassist.common.exceptions.UserAlreadyExistsException;
import com.example.airassist.common.exceptions.UserSaveFailedException;
import com.example.airassist.jwt.JwtTokenProvider;
import com.example.airassist.persistence.dao.UserRepository;
import com.example.airassist.persistence.model.Role;
import com.example.airassist.persistence.model.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private RedisTemplate redisTemplate;
    private MailSenderService mailSenderService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserRepository userRepository, PasswordEncoder passwordEncoder, RedisTemplate redisTemplate, MailSenderService mailSenderService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisTemplate = redisTemplate;
        this.mailSenderService = mailSenderService;
    }

    @Value("${redis.email.existence.key}")
    private String REDIS_PREFIX;


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
        String password = user.getPassword();
        user.setPassword(passwordEncoder.encode(password));
        Set<Role> roles = new HashSet<Role>();
        roles.add(new Role("PASSENGER"));
        user.setRole(roles);
        user =  Optional.ofNullable(userRepository.save(user)).orElseThrow(() ->{
            log.error("An unexpected error occurred while saving user");
            return new UserSaveFailedException("An unexpected error occurred while saving user", HttpStatus.INTERNAL_SERVER_ERROR);
        });

        String key = REDIS_PREFIX + user.getEmail();
        redisTemplate.opsForValue().set(key, true, Duration.ofHours(1));
        log.info("User {} saved successfully", user.getEmail());

        mailSenderService.sendMailWithPass(user.getEmail(), password);
    }

    @Override
    public boolean checkLogged(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    @Override
    public boolean checkMatchID(String token, UUID passengerId) {
        UUID idFromToken = jwtTokenProvider.getId(token);
        if(passengerId.equals(idFromToken)){
            return true;
        }
        return false;
    }

    private User createUserWithGeneratedPassword(String email, String firstName, String lastName) {
        String generatedPassword = UUID.randomUUID().toString().substring(0, 6);
        if(email == null || email.isEmpty()) {
            log.error("Email cannot be null or empty");
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if(firstName == null || firstName.isEmpty()) {
            log.error("First name cannot be null or empty");
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if(lastName == null || lastName.isEmpty()) {
            log.error("Last name cannot be null or empty");
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        return User.builder()
                .email(email)
                .password(generatedPassword)
                .firstName(firstName)
                .lastName(lastName)
                .isFirstLogin(true)
                .role(new HashSet<>())
                .build();
    }
}
