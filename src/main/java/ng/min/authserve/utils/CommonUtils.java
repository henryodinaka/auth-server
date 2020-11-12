package ng.min.authserve.utils;

import ng.min.authserve.execeptioins.MinServiceException;
import lombok.extern.slf4j.Slf4j;
import ng.min.authserve.constants.ResponseCode;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Created by Odinaka Onah on 16/07/2019.
 */
@Slf4j
public final class CommonUtils {

    private CommonUtils() {
    }
    private static final String WORD_SEPARATOR = " ";

    public static String convertToTitleCase(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        return Arrays
                .stream(text.split(WORD_SEPARATOR))
                .map(word -> word.isEmpty()
                        ? word
                        : Character.toTitleCase(word.charAt(0)) + word
                        .substring(1)
                        .toLowerCase())
                .collect(Collectors.joining(WORD_SEPARATOR));
    }
    public static Object splitString(String property) throws MinServiceException {
        if (!Validation.validData(property))
            throw new MinServiceException(ResponseCode.UNAVAILABLE1.getCode(), "Property file is empty", ResponseCode.UNAVAILABLE1.getStatusCode());
        try {
            return property.split(",");
        } catch (Exception e) {
            log.error("Split string error ", e);
            throw new MinServiceException(ResponseCode.UNAVAILABLE1.getCode(), e.getMessage(), ResponseCode.UNAVAILABLE1.getStatusCode());
        }
    }

    public boolean compareDob(String dob, String dobBvn) {
        String[] dobArr = dob.split("-");
        String[] dobBv = dobBvn.split("-");
        log.info("The split dates DOB {} BVN DOB {}", dobArr, dobBv);

        if (!dobArr[0].equalsIgnoreCase(dobBv[0])) {
            log.info(" DOB at index 0 {} BVN DOB at index 0 {}", dobArr[0], dobBv[0]);
            return false;
        }
        if (!dobArr[1].equalsIgnoreCase(dobBv[1])) {
            log.info(" DOB at index 1 {} BVN DOB at index 1 {}", dobArr[1], dobBv[1]);
            return false;
        }
        if (!dobArr[2].equalsIgnoreCase(dobBv[2])) {
            log.info(" DOB at index 2 {} BVN DOB at index 2 {}", dobArr[2], dobBv[2]);
            return false;
        }
        return true;
    }

