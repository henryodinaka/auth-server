package execeptioins;

import lombok.Data;

/**
 * Created by Odinaka Onah on 08 Jul, 2020.
 */
@Data
public class MinServiceException extends Exception {
    private final Integer httpCode;
    private String statusCode;
    public MinServiceException(Integer httpCode, String message, String statusCode)
    {
        super(message);
        this.httpCode = httpCode;
        this.statusCode = statusCode;
    }
}