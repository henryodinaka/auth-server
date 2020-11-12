package ng.min.authserve.controller;

import ng.min.authserve.execeptioins.MinServiceException;
import lombok.extern.slf4j.Slf4j;
import ng.min.authserve.constants.CommonConstant;
import ng.min.authserve.constants.ResponseCode;
import ng.min.authserve.dto.Response;
import ng.min.authserve.service.impl.AuthenticationService;
import ng.min.authserve.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Unogwu Daniel on 13/07/2020.
 */
@CrossOrigin(origins = "*", maxAge = 3600,methods = {RequestMethod.GET,RequestMethod.PATCH,RequestMethod.POST,RequestMethod.PATCH,RequestMethod.PUT})
@RestController
@RequestMapping(CommonConstant.API_VERSION + "token")
@Slf4j
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping
    public ResponseEntity<Response> getToken(HttpServletRequest servletRequest) throws MinServiceException {
        var clientId = servletRequest.getHeader(CommonConstant.HEADER_STRING_CLIENT_ID);
        var apiKey = servletRequest.getHeader(CommonConstant.HEADER_STRING_KEY);
        log.info("controller The caller details clientId {} ::: apikey {}",clientId,apiKey);
        if (!Validation.validData(clientId))
            return Response.setUpResponse(ResponseCode.ACCESS_DENIED, "client Id is required :key ClientId");
        if (!Validation.validData(apiKey))
            return Response.setUpResponse(ResponseCode.ACCESS_DENIED, "apiKey is required :key ApiKey");
        return authenticationService.authenticate(clientId, apiKey);
    }

}
