package ng.min.authserve.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ng.min.authserve.model.Role;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {
    private String name;
    private Boolean active;

    public RoleDto(Role role) {
        this.name = role.getName();
        this.active = role.getActive();
    }
}
