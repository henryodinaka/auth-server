package ng.min.authserve.security;

import lombok.extern.slf4j.Slf4j;
import ng.min.authserve.model.PermissionRole;
import ng.min.authserve.model.ServiceClient;
import ng.min.authserve.repo.ApiClientRepo;
import ng.min.authserve.repo.PermissionRoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Unogwu Daniel on 13/07/2020.
 */
@Service
@Slf4j
public class ProfileDetailsService implements UserDetailsService {

    private ApiClientRepo apiClientRepo;

    private PermissionRoleRepo permissionRoleRepo;
    @Autowired
    public void setApiClientRepo(ApiClientRepo apiClientRepo) {
        this.apiClientRepo = apiClientRepo;
    }
    @Autowired
    public void setPermissionRoleRepo(PermissionRoleRepo permissionRoleRepo) {
        this.permissionRoleRepo = permissionRoleRepo;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<ServiceClient> userOpt = apiClientRepo.findByClientId(username);
        if(!userOpt.isPresent()){
            throw new UsernameNotFoundException("Invalid client Id or ApiKey.");
        }
        ServiceClient serviceClient = userOpt.get();
        List<PermissionRole> permissionRoles = new ArrayList<>();

        if (serviceClient.getUserRole() != null && serviceClient.getUserRole().getActive()) {
            serviceClient.setRole(serviceClient.getUserRole());
            permissionRoles = permissionRoleRepo.findByRoleId(serviceClient.getRole().getId());
//            log.info("inside role {}",users.getRole());
//            log.info("inside permission role {}",permissionRoles);
        }
        return new ProfileDetails(serviceClient, permissionRoles);
    }
}
