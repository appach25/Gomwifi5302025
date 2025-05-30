package gomwifiv1.gomwifiv1.config;

import gomwifiv1.gomwifiv1.model.User;
import gomwifiv1.gomwifiv1.model.Role;
import gomwifiv1.gomwifiv1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // Create admin user if it doesn't exist
        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin"));
            adminUser.setRole(Role.ADMIN);
            adminUser.setEnabled(true);
            userRepository.save(adminUser);
            System.out.println("Admin user created successfully");
        }
    }
}
