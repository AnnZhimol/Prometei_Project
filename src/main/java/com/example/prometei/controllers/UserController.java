package com.example.prometei.controllers;

import com.example.prometei.dto.UserDtos.EditUserDto;
import com.example.prometei.dto.UserDtos.UserDto;
import com.example.prometei.models.User;
import com.example.prometei.services.baseServices.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.prometei.utils.CipherUtil.decryptId;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/getCurrent")
    public ResponseEntity<UserDto> getCurrentUser() {
        User user = userService.getCurrentUser();
        return user == null
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(new UserDto(user), HttpStatus.OK);
    }

    /**
     * Получает информацию о пользователе по его идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return объект ResponseEntity с UserDto и статусом OK, если пользователь найден, или NO_CONTENT, если пользователь не найден
     */
    @GetMapping("/get")
    public ResponseEntity<UserDto> getUser(@RequestParam String userId) {
        User user = userService.getById(decryptId(userId));
        return user == null
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(new UserDto(user), HttpStatus.OK);
    }

    /**
     * Получает список всех пользователей.
     *
     * @return объект ResponseEntity со списком UserDto и статусом OK
     */
    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return new ResponseEntity<>(userService.getAll()
                                    .stream()
                                    .map(UserDto::new)
                                    .toList(),
                                    HttpStatus.OK);
    }

    @Deprecated
    @PostMapping("/create")
    public void addUser(@RequestBody EditUserDto userDto) {
        userService.add(userDto.dtoToEntity());
    }

    /**
     * Редактирует информацию о пользователе.
     *
     * @param userId идентификатор пользователя
     * @param userDto объект UserDto, содержащий обновленную информацию о пользователе
     */
    @PatchMapping("/edit")
    public void editUser(@RequestParam String userId,
                         @RequestBody EditUserDto userDto) {
        userService.edit(decryptId(userId), userDto.dtoToEntity());
    }

    @Deprecated
    @DeleteMapping("/delete")
    public void deleteUser(@RequestBody EditUserDto userDto) {
        userService.delete(userDto.dtoToEntity());
    }
}
