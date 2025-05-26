package org.example.movie.controller;

import org.example.movie.dto.request.AddEmployeeRequest;
import org.example.movie.dto.request.EditEmployeeRequest;
import org.example.movie.dto.response.EmployeeResponse;
import org.example.movie.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EmployeeResponse>> getEmployeeList(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(employeeService.getEmployeeList(search));
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponse> addEmployee(@RequestBody AddEmployeeRequest request) {
        EmployeeResponse response = employeeService.addEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/edit/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> editEmployee(@PathVariable String employeeId, @RequestBody EditEmployeeRequest request) {
        String result = employeeService.editEmployee(employeeId, request);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteEmployee(@PathVariable String employeeId) {
        String result = employeeService.deleteEmployee(employeeId);
        return ResponseEntity.ok(result);
    }
}