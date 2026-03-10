package com.conquest.user_service.service;

import com.conquest.user_service.dto.UserDto;
import com.conquest.user_service.entity.User;
import com.conquest.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDto);
    }
    
    public Optional<UserDto> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::convertToDto);
    }
    
    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("User with email " + userDto.getEmail() + " already exists");
        }
        
        User user = convertToEntity(userDto);
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }
    
    public Optional<UserDto> updateUser(Long id, UserDto userDto) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setFirstName(userDto.getFirstName());
                    existingUser.setLastName(userDto.getLastName());
                    existingUser.setEmail(userDto.getEmail());
                    existingUser.setPhone(userDto.getPhone());
                    existingUser.setAddress(userDto.getAddress());
                    return convertToDto(userRepository.save(existingUser));
                });
    }
    
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    private UserDto convertToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress()
        );
    }
    
    private User convertToEntity(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getEmail(),
                userDto.getPhone(),
                userDto.getAddress()
        );
    }
}