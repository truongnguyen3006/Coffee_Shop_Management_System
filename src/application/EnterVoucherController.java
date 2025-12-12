package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;

public class EnterVoucherController implements Initializable {
	@FXML
	private TextField voucherTF;

	@FXML
	void confirmVoucher() {
		if(voucherTF.getText() == null || voucherTF.getText() != null) {
			AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Voucher không tồn tại", "Voucher bạn nhập không tồn tại. Vui lòng kiểm tra lại.");

		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}

}
