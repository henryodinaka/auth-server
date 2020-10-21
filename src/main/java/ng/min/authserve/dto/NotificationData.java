package ng.min.authserve.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Odinaka Onah on 24 Sep, 2020.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NotificationData {
    private List<Receivers> recievers;
    private String from;
    private String subject;
    private String emailBody;
    private String emailType;
}
