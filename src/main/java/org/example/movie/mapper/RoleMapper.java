package org.example.movie.mapper;

import org.example.movie.dto.request.RoleRequest;
import org.example.movie.dto.response.RoleResponse;
import org.example.movie.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);

    @Mapping(target = "roleId", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    void updateRoleFromRequest(@MappingTarget Role role, RoleRequest request);
}