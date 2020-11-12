package ng.min.authserve.service.impl;

import ng.min.authserve.execeptioins.MinServiceException;
import lombok.extern.slf4j.Slf4j;
import ng.min.authserve.constants.ResponseCode;
import ng.min.authserve.dto.RequestUtil;
import ng.min.authserve.dto.Response;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Unogwu Daniel on 21/07/2020.
 */
@Slf4j
@Service
public class PolicyService {
    private final RoleRepo roleRepo;
    private final PermissionRepo permissionRepo;
    private final PermissionRoleRepo permissionRoleRepo;
    private final ApiClientRepo clientRepo;

    @Autowired
    public PolicyService(RoleRepo roleRepo, PermissionRepo permissionRepo, PermissionRoleRepo permissionRoleRepo, ApiClientRepo clientRepo) {
        this.roleRepo = roleRepo;
        this.permissionRepo = permissionRepo;
        this.permissionRoleRepo = permissionRoleRepo;
        this.clientRepo = clientRepo;
    }

    public List<Role> getAccessibleRoles(Role role) {
        List<Role> roles = new ArrayList<Role>() {{
            add(role);
        }};
        getChildRoles(role, roles);
        roles.forEach(userRole -> userRole.setPermissions(getPermissionsFromRole(userRole.getId())));
        return roles;
    }

    private void getChildRoles(Role role, List<Role> roles) {
        ServiceClient serviceClient = RequestUtil.getUser();
        List<Role> childRoles = new ArrayList<>();
        childRoles = roleRepo.findByParentRoleId(role.getId());

        if (!childRoles.isEmpty()) {
            roles.addAll(childRoles);
            for (Role childRole : childRoles) {
                getChildRoles(childRole, roles);
            }
        }
    }

    public List<Permission> getPermissionsFromRole(Long roleId) {
        List<PermissionRole> permissionRoles = permissionRoleRepo.findByRoleId(roleId);

        return permissionRoles.stream().map(permissionRole -> permissionRole.getPermission()).
                collect(Collectors.toList());
    }

    public ResponseEntity assignPermissionToRole(Long roleId, Long permissionId) throws MinServiceException {
        Optional<Role> role = roleRepo.findById(roleId);
        if (!role.isPresent()) return Response.setUpResponse(ResponseCode.ITEM_NOT_FOUND, "role", null);
        Permission permission = permissionRepo.getOne(permissionId);
        if (!permissionRepo.findById(permissionId).isPresent())
            return Response.setUpResponse(ResponseCode.ITEM_NOT_FOUND, "permission", null);
        if (permissionRoleRepo.findByPermissionIdAndRoleId(permissionId, roleId) != null) {
            throw new MinServiceException(ResponseCode.DUPLICATE_PERMISSION_ROLE.getCode(), ResponseCode.DUPLICATE_PERMISSION_ROLE.getValue(), ResponseCode.DUPLICATE_PERMISSION_ROLE.getStatusCode());
        }
        try {
            PermissionRole permissionRole = new PermissionRole();
            permissionRole.setRole(role.get());
            permissionRole.setPermission(permission);
            permissionRole = permissionRoleRepo.saveAndFlush(permissionRole);
            return Response.setUpResponse(ResponseCode.SUCCESS, "", permissionRole);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            throw new MinServiceException(ResponseCode.UNAVAILABLE1.getCode(), ResponseCode.UNAVAILABLE1.getValue(), ResponseCode.UNAVAILABLE1.getStatusCode());
        }
    }

    public PermissionRole findByPermissionAndRoleIds(Long permissionId, Long roleId) {
        return permissionRoleRepo.findByPermissionIdAndRoleId(permissionId, roleId);
    }

    public ResponseEntity removePermission(Long permissionId, Long roleId) throws MinServiceException {

        Role role = roleRepo.getOne(roleId);
        if (role == null) return Response.setUpResponse(ResponseCode.ITEM_NOT_FOUND, "role", null);
        Permission permission = permissionRepo.getOne(permissionId);
        if (permission == null) return Response.setUpResponse(ResponseCode.ITEM_NOT_FOUND, "permission", null);

        try {
            PermissionRole permissionRole = permissionRoleRepo.
                    findByPermissionIdAndRoleId(permission.getId(), role.getId());
            if (permissionRole != null) {
                permissionRoleRepo.delete(permissionRole);
                List<Role> roles = getAccessibleRoles(role);
                roles.remove(role);
                roles.forEach(r -> {
                    PermissionRole pr = permissionRoleRepo.
                            findByPermissionIdAndRoleId(permission.getId(), role.getId());
                    if (pr != null) {
                        permissionRoleRepo.delete(pr);
                    }
                });
                permissionRoleRepo.flush();
            }
            return Response.setUpResponse(ResponseCode.SUCCESS, "", permissionRole);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            throw new MinServiceException(ResponseCode.UNAVAILABLE1.getCode(), ResponseCode.UNAVAILABLE1.getValue(), ResponseCode.UNAVAILABLE1.getStatusCode());
        }
    }

    public ResponseEntity assignRole(Long userId, Long roleId) throws MinServiceException {
        ServiceClient serviceClient = clientRepo.getOne(userId);
        if (serviceClient == null) return Response.setUpResponse(ResponseCode.ITEM_NOT_FOUND, " user ", null);
        Role role = roleRepo.getOne(roleId);
        if (role == null) return Response.setUpResponse(ResponseCode.ITEM_NOT_FOUND, " role ", null);

        try {
            serviceClient.setUserRole(role);
            serviceClient = clientRepo.saveAndFlush(serviceClient);
            return Response.setUpResponse(ResponseCode.SUCCESS, "", serviceClient);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            throw new MinServiceException(ResponseCode.UNAVAILABLE1.getCode(), ResponseCode.UNAVAILABLE1.getValue(), ResponseCode.UNAVAILABLE1.getStatusCode());
        }

    }

}
