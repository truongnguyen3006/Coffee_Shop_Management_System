package application.repository;


import application.*;
import application.model.*;
import application.repository.*;
import application.service.*;
import application.messaging.*;
import application.util.*;
import application.config.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Logger;

import application.Database;
import application.model.AuthenticatedUser;
import application.model.RegistrationRequest;
import application.model.UserContactInfo;
import application.util.AppLogger;

public class UserRepository {
    private static final Logger LOGGER = AppLogger.getLogger(UserRepository.class);

    public boolean existsByUserName(String userName) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE userName = ?";
        try (Connection connection = Database.connect(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection connection = Database.connect(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public AuthenticatedUser findByUserName(String userName) throws SQLException {
        String sql = "SELECT userID, userName, password, userType, email, fullName, phoneNumber FROM users WHERE userName = ?";
        try (Connection connection = Database.connect(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new AuthenticatedUser(rs.getInt("userID"), rs.getString("userName"), rs.getString("userType"),
                        rs.getString("password"), rs.getString("email"), rs.getString("fullName"),
                        rs.getString("phoneNumber"));
            }
        }
    }

    public void createUser(RegistrationRequest request, String hashedPassword, String userType) throws SQLException {
        String sql = "INSERT INTO users (userName, email, fullName, userType, password, date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = Database.connect(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, request.getUserName());
            ps.setString(2, request.getEmail());
            ps.setString(3, request.getFullName());
            ps.setString(4, userType);
            ps.setString(5, hashedPassword);
            ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();
        }
    }

    public void updatePassword(String userName, String hashedPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE userName = ?";
        try (Connection connection = Database.connect(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setString(2, userName);
            ps.executeUpdate();
        }
    }

    public UserContactInfo findContactInfoByUserName(String userName) throws SQLException {
        String sql = "SELECT fullName, phoneNumber, email FROM users WHERE userName = ?";
        try (Connection connection = Database.connect(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return new UserContactInfo("", "", "");
                }
                return new UserContactInfo(rs.getString("fullName"), rs.getString("phoneNumber"), rs.getString("email"));
            }
        }
    }

    public void upgradePasswordIfNeeded(AuthenticatedUser user, String newPasswordHash) {
        if (user == null) {
            return;
        }
        try {
            updatePassword(user.getUserName(), newPasswordHash);
        } catch (SQLException e) {
            AppLogger.error(LOGGER, "Không thể nâng cấp password hash cho user: " + user.getUserName(), e);
        }
    }
}
