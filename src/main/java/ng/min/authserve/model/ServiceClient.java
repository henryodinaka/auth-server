package ng.min.authserve.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.min.authserve.utils.CommonUtils;
import ng.min.authserve.utils.Validation;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Odinaka Onah on 23 Sep, 2020.
 */

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ServiceClient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String serviceName;
    private String clientId;
    private String apiKey;  //store or buyer
    @ManyToOne
    private ServiceClient createdBy;
    private LocalDateTime createdAt;
    private boolean active;

    @JsonIgnore
    @ManyToOne
    @Cascade(value = org.hibernate.annotations.CascadeType.MERGE)
    private Role userRole;

    @Transient
    @JsonIgnore
    private List<Permission> permissions;

    @Transient
    @JsonIgnore
    private Role role;

    @PrePersist
    public void beforeSave() {
        this.createdAt = LocalDateTime.now();
    }

    public ServiceClient(ServiceClient serviceClient, String apiKey) {
        this.clientId = serviceClient.getClientId();
        this.apiKey = apiKey;
        this.serviceName = serviceClient.getServiceName();
        this.userRole = serviceClient.getUserRole();
    }

    public String getCreatedAt() {
        return Validation.validData(createdAt)? CommonUtils.dateToStringFormated(createdAt):" ";
    }
}
