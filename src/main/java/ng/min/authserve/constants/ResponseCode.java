package ng.min.authserve.constants;

/**
 * Created by Odinaka Onah on 08 Jul, 2020.
 */
public enum ResponseCode {
    SUCCESS(200,"00","{} Successful"),
    OK(200,"00","OK"),
    ACCOUNT_CREATED(200,"00","Your Account Has Been Created Successfully: {}"),
    ACCOUNT_FOUND(200,"00","You already have account...Do you wish to continue with it ? "),
    PROCESSING_PAYMENT(202,"00","{} in progress, you will be notified by your bank once the payment has been processed"),
    NO_ACTION_PERFORMED(200,"02","No action performed {}"),
    DUPLICATE_DOC(200,"03","{} was uploaded and verified previously. It cannot be uploaded again. Please upload other required documents"),
    ITEM_NOT_FOUND(404,"04","Sorry seems the {} cannot be found on our system,please make sure you pass the correct value"),
    NOT_ACCOUNT_ATTACHED(200,"05","!Oops You have not setup Cash-out account for your wallet. proceed to setup your account "),
    INVALID_DOCUMENT_UPLOADED(200,"400","!Oops The following documents uploaded failed or still undergoing validation...Please upload a replacement where necessary to continue"),
    TOKEN_ERROR(200,"07" ," Token validation error"),
    INVALID_ITEM(200,"08"," {} supplied is incorrect"),
    INVALID_TOKEN(400,"06","The supplied token is invalid"),
    ID_REQUIRED(200,"09","Valid means of Id is required for account tier"),
    OTP_EXPIRED(200,"15","OTP has expired"),
    BAD_REQUEST(400,"16","Invalid request: {} ... please try again "),
    DUPLICATE_REQUEST(409,"21","{} record exist."),
    DUPLICATE_PERMISSION_ROLE(409,"21","This permission has already been assigned to this role."),
    INSUFFICIENT_FUND(400,"99","Insufficient fund."),
    PAYMENT_REQUIRE(200,"23","Payment is required"),
    DOCUMENT_REQUIRED(200,"25","Document upload is required: {}"),
    ACCESS_DENIED(401,"27","Access denied: {}"),
    REACTIVATE(401,"11","{} is disabled, do you want to reactivate account?"),
    MATCH_LAST_PIN(400,"29","{}... please chose a new pin that is different from the last 2 pins used on your wallet before"),
    NOT_IMPLEMENTED(200,"32","!Oops we would love to serve you better in our next version"),
    REQUEST_TIMEOUT(200,"50","Sorry...request is taking longer than expected.. please trying again later"),
    INCORRECT_OLD_PASSWORD(400,"16","The current pin supplied is incorrect"),
    UNAVAILABLE1(503,"100","Sorry we cannot process {} at the moment, please try again later or call any of our service help desk for assistance");
    String value;
    Integer code;
    String statusCode;
    ResponseCode(Integer code, String statusCode, String value) {
        this.value = value;
        this.code = code;
        this.statusCode =statusCode;
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }

    public String getStatusCode() {
        return statusCode;
    }
}
