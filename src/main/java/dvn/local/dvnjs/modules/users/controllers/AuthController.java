package dvn.local.dvnjs.modules.users.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import dvn.local.dvnjs.modules.users.requests.LoginRequest;
import dvn.local.dvnjs.modules.users.resources.LoginResource;
import dvn.local.dvnjs.modules.users.services.interfaces.UserServiceInterface;
import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final UserServiceInterface userService;

    public AuthController(UserServiceInterface userService) {
        this.userService = userService;
    }

    @PostMapping("login")
    public ResponseEntity<LoginResource> login(@Valid @RequestBody LoginRequest request) {
        LoginResource auth = userService.login(request);
        return ResponseEntity.ok(auth);
    }
    
}
