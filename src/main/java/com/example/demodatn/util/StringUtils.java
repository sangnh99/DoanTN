package com.example.demodatn.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;


import com.example.demodatn.constant.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public final class StringUtils {
    public static final String PATTERN_LOG = "[line: %s] %s: %s";
    private static Logger logger = LoggerFactory.getLogger(StringUtils.class.getName());
    /**
     * compare String
     *
     * @param str1
     * @param str2
     * @return boolean
     */
    public static boolean compareString(String str1, String str2) {
        String str1Temp = str1;
        String str2Temp = str2;
        if (str1Temp == null) {
            str1Temp = "";
        }
        if (str2Temp == null) {
            str2Temp = "";
        }

        if (str1Temp.equals(str2Temp)) {
            return true;
        }
        return false;
    }

    /**
     * is Valid String
     *
     * @param temp
     * @return boolean
     */
    public static boolean isValidString(Object temp) {
        if (temp == null || temp.toString().trim().equals("")) {
            return false;
        }
        return true;
    }

    /**
     * is Integer
     *
     * @param str
     * @return boolean
     */
    public static boolean isInteger(String str) {
        if (str == null || !str.matches("[0-9]+$")) {
            return false;
        }
        return true;
    }

    /**
     * is Long
     *
     * @param str
     * @return boolean
     */
    public static boolean isLong(String str) {
        try {
            Long.valueOf(str);
            return true;
        } catch (Exception ex) {
            logger.error(buildLog(ex.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getLineNumber()));
            return false;
        }
    }

    /**
     * is Double
     *
     * @param str
     * @return boolean
     */
    public static boolean isDouble(String str) {
        try {
            Double.valueOf(str);
            return true;
        } catch (Exception ex) {

            return false;
        }
    }

    /**
     * is Boolean
     *
     * @param str
     * @return boolean
     */
    public static boolean isBoolean(String str) {
        try {
            Boolean.valueOf(str);
            return true;
        } catch (Exception ex) {
            logger.error(buildLog(ex.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getLineNumber()));
            return false;
        }
    }

    /**
     * convert String To Boolean Or Null
     *
     * @param input
     * @return Boolean
     */
    public static Boolean convertStringToBooleanOrNull(String input) {
        try {
            return Boolean.valueOf(input);
        } catch (Exception e) {
            logger.error(buildLog(e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getLineNumber()));
            return null;
        }
    }

    /**
     * convert String To Long Or Null
     *
     * @param input
     * @return Long
     */
    public static Long convertStringToLongOrNull(String input) {
        try {
            return Long.valueOf(input);
        } catch (Exception e) {
            logger.error(buildLog(e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getLineNumber()));
            return null;
        }
    }

    /**
     * convert Object To Long Or Null
     *
     * @param input
     * @return Long
     */
    public static Long convertObjectToLongOrNull(Object input) {
        try {
            return convertStringToLongOrNull(input.toString());
        } catch (Exception e) {
            logger.error(buildLog(e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getLineNumber()));
            return null;
        }
    }

    /**
     * convert String To Integer Or Null
     *
     * @param input
     * @return Integer
     */
    public static Integer convertStringToIntegerOrNull(String input) {
        try {
            return Integer.valueOf(input);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * convert String To Float Or Null
     *
     * @param input
     * @return Float
     */
    public static Float convertStringToFloatOrNull(String input) {
        try {
            return Float.valueOf(input);
        } catch (Exception e) {
            logger.error(buildLog(e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getLineNumber()));
            return null;
        }
    }

    /**
     * convert Object To String
     *
     * @param input
     * @return
     */
    public static String convertObjectToString(Object input) {
        return input == null ? null : input.toString();

    }

    /**
     * convert Date To String
     *
     * @param input
     * @return String
     */
    public static String convertDateToString(Date input) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String date = dateFormat.format(input).toString();
        return date;

    }

    /**
     * convert String To Boolean
     *
     * @param input
     * @return boolean
     * @throws Exception
     */
    public static boolean convertStringToBoolean(String input) throws Exception {
        if (input != null) {
            if (input.equals(Boolean.TRUE.toString()))
                return true;
            if (input.equals(Boolean.FALSE.toString()))
                return false;
        }
        throw new Exception();
    }

    /**
     * is String Ascii
     *
     * @param str
     * @return boolean
     */
    public static boolean isStringAscii(String str) {
        for (char ch : str.toCharArray()) {
            if (!isAscii(ch)) {
                return false;
            }
        }
        return true;
    }

    /**
     * is Ascii
     *
     * @param ch
     * @return boolean
     */
    public static boolean isAscii(char ch) {
        return ch < 128;
    }

    /**
     * convert String To Double Or Null
     *
     * @param amountNumber
     * @return Double
     */
    public static Double convertStringToDoubleOrNull(String amountNumber) {
        try {
            return Double.valueOf(amountNumber);
        } catch (Exception e) {
            logger.error(buildLog(e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getLineNumber()));
            return null;
        }
    }

    /**
     * convert String To BigDecimal Or Null
     *
     * @param number
     * @return BigDecimal
     */
    public static BigDecimal convertStringToBigDecimalOrNull(String number) {
        try {
            return new BigDecimal(number);
        } catch (Exception e) {
            logger.error(buildLog(e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getLineNumber()));
            return null;
        }
    }

    /**
     * is Valid Email
     *
     * @param email
     * @return boolean
     */
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
                + "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null) {
            return false;
        }
        return pat.matcher(email).matches();
    }

    /**
     * contains Only Numbers
     *
     * @param str
     * @return boolean
     */
    public static boolean containsOnlyNumbers(String str) {
        String regex = "[0-9]+";
        boolean b = str.matches(regex);
        return b;
    }

    /**
     * convert Double To String Or Null
     *
     * @param value
     * @param format
     * @return String
     */
    public static String convertDoubleToStringOrNull(Double value, String format) {
        return value == null ? null : new DecimalFormat(format).format(new Double(value.toString()));
    }

    public static String convertDateToStringFormatyyyyMMdd(Date input) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return input == null ? null : dateFormat.format(input).toString();

    }

    public static String convertDateToStringFormatMMddyyy(Date input) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyy");
        return input == null ? null : dateFormat.format(input).toString();

    }

    public static boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }

    public static String formatId(String strOriginal, char leadingCharacter, int outputLen) {
        String str = String.format("%" + outputLen + "s", "").replace(' ', leadingCharacter);

        return (strOriginal.length() == outputLen) ? strOriginal : (str + strOriginal).substring(strOriginal.length());
    }

    public static String convertObjectToStringOrEmpty(Object input) {
        return input == null ? "" : input.toString();
    }

    public static boolean isMatcherPattern(String str, String regex) {
        return str.matches(regex);
    }

    public static boolean isCharacterWithSize(String str, int regex) {
        return str.matches("^[\\w]{0," + regex + "}+$");
    }

    public static boolean isDigitWithSize(String str, int regex) {
        return str.matches("^[\\d]{0," + regex + "}+$");
    }

    public static String convertDateToStringFormatPattern(Date input, String Pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Pattern);
        return input == null ? null : dateFormat.format(input).toString();
    }

    /**
     * get Browser Name
     *
     * @author at-hungnguyen2
     * @return String
     */
    public static String getBrowserName() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        String userAgent = request.getHeader("User-Agent");
        String user = userAgent.toLowerCase();
        String browser = null;
        if (user.contains("edge")) {
            browser = (userAgent.substring(userAgent.indexOf("Edge")).split(" ")[0]).replace("/", "-");
        } else if (user.contains("msie")) {
            String substring = userAgent.substring(userAgent.indexOf("MSIE")).split(";")[0];
            browser = substring.split(" ")[0].replace("MSIE", "IE") + "-" + substring.split(" ")[1];
        } else if (user.contains("safari") && user.contains("version")) {
            browser = (userAgent.substring(userAgent.indexOf("Safari")).split(" ")[0]).split("/")[0] + "-"
                    + (userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
        } else if (user.contains("chrome")) {
            browser = (userAgent.substring(userAgent.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
        } else if (user.contains("firefox")) {
            browser = (userAgent.substring(userAgent.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
        } else if (user.contains("rv")) {
            browser = "IE-" + user.substring(user.indexOf("rv") + 3, user.indexOf(")"));
        } else {
            browser = "Other";
        }

        return browser;
    }

    /**
     * Check string value are valid pattern
     *
     * @param valueCheck String need to validate.
     * @return boolean match status.
     */
    public static boolean validateStringFormat(String valueCheck, String pattern) {
        Pattern patternCheck = Pattern.compile(pattern);
        return patternCheck.matcher(valueCheck).matches();
    }

    /**
     * Replace special character string.
     *
     * @param value the value
     * @return the string
     */
    public static String replaceSpecialCharacter(String value) {
        if(value != null) {
            value = value.replaceAll("\\\\", "\\\\\\\\");
            value = value.replaceAll("%","\\\\%");
            value = value.replaceAll("","\\\\");
        } else {
            value = "";
        }
        return value;
    }

    /**
     * Build Delete condition for Query
     *
     * @param deleteParam param
     * @return condition string
     */
    public static String buildDeleteConditionQuery(String deleteParam) {
        return " (" + deleteParam + " != :isDeleted)";
    }

    /**
     * Build select query string.
     *
     * @param attribute the attribute
     * @param table     the table
     * @param condition the condition
     * @return the string
     */
    public static String buildSelectQuery(List<String> attribute, List<String> table, List<String> condition) {
        StringBuilder selectQuery = new StringBuilder();
        selectQuery.append(" SELECT ");
        selectQuery.append(String.join(", ", attribute));
        selectQuery.append(" FROM ");
        selectQuery.append(String.join(" ", table));
        selectQuery.append(" WHERE ");
        selectQuery.append(String.join(" AND ", condition));
        return selectQuery.toString();
    }

    /**
     * Build select query string.
     *
     * @param attributes   the attributes
     * @param tables       the tables
     * @param conditions   the conditions
     * @param orderByValue the order by value
     * @return the string
     */
    public static String buildSelectQuery(List<String> attributes, List<String> tables, List<String> conditions,
                                          String orderByValue) {
        StringBuilder selectQuery = new StringBuilder();
        selectQuery.append(" SELECT ");
        selectQuery.append(String.join(", ", attributes));
        selectQuery.append(" FROM ");
        selectQuery.append(String.join(" ", tables));
        selectQuery.append(" WHERE ");
        selectQuery.append(String.join(" AND ", conditions));
        selectQuery.append(" ORDER BY " + orderByValue);
        return selectQuery.toString();
    }

    /**
     * Build select query string.
     *
     * @param attributes   the attributes
     * @param tables       the tables
     * @param conditions   the conditions
     * @param groupBy      the group by
     * @param orderByValue the order by value
     * @return the string
     */
    public static String buildSelectQuery(List<String> attributes, List<String> tables, List<String> conditions, List<String> groupBy,
                                          String orderByValue) {
        StringBuilder selectQuery = new StringBuilder();
        selectQuery.append(" SELECT ");
        selectQuery.append(String.join(", ", attributes));
        selectQuery.append(" FROM ");
        selectQuery.append(String.join(" ", tables));
        selectQuery.append(" WHERE ");
        selectQuery.append(String.join(" AND ", conditions));
        selectQuery.append(" GROUP BY ");
        selectQuery.append(String.join(", ", groupBy));
        selectQuery.append(" ORDER BY " + orderByValue);
        return selectQuery.toString();
    }

    /**
     * Build select query string.
     *
     * @param attributes   the attributes
     * @param tables       the tables
     * @param conditions   the conditions
     * @param groupBy      the group by
     * @return the string
     */
    public static String buildSelectQuery(List<String> attributes, List<String> tables, List<String> conditions, List<String> groupBy) {
        StringBuilder selectQuery = new StringBuilder();
        selectQuery.append(" SELECT ");
        selectQuery.append(String.join(", ", attributes));
        selectQuery.append(" FROM ");
        selectQuery.append(String.join(" ", tables));
        selectQuery.append(" WHERE ");
        selectQuery.append(String.join(" AND ", conditions));
        selectQuery.append(" GROUP BY ");
        selectQuery.append(String.join(", ", groupBy));
        return selectQuery.toString();
    }


    /**
     * @descripton build select query
     * @param columns
     * @param tables
     * @param conditions
     * @return
     */
    public static String buildSelectQuery(String[] columns, String[] tables, String[] conditions, boolean isDuplicate) {
        StringBuilder selectQuery = new StringBuilder();
        selectQuery.append(" SELECT ");
        if (!isDuplicate) {
            selectQuery.append(" DISTINCT ");
        }
        selectQuery.append(String.join(", ", columns));
        selectQuery.append(" FROM ");
        selectQuery.append(String.join(" ", tables));
        selectQuery.append(" WHERE ");
        selectQuery.append(String.join(" AND ", conditions));

        return selectQuery.toString();
    }

    /**
     * Make sort by multiple column.
     *
     * @param firstField
     * @param secondField
     * @param firstFieldDirection
     * @return
     */
    public static String makeSortTwoLevel(String firstField, String secondField, String firstFieldDirection) {
        StringBuilder sortString = new StringBuilder();
        sortString.append(firstField);
        sortString.append(" ");
        sortString.append(firstFieldDirection);
        sortString.append(", ");
        sortString.append(secondField);

        return sortString.toString();
    }

    /**
     * Is valid email address boolean.
     *
     * @param email the email
     * @return the boolean
     */
    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            logger.error(buildLog(ex.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getLineNumber()));
            result = false;
        }
        return result;
    }

    /**
     * Gets alpha numeric string.
     *
     * @param n the n
     * @return the alpha numeric string
     */
    public static String getAlphaNumericString(int n) {
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    public static String buildLog(Error err, int line) {
        return String.format(PATTERN_LOG, line, err.getCode(), err.getMessage());
    }

    public static String buildLog(String err, int line) {
        return String.format(PATTERN_LOG, line, null , err);
    }
}
