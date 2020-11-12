package ng.min.authserve.execeptioins;

/**
 * Created by Unogwu Daniel on 13/07/2020.
 */
public class DataEncryptionException extends Exception {
    public DataEncryptionException(String message) {
        super(message);
    }

    public DataEncryptionException(Throwable cause) {
        super(cause);
    }
}
