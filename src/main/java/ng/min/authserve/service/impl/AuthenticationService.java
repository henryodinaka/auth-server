package ng.min.authserve.service.impl;

import lombok.extern.slf4j.Slf4j;
import ng.min.authserve.constants.ResponseCode;
import ng.min.authserve.dto.Response;
import ng.min.authserve.dto.AuthToken;
import ng.min.authserve.model.ProfileDetails;
import ng.min.authserve.model.TokenProvider;
import ng.min.authserve.utils.AES;
import ng.min.authserve.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class AuthenticationService {
    @Value("${app.api.jwt.expiration}")
    private long expirationTime;

    private AuthenticationManager authenticationManager;


    private TokenProvider jwtTokenUtil;
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    @Autowired
    public void setJwtTokenUtil(TokenProvider jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public ResponseEntity<Response> authenticate(String username, String password) {
        final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Date expiration =  new Date(System.currentTimeMillis() + expirationTime);
        String token = jwtTokenUtil.generateToken(authentication,expiration);
        var tokenEncrypted = AES.encrypt(token);
        if (!Validation.validData(tokenEncrypted)) return Response.setUpResponse(ResponseCode.UNAVAILABLE1,"authentication request");
//        log.info("Token before encryption {}",token);
//        log.info("Encrypted token {}",tokenEncrypted);
        AuthToken authToken = new AuthToken(tokenEncrypted);
        ProfileDetails details =
                (ProfileDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        authToken.setExpiration(expiration);
//        authToken.setUser(new UserResponse(details.toUser()));

        return Response.setUpResponse(ResponseCode.SUCCESS, "", authToken);
    }

    public static void main(String[] args) {
        System.out.println("Current date "+new Date(System.currentTimeMillis()));
        System.out.println("Expires at "+new Date(System.currentTimeMillis()+600000));
    }
}