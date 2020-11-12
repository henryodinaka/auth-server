package ng.min.authserve.controller;


import ng.min.authserve.execeptioins.MinServiceException;
import ng.min.authserve.constants.CommonConstant;
import ng.min.authserve.dto.RoleDto;
import ng.min.authserve.service.impl.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

/**
 * Created by Unogwu Daniel on 21/07/2020.
 */
@CrossOrigin(origins = "*", maxAge = 3600,methods = {RequestMethod.GET,RequestMethod.PATCH,RequestMethod.POST,RequestMethod.PATCH,RequestMethod.PUT})
@RestController
@RequestMapping(CommonConstant.API_VERSION + "role")
public class RoleController {
    private RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PreAuthorize("hasAnyAuthority('CREATE_ROLE')")
    @PostMapping
    public ResponseEntity createRole(@RequestBody RoleDto dto) throws NoSuchAlgorithmException, MinServiceException {
        return roleService.createRole(dto);
    }

    @PreAuthorize("hasAnyAuthority('EDIT_ROLE')")
    @PutMapping("/{id}")
    public ResponseEntity editRole(@PathVariable("id") Long id, @RequestBody RoleDto dto) throws MinServiceException {
        return roleService.editRole(dto, id);
    }

    @PreAuthorize("hasAnyAuthority('VIEW_ROLES')")
    @GetMapping("/{id}")
    public ResponseEntity getRoleById(@PathVariable("id") Long id) {
        return roleService.getRoleById(id);
    }

    @PreAuthorize("hasAnyAuthority('VIEW_ROLES')")
    @GetMapping("/list")
    public ResponseEntity getAllRoles() {
        return roleService.getAllRoles();
    }

    @PreAuthorize("hasAnyAuthority('ACTIVATE_ROLE')")
    @PostMapping("/{id}/status")
    public ResponseEntity activateOrDeactivateRole(@PathVariable("id") Long id) {
        return roleService.activateOrDeactivate(id);
    }



}
