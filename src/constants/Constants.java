package constants;

/**
 * Trida obsahujici globalni promene
 * 
 * @author Petr A15B0055K
 *
 */
public class Constants {

    private Constants() {
        // TODO Auto-generated constructor stub
    }

    public static final String ALL = "1";
    public static final String PRIVATE = "2";
    public static final String PING = "3";
    public static final String LOG_IN = "4";
    public static final String LOG_OUT = "5";
    public static final String CON_USERS = "6";
    public static final String OK = "7";
    public static final String ERROR = "80";
    public static final String SHUTDOWN = "9";
    public static final String CHAT_ALL = "All";
    public static final String SEPARATOR = ";";

    public static final String UNDEF = "-1";

    public static final int MAX_MESSAGE_LENGTH = 900;

    public static final char ERROR_USER_EXIST = '1';
    public static final char ERROR_MAX_USERS = '2';
    public static final char ERROR_UNABLE_SEND_PRIVATE = '3';
    public static final char ERROR_UNABLE_SEND_PUBLIC = '4';

}
