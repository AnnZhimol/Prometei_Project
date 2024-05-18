package com.example.prometei.controllers;

import com.example.prometei.models.User;
import com.example.prometei.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/get")
    public ResponseEntity<User> getUser(@RequestParam Long id) {
        User user = userService.getById(id);
        return user == null
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
    }

    @PostMapping("/create")
    public void addUser(@RequestBody User user) {
        userService.add(user);
    }

    @PatchMapping("/edit")
    public void editUser(@RequestParam Long id,
                         @RequestBody User user) {
        userService.edit(id, user);
    }

    @DeleteMapping("/delete")
    public void deleteUser(@RequestBody User user) {
        userService.delete(user);
    }
}
