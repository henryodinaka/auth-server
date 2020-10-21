package ng.min.authserve.constants;

public interface CommonConstant {
    String API_VERSION = "/api/v1/";

    String ASSET_BASE_PATH = "src/main/resources/static/";

    String CREDIT_WALLET = "creditWallet";
    String CREATE_WALLET = "Wallet Creation Notification";
    String CREDIT_ALERT = "Credit Alert Notification";
    String DEBIT_ALERT = "Debit Alert Notification";
    String OTP_ALERT = "One Time Password (OTP)";
    String PAYMENT_INIT = "PAYMENT_INITIALIZED";
    String  ACCOUNT_UPDATE = "Account Update";
    String SUSPEND_OTP = "OTP Suspended";
    String SUSPEND_WALLET = "Wallet SUSPENDED";
    String BLOCK_WALLET = "Wallet Blocked";
    String REACTIVATE_WALLET = "Wallet Reactivation";
    String RESET_OTP = "OTP RESET";
    String PIN_RESET = "Pin Reset";
    String PIN_CHANGE = "Pin change";

    String LOGIN_ID = "login_id";
    String LOGIN = "login";
    String LOGIN_TOKEN = "loginToken";
    String CURRENCY_CODE = "currency_code";
    String RANGE = "range";
    String LOGOPATH = "path";

    long ACCESS_TOKEN_VALIDITY_SECONDS = 1 * 60 * 60;
    String SIGNING_KEY = "devinits123r";
    String TOKEN_PREFIX = "Bearer ";
    String ADMIN_TOKEN_PREFIX = "ISWAdmin ";
    String HEADER_STRING = "Authorization";
    String HEADER_STRING_KEY = "ApiKey";
    String HEADER_STRING_CLIENT_ID = "ClientId";
    String AUTHORITIES_KEY = "scopes";

    String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    Long PASSWORD_TOKEN_EXPIRY_MIN = 30l;

    // Email Subjects
    String PASSWORD_RESET_SUBJECT = "MIN: Password Reset";

    // Email Templates
    String PASSWORD_RESET_TEMPLATE = "password-reset";

}