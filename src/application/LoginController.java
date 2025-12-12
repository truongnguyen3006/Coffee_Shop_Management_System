package application;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessageRemovedException;
import javax.mail.MessagingException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginController implements Initializable {

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

	private Timeline otpCountdownRe;

	private int otpValidSecondsRe = 60;
	private boolean isOTPExpiredRe = false;

	private Timeline otpCountdownFor;

	private int otpValidSecondsFor = 60;
	private boolean isOTPExpiredFor = false;

	private Connection connect;
	private PreparedStatement prepare;
	private ResultSet result;
	private Alert alert;

	private boolean loginSuccessfull = false;

	public boolean IsLoginSuccesfull() {
		return loginSuccessfull;
	}

	private String userName = "";

	AlertMessage alertMessage;

	String generatedOTPRegister = "";

	String generatedOTPForgotPass = "";

	@FXML
	public void GetOTP_Register() throws MessageRemovedException {
		String email = su_email.getText();

		if (!isValidEmai(email)) {
			AlertMessage.showAlert(AlertType.ERROR, "Lỗi nhập liệu", "Địa chỉ email không hợp lệ",
					"Vui lòng nhập địa chỉ email hợp lệ.");
			return;
		}
		try {
			Connection connect = Database.connect();
			String checkEmail = "SELECT email from users WHERE email= ?";
			prepare = connect.prepareStatement(checkEmail);
			prepare.setString(1, su_email.getText());
			ResultSet resultEmail = prepare.executeQuery();
			if (resultEmail.next()) {
				AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Email đã tồn tại",
						"Email bạn nhập đã được đăng ký trước đó. Vui lòng thử một email khác.");
				return;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		generatedOTPRegister = "";
		generatedOTPRegister = generateOTP();
		try {
			EmailSender.sendOTPEmail(email, generatedOTPRegister);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AlertMessage.showAlert(AlertType.INFORMATION, "Thông báo", "Gửi mã OTP thành công",
				"Mã OTP đã được gửi đến " + su_email.getText());
		isOTPExpiredRe = false;
		su_OTP.setDisable(false);
		btnReceive.setDisable(true);
		startOTPTimerRegister();
	}

	@FXML
	public void GetOTP_ForgotPass() throws MessageRemovedException {
		String email = fp_email.getText();

		if (!isValidEmai(email)) {
			AlertMessage.showAlert(AlertType.ERROR, "Lỗi nhập liệu", "Email không hợp lệ",
					"Vui lòng nhập đúng định dạng email (ví dụ: example@gmail.com).");
			return;
		}
		generatedOTPForgotPass = "";
		generatedOTPForgotPass = generateOTP();
		try {
			EmailSender.sendOTPEmail(email, generatedOTPForgotPass);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AlertMessage.showAlert(AlertType.INFORMATION, "Thông báo", "Gửi mã OTP",
				"Mã OTP đã được gửi đến " + fp_email.getText());
		isOTPExpiredFor = false;
		fp_OTP.setDisable(false);
		btnReceiveForgot.setDisable(true);
		startOTPTimerForget();
	}

	private void startOTPTimerRegister() {
		if (otpCountdownRe != null) {
			otpCountdownRe.stop();
		}
		otpValidSecondsRe = 60;
		su_countdown.setText(otpValidSecondsRe + "s");

		otpCountdownRe = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
			otpValidSecondsRe--;
			su_countdown.setText(otpValidSecondsRe + "s");
			if (otpValidSecondsRe <= 0) {
				otpCountdownRe.stop();
				isOTPExpiredRe = true;
				su_countdown.setDisable(true);
				su_countdown.setText("");
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
		fp_countdown_forgot.setText(otpValidSecondsFor + "s");

		otpCountdownFor = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
			otpValidSecondsFor--;
			fp_countdown_forgot.setText(otpValidSecondsFor + "s");
			if (otpValidSecondsFor <= 0) {
				otpCountdownFor.stop();
				isOTPExpiredFor = true;
				fp_countdown_forgot.setDisable(true);
				fp_countdown_forgot.setText("");
				btnReceiveForgot.setDisable(false);
				AlertMessage.showAlertOTP(AlertType.ERROR, "Lỗi xác thực", "Mã OTP hết hạn",
						"Mã OTP đã hết hạn. Vui lòng yêu cầu mã OTP mới.");
			}
		}));
		otpCountdownFor.setCycleCount(otpValidSecondsFor);
		otpCountdownFor.play();
	}

	// Tạo mã OTP ngẫu nhiên
	private String generateOTP() {
		SecureRandom random = new SecureRandom();
		StringBuilder otp = new StringBuilder();
		for (int i = 0; i < 6; i++) {
			otp.append(random.nextInt(10));
		}
		return otp.toString();
	}

	public void switchHome() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MainLayout.fxml"));
			Parent parent = loader.load();
			MainLayoutController mainLayoutController = loader.getController();
//			mainLayoutController.setWelComeName(userName);

			Stage homeStage = new Stage();
//			homeStage.setTitle(null);
			homeStage.setScene(new Scene(parent));
			homeStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void switchProductManagement() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MainLayoutAdmin.fxml"));
			Parent parent = loader.load();
			Stage productManagement = new Stage();
			productManagement.setTitle(null);
			productManagement.setScene(new Scene(parent));
			productManagement.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Kiểm tra user có tồn tại không
	public boolean userNameExists(String si_username) throws SQLException {
		connect = Database.connect();
		try {
			String checkUserName = "SELECT userName FROM users WHERE userName = ?";
			prepare = connect.prepareStatement(checkUserName);
			prepare.setString(1, si_username);
			result = prepare.executeQuery();
			return result.next();
		} catch (Exception e) {
			e.printStackTrace();
		}
		connect.close();
		return false;
	}

	public void loginBtn(ActionEvent event) throws SQLException {

		if (si_username.getText().isEmpty() || si_password.getText().isEmpty()) {
			AlertMessage.showAlert(AlertType.ERROR, "Thông báo lỗi", "Thông tin không đầy đủ",
					"Vui lòng điền đầy đủ thông tin!");
		} else {
			if (!userNameExists(si_username.getText().trim())) {
				AlertMessage.showAlert(AlertType.ERROR, "Thông báo lỗi", "Tên người dùng không tồn tại",
						"Tên người dùng không tồn tại. Vui lòng đăng ký!");
			} else {
				connect = Database.connect();
				String hashPassword = hashPassword(si_password.getText());
				try {
					String checkPassWord = "SELECT userID, userName, password, userType FROM users WHERE userName = ? and password = ?";
					prepare = connect.prepareStatement(checkPassWord);
					prepare.setString(1, si_username.getText().trim());
					prepare.setString(2, hashPassword);
					result = prepare.executeQuery();
					if (result.next()) {
						// truyen userID vao cho gio hang
						String userType = result.getString("userType");
						int userID = result.getInt("userID");
						userName = result.getString("userName");

						Session.setUserID(userID);
						Session.setUserName(userName);
						AlertMessage.showAlert(AlertType.INFORMATION, "Thông báo", "Đăng nhập thành công",
								"Đăng nhập thành công!");

						((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

						if (userType.equals("user")) {
							UserSessionManager userSessionManager = new UserSessionManager();
							userSessionManager.login(Session.getUserName());
							switchHome();
						} else {
							switchProductManagement();
						}

					} else {
						AlertMessage.showAlert(AlertType.ERROR, "Thông báo lỗi", "Mật khẩu không chính xác",
								"Mật khẩu không chính xác.");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				connect.close();
			}
		}
	}

	// Định nghĩa Regex cho email hợp lệ
	private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
	// Biên dịch pattern từ regex trên
	private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

	public static boolean isValidEmai(String email) {
		if (email == null) {
			return false;
		}
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	public static boolean isValidPassword(String password) {
		if (password == null) {
			return false;
		}
		String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
		return password.matches(pattern);
	}

	public static String hashPassword(String password) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashedBytes = digest.digest(password.getBytes("UTF-8"));

			// Chuyển đổi byte[] thành chuỗi hex
			StringBuilder sb = new StringBuilder();
			for (byte b : hashedBytes) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return password;
	}

	public void regBtn() throws SQLException {
		if (su_username.getText().isEmpty() || su_email.getText().isEmpty() || su_fullName.getText().isEmpty()
				|| su_password.getText().isEmpty() || su_OTP.getText().isEmpty()) {
			AlertMessage.showAlert(AlertType.ERROR, "Thông báo lỗi", "Vui lòng điền đầy đủ thông tin",
					"Vui lòng điền tất cả các trường thông tin!");
		} else {

			String regData = "INSERT INTO users (userName, email, fullName, userType, password, date)"
					+ "VALUES(?,?,?,?,?,?)";
			connect = Database.connect();

			try {
				String checkUserName = "SELECT userName, email from users WHERE  userName = ?";
				prepare = connect.prepareStatement(checkUserName);
				prepare.setString(1, su_username.getText());
				ResultSet resultUserName = prepare.executeQuery();

				String checkEmail = "SELECT email from users WHERE email= ?";
				prepare = connect.prepareStatement(checkEmail);
				prepare.setString(1, su_email.getText());
				ResultSet resultEmail = prepare.executeQuery();

				if (resultUserName.next()) {
					AlertMessage.showAlert(AlertType.ERROR, "Thông báo lỗi", "Tên người dùng đã tồn tại",
							su_username.getText() + " đã được sử dụng.");
				} else if (resultEmail.next()) {
					AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Email đã tồn tại",
							"Email bạn nhập đã được đăng ký trước đó. Vui lòng thử một email khác.");
				} else if (!isValidEmai(su_email.getText())) {
					AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Email chưa hợp lệ",
							"Vui lòng nhập một địa chỉ email hợp lệ.");
				} else if (!isValidPassword(su_password.getText())) {
					AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Mật khẩu không hợp lệ",
							"Mật khẩu phải có ít nhất 8 ký tự, chứa ít nhất 1 chữ cái thường, 1 chữ cái in hoa, 1 chữ số và 1 ký tự đặc biệt.");
				} else if (!su_OTP.getText().equals(generatedOTPRegister)) {
					AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "OTP không hợp lệ",
							"Mã OTP bạn nhập không đúng. Vui lòng thử lại.");
				} else if (isOTPExpiredRe) {
					btnReceive.setDisable(false);
					AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Mã OTP hết hạn",
							"Mã OTP đã hết hạn, vui lòng lấy lại.");
					return;
				} else {
					if (su_username.getText().equals("admin")) {
						String hasdPassword = hashPassword(su_password.getText());
						prepare = connect.prepareStatement(regData);
						prepare.setString(1, su_username.getText());
						prepare.setString(2, su_email.getText());
						prepare.setString(3, su_fullName.getText());
						prepare.setString(4, su_username.getText());
						prepare.setString(5, hasdPassword);
						prepare.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
						prepare.executeUpdate();

						AlertMessage.showAlert(AlertType.INFORMATION, "Thông báo", "Đăng ký tài khoản thành công",
								"Tài khoản đã được đăng ký thành công!");

						otpCountdownRe.stop();
						su_countdown.setText("");
						btnReceive.setDisable(false);

						su_username.clear();
						su_password.clear();
						su_fullName.clear();
						su_email.clear();
						su_OTP.clear();
					} else {
						String hasdPassword = hashPassword(su_password.getText());
						prepare = connect.prepareStatement(regData);
						prepare.setString(1, su_username.getText());
						prepare.setString(2, su_email.getText());
						prepare.setString(3, su_fullName.getText());
						prepare.setString(4, "user");
						prepare.setString(5, hasdPassword);
						prepare.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
						prepare.executeUpdate();

						AlertMessage.showAlert(AlertType.INFORMATION, "Thông báo", "Đăng ký tài khoản thành công",
								"Tài khoản của bạn đã được đăng ký thành công!");

						otpCountdownRe.stop();
						su_countdown.setText("");
						btnReceive.setDisable(false);

						su_OTP.clear();
						su_username.clear();
						su_password.clear();
						su_fullName.clear();
						su_email.clear();
					}

					TranslateTransition slider = new TranslateTransition();
					slider.setNode(side_form);
					slider.setToX(0);
					slider.setDuration(Duration.seconds(.5));
					slider.setOnFinished((ActionEvent e) -> {
						side_alreadyHave.setVisible(false);
						side_CreateBtn.setVisible(true);
					});
					slider.play();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void switchForgotPass() {
		fp_questionForm.setVisible(true);
		si_loginForm.setVisible(false);
	}

	public void proceedBtn() throws SQLException {
		if (fp_username.getText().isEmpty() || fp_OTP.getText().isEmpty()) {
			AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Vui lòng điền đầy đủ thông tin",
					"Vui lòng điền tất cả các trường thông tin!");
		} else if (!fp_OTP.getText().equals(generatedOTPForgotPass)) {
			AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Mã OTP không hợp lệ",
					"Mã OTP bạn nhập không đúng. Vui lòng kiểm tra lại.");
		} else if (isOTPExpiredFor) {
			btnReceiveForgot.setDisable(false);
			AlertMessage.showAlertOTP(AlertType.ERROR, "Lỗi", "Mã OTP hết hạn", "Mã OTP đã hết hạn, vui lòng lấy lại.");
			return;
		} else {
			String selectData = "SELECT userName, email FROM users WHERE userName = ?";
			connect = Database.connect();

			try {
				prepare = connect.prepareStatement(selectData);
				prepare.setString(1, fp_username.getText());

				result = prepare.executeQuery();

				if (result.next()) {
					np_newPassForm.setVisible(true);
					fp_questionForm.setVisible(false);

					otpCountdownFor.stop();
					btnReceiveForgot.setDisable(false);
					fp_countdown_forgot.setText("");


					fp_email.clear();
					fp_OTP.clear();
				} else {
					AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Thông tin không chính xác",
							"Thông tin bạn nhập không chính xác. Vui lòng kiểm tra lại.");
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void changePassBtn() throws SQLException {
		if (np_NewPassword.getText().isEmpty() || np_newConfirmPassword.getText().isEmpty()) {
			AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Vui lòng điền đầy đủ thông tin",
					"Vui lòng điền tất cả các trường còn trống!");
		} else {
			String hashPassword = hashPassword(np_NewPassword.getText());
			if (np_NewPassword.getText().equals(np_newConfirmPassword.getText())) {
				String getDate = "SELECT date from users WHERE userName = '" + fp_username.getText() + "'";
				connect = Database.connect();

				try {
					prepare = connect.prepareStatement(getDate);
					result = prepare.executeQuery();

					String date = "";

					if (result.next()) {
						date = result.getString("date");
					}

					String updatePass = "UPDATE users SET password = ? , date = ? WHERE userName = ?";

					prepare = connect.prepareStatement(updatePass);
					prepare.setString(1, hashPassword);
					prepare.setString(2, date);
					prepare.setString(3, fp_username.getText());
					prepare.executeUpdate();

					AlertMessage.showAlert(AlertType.INFORMATION, "Thông báo", "Đổi mật khẩu thành công",
							"Mật khẩu của bạn đã được thay đổi thành công!");

					np_NewPassword.clear();
					np_newConfirmPassword.clear();

					si_loginForm.setVisible(true);
					np_newPassForm.setVisible(false);

					np_newConfirmPassword.clear();
					np_NewPassword.clear();
					np_NewShowPassword.clear();
					np_newConfirmPassword.clear();
					fp_username.clear();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Mật khẩu không khớp",
						"Mật khẩu xác nhận không trùng khớp. Vui lòng kiểm tra lại.");
			}
		}
	}

	public void backToLoginForm() {
		if (otpCountdownFor == null) {
			btnReceiveForgot.setDisable(false);
			fp_countdown_forgot.setText("");
			si_loginForm.setVisible(true);
			fp_questionForm.setVisible(false);
		} else {
			otpCountdownFor.stop();
			btnReceiveForgot.setDisable(false);
			fp_countdown_forgot.setText("");
			si_loginForm.setVisible(true);
			fp_questionForm.setVisible(false);
		}

	}

	public void backToQuestionForm() {
		fp_questionForm.setVisible(true);
		np_newPassForm.setVisible(false);
	}

	public void switchForm(ActionEvent event) {
		TranslateTransition slider = new TranslateTransition();
		if (event.getSource() == side_CreateBtn) {
			slider.setNode(side_form);
			slider.setToX(300);
			slider.setDuration(Duration.seconds(.5));
			slider.setOnFinished((ActionEvent e) -> {
				side_alreadyHave.setVisible(true);
				side_CreateBtn.setVisible(false);

				fp_questionForm.setVisible(false);
				si_loginForm.setVisible(true);
				np_newPassForm.setVisible(false);

			});
			slider.play();
		} else if (event.getSource() == side_alreadyHave) {
			slider.setNode(side_form);
			slider.setToX(0);
			slider.setDuration(Duration.seconds(.5));
			slider.setOnFinished((ActionEvent e) -> {
				side_alreadyHave.setVisible(false);
				side_CreateBtn.setVisible(true);
			});
			slider.play();
		}
	}

	String si_save_password, su_save_password, np_new_save_password, np_confirm_save_password;

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

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
//		 TODO Auto-generated method stub
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
