package application.service;


import application.*;
import application.model.*;
import application.repository.*;
import application.service.*;
import application.messaging.*;
import application.util.*;
import application.config.*;

import java.sql.SQLException;

import application.model.UserContactInfo;
import application.repository.UserRepository;

public class UserService {
    private final UserRepository userRepository;

    public UserService() {
        this(new UserRepository());
    }

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserContactInfo getContactInfoByUserName(String userName) throws SQLException {
        return userRepository.findContactInfoByUserName(userName);
    }
}
