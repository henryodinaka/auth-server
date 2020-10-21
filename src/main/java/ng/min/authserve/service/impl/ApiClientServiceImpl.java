package ng.min.authserve.service.impl;

import execeptioins.MinServiceException;
import lombok.extern.slf4j.Slf4j;

import ng.min.authserve.constants.ResponseCode;
import ng.min.authserve.dto.Response;
import ng.min.authserve.dto.ServiceRequest;
import ng.min.authserve.model.Role;
import ng.min.authserve.model.ServiceClient;
import ng.min.authserve.repo.ApiClientRepo;
import ng.min.authserve.repo.PermissionRoleRepo;
import ng.min.authserve.repo.RoleRepo;
import ng.min.authserve.service.ApiClientService;
import ng.min.authserve.utils.Validation;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static ng.min.authserve.dto.RequestUtil.getUser;


/**
 * Created by Odinaka Onah on 30 Sep, 2020.
 */
@Service
@Slf4j
public class ApiClientServiceImpl implements ApiClientService {
    private ApiClientRepo clientRepo;
    private RoleRepo roleRepo;

    private final BCryptPasswordEncoder passwordEncoder;
    private PermissionRoleRepo permissionRoleRepo;
    @Value("${spring.profiles.active}")
    private String profile;

    public ApiClientServiceImpl(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setClientRepo(ApiClientRepo clientRepo) {
        this.clientRepo = clientRepo;
    }

    @Autowired
    public void setPermissionRoleRepo(PermissionRoleRepo permissionRoleRepo) {
        this.permissionRoleRepo = permissionRoleRepo;
    }

    @Autowired
    public void setRoleRepo(RoleRepo roleRepo) {
        this.roleRepo = roleRepo;
    }

    public ResponseEntity<Response> createService(boolean isUpdate, ServiceRequest userRequest) throws MinServiceException {

        var apiKey = generateApiKey();
        try {
            ServiceClient admin = getUser();

            if (userRequest == null || !Validation.validData(userRequest.getServiceName())) {
                return Response.setUpResponse(ResponseCode.BAD_REQUEST, "Invalid request");
            }
            long count = clientRepo.countByServiceName(userRequest.getServiceName());
            ServiceClient serviceClient;
            Role role;
            if (isUpdate) {
                serviceClient = clientRepo.findById(userRequest.getId()).orElseThrow(() -> new MinServiceException(ResponseCode.ITEM_NOT_FOUND.getCode(), ResponseCode.ITEM_NOT_FOUND.getValue().replace("{}", "service to be updated"), ResponseCode.ITEM_NOT_FOUND.getStatusCode()));

                role = serviceClient.getUserRole();
                log.info("Role {}", userRequest);
                if (role == null || !role.getId().equals(userRequest.getRoleId())) {
                    role = roleRepo.findById(userRequest.getRoleId())
                            .orElseThrow(() -> new MinServiceException(ResponseCode.ITEM_NOT_FOUND.getCode(), ResponseCode.ITEM_NOT_FOUND.getValue().replace("{}", "ROLE you want to assign to this Service"), ResponseCode.ITEM_NOT_FOUND.getStatusCode()));
                }

                if (count > 0 && !serviceClient.getServiceName().equalsIgnoreCase(userRequest.getServiceName())) {
                    return Response.setUpResponse(ResponseCode.DUPLICATE_REQUEST, "Service name ");
                }

            } else {
                serviceClient = new ServiceClient();
                if (count > 0) {
                    return Response.setUpResponse(ResponseCode.DUPLICATE_REQUEST, "Service name ");
                }
                serviceClient.setApiKey(passwordEncoder.encode(apiKey));
                var clientId = "";
                if (userRequest.isNotUser()) clientId = generateClientId();
                else clientId = userRequest.getServiceName();
                serviceClient.setClientId(clientId);
                serviceClient.setCreatedBy(admin);
                serviceClient.setActive(true);
                role = roleRepo.findById(userRequest.getRoleId())
                        .orElseThrow(() -> new MinServiceException(ResponseCode.ITEM_NOT_FOUND.getCode(), ResponseCode.ITEM_NOT_FOUND.getValue().replace("{}", "ROLE you want to assign to this service"), ResponseCode.ITEM_NOT_FOUND.getStatusCode()));
            }
//            if (role == null)
//                return Response.setUpResponse1(ResponseCode.ITEM_NOT_FOUND, "ROLE you want to assign to this user");
            serviceClient.setUserRole(role);
            serviceClient.setServiceName(userRequest.getServiceName());
            clientRepo.save(serviceClient);
            return Response.setUpResponse(ResponseCode.SUCCESS, !isUpdate ? " Service created" : " Service Updated", new ServiceClient(serviceClient, apiKey));
        } catch (MinServiceException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new MinServiceException(e.getHttpCode(), e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            throw new MinServiceException(ResponseCode.UNAVAILABLE1.getCode(), e.getMessage(), ResponseCode.UNAVAILABLE1.getStatusCode());
        }
    }


    public ResponseEntity<Response> resetApiKey(Long id) throws MinServiceException {
        var serviceClient = clientRepo.findById(id).orElseThrow(() -> new MinServiceException(ResponseCode.ITEM_NOT_FOUND.getCode(), ResponseCode.ITEM_NOT_FOUND.getValue().replace("{}", "service to be updated"), ResponseCode.ITEM_NOT_FOUND.getStatusCode()));
        var apiKey = generateApiKey();
        serviceClient.setApiKey(passwordEncoder.encode(apiKey));
        clientRepo.save(serviceClient);
        return Response.setUpResponse(ResponseCode.SUCCESS,  " Service apikey reset " , new ServiceClient(serviceClient, apiKey));
    }

    public ResponseEntity<Response> getAllClient() {
        var all = clientRepo.findAll();
        return Response.setUpResponse(ResponseCode.OK,  " ", all);
    }

    public String generateClientId() {
        String pre = "svc_t_";
        if (profile.equalsIgnoreCase("prod")) {
            pre = "prod_svc_";
            return pre + RandomStringUtils.random(20,false,true);
        }
        return pre + RandomStringUtils.random(5,false,true);
    }

    public String generateApiKey() {
        String pre = "min_t_";
        if (profile.equalsIgnoreCase("prod")) {
            pre = "prod_s_key_";
            return pre + RandomStringUtils.randomAlphanumeric(40);
        }
        return pre + RandomStringUtils.randomAlphanumeric(20);
    }

    public ResponseEntity<Response> toggle(Long serviceId) throws MinServiceException {

        var users = clientRepo.findById(serviceId).orElseThrow(() -> new MinServiceException(ResponseCode.ITEM_NOT_FOUND.getCode(), ResponseCode.ITEM_NOT_FOUND.getValue().replace("{}", "service to be updated"), ResponseCode.ITEM_NOT_FOUND.getStatusCode()));
        users.setActive(!users.isActive());
        users = clientRepo.save(users);
        return Response.setUpResponse(ResponseCode.SUCCESS, users.isActive() ? "Service reactivated " : "Service deactivated");
    }
}
