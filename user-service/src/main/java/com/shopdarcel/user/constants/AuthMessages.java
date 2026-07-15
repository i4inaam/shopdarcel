package com.shopdarcel.user.constants;

public final class AuthMessages {

    private AuthMessages() {
    }

    public static final String EMAIL_ALREADY_REGISTERED = "Email is already registered";
    public static final String INVALID_CREDENTIALS = "Invalid email or password";
    public static final String ACCOUNT_LOCKED = "Account is locked due to too many failed login attempts. Please reset your password to unlock it.";
    public static final String ACCOUNT_DEACTIVATED = "Account is deactivated. Please contact support or reactivate your account.";
    public static final String INVALID_REFRESH_TOKEN = "Invalid or expired refresh token";
}