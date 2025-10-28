package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.TeamService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TeamService teamService;

    @GetMapping
    public ResponseEntity<List<User>> findUsers(
            @RequestParam(name = "team", required = false) String teamName,
            @RequestHeader(name = "X-Trace-Id", required = false) String traceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Trace-Id", traceId != null ? traceId : "generated-" + System.currentTimeMillis());
        List<User> users = teamName == null ? userService.findAll() : userService.findByTeamName(teamName);
        return new ResponseEntity<>(users, headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findUserById(@PathVariable Long id) {
        return userService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User created = userService.createUser(user);
        return ResponseEntity.created(URI.create("/api/users/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        return userService.updateUser(id, user).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> patchEmail(@PathVariable Long id, @RequestParam("email") String email) {
        return userService.partialUpdate(id, email).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/lead/{teamId}")
    public ResponseEntity<User> assignLead(@PathVariable Long userId, @PathVariable UUID teamId) {
        return teamService.attachLead(teamId, userId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> headUser(@PathVariable Long id) {
        boolean exists = userService.findById(id).isPresent();
        return exists ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        return ResponseEntity.ok().allow(RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH,
                RequestMethod.DELETE, RequestMethod.HEAD, RequestMethod.OPTIONS).build();
    }

    @GetMapping("/teams/{teamId}/supporters")
    public ResponseEntity<Long> countSupporters(@PathVariable UUID teamId) {
        long supporters = teamService.countSupporters(teamId);
        return ResponseEntity.ok(supporters);
    }
}
