package ng.min.authserve.repo;

import ng.min.authserve.model.PermissionRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Unogwu Daniel on 16/07/2020.
 */
@Repository
public interface PermissionRoleRepo extends JpaRepository<PermissionRole, Long> {
    PermissionRole findByPermissionIdAndRoleId(Long permissionId, Long roleId);
    List<PermissionRole> findByRoleId(Long id);
}
