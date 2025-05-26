package org.example.movie.mapper;

import org.example.movie.dto.response.EmployeeResponse;
import org.example.movie.entity.Account;
import org.example.movie.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    @Mapping(source = "employee.employeeId", target = "employeeId")
    @Mapping(source = "account.fullName", target = "fullName")
    @Mapping(source = "account.identityCard", target = "identityCard")
    @Mapping(source = "account.email", target = "email")
    @Mapping(source = "account.phoneNumber", target = "phoneNumber")
    @Mapping(source = "account.address", target = "address")
    EmployeeResponse toResponse(Employee employee, Account account);
}