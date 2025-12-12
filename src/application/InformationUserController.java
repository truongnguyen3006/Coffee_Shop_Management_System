package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class InformationUserController implements Initializable {

	@FXML
	private TextField textFieldName;

	@FXML
	private TextField textFieldPhone;

	private CartController cartController;

//	Đăng ký callback để gọi lại CartController sau khi confirm
	public void setInformationController(CartController cartController) {
		this.cartController = cartController;
	}

	public void setInformation(String name, String phone) {
		textFieldName.setText(name);
		textFieldPhone.setText(phone);

		if (phone != null) {
			textFieldName.setDisable(true);
			textFieldPhone.setDisable(true);
		} else {
			textFieldName.setDisable(true);
		}
	}

	public static boolean isValidPhone(String phone) {
		// Regex kiểm tra số điện thoại Việt Nam cơ bản
		String regex = "^(0|\\+84)(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-5]|9[0-9])[0-9]{7}$";
		return Pattern.matches(regex, phone);
	}

	@FXML
	public void conFirmInformation() {
		String name = textFieldName.getText();
		String phone = textFieldPhone.getText();
		if (name.isEmpty() || phone == null) {
			AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Thiếu thông tin", "Vui lòng điền đầy đủ thông tin.");
		} else if (!isValidPhone(phone)) {
			AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Số điện thoại không hợp lệ", "Vui lòng nhập đúng định dạng số điện thoại.");
		} else {
			try {
				Connection connect = Database.connect();
				String checkPhone = "SELECT phoneNumber From users WHERE phoneNumber = ? AND userID <> ?";
				PreparedStatement prepareCheckPhone = connect.prepareStatement(checkPhone);
				prepareCheckPhone.setString(1, textFieldPhone.getText());
				prepareCheckPhone.setInt(2, Session.getUserID());
				ResultSet rsCheckPhone = prepareCheckPhone.executeQuery();
				if (rsCheckPhone.next()) {
					AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Số điện thoại đã được sử dụng", "Số điện thoại này đã được đăng ký trước đó. Vui lòng sử dụng số điện thoại khác.");
				} else {
//					Gọi callback để truyền dữ liệu về CartController
					if (cartController != null) {
						cartController.translateSelectInformatinUser(name, phone);
					}
					Stage currentStage = (Stage) textFieldName.getScene().getWindow();
					currentStage.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	// ham nhan
	public interface CartController {
		void translateSelectInformatinUser(String name, String phone);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
	}

}
