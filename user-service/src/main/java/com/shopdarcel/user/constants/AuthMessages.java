package com.shopdarcel.user.constants;

public final class AuthMessages {

    private AuthMessages() {
    }

    public static final String EMAIL_ALREADY_REGISTERED = "Email is already registered";
    public static final String INVALID_CREDENTIALS = "Invalid email or password";
    public static final String ACCOUNT_LOCKED = "Account is locked due to too many failed login attempts. Please reset your password to unlock it.";
    public static final String ACCOUNT_DEACTIVATED = "Account is deactivated. Please contact support or reactivate your account.";
    public static final String INVALID_REFRESH_TOKEN = "Invalid or expired refresh token";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String MISSING_USER_ID_HEADER = "Missing X-User-Id header";
    public static final String CURRENT_PASSWORD_INCORRECT = "Current password is incorrect";
    public static final String NEW_PASSWORD_SAME_AS_OLD = "New password must be different from current password";
    public static final String BIRTH_YEAR_INCORRECT = "Birth year must be between 1900 and ";
}