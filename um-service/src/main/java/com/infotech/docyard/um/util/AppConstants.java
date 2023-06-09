package com.infotech.docyard.um.util;

public class AppConstants {

    public static class DB {

    }

    public static class Status {
        public final static String ACTIVE = "Active";
        public final static String SUSPEND = "Suspend";
        public final static String TERMINATE = "Terminate";

        public static final String LOCKED = "Locked";
    }

    public static class DateFormats {
        public final static String DATE_FORMAT_ONE = "dd-MM-yyyy";
        public final static String MONTH_DATE_YEAR = "M/d/yyyy";
        public final static String DATE_MONTH_YEAR = "d/m/yyyy";
        public final static String DATE_FORMAT_TWO = "dd-M-yyyy";
        public final static String DATE_FORMAT_THREE = "d-M-yyyy";
        public final static String DATE_FORMAT_FOUR = "d/M/yyyy";
    }

    public static class EmailSubjectConstants {
        public final static String PASSWORD_EXPIRED = "Password Expired";
        public final static String FORGOT_PASSWORD = "Forgot Password";
        public final static String CHANGE_PASSWORD = "Change Password";
        public final static String USER_CREATED = "User Created";
        public final static String GD_ASSIGNED_EXAMINER = "Goods Declaration Assigned";
    }

    public static class EmailConstants {
        public static final String EMAIL_STATUS_NOT_SEND = "NOT_SENT";
        public static final String EMAIL_STATUS_SEND = "SENT";

    }

    public static class DBConstraints {
        public final static String UNQ_CODE = "UNQ_CODE";
        public final static String UNQ_ROLE_CODE = "UNQ_ROLE_CODE";
        public final static String UNQ_GROUP_CODE = "UNQ_GROUP_CODE";
        public final static String UNQ_DEPARTMENT_CODE = "UNQ_DEPARTMENT_CODE";

    }
}
