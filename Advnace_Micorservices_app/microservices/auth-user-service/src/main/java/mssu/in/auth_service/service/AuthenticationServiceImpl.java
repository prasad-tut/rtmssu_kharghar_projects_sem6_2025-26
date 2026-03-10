package mssu.in.auth_service.service;

import mssu.in.auth_service.dto.LoginRequest;
import mssu.in.auth_service.dto.LoginResponse;
import mssu.in.auth_service.dto.SignupRequest;
import mssu.in.auth_service.entity.Role;
import mssu.in.auth_service.entity.User;
import mssu.in.auth_service.exception.InvalidCredentialsException;
import mssu.in.auth_service.exception.InvalidTokenException;
import mssu.in.auth_service.exception.UserAlreadyExistsException;
import mssu.in.auth_service.exception.UserNotFoundException;
import mssu.in.auth_service.repository.UserRepository;
import mssu.in.auth_service.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthenticationServiceImpl(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException());

        if (!user.isActive()) {
            throw new InvalidCredentialsException("Account is deactivated");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        user.setOnline(true);
        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user);

        return new LoginResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                86400000L // 24 hours in ms, hardcoded for now or use provider getter if available
        );
    }

    @Override
    public User signup(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new UserAlreadyExistsException("email", signupRequest.getEmail());
        }

        User user = new User();
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setPhone(signupRequest.getPhone());
        user.setAddress(signupRequest.getAddress());
        user.setRole(signupRequest.getRole() != null ? signupRequest.getRole() : Role.CUSTOMER);

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserFromToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new InvalidTokenException();
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found for token"));
    }

    @Override
    public void logout(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setOnline(false);
            userRepository.save(user);
        });
    }
}
