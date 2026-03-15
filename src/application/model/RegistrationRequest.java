package application.model;


import application.*;
import application.model.*;
import application.repository.*;
import application.service.*;
import application.messaging.*;
import application.util.*;
import application.config.*;

public class RegistrationRequest {
    private final String userName;
    private final String email;
    private final String fullName;
    private final String password;

    public RegistrationRequest(String userName, String email, String fullName, String password) {
        this.userName = userName;
        this.email = email;
        this.fullName = fullName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPassword() {
        return password;
    }
}
