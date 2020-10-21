package ng.min.authserve.service.impl;

import execeptioins.MinServiceException;
import lombok.extern.slf4j.Slf4j;
import ng.min.authserve.constants.ResponseCode;
import ng.min.authserve.dto.RequestUtil;
import ng.min.authserve.dto.Response;
import ng.min.authserve.dto.RoleDto;
import ng.min.authserve.model.Permission;
import ng.min.authserve.model.PermissionRole;
import ng.min.authserve.model.Role;
import ng.min.authserve.model.ServiceClient;
import ng.min.authserve.repo.ApiClientRepo;
import ng.min.authserve.repo.PermissionRepo;
import ng.min.authserve.repo.PermissionRoleRepo;
import ng.min.authserve.repo.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Unogwu Daniel on 21/07/2020.
 */
@Slf4j
@Service
public class PermissionService {
    private final RoleRepo roleRepo;
    private final PermissionRepo permissionRepo;
    private final PermissionRoleRepo permissionRoleRepo;

    @Autowired
    public PermissionService(RoleRepo roleRepo, PermissionRepo permissionRepo, PermissionRoleRepo permissionRoleRepo) {
        this.roleRepo = roleRepo;
        this.permissionRepo = permissionRepo;
        this.permissionRoleRepo = permissionRoleRepo;
    }

    public ResponseEntity<Response> createPermission(Permission permission) throws MinServiceException {
        if (permissionRepo.findByName(permission.getName()) != null) {
            return Response.setUpResponse(ResponseCode.DUPLICATE_REQUEST, "Permission", null);
        }
        permission.setName(permission.getName().toUpperCase().replaceAll(" ", "_"));
        try {
            Permission p = permissionRepo.saveAndFlush(permission);
            return Response.setUpResponse(ResponseCode.SUCCESS, "", p);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            throw new MinServiceException(ResponseCode.UNAVAILABLE1.getCode(), e.getMessage(), ResponseCode.UNAVAILABLE1.getStatusCode());
        }
    }

    public ResponseEntity<Response> editPermission(Long permissionId, Permission record) throws MinServiceException {
        Permission permission = permissionRepo.getOne(permissionId);
        if (permission == null) {
            return Response.setUpResponse(ResponseCode.ITEM_NOT_FOUND, "permission", null);
        } if (!Objects.isNull(permissionRepo.findByName(record.getName()))){
            return Response.setUpResponse(ResponseCode.DUPLICATE_REQUEST, "permission", null);
        }
        try {
            permission.setName(record.getName().toUpperCase().replaceAll(" ", "_"));
            permission.setModified(LocalDateTime.now());
            Permission p = permissionRepo.saveAndFlush(permission);
            return Response.setUpResponse(ResponseCode.SUCCESS, "", p);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            throw new MinServiceException(ResponseCode.UNAVAILABLE1.getCode(), ResponseCode.UNAVAILABLE1.getValue(), ResponseCode.UNAVAILABLE1.getStatusCode());
        }
    }

    public ResponseEntity<Response> getPermissionById(Long id) {
        Permission permission = permissionRepo.getOne(id);
        if (Objects.isNull(permission)) {
            return Response.setUpResponse(ResponseCode.ITEM_NOT_FOUND, "permission", null);
        }
        return Response.setUpResponse(ResponseCode.OK, "", permission);
    }

    public ResponseEntity<Response> getAllPermissions() {
        return Response.setUpResponse(ResponseCode.OK,"",permissionRepo.findAll());
    }

    public ResponseEntity<Response> getRolePermissions(Long roleId) {
        Role role = roleRepo.getOne(roleId);
        if (role == null) return Response.setUpResponse(ResponseCode.ITEM_NOT_FOUND, "role", null);
        List<PermissionRole> permissionRoles = permissionRoleRepo.findByRoleId(roleId);

        List<Permission> permissions = permissionRoles.stream().map(permissionRole -> permissionRole.getPermission()).
                collect(Collectors.toList());

        return Response.setUpResponse(ResponseCode.OK, "", permissions);
    }
}
