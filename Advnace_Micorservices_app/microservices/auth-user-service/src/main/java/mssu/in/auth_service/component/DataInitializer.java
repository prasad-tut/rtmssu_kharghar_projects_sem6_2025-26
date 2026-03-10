package mssu.in.auth_service.component;

import mssu.in.auth_service.entity.Role;
import mssu.in.auth_service.entity.User;
import mssu.in.auth_service.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail("admin@tickethub.com")) {
            User admin = new User();
            admin.setName("Super Admin");
            admin.setEmail("admin@tickethub.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setActive(true);
            userRepository.save(admin);
            System.out.println("Default admin user created: admin@tickethub.com / admin123");
        }

        // Create 3 Executives
        createExecutiveIfNotExist("Executive One", "executive1@tickethub.com", "exec123");
        createExecutiveIfNotExist("Executive Two", "executive2@tickethub.com", "exec456");
        createExecutiveIfNotExist("Executive Three", "executive3@tickethub.com", "exec789");
    }

    private void createExecutiveIfNotExist(String name, String email, String password) {
        if (!userRepository.existsByEmail(email)) {
            User exec = new User();
            exec.setName(name);
            exec.setEmail(email);
            exec.setPassword(passwordEncoder.encode(password));
            exec.setRole(Role.EXECUTIVE);
            exec.setActive(true);
            userRepository.save(exec);
            System.out.println("Executive user created: " + email + " / " + password);
        }
    }
}
