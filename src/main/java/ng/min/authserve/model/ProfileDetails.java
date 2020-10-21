package ng.min.authserve.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Unogwu Daniel on 13/07/2020.
 */
@Slf4j
public class ProfileDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private ServiceClient client;
    private List<GrantedAuthority> authorities;

    public ProfileDetails(ServiceClient client, List<PermissionRole> permissionRoles) {
        this.client = client;
        authorities = new ArrayList<>();
        addAuthorities(permissionRoles);
    }

    private void addAuthorities(List<PermissionRole> permissionRoles) {
//        log.info("Permissions role to be added {}",permissionRoles);
        List<Permission> permissions = permissionRoles.stream().filter(PermissionRole::getActive)
                .map(PermissionRole::getPermission).collect(Collectors.toList());

//        log.info("Permissions to be added {}",permissions);
        client.setPermissions(permissions);
        permissions.forEach(permission ->
                authorities.add(new SimpleGrantedAuthority(/*"ROLE_" +*/ permission.getName())));
    }

    /**
     * Returns the authorities granted to the user. Cannot return <code>null</code>.
     *
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    };

    /**
     * Returns the password used to authenticate the user.
     *
     * @return the password
     */
    public String getPassword() {
        return client.getApiKey();
    };

    /**
     * Returns the username used to authenticate the user. Cannot return <code>null</code>
     * .
     *
     * @return the username (never <code>null</code>)
     */
    public String getUsername() {
        return client.getClientId();
    };

    /**
     * Indicates whether the user's account has expired. An expired account cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user's account is valid (ie non-expired),
     * <code>false</code> if no longer valid (ie expired)
     */
    public boolean isAccountNonExpired() {
        return true;
    };

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
     */
    public boolean isAccountNonLocked() {
        return true;
    };

    /**
     * Indicates whether the user's credentials (password) has expired. Expired
     * credentials prevent authentication.
     *
     * @return <code>true</code> if the user's credentials are valid (ie non-expired),
     * <code>false</code> if no longer valid (ie expired)
     */
    public boolean isCredentialsNonExpired() {
        return true;
    };

    /**
     * Indicates whether the user is enabled or disabled. A disabled user cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user is enabled, <code>false</code> otherwise
     */
    public boolean isEnabled() {
        return client.isActive();
    };

    public ServiceClient toUser() {
        return client;
    }
}