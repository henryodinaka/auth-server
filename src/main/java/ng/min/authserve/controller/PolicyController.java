package ng.min.authserve.controller;


import ng.min.authserve.execeptioins.MinServiceException;
import ng.min.authserve.constants.CommonConstant;
import ng.min.authserve.service.impl.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Unogwu Daniel on 21/07/2020.
 */
@CrossOrigin(origins = "*", maxAge = 3600,methods = {RequestMethod.GET,RequestMethod.PATCH,RequestMethod.POST,RequestMethod.PATCH,RequestMethod.PUT})
@RestController
@RequestMapping(CommonConstant.API_VERSION + "policy")
public class PolicyController {
    private PolicyService policyService;

    @Autowired
    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PreAuthorize("hasAnyAuthority('ASSIGN_PERMISSION')")
    @PostMapping("role/{roleId}/permissions/{permissionId}")
    public ResponseEntity assignPermission(@PathVariable("roleId") Long roleId, @PathVariable("permissionId") Long permissionId) throws MinServiceException {
        return policyService.assignPermissionToRole(roleId, permissionId);
    }

    @PreAuthorize("hasAnyAuthority('ASSIGN_PERMISSION')")
    @PostMapping("role/{id}/permissions/{permissionId}/delete")
    public ResponseEntity removePermissionRole(@PathVariable("id") Long roleId,
                                               @PathVariable("permissionId") Long permissionId) throws MinServiceException {
        return policyService.removePermission(permissionId, roleId);
    }

    @PreAuthorize("hasAnyAuthority('ASSIGN_ROLE')")
    @PostMapping("role/{id}/users/{userId}")
    public ResponseEntity assignRole(@PathVariable("id") Long roleId, @PathVariable("userId") Long userId) throws MinServiceException {
        return policyService.assignRole(userId, roleId);
    }

}
