package ng.min.authserve.repo;


import ng.min.authserve.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Unogwu Daniel on 16/07/2020.
 */
public interface PermissionRepo extends JpaRepository<Permission, Long> {
    Permission findByName(String displayName);
}

