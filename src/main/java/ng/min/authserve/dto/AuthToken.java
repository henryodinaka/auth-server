package ng.min.authserve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by Unogwu Daniel on 13/07/2020.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthToken {

    private String token;

    private Date expiration;


    public AuthToken(String token) {
        this.token = token;
    }
}
