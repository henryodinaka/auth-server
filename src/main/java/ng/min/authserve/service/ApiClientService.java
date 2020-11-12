package ng.min.authserve.service;


import ng.min.authserve.execeptioins.MinServiceException;
import ng.min.authserve.dto.Response;
import ng.min.authserve.dto.ServiceRequest;
import org.springframework.http.ResponseEntity;

/**
 * Created by Odinaka Onah on 30 Sep, 2020.
 */
public interface ApiClientService {
    //    ApiClients findByApiKey(String key);
    ResponseEntity<Response> createService(boolean isUpdate, ServiceRequest userRequest) throws MinServiceException;
    ResponseEntity<Response> resetApiKey(Long id) throws MinServiceException;
    ResponseEntity<Response> getAllClient();
    String generateClientId();

    String generateApiKey();

    ResponseEntity<Response> toggle(Long serviceId) throws MinServiceException;
}
