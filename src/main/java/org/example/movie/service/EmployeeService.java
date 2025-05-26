 package org.example.movie.service;
import org.example.movie.dto.request.AddEmployeeRequest;
import org.example.movie.dto.request.EditEmployeeRequest;
import org.example.movie.dto.response.EmployeeResponse;
import org.example.movie.entity.Account;
import org.example.movie.entity.Employee;
import org.example.movie.entity.Role;
import org.example.movie.enums.AccountStatus;
import org.example.movie.mapper.EmployeeMapper;
import org.example.movie.repository.AccountRepository;
import org.example.movie.repository.EmployeeRepository;
import org.example.movie.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public List<EmployeeResponse> getEmployeeList(String searchKeyword) {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .map(employee -> {
                    Account account = accountRepository.findById(employee.getAccount().getAccountId())
                            .orElseThrow(() -> new RuntimeException("Account not found"));
                    return employeeMapper.toResponse(employee, account);
                })
                .filter(response -> searchKeyword == null || response.getFullName().contains(searchKeyword) ||
                        response.getEmail().contains(searchKeyword) || response.getPhoneNumber().contains(searchKeyword))
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponse addEmployee(AddEmployeeRequest request) {
        // 1. Kiểm tra password và confirm password
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        // 2. Kiểm tra username đã tồn tại chưa
        if (accountRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("The inputted account is already existed, please choose another account name");
        }

        // 3. Lấy role EMPLOYEE từ DB
        Role employeeRole = roleRepository.findByRoleName("EMPLOYEE")
                .orElseThrow(() -> new RuntimeException("Role EMPLOYEE not found"));

        // 4. Tạo account mới
        Account account = Account.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .address(request.getAddress())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .identityCard(request.getIdentityCard())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .image(request.getImage())
                .status(AccountStatus.ACTIVE) // trạng thái mặc định
                .role(employeeRole) // gán role EMPLOYEE
                .build();

        // 5. Lưu account trước để có ID
        accountRepository.save(account);

        // 6. Tạo employee mới liên kết account
        Employee employee = Employee.builder()
                .employeeId(generateEmployeeId()) // Hàm tạo mã nhân viên
                .account(account)
                .build();

        employeeRepository.save(employee);

        // 7. Trả về EmployeeResponse
        return employeeMapper.toResponse(employee, account); // Sửa ở đây: truyền cả employee và account
    }

    @PreAuthorize("hasRole('ADMIN')")
    public String editEmployee(String employeeId, EditEmployeeRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        Account account = accountRepository.findById(employee.getAccount().getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (request.getPassword() != null && !request.getPassword().equals(request.getConfirmPassword())) {
            return "Passwords do not match";
        }

        account.setImage(request.getImage() != null ? request.getImage() : account.getImage());
        account.setUsername(request.getUsername() != null ? request.getUsername() : account.getUsername());
        if (request.getPassword() != null) {
            account.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        account.setFullName(request.getFullName() != null ? request.getFullName() : account.getFullName());
        account.setDateOfBirth(request.getDateOfBirth() != null ? request.getDateOfBirth() : account.getDateOfBirth());
        account.setGender(request.getGender() != null ? request.getGender() : account.getGender());
        account.setIdentityCard(request.getIdentityCard() != null ? request.getIdentityCard() : account.getIdentityCard());
        account.setEmail(request.getEmail() != null ? request.getEmail() : account.getEmail());
        account.setPhoneNumber(request.getPhoneNumber() != null ? request.getPhoneNumber() : account.getPhoneNumber());
        account.setAddress(request.getAddress() != null ? request.getAddress() : account.getAddress());

        accountRepository.save(account);

        return "Employee edited successfully";
    }

    @PreAuthorize("hasRole('ADMIN')")
    public String deleteEmployee(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        Account account = accountRepository.findById(employee.getAccount().getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setStatus(AccountStatus.DISABLE);
        accountRepository.save(account);

        return "Employee deleted successfully";
    }
    private String generateEmployeeId() {
        // Logic tạo employeeId (ví dụ: EMP001, EMP002)
        List<Employee> employees = employeeRepository.findAll();
        String lastId = employees.stream().map(Employee::getEmployeeId)
                .filter(id -> id != null && id.startsWith("EMP"))
                .max((id1, id2) -> {
                    int num1 = Integer.parseInt(id1.replace("EMP", ""));
                    int num2 = Integer.parseInt(id2.replace("EMP", ""));
                    return Integer.compare(num1, num2);
                }).orElse("EMP000");
        int nextNum = Integer.parseInt(lastId.replace("EMP", "")) + 1;
        return String.format("EMP%03d", nextNum);
    }
}
