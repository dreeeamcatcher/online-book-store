package store.onlinebookstore.service.impl;

import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import store.onlinebookstore.dto.user.UserRegistrationRequestDto;
import store.onlinebookstore.dto.user.UserResponseDto;
import store.onlinebookstore.exception.RegistrationException;
import store.onlinebookstore.mapper.UserMapper;
import store.onlinebookstore.model.Role;
import store.onlinebookstore.model.RoleName;
import store.onlinebookstore.model.User;
import store.onlinebookstore.repository.role.RoleRepository;
import store.onlinebookstore.repository.user.UserRepository;
import store.onlinebookstore.service.ShoppingCartService;
import store.onlinebookstore.service.UserService;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ShoppingCartService shoppingCartService;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findUserByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("Unable to complete registration.");
        }
        Role roleUser = roleRepository.findRoleByName(RoleName.ROLE_USER);
        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        user.setShippingAddress(requestDto.getShippingAddress());
        user.setRoles(new HashSet<>(List.of(roleUser)));
        User savedUser = userRepository.save(user);
        shoppingCartService.createShoppingCart(savedUser);
        return userMapper.toUserResponse(savedUser);
    }
}
