package ng.min.authserve.controller;


import ng.min.authserve.execeptioins.MinServiceException;
import ng.min.authserve.constants.CommonConstant;
import ng.min.authserve.model.Permission;
import ng.min.authserve.service.impl.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Unogwu Daniel on 21/07/2020.
 */
@CrossOrigin(origins = "*", maxAge = 3600,methods = {RequestMethod.GET,RequestMethod.PATCH,RequestMethod.POST,RequestMethod.PATCH,RequestMethod.PUT})
@RestController
@RequestMapping(CommonConstant.API_VERSION + "permission")
public class PermissionController {
    private PermissionService permissionService;

    @Autowired
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PreAuthorize("hasAnyAuthority('CREATE_PERMISSION')")
    @PostMapping
    public ResponseEntity createPermission(@RequestBody Permission permission) throws MinServiceException {
        return permissionService.createPermission(permission);
    }

    @PreAuthorize("hasAnyAuthority('EDIT_PERMISSION')")
    @PutMapping("{id}")
    public ResponseEntity editPermission(@PathVariable("id") Long id, @RequestBody Permission permission) throws MinServiceException {
        return permissionService.editPermission(id, permission);
    }

    @PreAuthorize("hasAnyAuthority('VIEW_PERMISSION')")
    @GetMapping("/{id}")
    public ResponseEntity getPermissionById(@PathVariable("id") Long id) {
        return permissionService.getPermissionById(id);
    }

    @PreAuthorize("hasAnyAuthority('VIEW_PERMISSION')")
    @GetMapping("role/{id}")
    public ResponseEntity getRolePermissions(@PathVariable("id") Long roleId) {
        return permissionService.getRolePermissions(roleId);
    }

    @PreAuthorize("hasAnyAuthority('VIEW_PERMISSION')")
    @GetMapping("list")
    public ResponseEntity getAllPermission() {
        return permissionService.getAllPermissions();
    }

}
