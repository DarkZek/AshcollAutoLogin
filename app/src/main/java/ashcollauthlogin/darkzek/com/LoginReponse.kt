package ashcollauthlogin.darkzek.com

enum class LoginResponse {
    SUCCESS,
    ALREADY_LOGGED_IN,
    LOGIN_PAGE_UNACCESSABLE,
    COULDNT_SEND_LOGIN_REQUEST,
    NO_INTERNET,
    INCORRECT_USER_INFO,
    UNKNOWN_ERROR
}
