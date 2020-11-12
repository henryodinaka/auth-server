package ng.min.authserve.controller;

import com.fasterxml.jackson.core.JsonParseException;
import ng.min.authserve.execeptioins.MinServiceException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import ng.min.authserve.constants.ResponseCode;
import ng.min.authserve.dto.Response;
import ng.min.authserve.utils.Validation;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.PersistenceException;
import java.net.SocketTimeoutException;
import java.util.Objects;

/**
 * Created by Odinaka Onah on 08 Jul, 2020.
 */
@ControllerAdvice(annotations = RestController.class, basePackages = "ng.min.wallet.controller")
@ResponseBody
@Slf4j
public class AdviceControllerThrowable {
//    private static final Logger log = LoggerFactory.getLogger(AdviceControllerThrowable.class);

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleLockedException(HttpClientErrorException e) {
        log.error("Error",e);
        return Response.setUpResponse(e.getRawStatusCode(),e.getStatusText()+" "+e.getResponseBodyAsString(),null);
    }

    @ExceptionHandler(PersistenceException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleLoginException(PersistenceException e) {
        log.error("PersistenceException ",e);
        return Response.setUpResponse(400,"Data error",null);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleLoginException(IllegalArgumentException e) {
        log.error("IllegalArgumentException ",e);
        return Response.setUpResponse(400,e.getLocalizedMessage(),null);
    }
    @ExceptionHandler(MinServiceException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> jsonException(MinServiceException e) {
        return Response.setUpResponse(e.getHttpCode(),e.getStatusCode(),e.getMessage(),null);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Response> noAccessException(AuthenticationException e) {
        log.debug("AuthenticationException {}",e.getMessage());
        return Response.setUpResponse(ResponseCode.ACCESS_DENIED,e.getMessage()+", you do not privilege to access this resource");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> noAccessException(MethodArgumentNotValidException e) {
        log.debug("MethodArgumentNotValidException {}",e.getMessage());
        return Response.setUpResponse(ResponseCode.BAD_REQUEST,e.getMessage()+": some required fields were not provided");
    }

    @ExceptionHandler(SocketTimeoutException.class)
    public ResponseEntity<Response> timeOutException(MethodArgumentNotValidException e) {
        log.debug("SocketTimeoutException {}",e.getMessage());
        return Response.setUpResponse(ResponseCode.REQUEST_TIMEOUT,"");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Response> noAccessException(AccessDeniedException e) {
        log.debug("AccessDeniedException {}",e.getMessage());
        return Response.setUpResponse(ResponseCode.ACCESS_DENIED,e.getMessage());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Response> noAccessException(ExpiredJwtException e) {
        log.debug("ExpiredJwtException {}",e.getMessage());
        return Response.setUpResponse(ResponseCode.ACCESS_DENIED,e.getMessage());
    }
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Response> noAccessException(NullPointerException e) {
        log.error("Null Pointer exception",e);
        return Response.setUpResponse( ResponseCode.UNAVAILABLE1, "your request");
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Response> noAccessException(MissingServletRequestParameterException e) {
        log.debug("MissingServletRequestParameterException error {}",e.getMessage());
        return Response.setUpResponse(ResponseCode.BAD_REQUEST, Validation.validData(e.getLocalizedMessage())?e.getLocalizedMessage(): Objects.requireNonNull(e.getMessage()));
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response> noAccessException(DataIntegrityViolationException e) {
        log.error("Data integrity violation",e);
        return Response.setUpResponse(ResponseCode.UNAVAILABLE1,"your request");
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response> noAccessException(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException",e);
        return Response.setUpResponse(ResponseCode.BAD_REQUEST, Objects.requireNonNull(e.getMessage()).split(":")[0]);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> noAccessException(Exception e) {
        log.error("Unknown Exception",e);
        return Response.setUpResponse(ResponseCode.UNAVAILABLE1,"your request");
    }
    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<Response> noAccessException(JsonParseException e) {
        log.error("The request body from the client is invalid",e);
        return Response.setUpResponse(ResponseCode.BAD_REQUEST,e.getMessage()+":Check your payload");
    }
}