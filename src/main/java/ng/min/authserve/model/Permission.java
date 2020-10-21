package ng.min.authserve.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Unogwu Daniel on 13/07/2020.
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "permissions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Permission extends BaseModel<Permission> {

    private String description;

    @Column(length = 100, name="name", unique = true)
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Permission object = (Permission) o;

        return getId().equals(object.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
