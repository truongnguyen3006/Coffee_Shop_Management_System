package application.service;


import application.*;
import application.model.*;
import application.repository.*;
import application.service.*;
import application.messaging.*;
import application.util.*;
import application.config.*;

import java.sql.SQLException;

import application.model.AuthResult;
import application.model.AuthenticatedUser;
import application.model.RegistrationRequest;
import application.repository.UserRepository;
import application.util.ValidationUtil;

public class AuthService {
    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public AuthService() {
        this(new UserRepository(), new PasswordService());
    }

    public AuthService(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    public AuthResult login(String userName, String rawPassword) throws SQLException {
        if (userName == null || userName.trim().isEmpty() || rawPassword == null || rawPassword.isEmpty()) {
            return AuthResult.failure("Vui lòng điền đầy đủ thông tin đăng nhập.");
        }

        AuthenticatedUser user = userRepository.findByUserName(userName.trim());
        if (user == null) {
            return AuthResult.failure("Tên người dùng không tồn tại. Vui lòng đăng ký.");
        }

        if (!passwordService.matches(rawPassword, user.getPasswordHash())) {
            return AuthResult.failure("Mật khẩu không chính xác.");
        }

        boolean upgraded = false;
        if (passwordService.needsRehash(user.getPasswordHash())) {
            userRepository.upgradePasswordIfNeeded(user, passwordService.hashPassword(rawPassword));
            upgraded = true;
        }
        return AuthResult.success(user, upgraded);
    }

    public String validateRegistration(RegistrationRequest request) throws SQLException {
        if (request.getUserName() == null || request.getUserName().trim().isEmpty() || request.getEmail() == null
                || request.getEmail().trim().isEmpty() || request.getFullName() == null
                || request.getFullName().trim().isEmpty() || request.getPassword() == null
                || request.getPassword().isEmpty()) {
            return "Vui lòng điền tất cả các trường thông tin!";
        }
        if (!ValidationUtil.isValidEmail(request.getEmail())) {
            return "Vui lòng nhập một địa chỉ email hợp lệ.";
        }
        if (!ValidationUtil.isValidPassword(request.getPassword())) {
            return "Mật khẩu phải có ít nhất 8 ký tự, chứa chữ thường, chữ hoa, số và ký tự đặc biệt.";
        }
        if (userRepository.existsByUserName(request.getUserName().trim())) {
            return "Tên người dùng đã tồn tại.";
        }
        if (userRepository.existsByEmail(request.getEmail().trim())) {
            return "Email đã tồn tại.";
        }
        return null;
    }

    public void register(RegistrationRequest request) throws SQLException {
        String role = "admin".equalsIgnoreCase(request.getUserName().trim()) ? request.getUserName().trim() : "user";
        userRepository.createUser(request, passwordService.hashPassword(request.getPassword()), role);
    }

    public boolean existsByEmail(String email) throws SQLException {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUserName(String userName) throws SQLException {
        return userRepository.existsByUserName(userName);
    }

    public void resetPassword(String userName, String newRawPassword) throws SQLException {
        userRepository.updatePassword(userName, passwordService.hashPassword(newRawPassword));
    }

    public PasswordService getPasswordService() {
        return passwordService;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }
}
