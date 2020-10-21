package ng.min.authserve.security;

import execeptioins.MinServiceException;
import lombok.extern.slf4j.Slf4j;
import ng.min.authserve.constants.PermissionType;
import ng.min.authserve.dto.NotificationData;
import ng.min.authserve.dto.Receivers;
import ng.min.authserve.model.Permission;
import ng.min.authserve.model.PermissionRole;
import ng.min.authserve.model.Role;
import ng.min.authserve.model.ServiceClient;
import ng.min.authserve.repo.ApiClientRepo;
import ng.min.authserve.repo.PermissionRepo;
import ng.min.authserve.repo.PermissionRoleRepo;
import ng.min.authserve.repo.RoleRepo;
import ng.min.authserve.service.ApiClientService;
import ng.min.authserve.service.impl.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Unogwu Daniel on 21/07/2020.
 */
@Component
@Slf4j
public class DataLoader implements ApplicationListener<ContextRefreshedEvent> {
    @Value("${app.admin.email}")
    private String adminEmail;
    @Value("${app.email.alert}")
    private String emailAlert;
    @Value("${spring.profiles.active}")
    private String profile;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private PermissionRepo permissionRepo;
    @Autowired
    private PermissionRoleRepo permissionRoleRepo;
    @Autowired
    private ApiClientRepo userRepo;
    @Autowired
    private ApiClientService apiClientService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    private static HashMap<String, List<String>> roleMap = new HashMap<>();
    private static HashMap<String, String> permissionPathMap = new HashMap<>();
    private static String admin = "ADMIN";

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        seedRoles();
        seedUsers();
    }

    static {
        List<String> minAdminRoles = PermissionType.getAllList();

        roleMap.put(admin, minAdminRoles);

        permissionPathMap = PermissionType.getAllBack();
//        permissionPathMap.put(PermissionType.CREATE_ROLE.name(), "Create Role");
//        permissionPathMap.put(PermissionType.CREATE_USER.name(), "Create User");
    }

    private void seedRoles() {
        Map<String, Permission> permissionMap = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : roleMap.entrySet()) {
            Role role = roleRepo.findByNameEquals(entry.getKey());
            if (role == null) {
                role = new Role();
                role.setName(entry.getKey());
                role = roleRepo.saveAndFlush(role);
            }
            for (String permissionName : entry.getValue()) {

                Permission permission = permissionMap.get(permissionName);
                if (permission == null) {
                    permission = permissionRepo.findByName(permissionName);
                    if (permission == null) {
                        permission = new Permission();
                        permission.setName(permissionName);
                        permission.setDescription(permissionPathMap.get(permissionName));
                        permission = permissionRepo.saveAndFlush(permission);
                    }
                    permissionMap.put(permissionName, permission);
                }
                PermissionRole permissionRole = permissionRoleRepo.findByPermissionIdAndRoleId(permission.getId(), role.getId());
                if (permissionRole == null) {
                    permissionRole = new PermissionRole();
                    permissionRole.setPermission(permission);
                    permissionRole.setRole(role);
                    permissionRoleRepo.saveAndFlush(permissionRole);
                }
            }
        }
    }

    private void seedUsers() {
        if (userRepo.findByClientId(adminEmail).isEmpty()) {
            ServiceClient serviceClient = new ServiceClient();
            serviceClient.setServiceName("Admin");
            serviceClient.setClientId(adminEmail);
            var adminApiKey = apiClientService.generateApiKey();
            serviceClient.setApiKey(passwordEncoder.encode(adminApiKey));
            serviceClient.setActive(Boolean.TRUE);
            var role = roleRepo.findAll().stream().filter(r -> r.getName().equals(admin)).findFirst();
            log.info("admin role {}",role);
            if (role.isPresent()) {
                serviceClient.setUserRole(role.get());
                userRepo.saveAndFlush(serviceClient);
            }

            String emailBody = " <p>Client Id <strong>" + adminEmail + "</strong></p> <p>Api Key <strong>" + adminApiKey + "</strong></p><p>Env : "+profile+"</p>";
log.info("Email body {}",emailBody);
            new Thread(() -> {
                try {
                    notificationService.sendNotification(builtNotificationRequest(adminEmail, admin, emailBody, "Default Admin Cred", emailAlert, "Default Admin Cred", ""));
                } catch (MinServiceException e) {
                    log.error("Error occurred on data loader ", e);
                }
            }).start();

        }
    }

    public /*NotificationRequest*/ NotificationData builtNotificationRequest(String email, String name, String emailBody, String emailType, String from, String subject, String url) {
        Receivers receivers = Receivers.builder().email(email).firstName(name).url(url).build();

        //        return NotificationRequest.builder().data(notificationData).build();
        return NotificationData.builder()
                .emailBody(emailBody)
                .emailType(emailType)
                .from(from)
                .recievers(Collections.singletonList(receivers))
                .subject(subject)
                .build();
    }
}
