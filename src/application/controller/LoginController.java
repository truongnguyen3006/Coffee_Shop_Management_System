package application.controller;


import application.*;
import application.model.*;
import application.repository.*;
import application.service.*;
import application.messaging.*;
import application.util.*;
import application.config.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import application.config.AdminSessionManager;
import application.model.AuthResult;
import application.model.RegistrationRequest;
import application.model.UserContactInfo;
import application.service.AuthService;
import application.service.EmailService;
import application.service.OtpService;
import application.util.AppLogger;
import application.util.ValidationUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginController implements Initializable {
    private static final Logger LOGGER = AppLogger.getLogger(LoginController.class);

    @FXML
    private TextField fp_OTP;
    @FXML
    private TextField fp_email;
    @FXML
    private TextField su_OTP;
    @FXML
    private Button btnReceive;
    @FXML
    private Button btnReceiveForgot;
    @FXML
    private TextField su_email;
    @FXML
    private TextField su_fullName;
    @FXML
    private Button fp_back;
    @FXML
    private Button fp_proceddBtn;
    @FXML
    private ComboBox<String> fp_question;
    @FXML
    private AnchorPane fp_questionForm;
    @FXML
    private TextField fp_username;
    @FXML
    private TextField np_ConfirmShowPassword;
    @FXML
    private PasswordField np_NewPassword;
    @FXML
    private TextField np_NewShowPassword;
    @FXML
    private Button np_back;
    @FXML
    private Button np_changPassBtn;
    @FXML
    private ImageView np_closeEyeConfirm;
    @FXML
    private ImageView np_closeEyeNew;
    @FXML
    private PasswordField np_newConfirmPassword;
    @FXML
    private AnchorPane np_newPassForm;
    @FXML
    private ImageView np_openEyeConfirm;
    @FXML
    private ImageView np_openEyeNew;
    @FXML
    private ImageView si_closeEye;
    @FXML
    private Hyperlink si_forgotPass;
    @FXML
    private Button si_loginBtn;
    @FXML
    private AnchorPane si_loginForm;
    @FXML
    private ImageView si_openEye;
    @FXML
    private PasswordField si_password;
    @FXML
    private TextField si_show_password;
    @FXML
    private TextField si_username;
    @FXML
    private Button side_CreateBtn;
    @FXML
    private Button side_alreadyHave;
    @FXML
    private AnchorPane side_form;
    @FXML
    private ImageView su_closeEye;
    @FXML
    private ImageView su_openEye;
    @FXML
    private PasswordField su_password;
    @FXML
    private TextField su_show_password;
    @FXML
    private Button su_signUpBtn;
    @FXML
    private AnchorPane su_signUpForm;
    @FXML
    private TextField su_username;
    @FXML
    private Label su_countdown;
    @FXML
    private Label fp_countdown_forgot;

    private final AuthService authService = new AuthService();
    private final OtpService otpService = new OtpService();
    private final EmailService emailService = new EmailService();

    private Timeline otpCountdownRe;
    private int otpValidSecondsRe = 60;
    private boolean isOTPExpiredRe = false;

    private Timeline otpCountdownFor;
    private int otpValidSecondsFor = 60;
    private boolean isOTPExpiredFor = false;

    private boolean loginSuccessfull = false;
    private String generatedOTPRegister = "";
    private String generatedOTPForgotPass = "";

    public boolean IsLoginSuccesfull() {
        return loginSuccessfull;
    }

    public boolean isLoginSuccessful() {
        return loginSuccessfull;
    }

    @FXML
    public void GetOTP_Register() {
        String email = su_email.getText() == null ? "" : su_email.getText().trim();
        if (!ValidationUtil.isValidEmail(email)) {
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi nhập liệu", "Địa chỉ email không hợp lệ",
                    "Vui lòng nhập địa chỉ email hợp lệ.");
            return;
        }
        try {
            if (authService.existsByEmail(email)) {
                AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Email đã tồn tại",
                        "Email bạn nhập đã được đăng ký trước đó. Vui lòng thử email khác.");
                return;
            }
            generatedOTPRegister = otpService.generateOtp();
            emailService.sendOtpEmail(email, generatedOTPRegister);
            isOTPExpiredRe = false;
            su_OTP.setDisable(false);
            btnReceive.setDisable(true);
            startOTPTimerRegister();
            AlertMessage.showAlert(AlertType.INFORMATION, "Thông báo", "Gửi mã OTP thành công",
                    "Mã OTP đã được gửi đến " + email);
        } catch (SQLException e) {
            AppLogger.error(LOGGER, "Không thể kiểm tra email đăng ký", e);
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi hệ thống", "Không thể gửi OTP",
                    "Hệ thống đang gặp sự cố khi kiểm tra email. Vui lòng thử lại.");
        } catch (MessagingException e) {
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi hệ thống", "Gửi OTP thất bại",
                    "Không thể gửi OTP tới email này. Vui lòng kiểm tra lại cấu hình email.");
        }
    }

    @FXML
    public void GetOTP_ForgotPass() {
        String email = fp_email.getText() == null ? "" : fp_email.getText().trim();
        if (!ValidationUtil.isValidEmail(email)) {
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi nhập liệu", "Email không hợp lệ",
                    "Vui lòng nhập đúng định dạng email.");
            return;
        }
        try {
            generatedOTPForgotPass = otpService.generateOtp();
            emailService.sendOtpEmail(email, generatedOTPForgotPass);
            isOTPExpiredFor = false;
            fp_OTP.setDisable(false);
            btnReceiveForgot.setDisable(true);
            startOTPTimerForget();
            AlertMessage.showAlert(AlertType.INFORMATION, "Thông báo", "Gửi mã OTP thành công",
                    "Mã OTP đã được gửi đến " + email);
        } catch (MessagingException e) {
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi hệ thống", "Gửi OTP thất bại",
                    "Không thể gửi OTP tới email này. Vui lòng thử lại.");
        }
    }

    private void startOTPTimerRegister() {
        if (otpCountdownRe != null) {
            otpCountdownRe.stop();
        }
        otpValidSecondsRe = 60;
        su_countdown.setDisable(false);
        su_countdown.setText(otpValidSecondsRe + "s");
        otpCountdownRe = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            otpValidSecondsRe--;
            su_countdown.setText(otpValidSecondsRe + "s");
            if (otpValidSecondsRe <= 0) {
                otpCountdownRe.stop();
                isOTPExpiredRe = true;
                su_countdown.setText("");
                su_countdown.setDisable(true);
                btnReceive.setDisable(false);
                AlertMessage.showAlertOTP(AlertType.ERROR, "Lỗi xác thực", "OTP hết hạn",
                        "Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới.");
            }
        }));
        otpCountdownRe.setCycleCount(otpValidSecondsRe);
        otpCountdownRe.play();
    }

    private void startOTPTimerForget() {
        if (otpCountdownFor != null) {
            otpCountdownFor.stop();
        }
        otpValidSecondsFor = 60;
        fp_countdown_forgot.setDisable(false);
        fp_countdown_forgot.setText(otpValidSecondsFor + "s");
        otpCountdownFor = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            otpValidSecondsFor--;
            fp_countdown_forgot.setText(otpValidSecondsFor + "s");
            if (otpValidSecondsFor <= 0) {
                otpCountdownFor.stop();
                isOTPExpiredFor = true;
                fp_countdown_forgot.setText("");
                fp_countdown_forgot.setDisable(true);
                btnReceiveForgot.setDisable(false);
                AlertMessage.showAlertOTP(AlertType.ERROR, "Lỗi xác thực", "OTP hết hạn",
                        "Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới.");
            }
        }));
        otpCountdownFor.setCycleCount(otpValidSecondsFor);
        otpCountdownFor.play();
    }

    private void openMainWindow(String fxml, Stage currentStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(ResourceUtil.getFxml(fxml));
        Parent parent = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(parent));
        stage.show();
        currentStage.close();
    }

    public void loginBtn(ActionEvent event) {
        try {
            AuthResult result = authService.login(si_username.getText(), si_password.getText());
            if (!result.isSuccess()) {
                AlertMessage.showAlert(AlertType.ERROR, "Thông báo lỗi", "Đăng nhập thất bại", result.getMessage());
                return;
            }
            loginSuccessfull = true;
            Session.setUserID(result.getUser().getUserId());
            Session.setUserName(result.getUser().getUserName());
            AlertMessage.showAlert(AlertType.INFORMATION, "Thông báo", "Đăng nhập thành công", result.getMessage());
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            if ("user".equalsIgnoreCase(result.getUser().getUserType())) {
                new UserSessionManager().login(Session.getUserName());
                openMainWindow("MainLayout.fxml", currentStage);
            } else {
                new AdminSessionManager().initAdminQueue();
                openMainWindow("MainLayoutAdmin.fxml", currentStage);
            }
        } catch (SQLException e) {
            AppLogger.error(LOGGER, "Đăng nhập thất bại do lỗi database", e);
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi hệ thống", "Không thể đăng nhập",
                    "Hệ thống đang gặp lỗi khi xác thực tài khoản. Vui lòng thử lại.");
        } catch (IOException e) {
            AppLogger.error(LOGGER, "Không thể chuyển màn hình sau đăng nhập", e);
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi hệ thống", "Không thể mở giao diện chính",
                    "Đăng nhập thành công nhưng không thể mở giao diện tiếp theo.");
        }
    }

    public static boolean isValidEmai(String email) {
        return ValidationUtil.isValidEmail(email);
    }

    public static boolean isValidPassword(String password) {
        return ValidationUtil.isValidPassword(password);
    }

    public static String hashPassword(String password) {
        return new AuthService().getPasswordService().hashPassword(password);
    }

    public void regBtn() {
        RegistrationRequest request = new RegistrationRequest(trim(su_username.getText()), trim(su_email.getText()),
                trim(su_fullName.getText()), su_password.getText());
        if (su_OTP.getText() == null || su_OTP.getText().trim().isEmpty()) {
            AlertMessage.showAlert(AlertType.ERROR, "Thông báo lỗi", "Thiếu OTP", "Vui lòng nhập mã OTP.");
            return;
        }
        if (!su_OTP.getText().trim().equals(generatedOTPRegister)) {
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "OTP không hợp lệ",
                    "Mã OTP bạn nhập không đúng. Vui lòng thử lại.");
            return;
        }
        if (isOTPExpiredRe) {
            btnReceive.setDisable(false);
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Mã OTP hết hạn", "Mã OTP đã hết hạn, vui lòng lấy lại.");
            return;
        }

        try {
            String validationMessage = authService.validateRegistration(request);
            if (validationMessage != null) {
                AlertMessage.showAlert(AlertType.ERROR, "Lỗi nhập liệu", "Đăng ký thất bại", validationMessage);
                return;
            }
            authService.register(request);
            AlertMessage.showAlert(AlertType.INFORMATION, "Thông báo", "Đăng ký tài khoản thành công",
                    "Tài khoản đã được đăng ký thành công!");
            resetRegistrationForm();
            slideToLogin();
        } catch (SQLException e) {
            AppLogger.error(LOGGER, "Đăng ký tài khoản thất bại", e);
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi hệ thống", "Không thể đăng ký tài khoản",
                    "Hệ thống đang gặp lỗi khi lưu tài khoản. Vui lòng thử lại.");
        }
    }

    public void switchForgotPass() {
        fp_questionForm.setVisible(true);
        si_loginForm.setVisible(false);
    }

    public void proceedBtn() {
        if (trim(fp_username.getText()).isEmpty() || trim(fp_OTP.getText()).isEmpty()) {
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Thiếu thông tin",
                    "Vui lòng điền đầy đủ tên người dùng và OTP.");
            return;
        }
        if (!fp_OTP.getText().trim().equals(generatedOTPForgotPass)) {
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Mã OTP không hợp lệ",
                    "Mã OTP bạn nhập không đúng. Vui lòng kiểm tra lại.");
            return;
        }
        if (isOTPExpiredFor) {
            btnReceiveForgot.setDisable(false);
            AlertMessage.showAlertOTP(AlertType.ERROR, "Lỗi", "Mã OTP hết hạn", "Mã OTP đã hết hạn, vui lòng lấy lại.");
            return;
        }
        try {
            if (!authService.existsByUserName(trim(fp_username.getText()))) {
                AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Thông tin không chính xác",
                        "Tên người dùng không tồn tại. Vui lòng kiểm tra lại.");
                return;
            }
            np_newPassForm.setVisible(true);
            fp_questionForm.setVisible(false);
            if (otpCountdownFor != null) {
                otpCountdownFor.stop();
            }
            btnReceiveForgot.setDisable(false);
            fp_countdown_forgot.setText("");
            fp_email.clear();
            fp_OTP.clear();
        } catch (SQLException e) {
            AppLogger.error(LOGGER, "Không thể kiểm tra tài khoản quên mật khẩu", e);
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi hệ thống", "Không thể xác minh tài khoản",
                    "Hệ thống đang gặp lỗi khi kiểm tra tài khoản. Vui lòng thử lại.");
        }
    }

    public void changePassBtn() {
        String newPassword = np_NewPassword.getText();
        String confirmPassword = np_newConfirmPassword.getText();
        if (trim(newPassword).isEmpty() || trim(confirmPassword).isEmpty()) {
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Thiếu thông tin",
                    "Vui lòng điền đầy đủ thông tin đổi mật khẩu.");
            return;
        }
        if (!ValidationUtil.isValidPassword(newPassword)) {
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Mật khẩu không hợp lệ",
                    "Mật khẩu phải có ít nhất 8 ký tự, chứa chữ thường, chữ hoa, số và ký tự đặc biệt.");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Mật khẩu không khớp",
                    "Mật khẩu xác nhận không trùng khớp. Vui lòng kiểm tra lại.");
            return;
        }
        try {
            authService.resetPassword(trim(fp_username.getText()), newPassword);
            AlertMessage.showAlert(AlertType.INFORMATION, "Thông báo", "Đổi mật khẩu thành công",
                    "Mật khẩu của bạn đã được thay đổi thành công!");
            clearResetPasswordForm();
            si_loginForm.setVisible(true);
            np_newPassForm.setVisible(false);
            fp_username.clear();
        } catch (SQLException e) {
            AppLogger.error(LOGGER, "Đổi mật khẩu thất bại", e);
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi hệ thống", "Không thể đổi mật khẩu",
                    "Hệ thống đang gặp lỗi khi cập nhật mật khẩu. Vui lòng thử lại.");
        }
    }

    public void backToLoginForm() {
        if (otpCountdownFor != null) {
            otpCountdownFor.stop();
        }
        btnReceiveForgot.setDisable(false);
        fp_countdown_forgot.setText("");
        si_loginForm.setVisible(true);
        fp_questionForm.setVisible(false);
    }

    public void backToQuestionForm() {
        fp_questionForm.setVisible(true);
        np_newPassForm.setVisible(false);
    }

    public void switchForm(ActionEvent event) {
        TranslateTransition slider = new TranslateTransition(Duration.seconds(.5), side_form);
        if (event.getSource() == side_CreateBtn) {
            slider.setToX(300);
            slider.setOnFinished(e -> {
                side_alreadyHave.setVisible(true);
                side_CreateBtn.setVisible(false);
                fp_questionForm.setVisible(false);
                si_loginForm.setVisible(true);
                np_newPassForm.setVisible(false);
            });
        } else if (event.getSource() == side_alreadyHave) {
            slider.setToX(0);
            slider.setOnFinished(e -> {
                side_alreadyHave.setVisible(false);
                side_CreateBtn.setVisible(true);
            });
        }
        slider.play();
    }

    private String si_save_password;
    private String su_save_password;
    private String np_new_save_password;
    private String np_confirm_save_password;

    public void showPassword(String savePassword, TextField showPassword, PasswordField hiddenPassword) {
        savePassword = showPassword.getText();
        hiddenPassword.setText(savePassword);
    }

    public void hiddenPassword(String savePassword, TextField showPassword, PasswordField hiddenPassword) {
        savePassword = hiddenPassword.getText();
        showPassword.setText(savePassword);
    }

    public void openEye(TextField showPassword, PasswordField hiddenPassword, ImageView openEye, ImageView closeEye) {
        hiddenPassword.setVisible(true);
        showPassword.setVisible(false);
        closeEye.setVisible(true);
        openEye.setVisible(false);
    }

    public void closeEye(TextField showPassword, PasswordField hiddenPassword, ImageView openEye, ImageView closeEye) {
        hiddenPassword.setVisible(false);
        showPassword.setVisible(true);
        closeEye.setVisible(false);
        openEye.setVisible(true);
    }

    @FXML
    public void si_showPassword(KeyEvent event) {
        showPassword(si_save_password, si_show_password, si_password);
    }

    @FXML
    public void si_hiddenPassword(KeyEvent event) {
        hiddenPassword(si_save_password, si_show_password, si_password);
    }

    @FXML
    public void si_open_Eye_Click_On(MouseEvent event) {
        openEye(si_show_password, si_password, si_openEye, si_closeEye);
    }

    @FXML
    public void si_close_Eye_Click_On(MouseEvent event) {
        closeEye(si_show_password, si_password, si_openEye, si_closeEye);
    }

    @FXML
    public void su_showPassword(KeyEvent event) {
        showPassword(su_save_password, su_show_password, su_password);
    }

    @FXML
    public void su_hiddenPassword(KeyEvent event) {
        hiddenPassword(su_save_password, su_show_password, su_password);
    }

    @FXML
    public void su_open_Eye_Click_On(MouseEvent event) {
        openEye(su_show_password, su_password, su_openEye, su_closeEye);
    }

    @FXML
    public void su_close_Eye_Click_On(MouseEvent event) {
        closeEye(su_show_password, su_password, su_openEye, su_closeEye);
    }

    @FXML
    public void np_new_showPassword(KeyEvent event) {
        showPassword(np_new_save_password, np_NewShowPassword, np_NewPassword);
    }

    @FXML
    public void np_new_hiddenPassword(KeyEvent event) {
        hiddenPassword(np_new_save_password, np_NewShowPassword, np_NewPassword);
    }

    @FXML
    public void np_new_open_Eye_Click_On(MouseEvent event) {
        openEye(np_NewShowPassword, np_NewPassword, np_openEyeNew, np_closeEyeNew);
    }

    @FXML
    public void np_new_close_Eye_Click_On(MouseEvent event) {
        closeEye(np_NewShowPassword, np_NewPassword, np_openEyeNew, np_closeEyeNew);
    }

    @FXML
    public void np_confirm_showPassword(KeyEvent event) {
        showPassword(np_confirm_save_password, np_ConfirmShowPassword, np_newConfirmPassword);
    }

    @FXML
    public void np_confirm_hiddenPassword(KeyEvent event) {
        hiddenPassword(np_confirm_save_password, np_ConfirmShowPassword, np_newConfirmPassword);
    }

    @FXML
    public void np_confirm_open_Eye_Click_On(MouseEvent event) {
        openEye(np_ConfirmShowPassword, np_newConfirmPassword, np_openEyeConfirm, np_closeEyeConfirm);
    }

    @FXML
    public void np_confirm_close_Eye_Click_On(MouseEvent event) {
        closeEye(np_ConfirmShowPassword, np_newConfirmPassword, np_openEyeConfirm, np_closeEyeConfirm);
    }

    private void resetRegistrationForm() {
        if (otpCountdownRe != null) {
            otpCountdownRe.stop();
        }
        su_countdown.setText("");
        btnReceive.setDisable(false);
        su_username.clear();
        su_password.clear();
        su_show_password.clear();
        su_fullName.clear();
        su_email.clear();
        su_OTP.clear();
    }

    private void clearResetPasswordForm() {
        np_NewPassword.clear();
        np_newConfirmPassword.clear();
        np_NewShowPassword.clear();
        np_ConfirmShowPassword.clear();
    }

    private void slideToLogin() {
        TranslateTransition slider = new TranslateTransition(Duration.seconds(.5), side_form);
        slider.setToX(0);
        slider.setOnFinished(e -> {
            side_alreadyHave.setVisible(false);
            side_CreateBtn.setVisible(true);
        });
        slider.play();
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        si_show_password.setVisible(false);
        si_openEye.setVisible(false);
        su_show_password.setVisible(false);
        su_openEye.setVisible(false);
        np_NewShowPassword.setVisible(false);
        np_openEyeNew.setVisible(false);
        np_ConfirmShowPassword.setVisible(false);
        np_openEyeConfirm.setVisible(false);
    }
}
