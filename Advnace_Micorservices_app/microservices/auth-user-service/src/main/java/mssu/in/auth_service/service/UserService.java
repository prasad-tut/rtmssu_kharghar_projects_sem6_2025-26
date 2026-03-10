package mssu.in.auth_service.service;

import mssu.in.auth_service.dto.UserResponse;
import mssu.in.auth_service.entity.Role;
import mssu.in.auth_service.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User createUser(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    List<User> findByRole(Role role);

    User updateUser(Long id, User userDetails);

    void deleteUser(Long id);

    void deactivateUser(Long id);

    void activateUser(Long id);

    boolean existsByEmail(String email);

    long countByRole(Role role);

    UserResponse toUserResponse(User user);
}
