package ng.min.authserve.repo;


import ng.min.authserve.model.ServiceClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Odinaka Onah on 30 Sep, 2020.
 */
@Repository
public interface ApiClientRepo extends JpaRepository<ServiceClient,Long> {
    Optional<ServiceClient> findByApiKey(String key);
    Optional<ServiceClient> findByClientId(String clientId);
    long countByServiceName(String ap);
}
