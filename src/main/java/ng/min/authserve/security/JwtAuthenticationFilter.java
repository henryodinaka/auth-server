package ng.min.authserve.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import execeptioins.MinServiceException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import ng.min.authserve.constants.CommonConstant;
import ng.min.authserve.model.ProfileDetailsService;
import ng.min.authserve.model.TokenProvider;
import ng.min.authserve.utils.AES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Unogwu Daniel on 13/07/2020.
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private ProfileDetailsService userDetailsService;

    @Autowired
    private TokenProvider jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(CommonConstant.HEADER_STRING);
        String username = null;
        String authToken = null;
//        logger.info("Authorization -->>>>>>>>" + header);
        if (header != null && (header.startsWith(CommonConstant.TOKEN_PREFIX))) {

            String[] authTokenArray = header.split("\\s+");
            if (authTokenArray.length == 2) {

                var authTokenEncrypted = authTokenArray[1]; /*Decrypt token*/
                try {
                    authToken = AES.decrypt(authTokenEncrypted);
                } catch (MinServiceException e) {
                    res.resetBuffer();
                    res.setStatus(e.getHttpCode());
                    res.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                    res.getOutputStream().print(new ObjectMapper().writeValueAsString(e.getMessage()));
                    res.flushBuffer();
                }
//                log.info("Decrypted token {}",authToken);
                try {
                    if (header.startsWith(CommonConstant.TOKEN_PREFIX))
                        username = jwtTokenUtil.getUsernameFromToken(authToken);
                } catch (IllegalArgumentException e) {
                    logger.error("an error occurred during getting username fromUser token", e);
                } catch (ExpiredJwtException e) {
                    logger.warn("the token is expired and not valid anymore "+ e.getMessage());
                } catch (SignatureException e) {
                    logger.warn("Authentication Failed. Username or Password not valid. "+e.getMessage());
                } catch (MalformedJwtException e) {
                    logger.warn("Malformed token."+e.getMessage());
                }
            }
        }  //            logger.warn("couldn't find bearer string, will ignore the header");

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = jwtTokenUtil.getAuthentication(userDetails);
                logger.info("authenticated user " + username + ", setting security context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(req, res);
    }
}