package ng.min.authserve.controller;


import ng.min.authserve.execeptioins.MinServiceException;
import ng.min.authserve.constants.CommonConstant;
import ng.min.authserve.dto.Response;
import ng.min.authserve.dto.ServiceRequest;
import ng.min.authserve.service.ApiClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Odinaka Onah on 01/10/2020.
 */
@CrossOrigin(origins = "*", maxAge = 3600,methods = {RequestMethod.GET,RequestMethod.PATCH,RequestMethod.POST,RequestMethod.PATCH,RequestMethod.PUT})
@RestController
@RequestMapping(CommonConstant.API_VERSION + "client")
public class ClientController {
    private ApiClientService apiClientService;

    @Autowired
    public void setApiClientService(ApiClientService apiClientService) {
        this.apiClientService = apiClientService;
    }

    @PreAuthorize("hasAnyAuthority('CREATE_USER')")
    @PostMapping
    public ResponseEntity<Response> createClient(@RequestBody ServiceRequest userRequest, HttpServletRequest httpServletRequest) throws MinServiceException {
        return apiClientService.createService(false, userRequest);
    }

    @PreAuthorize("hasAnyAuthority('UPDATE_USER')")
    @PutMapping
    public ResponseEntity<Response> updateClient(@RequestBody ServiceRequest userRequest, HttpServletRequest httpServletRequest) throws MinServiceException {
        return apiClientService.createService(true, userRequest);
    }

    @PreAuthorize("hasAnyAuthority('RESET_APIKEY')")
    @GetMapping("{id}")
    public ResponseEntity<Response> resetApikey(@PathVariable("id") Long id, HttpServletRequest httpServletRequest) throws MinServiceException {
        return apiClientService.resetApiKey(id);
    }


    @PreAuthorize("hasAnyAuthority('VIEW_USER')")
    @GetMapping
    public ResponseEntity<Response> getAll(HttpServletRequest httpServletRequest) throws MinServiceException {
        return apiClientService.getAllClient();
    }


    @PreAuthorize("hasAnyAuthority('ACTIVATE_USER')")
    @PatchMapping
    public ResponseEntity<Response> toggle(@RequestParam Long serviceId, HttpServletRequest httpServletRequest) throws MinServiceException {
        return apiClientService.toggle(serviceId);
    }

}
