package constants;

public class Constants {

    public static final String USERNAME = "username";
    public static final String USER_NAME_ERROR = "username_error";
    public static final String USER_INDEX = "userIndex";
    public static final String GAME_NAME = "gameName";

    public static final String CHAT_PARAMETER = "userstring";
    public static final String CHAT_VERSION_PARAMETER = "chatversion";

    public static final int INT_PARAMETER_ERROR = Integer.MIN_VALUE;

//    public static final String SIGN_UP_URL = "../signup/singup.html";
    public static final String SIGN_UP_URL = "/pages/signup/signup.html";
    public static final String LOGIN_ERROR_URL = "/pages/loginerror/login_attempt_after_error.jsp";  // must start with '/' since will be used in request dispatcher...
    public static final String LOBY_PAGE_URL = "/pages/loby/loby.html";
}