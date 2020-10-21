package ng.min.authserve.service.impl;

import execeptioins.MinServiceException;
import lombok.extern.slf4j.Slf4j;

import ng.min.authserve.constants.ResponseCode;
import ng.min.authserve.dto.NotificationData;
import ng.min.authserve.utils.JsonConverter;
import ng.min.authserve.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Odinaka Onah on 24 Sep, 2020.
 */
@Service
@Slf4j
public class NotificationService {
    @Value("${app.notification.url}")
    private String notificationUrl;
    @Autowired
    private RestTemplate restTemplate;

//    @Async
    public void sendNotificationAsync(/*NotificationRequest*/NotificationData notificationRequest) {
      new Thread(() -> {
          try {
              sendNotification(notificationRequest);
          } catch (MinServiceException e) {
              log.error("Error occurred while sending email ",e);
          }
      }).start();
         }

    public void sendNotification(/*NotificationRequest*/NotificationData notificationRequest) throws MinServiceException {
        String error = Validation.validateNotificationRequest(notificationRequest);
//        log.info("Validation Error occurred on the notification method {}", error);
        if (error != null)
            throw new MinServiceException(ResponseCode.BAD_REQUEST.getCode(), ResponseCode.BAD_REQUEST.getValue().replace("{}", error), ResponseCode.BAD_REQUEST.getStatusCode());
        var notificationJson = JsonConverter.getJsonRecursive(notificationRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {

            HttpEntity<String> entity = new HttpEntity(notificationJson, headers);
//log.info("Notification json {}",notificationJson);
            ResponseEntity<String> forEntity = restTemplate.postForEntity(notificationUrl, entity, String.class);
            log.info(":::::::::: RESPONSE from notification url {} is {}",notificationUrl, forEntity);
        } catch (Exception ex) {
            log.error(":::: ERROR while making call to Notification {}", notificationUrl, ex);
//            throw new MinServiceException(ResponseCode.UNAVAILABLE1.getCode(), "your request", ResponseCode.UNAVAILABLE1.getStatusCode());
        }
    }

}
