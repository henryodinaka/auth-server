package ng.min.authserve.repo;


import ng.min.authserve.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Unogwu Daniel on 21/07/2020.
 */
public interface RoleRepo extends JpaRepository<Role, Long> {
    Role findByNameEquals(String name);

    List<Role> findByParentRoleId(Long id);
}
