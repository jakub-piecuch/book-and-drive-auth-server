package redcode.bookanddrive.auth_server.users.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redcode.bookanddrive.auth_server.users.controller.dto.CreateUserRequest;
import redcode.bookanddrive.auth_server.users.controller.dto.UsersResponse;
import redcode.bookanddrive.auth_server.users.model.User;
import redcode.bookanddrive.auth_server.users.service.UsersService;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @PostMapping("/register")
    public ResponseEntity<UsersResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = User.from(request);
        User savedUser = usersService.create(user);
        UsersResponse response = UsersResponse.from(savedUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
