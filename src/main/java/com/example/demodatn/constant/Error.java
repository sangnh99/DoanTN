package com.example.demodatn.constant;

public enum Error {

    PARAMETER_INVALID("SYS_0001", "Parameter invalid"),
    CANT_SEND_EMAIL("SYS_0002", "Can't send email"),
    EXTERNAL_LOGIN_FAIL("SYS_0003", "External login fail"),
    ROLE_DOES_NOT_EXIST("SYS_0004", "Role does not exist"),
    MEDIA_DOES_NOT_EXIST("SYS_0005", "Media does not exist"),
    PLAYLIST_START_TIME_HAS_PROBLEM("SYS_0006", "Playlist start time has problem"),
    USER_DOES_NOT_EXIST("SYS_0007", "User does not exist"),
    USER_NOT_EXIST("SYS_0008", "User is not exist"),
    TOKEN_INVALID("SYS_009", "Token is invalid"),
    VERIFY_EMAIL_TOKEN_INVALID("SYS_0010", "Expired or invalid verify email token"),
    TIME_INVALID("SYS_0011", "Time format invalid."),
    PARENT_CATEGORY_INVALID("SYS_0012", "Parent category is invalid"),
    PROGRAM_ID_OR_MEDIA_ID_NOT_CORRECT("SYS_0013", "Program id or media id is not correct"),
    CHILD_CATEGORY_INVALID("SYS_0014", "Child category is invalid"),
    STREAM_CDN_INVALID("SYS_0015", "Stream CDN is invalid"),
    PLATFORM_INVALID("SYS_0016", "Platform is invalid")
    ;

    private String code;

    private String message;

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @param code
     * @param message
     */
    Error(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
