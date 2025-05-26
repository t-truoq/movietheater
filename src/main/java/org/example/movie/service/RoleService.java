package org.example.movie.service;

import org.example.movie.dto.request.RoleRequest;
import org.example.movie.dto.response.RoleResponse;
import org.example.movie.entity.Role;
import org.example.movie.exception.AppException;
import org.example.movie.exception.ErrorCode;
import org.example.movie.mapper.RoleMapper;
import org.example.movie.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleMapper roleMapper;

    // Thêm vai trò
    public RoleResponse addRole(RoleRequest request) {
        if (roleRepository.findByRoleName(request.getRoleName()).isPresent()) {
            throw new AppException(ErrorCode.ROLE_ALREADY_EXISTS);
        }

        Role role = roleMapper.toRole(request);
        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    // Cập nhật vai trò
    public RoleResponse updateRole(Long roleId, RoleRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        roleRepository.findByRoleName(request.getRoleName())
                .filter(existingRole -> !existingRole.getRoleId().equals(roleId))
                .ifPresent(r -> {
                    throw new AppException(ErrorCode.ROLE_ALREADY_EXISTS);
                });

        roleMapper.updateRoleFromRequest(role, request);
        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    // Xóa vai trò
    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        if (!role.getAccounts().isEmpty()) {
            throw new AppException(ErrorCode.ROLE_IN_USE);
        }

        roleRepository.delete(role);
    }

    // Lấy danh sách vai trò
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toRoleResponse)
                .collect(Collectors.toList());
    }

    // Tìm vai trò theo tên
    public Role findByRoleName(String roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
    }

    // Tìm vai trò theo ID
    public Role findById(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
    }

    // Lấy hoặc tạo vai trò mặc định
    public Role getOrCreateDefaultRole(String roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseGet(() -> {
                    RoleRequest roleRequest = new RoleRequest();
                    roleRequest.setRoleName(roleName);
                    Role role = roleMapper.toRole(roleRequest);
                    return roleRepository.save(role);
                });
    }
}