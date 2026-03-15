package application.controller;


import application.*;
import application.model.*;
import application.repository.*;
import application.service.*;
import application.messaging.*;
import application.util.*;
import application.config.*;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import application.model.AuthenticatedUser;
import application.repository.UserRepository;
import application.service.PasswordService;
import application.util.AppLogger;
import application.util.ValidationUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ChangePasswordController implements Initializable {
    private static final Logger LOGGER = AppLogger.getLogger(ChangePasswordController.class);

    @FXML
    private TextField tfCurrentPassword;

    @FXML
    private TextField tfNewPassword;

    @FXML
    private TextField tfNewPasswordAgain;

    private final UserRepository userRepository = new UserRepository();
    private final PasswordService passwordService = new PasswordService();

    public static String hashPassword(String password) {
        return new PasswordService().hashPassword(password);
    }

    @FXML
    void confirmChangePassword() {
        String currentPassword = tfCurrentPassword.getText();
        String newPassword = tfNewPassword.getText();
        String confirmedPassword = tfNewPasswordAgain.getText();

        if (currentPassword == null || currentPassword.isBlank() || newPassword == null || newPassword.isBlank()
                || confirmedPassword == null || confirmedPassword.isBlank()) {
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Thiếu thông tin",
                    "Vui lòng nhập đầy đủ mật khẩu hiện tại và mật khẩu mới.");
            return;
        }

        if (!newPassword.equals(confirmedPassword)) {
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Mật khẩu không khớp",
                    "Mật khẩu mới và xác nhận mật khẩu không khớp. Vui lòng kiểm tra lại.");
            return;
        }

        if (!ValidationUtil.isValidPassword(newPassword)) {
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Mật khẩu chưa hợp lệ",
                    "Mật khẩu phải có ít nhất 8 ký tự, chứa chữ hoa, chữ thường, số và ký tự đặc biệt.");
            return;
        }

        try {
            AuthenticatedUser user = userRepository.findByUserName(Session.getUserName());
            if (user == null) {
                AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Không tìm thấy tài khoản",
                        "Không thể xác định người dùng hiện tại.");
                return;
            }

            if (!passwordService.matches(currentPassword, user.getPasswordHash())) {
                AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Mật khẩu không đúng",
                        "Mật khẩu hiện tại bạn nhập không đúng. Vui lòng thử lại.");
                return;
            }

            userRepository.updatePassword(user.getUserName(), passwordService.hashPassword(newPassword));
            AlertMessage.showAlert(AlertType.INFORMATION, "Thông báo", "Đổi mật khẩu thành công",
                    "Mật khẩu của bạn đã được thay đổi thành công.");
            Stage currentStage = (Stage) tfCurrentPassword.getScene().getWindow();
            currentStage.close();
        } catch (SQLException e) {
            AppLogger.error(LOGGER, "Không thể cập nhật mật khẩu cho user=" + Session.getUserName(), e);
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi hệ thống", "Đổi mật khẩu thất bại",
                    "Có lỗi xảy ra khi đổi mật khẩu. Vui lòng thử lại.");
        }
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    }
}
