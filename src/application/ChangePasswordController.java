package application;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ChangePasswordController implements Initializable {

	@FXML
	private TextField tfCurrentPassword;

	@FXML
	private TextField tfNewPassword;

	@FXML
	private TextField tfNewPasswordAgain;
	
	public static String hashPassword(String password) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			  byte[] hashedBytes  = digest.digest(password.getBytes("UTF-8"));
			  
			  // Chuyển đổi byte[] thành chuỗi hex
			  StringBuilder sb = new StringBuilder();
			  for(byte b : hashedBytes) {
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

	@FXML
	void confirmChangePassword() {
		try {
			Connection connect = Database.connect();
			String passwordsql = "SELECT password FROM users WHERE userID = ?";
			PreparedStatement prepare = connect.prepareStatement(passwordsql);
			prepare.setInt(1, Session.getUserID());
			ResultSet rsPassword = prepare.executeQuery();
			if(rsPassword.next()) {
				String passWordDB = rsPassword.getString("password");
				String currentPassHash = hashPassword(tfCurrentPassword.getText());
				if(passWordDB.equals(currentPassHash)) {
					if(tfNewPassword.getText().equalsIgnoreCase(tfNewPasswordAgain.getText())){
						String sqlUpdatePass = "UPDATE users SET password = ? WHERE userID = ?";
						PreparedStatement prepareUpdatePass = connect.prepareStatement(sqlUpdatePass);
						String hashNewPass = hashPassword(tfNewPassword.getText());
						prepareUpdatePass.setString(1, hashNewPass);
						prepareUpdatePass.setInt(2, Session.getUserID());
						
						int rows = prepareUpdatePass.executeUpdate();
						if(rows > 0) {
							AlertMessage.showAlert(AlertType.INFORMATION, "Thông báo", "Đổi mật khẩu thành công", "Mật khẩu của bạn đã được thay đổi thành công.");
							Stage currentStage = (Stage) tfCurrentPassword.getScene().getWindow();
							currentStage.close();
						}else {
							AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Đổi mật khẩu thất bại", "Có lỗi xảy ra khi đổi mật khẩu. Vui lòng thử lại.");
						}
					}else {
						AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Mật khẩu không khớp", "Mật khẩu mới và xác nhận mật khẩu không khớp. Vui lòng kiểm tra lại.");
					}
				}else {
					AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Mật khẩu không đúng", "Mật khẩu hiện tại bạn nhập không đúng. Vui lòng thử lại.");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}

}
