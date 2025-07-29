package com.example.airassist.user.service;


import com.example.airassist.exceptions.InvalidUserIdException;
import com.example.airassist.exceptions.UserNotFoundException;
import com.example.airassist.exceptions.UserSaveFailedException;
import com.example.airassist.persistence.dao.UserRepository;
import com.example.airassist.persistence.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        log.info("UserServiceImpl initialized");
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        log.info("Finding user by username: {}", username);
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            log.warn("User not found with username: {}", username);
        } else {
            log.info("User found: {}", user.get());
        }
        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {

        log.info("Finding user by email: {}", email);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            log.warn("User not found with email: {}", email);
        } else {
            log.info("User found: {}", user.get());
        }
        return user;
    }

    @Override
    public Optional<User> save(User user) {
        log.info("Saving user: {}", user);

        if(userRepository.findByUsername(user.getUsername()).isPresent() || userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.warn("User with username {} and/or email {} already exists", user.getUsername(), user.getEmail());
            return Optional.empty();
        }

        if(user.getId() != null){
            log.warn("User id {} should be null!", user.getId());
            throw new InvalidUserIdException("User id " + user.getId() + " should be null when saving", HttpStatus.BAD_REQUEST);
        }

        User savedUser = Optional.ofNullable(userRepository.save(user))
                .orElseThrow(() -> {
                    log.error("An unexpected error occurred while saving user");
                    return new UserSaveFailedException("User could not be saved!", HttpStatus.INTERNAL_SERVER_ERROR);
                });



        log.info("User saved successfully: {}", savedUser);
        return Optional.of(savedUser);
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            log.warn("User with id {} not found", id);
            throw new UserNotFoundException("User with id " + id + " not found", HttpStatus.NOT_FOUND);
        }

        userRepository.deleteById(id);
        log.info("User with id {} deleted successfully", id);
    }

    @Override
    public Page<User> findAll(Pageable pageable){
        log.info("Finding all users");
        Page<User> users = userRepository.findAll(pageable);
        if (!users.iterator().hasNext()) {
            log.warn("No users found");
        } else {
            log.info("Users found: {}", users);
        }
        return users;
    }


    @Override
    public Optional<User> findById(Long id) {
        log.info("Finding user by id: {}", id);
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty())
            log.warn("User not found with id: {}", id);
        else
            log.info("User found: {}", user.get());

        return user;
    }

    @Override
    public Optional<User> update(User user) {
        log.info("Updating user: {}", user);
        if (user.getId() == null) {
            log.error("User ID is null, cannot update user");
            return Optional.empty();
        }
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isEmpty()) {
            log.warn("User not found with id: {}", user.getId());
        }

        User updatedUser = Optional.ofNullable(userRepository.save(user)).orElseThrow(() ->{
            log.error("User {} could not be updated!", user);
            return new UserSaveFailedException("User " + user + " could not be updated!", HttpStatus.INTERNAL_SERVER_ERROR);
        });

        log.info("User updated successfully: {}", updatedUser);
        return Optional.of(updatedUser);
    }

    @Override
    public Optional<User> patch(User user) {
        log.info("Patching user: {}", user);
        if(user.getId() == null){
            log.error("User ID is null, cannot patch user!");
            throw new InvalidUserIdException("User ID is null, cannot patch user!", HttpStatus.BAD_REQUEST);
        }

        Optional<User> existingUser = userRepository.findById(user.getId());
        if(existingUser.isEmpty()){
            log.error("User not found with id: {}, cannot patch!", user.getId());
            throw new UserNotFoundException("User not found with id: " + user.getId(), HttpStatus.NOT_FOUND);
        }
        fillNullFieldsForPatch(user, existingUser.get());
        User updatedUser = Optional.ofNullable(userRepository.save(user)).orElseThrow(() -> {
            log.error("User {} could not be updated!", user);
            return new UserSaveFailedException("User " + user + " could not be updated!", HttpStatus.INTERNAL_SERVER_ERROR);
        });
        log.info("User patched successfully: {}", updatedUser);
        return Optional.of(updatedUser);
    }

    private void fillNullFieldsForPatch(User uncompleteUser, User completeUser){
        if (uncompleteUser.getUsername() == null)
            uncompleteUser.setUsername(completeUser.getUsername());
        if (uncompleteUser.getPassword() == null)
            uncompleteUser.setPassword(completeUser.getPassword());
        if (uncompleteUser.getEmail() == null)
            uncompleteUser.setEmail(completeUser.getEmail());
    }
}
