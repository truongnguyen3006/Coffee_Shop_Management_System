package application.model;


import application.*;
import application.model.*;
import application.repository.*;
import application.service.*;
import application.messaging.*;
import application.util.*;
import application.config.*;

public class AuthenticatedUser {
    private final int userId;
    private final String userName;
    private final String userType;
    private final String passwordHash;
    private final String email;
    private final String fullName;
    private final String phoneNumber;

    public AuthenticatedUser(int userId, String userName, String userType, String passwordHash, String email,
            String fullName, String phoneNumber) {
        this.userId = userId;
        this.userName = userName;
        this.userType = userType;
        this.passwordHash = passwordHash;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserType() {
        return userType;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
