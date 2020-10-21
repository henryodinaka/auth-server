package ng.min.authserve.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Odinaka Onah on 24 Sep, 2020.
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class Receivers {
    private String email;
    private String firstName;
    private String url;
}
