package org.example.movie.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Employee Management", description = "APIs for managing employee accounts, restricted to ADMIN role")
@SecurityRequirement(name = "bearerAuth") // Yêu cầu Bearer token cho tất cả endpoint
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get list of employees", description = "Retrieves a list of employees, optionally filtered by a search keyword.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of employees retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "User does not have ADMIN role", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<List<EmployeeResponse>> getEmployeeList(
            @Parameter(description = "Optional search keyword to filter employees by name or other attributes", example = "John")
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(employeeService.getEmployeeList(search));
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add new employee", description = "Creates a new employee account with the specified details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee added successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body or duplicate employee data", content = @Content),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "User does not have ADMIN role", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<EmployeeResponse> addEmployee(
            @Valid @RequestBody @Schema(description = "Details for creating a new employee") AddEmployeeRequest request) {
        EmployeeResponse response = employeeService.addEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/edit/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Edit employee details", description = "Updates the details of an existing employee by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee details updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body or employee ID", content = @Content),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "User does not have ADMIN role", content = @Content),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<String> editEmployee(
            @Parameter(description = "ID of the employee to update", example = "EMP001")
            @PathVariable String employeeId,
            @Valid @RequestBody @Schema(description = "Updated employee details") EditEmployeeRequest request) {
        String result = employeeService.editEmployee(employeeId, request);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete employee", description = "Deletes an employee account by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "User does not have ADMIN role", content = @Content),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<String> deleteEmployee(
            @Parameter(description = "ID of the employee to delete", example = "EMP001")
            @PathVariable String employeeId) {
        String result = employeeService.deleteEmployee(employeeId);
        return ResponseEntity.ok(result);
    }
}