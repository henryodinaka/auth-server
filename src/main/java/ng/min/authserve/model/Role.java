package ng.min.authserve.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Unogwu Daniel on 13/07/2020.
 */
@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@Table(name = "roles")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Role extends BaseModel<Role> {

    @Column(length = 45, name = "name", unique = true)
    private String name;

    @ManyToOne
    @ApiModelProperty(hidden = true)
    private Role parentRole;

    @Column(nullable = false)
    @ApiModelProperty(hidden = true)
    private Boolean active = Boolean.TRUE;

    @Transient
    @ApiModelProperty(hidden = true)
    private List<Permission> permissions = new ArrayList<>();

    public Role(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Role object = (Role) o;

        return getId().equals(object.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

}
