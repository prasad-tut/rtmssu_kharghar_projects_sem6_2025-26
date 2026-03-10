package mssu.in.auth_service.service;

import mssu.in.auth_service.dto.LoginRequest;
import mssu.in.auth_service.dto.LoginResponse;
import mssu.in.auth_service.dto.SignupRequest;
import mssu.in.auth_service.entity.User;

public interface AuthenticationService {

    LoginResponse login(LoginRequest loginRequest);

    User signup(SignupRequest signupRequest);

    boolean validateToken(String token);

    User getUserFromToken(String token);

    void logout(String email);
}
