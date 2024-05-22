package com.example.prometei.controllers;

import com.example.prometei.dto.UserDto;
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
    public ResponseEntity<UserDto> getUser(@RequestParam Long id) {
        User user = userService.getById(id);
        return user == null
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(new UserDto(user), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return new ResponseEntity<>(userService.getAll()
                                    .stream()
                                    .map(UserDto::new)
                                    .toList(),
                                    HttpStatus.OK);
    }

    @PostMapping("/create")
    public void addUser(@RequestBody UserDto userDto) {
        userService.add(userDto.dtoToEntity());
    }

    @PatchMapping("/edit")
    public void editUser(@RequestParam Long id,
                         @RequestBody UserDto userDto) {
        userService.edit(id, userDto.dtoToEntity());
    }

    @DeleteMapping("/delete")
    public void deleteUser(@RequestBody UserDto userDto) {
        userService.delete(userDto.dtoToEntity());
    }
}
