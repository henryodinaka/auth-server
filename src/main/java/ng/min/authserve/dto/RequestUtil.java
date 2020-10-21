package ng.min.authserve.dto;

import ng.min.authserve.model.ProfileDetails;
import ng.min.authserve.model.ServiceClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Unogwu Daniel on 13/07/2020.
 */
public class RequestUtil {

    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }

    public static ServiceClient getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication.getPrincipal() != null && !authentication.getPrincipal().getClass().getSimpleName().equals("String")) {
                ProfileDetails profileDetails = (ProfileDetails) authentication.getPrincipal();

                return profileDetails.toUser();
            }
        }

        return null;
    }

}
