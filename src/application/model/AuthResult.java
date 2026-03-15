package application.model;


import application.*;
import application.model.*;
import application.repository.*;
import application.service.*;
import application.messaging.*;
import application.util.*;
import application.config.*;

public class AuthResult {
    private final boolean success;
    private final String message;
    private final AuthenticatedUser user;
    private final boolean passwordUpgraded;

    public AuthResult(boolean success, String message, AuthenticatedUser user, boolean passwordUpgraded) {
        this.success = success;
        this.message = message;
        this.user = user;
        this.passwordUpgraded = passwordUpgraded;
    }

    public static AuthResult success(AuthenticatedUser user, boolean passwordUpgraded) {
        return new AuthResult(true, "Đăng nhập thành công!", user, passwordUpgraded);
    }

    public static AuthResult failure(String message) {
        return new AuthResult(false, message, null, false);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public AuthenticatedUser getUser() {
        return user;
    }

    public boolean isPasswordUpgraded() {
        return passwordUpgraded;
    }
}
