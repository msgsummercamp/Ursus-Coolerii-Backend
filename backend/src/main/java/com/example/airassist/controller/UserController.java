package com.example.airassist.controller;


import com.example.airassist.common.dto.UserDTO;
import com.example.airassist.persistence.model.User;
import com.example.airassist.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Slf4j
@Validated
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "3") Integer pageSize
    ) {

        log.info("Trying to fetch all users");
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        var users = userService.findAll(pageable);
        if (users.isEmpty()) {
            log.warn("No users found");
            return ResponseEntity.noContent().build();
        }
        log.info("Users fetched successfully: {}", users);
        return ResponseEntity.ok(users.getContent());

    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @PathVariable UUID id){
        log.info("Trying to fetch user with id: {}", id);

        var user = userService.findById(id);
        if(user.isEmpty())
        {
            log.warn("User with id {} could not be found", id);
            return ResponseEntity.notFound().build();
        }
        log.info("User with id {} has been found: {}", id, user);
        return ResponseEntity.ok().body(user.get());


    }

    @PostMapping
    public ResponseEntity<User> saveUser(@Valid @RequestBody User user){
        log.info("Trying to save user");

        var savedUser = userService.save(user);
        if(savedUser.isEmpty()) {
            log.warn("The specified user already exists: {}", user);
            return ResponseEntity.status(409).build();
        }
        URI location = URI.create("/api/user/" + savedUser.get().getId());

        return ResponseEntity.created(location)
                .body(user);

    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody User user){
         log.info("Trying to update user: {}", user);
         user.setId(id);
         var updatedUser = userService.update(user);
         log.info("User {} updated successfully", user);
         return ResponseEntity.status(200).body(updatedUser.get());


    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> patchUser(
            @PathVariable UUID id,
            @RequestBody User user
    ){
        log.info("Trying to patch user: {}, id: {}" , user, id);
        user.setId(id);

        var patchedUser = userService.patch(user);
        log.info("User {} has been patched successfully", patchedUser);
        return ResponseEntity.ok(patchedUser.get());

    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(
        @PathVariable UUID id
    ){
        log.info("Trying to delete user with ID: {}", id);
        userService.deleteById(id);
        log.info("Deleted user with ID: {} successfully!", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

}