    public static LocalDate dateFormat(String date) throws MinServiceException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (!Validation.validData(date))
            throw new MinServiceException(ResponseCode.BAD_REQUEST.getCode(), "Invalid date ", ResponseCode.BAD_REQUEST.getStatusCode());
        try {
            return LocalDate.parse(date, formatter);
        } catch (Exception e) {
            log.error("", e);
            throw new MinServiceException(ResponseCode.BAD_REQUEST.getCode(), e.getMessage() + ": yyyy-MM-dd", ResponseCode.BAD_REQUEST.getStatusCode());
        }
    }

    public static LocalDate dateFormatBvn(String date) throws MinServiceException {
        String format = "dd-MMM-yy";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        if (!Validation.validData(date))
            throw new MinServiceException(ResponseCode.BAD_REQUEST.getCode(), "Invalid date ", ResponseCode.BAD_REQUEST.getStatusCode());
        try {
            return LocalDate.parse(date, formatter);
        } catch (Exception e) {
            log.error("", e);
            throw new MinServiceException(ResponseCode.BAD_REQUEST.getCode(), e.getMessage() + " Valid date format " + format, ResponseCode.BAD_REQUEST.getStatusCode());
        }
    }

    public static LocalDateTime dateTimeFormat(String date) throws MinServiceException {
        String format = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        if (!Validation.validData(date))
            throw new MinServiceException(ResponseCode.BAD_REQUEST.getCode(), "Invalid date ", ResponseCode.BAD_REQUEST.getStatusCode());
        try {
            return LocalDateTime.parse(date, formatter);
        } catch (Exception e) {
            log.error("", e);
            throw new MinServiceException(ResponseCode.BAD_REQUEST.getCode(), e.getMessage() + " Valid date format " + format, ResponseCode.BAD_REQUEST.getStatusCode());
        }
    }

    public static String reconstructDate(String date) {
        String date1 = getStringDate(date);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss");
        return LocalDate.parse(date1, formatter).toString();
    }

    public static String dateToStringFormated(LocalDateTime date) {
        if (date != null && date.toString().length() > 19)
            return date.toString().substring(0, 19).replace("T"," ");
        return null;
    }

    public static String getStringDate(String date) {
        int currentYear = LocalDate.now().getYear();
        String[] elements = date.split("-");
        StringBuilder dateBuilder = new StringBuilder();

        String year = elements[2];
        String ye = String.valueOf(currentYear);
        String substring = ye.substring(0, 2);
        year = substring + year;
        int editYear = Integer.parseInt(year);
        if (editYear > currentYear) {
            editYear = editYear - 100;
        }
        dateBuilder.append(elements[0] + "-");
        dateBuilder.append(elements[1] + "-");
        dateBuilder.append(editYear);
        return dateBuilder.toString();
    }

    public static String buildUniqueId(String fName, String lName, String dob) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");

        String format = /*LocalDate.now().format(formatter);*/ transform(dob);
        return fName.substring(0, 3).toUpperCase() + lName.substring(0, 3).toUpperCase() + format;
    }

    public static String generatePaymentRef(String paymentType) {
        var type = paymentType.split("_");
        if (type.length != 3) return null;
        var typeFirstLetter = type[0].substring(0, 1) + type[1].substring(0, 1) + type[2].substring(0, 1);
        return typeFirstLetter + getTimeStampYY() + RandomStringUtils.random(5, true, true);

    }

    public static String transform(String date) {

        date = reconstructDate(date);
        String yy = date.substring(2, 4);
        String mm = date.substring(5, 7);
        String dd = date.substring(8, 10);

        return dd + mm + yy;
    }

    public static void main(String[] args) throws MinServiceException {


//        String local = buildUniqueId("ODC","OND","12-Aug-94");
//        System.out.println(local);
////        String uni = buildUniqueId("Joe","pus");
////        System.out.println(uni);


//        System.out.println("BOO "+transform("12-Aug-94"));
//        String timeStamp = getTimeStamp();
//        System.out.println(timeStamp);

//        String reconstructDate = getStringDate("19-MAY-19");
//        System.out.println("This is "+ reconstructDate);
//        System.out.println(covertDateTime(LocalDateTime.now().toString()));
//        System.out.println(generatePaymentRef(TransactionType.ORDER_PAYMENT_FUNDING.name()));
//        System.out.println(dateToStringFormated(LocalDateTime.now()));
        System.out.println(convertToTitleCase("for the purpose of coding"));
        System.out.println(convertToTitleCase("FOR THE PURPOSE OF LOVE"));


    }

    public static LocalDateTime covertDateTime(String date) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(date);
//        return LocalDateTime.parse(date, formatter);
    }

    public static String formatDateTime(String date) {
        LocalDateTime parse = LocalDateTime.parse(date);
        DateTimeFormatter formattera = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(Locale.forLanguageTag("en"));
        return parse.format(formattera);
    }

    public static boolean compareLocalDateTime(LocalDateTime firstDate, LocalDateTime secondDate) {
        return !firstDate.isAfter(secondDate);
    }

    public static boolean isNewEmail(String oldEmail, String newEmail) {
        if (!Validation.validData(oldEmail) && Validation.validData(newEmail)) return true;
        return Validation.validData(oldEmail) && !oldEmail.equals(newEmail);
    }

    public static String getTimeStamp() {
        LocalDateTime now = LocalDateTime.now();
        String nowString = now.toString();
        return nowString.replace("-", "").replace("T", "").replace(":", "").substring(0, 14);
    }

    public static String getTimeStampYY() {
        LocalDateTime now = LocalDateTime.now();
        String nowString = now.toString();
        return nowString.replace("-", "").replace("T", "").replace(":", "").substring(2, 14);
    }

    public static String encodeUrlString(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    public static String decodeUrlString(String value) throws UnsupportedEncodingException {
        return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
    }
}
