package org.example.movie.mapper;

import org.example.movie.dto.request.RegisterRequest;
import org.example.movie.dto.request.UpdateAccountRequest;
import org.example.movie.dto.response.LoginResponse;
import org.example.movie.dto.response.MemberAccountResponse;
import org.example.movie.dto.response.RegisterResponse;
import org.example.movie.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE
)

public interface AccountMapper {

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "registerDate", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "member", ignore = true)
    @Mapping(target = "invoices", ignore = true)
    Account toAccount(RegisterRequest request);

    @Mapping(source = "role.roleName", target = "role")
    @Mapping(target = "token", ignore = true)
    LoginResponse toLoginResponse(Account account);

    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "registerDate", ignore = true)
    @Mapping(target = "invoices", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "member", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "gender", ignore = true) // Sửa từ "sex" thành "gender"
    void updateAccountFromRequest(@MappingTarget Account account, UpdateAccountRequest request);

    @Mapping(source = "gender", target = "gender") // Ánh xạ gender sang sex trong MemberAccountResponse
    MemberAccountResponse toMemberAccountResponse(Account account);

    @Mapping(source = "role.roleName", target = "role")
    RegisterResponse toRegisterResponse(Account account);
}