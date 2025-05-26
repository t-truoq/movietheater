package org.example.movie.configuration;

import org.example.movie.entity.Account;
import org.example.movie.entity.Role;
import org.example.movie.enums.AccountStatus;
import org.example.movie.repository.AccountRepository;
import org.example.movie.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final String initialAdminPassword;

    public DataInitializer(RoleRepository roleRepository,
                           AccountRepository accountRepository,
                           PasswordEncoder passwordEncoder,
                           @Value("${initial.admin.password}") String initialAdminPassword) {
        this.roleRepository = roleRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.initialAdminPassword = initialAdminPassword;
    }

    @Override
    public void run(String... args) throws Exception {
        // Tạo vai trò ADMIN nếu chưa tồn tại
        Role adminRole = roleRepository.findByRoleName("ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setRoleName("ADMIN");
            return roleRepository.save(role);
        });

        // Kiểm tra và tạo tài khoản admin nếu chưa tồn tại
        if (!accountRepository.findByUsername("admin").isPresent()) {
            Account adminAccount = new Account();
            adminAccount.setUsername("admin");
            adminAccount.setPassword(passwordEncoder.encode(initialAdminPassword));
            adminAccount.setFullName("Admin User");
            adminAccount.setEmail("admin@example.com");
            adminAccount.setAddress("Admin Address");
            adminAccount.setDateOfBirth(LocalDate.of(1990, 1, 1));
            adminAccount.setGender("MALE");
            adminAccount.setIdentityCard("123456789");
            adminAccount.setPhoneNumber("0123456789");
            adminAccount.setStatus(AccountStatus.ACTIVE);
            adminAccount.setRole(adminRole);
            accountRepository.save(adminAccount);
            System.out.println("Admin account created with username: admin and password: " + initialAdminPassword);
        } else {
            System.out.println("Admin account already exists.");
        }
    }
}