package ng.min.authserve.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Odinaka Onah on 08 Jul, 2020.
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRequest {
    private Long id;
    private String serviceName;
    private boolean notUser;
    private Long roleId;
}
