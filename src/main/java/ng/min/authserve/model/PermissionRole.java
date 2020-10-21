package ng.min.authserve.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by Unogwu Daniel on 13/07/2020.
 */
@Getter
@Setter
@Entity
@Table(name = "permissions_roles", uniqueConstraints=
                        @UniqueConstraint(columnNames={"permissions_id", "roles_id"}))
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PermissionRole extends BaseModel<PermissionRole> {

    @ManyToOne
    @JoinColumn(name = "permissions_id", nullable = false)
    @ApiModelProperty(hidden = true)
    private Permission permission;

    @ManyToOne
    @JoinColumn(name = "roles_id", nullable = false)
    @ApiModelProperty(hidden = true)
    private Role role;

    @Column(nullable = false)
    private Boolean active = Boolean.TRUE;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PermissionRole object = (PermissionRole) o;

        return getId().equals(object.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

}
