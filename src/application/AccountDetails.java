package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import application.AddressService.District;
import application.AddressService.Province;
import application.AddressService.Ward;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.BoxBlur;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AccountDetails implements Initializable {

	@FXML
	private ComboBox<AddressService.District> cbDistrict;

	@FXML
	private ComboBox<AddressService.Province> cbProvince;

	@FXML
	private ComboBox<AddressService.Ward> cbWard;

	@FXML
	private TextArea textAreaAddress;

	@FXML
	private TextField textFieldEmail;

	@FXML
	private ComboBox<String> cbGender;

	@FXML
	private TextField textFieldName;

	@FXML
	private TextField textFieldPhone;

	private ObservableList<String> Optionsgender = FXCollections.observableArrayList("Nam", "Nữ");
	
	public MainLayoutController mainLayoutController;
	
	public void setMainLayoutController(MainLayoutController controller) {
		this.mainLayoutController = controller;
	}

	@FXML
	public void switchChangePassword() {
		try { 
    		Parent currentRoot = textFieldEmail.getScene().getRoot();
    		BoxBlur blur = new BoxBlur(5, 5, 3);
    		currentRoot.setEffect(blur);	
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ChangePassword.fxml"));
			Parent viewChangePassword = loader.load();
			Stage changePassStage = new Stage();
			changePassStage.setScene(new Scene(viewChangePassword));
			changePassStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
			changePassStage.show();
			changePassStage.setOnHiding(event -> currentRoot.setEffect(null));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
	private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);
	public static boolean isValidEmai(String email) {
		if (email == null) {
			return false;
		}
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
	
	public static boolean isValidPhone(String phone) {
        // Regex kiểm tra số điện thoại Việt Nam cơ bản
        String regex = "^(0|\\+84)(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-5]|9[0-9])[0-9]{7}$";
        return Pattern.matches(regex, phone);
    }

	@FXML
	public void btnSaveChange() {
		if (textAreaAddress.getText() == null || textFieldEmail.getText() == null
				|| cbGender.getValue() == null || textFieldName.getText()== null
				|| textFieldPhone.getText().isEmpty() || cbProvince.getValue() == null
				|| cbDistrict.getValue() == null || cbWard.getValue() == null) {

			AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Thiếu thông tin", "Vui lòng điền đầy đủ thông tin.");
		}else if(!isValidEmai(textFieldEmail.getText())) {
			AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Email không hợp lệ", "Vui lòng nhập đúng định dạng email.");
		}else if(!isValidPhone(textFieldPhone.getText())) {
			AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Số điện thoại không hợp lệ", "Vui lòng nhập đúng định dạng số điện thoại.");
		}
		else {
			try {
				Connection connect = Database.connect();

				String checkEmail = "SELECT email From users WHERE email = ? AND userID <> ?";
				PreparedStatement prepareCheckEmail = connect.prepareStatement(checkEmail);
				prepareCheckEmail.setString(1, textFieldEmail.getText());
				prepareCheckEmail.setInt(2, Session.getUserID());
				ResultSet rsCheckEmail = prepareCheckEmail.executeQuery();

				String checkPhone = "SELECT phoneNumber From users WHERE phoneNumber = ? AND userID <> ?";
				PreparedStatement prepareCheckPhone = connect.prepareStatement(checkPhone);
				prepareCheckPhone.setString(1, textFieldPhone.getText());
				prepareCheckPhone.setInt(2, Session.getUserID());
				ResultSet rsCheckPhone = prepareCheckPhone.executeQuery();

				if (rsCheckEmail.next()) {
					AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Email đã được sử dụng", "Email này đã được đăng ký trước đó. Vui lòng sử dụng email khác.");
				} else if (rsCheckPhone.next()) {
					AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Số điện thoại đã được sử dụng", "Số điện thoại này đã được đăng ký trước đó. Vui lòng sử dụng số điện thoại khác.");
				} else {
					String sql = "UPDATE users SET fullName = ?, phoneNumber = ?, email = ? , gender = ?,"
							+ "province = ? , district = ?, ward = ?, address = ?" + "WHERE userID = ?";
					PreparedStatement prepare = connect.prepareStatement(sql);
					prepare.setString(1, textFieldName.getText());
					prepare.setString(2, textFieldPhone.getText());
					prepare.setString(3, textFieldEmail.getText());
					prepare.setString(4, cbGender.getValue());
					prepare.setString(5, cbProvince.getSelectionModel().getSelectedItem().getName());
					prepare.setString(6, cbDistrict.getSelectionModel().getSelectedItem().getName());
					prepare.setString(7, cbWard.getSelectionModel().getSelectedItem().getName());
					prepare.setString(8, textAreaAddress.getText());
					prepare.setInt(9, Session.getUserID());

					int rows = prepare.executeUpdate();
					if (rows > 0) {
						AlertMessage.showAlert(AlertType.INFORMATION, "Thông báo", "Cập nhật thành công", "Thông tin đã được cập nhật.");
						Stage currentStage = (Stage) cbProvince.getScene().getWindow();
						currentStage.close();
						mainLayoutController.disPlayFullName();
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void displayInformation() {
		try {
			Connection connect = Database.connect();
			String sql = "SELECT * FROM users WHERE userID = ?";
			PreparedStatement prepare = connect.prepareStatement(sql);
			prepare.setInt(1, Session.getUserID());
			ResultSet rs = prepare.executeQuery();
			if (rs.next()) {
				textFieldName.setText(rs.getString("fullName"));
				textFieldPhone.setText(rs.getString("phoneNumber"));
				textFieldEmail.setText(rs.getString("email"));
				cbGender.setValue(rs.getString("gender"));

				// Lấy giá trị tên từ DB
				String provinceName = rs.getString("province");
				String districtName = rs.getString("district");
				String wardName = rs.getString("ward");

				// Chuyển đổi giá trị thành đối tượng Province bằng cách tìm trong ComboBox
				for (Province p : cbProvince.getItems()) {
					if (p.getName().equalsIgnoreCase(provinceName)) {
						cbProvince.setValue(p);
						break;
					}
				}

				for (District d : cbDistrict.getItems()) {
					if (d.getName().equalsIgnoreCase(districtName)) {
						cbDistrict.setValue(d);
						break;
					}
				}

				for (Ward w : cbWard.getItems()) {
					if (w.getName().equalsIgnoreCase(wardName)) {
						cbWard.setValue(w);
						break;
					}
				}

				textAreaAddress.setText(rs.getString("address"));
			}
			rs.close();
			prepare.close();
			connect.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void reload() {
		textFieldName.clear();
		textFieldPhone.clear();
		textFieldEmail.clear();
		cbGender.setValue("");
		textAreaAddress.clear();

		cbProvince.getSelectionModel().clearSelection();
		cbDistrict.getSelectionModel().clearSelection();
		cbWard.getSelectionModel().clearSelection();

		cbDistrict.setDisable(true);
		cbWard.setDisable(true);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		cbGender.setItems(Optionsgender);
		cbDistrict.setDisable(true);
		cbWard.setDisable(true);

		AddressService addressService = new AddressService();
		addressService.loadProvinces(provinces -> {
			cbProvince.setItems(FXCollections.observableArrayList(provinces));
			displayInformation();
		});

		cbProvince.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal != null) {
				cbDistrict.setItems(FXCollections.observableArrayList(newVal.getDistricts()));
				cbDistrict.getSelectionModel().clearSelection();
				cbDistrict.setDisable(false);

				cbWard.getSelectionModel().clearSelection();
				cbWard.setDisable(true);
			} else {
				// Nếu không chọn province thì disable district và ward
				cbDistrict.getSelectionModel().clearSelection();
				cbDistrict.setDisable(true);
				cbWard.getSelectionModel().clearSelection();
				cbWard.setDisable(true);
			}
		});

		cbDistrict.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal != null) {
				cbWard.setItems(FXCollections.observableArrayList(newVal.getWards()));
				cbWard.getSelectionModel().clearSelection();
				// Enable ward khi có district được chọn
				cbWard.setDisable(false);
			} else {
				// Nếu không chọn district thì disable ward
				cbWard.getSelectionModel().clearSelection();
				cbWard.setDisable(true);
			}
		});

	}
}
