package store.onlinebookstore.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.onlinebookstore.dto.user.UserLoginRequestDto;
import store.onlinebookstore.dto.user.UserLoginResponseDto;
import store.onlinebookstore.dto.user.UserRegistrationRequestDto;
import store.onlinebookstore.dto.user.UserResponseDto;
import store.onlinebookstore.exception.RegistrationException;
import store.onlinebookstore.security.AuthenticationService;
import store.onlinebookstore.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }

    @PostMapping("/register")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto request)
            throws RegistrationException {
        return userService.register(request);
    }

}
