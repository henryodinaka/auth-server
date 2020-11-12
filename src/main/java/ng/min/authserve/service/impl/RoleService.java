package ng.min.authserve.service.impl;

import ng.min.authserve.execeptioins.MinServiceException;
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
public class RoleService {
    private final RoleRepo roleRepo;
    private final PermissionRepo permissionRepo;
    private final PermissionRoleRepo permissionRoleRepo;
    private final ApiClientRepo clientRepo;

    @Autowired
    public RoleService(RoleRepo roleRepo, PermissionRepo permissionRepo, PermissionRoleRepo permissionRoleRepo, ApiClientRepo clientRepo) {
        this.roleRepo = roleRepo;
        this.permissionRepo = permissionRepo;
        this.permissionRoleRepo = permissionRoleRepo;
        this.clientRepo = clientRepo;
    }

    @Transactional
    public ResponseEntity createRole(RoleDto dto) throws NoSuchAlgorithmException, MinServiceException {
        if (roleRepo.findByNameEquals(dto.getName()) != null) {
            return Response.setUpResponse(ResponseCode.DUPLICATE_REQUEST, "Role");
        }
        try {
//            Role role = roleRepo.saveAndFlush(getRole(RequestUtil.getUser(), dto));
            Role role = roleRepo.saveAndFlush(new Role(dto.getName()));
            return Response.setUpResponse(ResponseCode.SUCCESS, " Role created" , role);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            throw new MinServiceException(ResponseCode.UNAVAILABLE1.getCode(), ResponseCode.UNAVAILABLE1.getValue(), ResponseCode.UNAVAILABLE1.getStatusCode());
        }
    }

    private Role getRole(ServiceClient serviceClient, RoleDto dto) {
        Role role = new Role(dto.getName());
        Role parentRole = serviceClient.getUserRole();
        role. setParentRole(parentRole);
        return role;
    }

    public ResponseEntity editRole(RoleDto dto, Long id) throws MinServiceException {
        Role role = roleRepo.getOne(id);
        if (role == null) return Response.setUpResponse(ResponseCode.ITEM_NOT_FOUND, " role ", null);
        if (!Objects.isNull(permissionRepo.findByName(role.getName()))){
            return Response.setUpResponse(ResponseCode.DUPLICATE_REQUEST,"Role", null);
        }
        try {
            role.setName(dto.getName());
            role.setModified(LocalDateTime.now());
            roleRepo.saveAndFlush(role);
            return Response.setUpResponse(ResponseCode.SUCCESS, "", role);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            throw new MinServiceException(ResponseCode.UNAVAILABLE1.getCode(), ResponseCode.UNAVAILABLE1.getValue(), ResponseCode.UNAVAILABLE1.getStatusCode());
        }
    }

    public Role findByName(String name) {
        return roleRepo.findByNameEquals(name);
    }

    public Optional<Role> findById(Long id) {
        return roleRepo.findById(id);
    }

    public ResponseEntity getRoleById(Long id) {
        Role role = roleRepo.getOne(id);
        if (role == null) return Response.setUpResponse(ResponseCode.ITEM_NOT_FOUND, "role", null);
        return Response.setUpResponse(ResponseCode.OK, "", new RoleDto(role));
    }

    public ResponseEntity getAllRoles() {
        List<Role> roles = roleRepo.findAll();
        roles.forEach(role -> role.setPermissions(getPermissionsFromRole(role.getId())));
        return Response.setUpResponse(ResponseCode.OK, "", roles);
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

    @Transactional
    public ResponseEntity activateOrDeactivate(Long roleId) {
        Role role = roleRepo.getOne(roleId);
        if (role == null) return Response.setUpResponse(ResponseCode.ITEM_NOT_FOUND, "role", null);

        role.setActive(!role.getActive());
        role.setModified(LocalDateTime.now());
        role = roleRepo.saveAndFlush(role);
        if (!role.getActive()) {
            List<Role> roles = getAccessibleRoles(role);
            roles.remove(role);
            roles.forEach(r -> {
                r.setActive(Boolean.FALSE);
                r.setModified(LocalDateTime.now());
                roleRepo.saveAndFlush(r);
            });
        }
        return Response.setUpResponse(ResponseCode.OK, "", new RoleDto(role));
    }


    public List<Permission> getPermissionsFromRole(Long roleId) {
        List<PermissionRole> permissionRoles = permissionRoleRepo.findByRoleId(roleId);

        return permissionRoles.stream().map(permissionRole -> permissionRole.getPermission()).
                collect(Collectors.toList());
    }

}
