package com.example.airassist.user.controller;


import com.example.airassist.persistence.model.User;
import com.example.airassist.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Slf4j
@Validated
@Tag(name = "User API", description = "User management operations")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Get all users",
            description = "Fetches a paginated list of all users. Default page size is 3 and page number is 0."
    )
    @ApiResponse(responseCode = "200",
            description = "Users fetched successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "204", description = "No users found",
            content = @Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "500", description = "An unexpected error occurred",
            content = @Content(mediaType = "text/plain"))
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<User>> getAllUsers(
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

    @Operation(
            summary = "Get user by ID",
            description = "Fetches a user by their unique ID."
    )
    @ApiResponse(responseCode = "200", description = "User with specified ID found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "404", description = "User with specified ID not found",
            content = @Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "500", description = "An unexpected error occurred",
            content = @Content(mediaType = "text/plain"))
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @PathVariable Long id){
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

    @Operation(
            summary = "Save user",
            description = "Saves a new user, only if email or username does not exist. " +
                    "If the user already exists, it returns a conflict status (409)."
    )
    @ApiResponse(responseCode = "201", description = "User created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "409", description = "User already exists",
            content = @Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "500", description = "An unexpected error occurred",
            content = @Content(mediaType = "text/plain"))
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

    @Operation(
            summary = "Update user",
            description = "Updates an existing user by ID. " +
                    "If the user does not exist, it returns a bad request status (400)."
    )
    @ApiResponse(responseCode = "200", description = "User updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "400", description = "User with specified ID is null or user could not be updated",
            content = @Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "500", description = "An unexpected error occurred",
            content = @Content(mediaType = "text/plain"))
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody User user){
         log.info("Trying to update user: {}", user);
         user.setId(id);
         var updatedUser = userService.update(user);
         log.info("User {} updated successfully", user);
         return ResponseEntity.status(200).body(updatedUser.get());


    }

    @Operation(
            summary = "Patch user",
            description = "Patches an existing user by ID."
    )
    @ApiResponse(responseCode = "200", description = "User patched successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "400", description = "User with specified ID is null or user could not be patched",
            content = @Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "500", description = "An unexpected error occurred",
            content = @Content(mediaType = "text/plain"))
    @PatchMapping("/{id}")
    public ResponseEntity<User> patchUser(
            @PathVariable Long id,
            @RequestBody User user
    ){
        log.info("Trying to patch user: {}, id: {}" , user, id);
        user.setId(id);

        var patchedUser = userService.patch(user);
        log.info("User {} has been patched successfully", patchedUser);
        return ResponseEntity.ok(patchedUser.get());

    }

    @Operation(
            summary = "Delete user",
            description = "Deletes a user by their unique ID.")

    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @ApiResponse(responseCode = "404", description = "User with specified ID not found",
            content = @Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "500", description = "An unexpected error occurred",
            content = @Content(mediaType = "text/plain"))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(
        @PathVariable Long id
    ){
        log.info("Trying to delete user with ID: {}", id);
        userService.deleteById(id);
        log.info("Deleted user with ID: {} successfully!", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }
}

