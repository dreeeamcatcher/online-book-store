package store.onlinebookstore.service;

import store.onlinebookstore.dto.user.UserRegistrationRequestDto;
import store.onlinebookstore.dto.user.UserResponseDto;
import store.onlinebookstore.exception.RegistrationException;

public interface UserService {
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException;
}
