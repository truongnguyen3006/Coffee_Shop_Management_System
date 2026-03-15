package application.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import application.AlertMessage;
import application.Database;
import application.Session;
import application.service.AddressService;
import application.service.AddressService.District;
import application.service.AddressService.Province;
import application.service.AddressService.Ward;
import application.util.AppLogger;
import application.util.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AccountDetails implements Initializable {
    private static final Logger LOGGER = AppLogger.getLogger(AccountDetails.class);

    @FXML
    private ComboBox<District> cbDistrict;
    @FXML
    private ComboBox<Province> cbProvince;
    @FXML
    private ComboBox<Ward> cbWard;
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

    private final ObservableList<String> optionsGender = FXCollections.observableArrayList("Nam", "Nữ");
    private final AddressService addressService = new AddressService();
    private MainLayoutController mainLayoutController;

    public void setMainLayoutController(MainLayoutController controller) {
        this.mainLayoutController = controller;
    }

    @FXML
    public void switchChangePassword() {
        try {
            Parent currentRoot = textFieldEmail.getScene().getRoot();
            currentRoot.setEffect(new BoxBlur(5, 5, 3));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/ChangePassword.fxml"));
            Parent viewChangePassword = loader.load();
            Stage changePassStage = new Stage();
            changePassStage.setScene(new Scene(viewChangePassword));
            changePassStage.initModality(Modality.APPLICATION_MODAL);
            changePassStage.show();
            changePassStage.setOnHiding(event -> currentRoot.setEffect(null));
        } catch (IOException e) {
            AppLogger.error(LOGGER, "Không thể mở màn hình đổi mật khẩu", e);
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi hệ thống", "Không thể mở màn hình",
                    "Đã xảy ra lỗi khi mở giao diện đổi mật khẩu.");
        }
    }

    @FXML
    public void btnSaveChange() {
        if (textAreaAddress.getText() == null || textFieldEmail.getText() == null || cbGender.getValue() == null
                || textFieldName.getText() == null || textFieldPhone.getText() == null || cbProvince.getValue() == null
                || cbDistrict.getValue() == null || cbWard.getValue() == null || textFieldName.getText().trim().isEmpty()
                || textFieldPhone.getText().trim().isEmpty() || textAreaAddress.getText().trim().isEmpty()) {
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Thiếu thông tin", "Vui lòng điền đầy đủ thông tin.");
            return;
        }
        if (!ValidationUtil.isValidEmail(textFieldEmail.getText())) {
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Email không hợp lệ", "Vui lòng nhập đúng định dạng email.");
            return;
        }
        if (!ValidationUtil.isValidPhoneNumber(textFieldPhone.getText())) {
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Số điện thoại không hợp lệ",
                    "Vui lòng nhập đúng định dạng số điện thoại.");
            return;
        }

        try (Connection connect = Database.connect()) {
            try (PreparedStatement prepareCheckEmail = connect
                    .prepareStatement("SELECT email FROM users WHERE email = ? AND userID <> ?");
                    PreparedStatement prepareCheckPhone = connect
                            .prepareStatement("SELECT phoneNumber FROM users WHERE phoneNumber = ? AND userID <> ?");
                    PreparedStatement prepare = connect.prepareStatement(
                            "UPDATE users SET fullName = ?, phoneNumber = ?, email = ?, gender = ?, province = ?, district = ?, ward = ?, address = ? WHERE userID = ?")) {

                prepareCheckEmail.setString(1, textFieldEmail.getText().trim());
                prepareCheckEmail.setInt(2, Session.getUserID());
                try (ResultSet rsCheckEmail = prepareCheckEmail.executeQuery()) {
                    if (rsCheckEmail.next()) {
                        AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Email đã được sử dụng",
                                "Email này đã được đăng ký trước đó. Vui lòng sử dụng email khác.");
                        return;
                    }
                }

                prepareCheckPhone.setString(1, textFieldPhone.getText().trim());
                prepareCheckPhone.setInt(2, Session.getUserID());
                try (ResultSet rsCheckPhone = prepareCheckPhone.executeQuery()) {
                    if (rsCheckPhone.next()) {
                        AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Số điện thoại đã được sử dụng",
                                "Số điện thoại này đã được đăng ký trước đó. Vui lòng sử dụng số điện thoại khác.");
                        return;
                    }
                }

                District selectedDistrict = cbDistrict.getSelectionModel().getSelectedItem();
                prepare.setString(1, textFieldName.getText().trim());
                prepare.setString(2, textFieldPhone.getText().trim());
                prepare.setString(3, textFieldEmail.getText().trim());
                prepare.setString(4, cbGender.getValue());
                prepare.setString(5, cbProvince.getSelectionModel().getSelectedItem().getName());
                prepare.setString(6, AddressService.isTwoLevelDistrict(selectedDistrict) ? "" : selectedDistrict.getName());
                prepare.setString(7, cbWard.getSelectionModel().getSelectedItem().getName());
                prepare.setString(8, textAreaAddress.getText().trim());
                prepare.setInt(9, Session.getUserID());

                if (prepare.executeUpdate() > 0) {
                    AlertMessage.showAlert(AlertType.INFORMATION, "Thông báo", "Cập nhật thành công",
                            "Thông tin đã được cập nhật.");
                    Stage currentStage = (Stage) cbProvince.getScene().getWindow();
                    currentStage.close();
                    if (mainLayoutController != null) {
                        mainLayoutController.disPlayFullName();
                    }
                }
            }
        } catch (SQLException e) {
            AppLogger.error(LOGGER, "Không thể cập nhật thông tin tài khoản", e);
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi hệ thống", "Không thể cập nhật thông tin",
                    "Hệ thống đang gặp lỗi khi lưu thông tin tài khoản.");
        }
    }

    public void displayInformation() {
        try (Connection connect = Database.connect();
                PreparedStatement prepare = connect.prepareStatement("SELECT * FROM users WHERE userID = ?")) {
            prepare.setInt(1, Session.getUserID());
            try (ResultSet rs = prepare.executeQuery()) {
                if (!rs.next()) {
                    return;
                }
                textFieldName.setText(rs.getString("fullName"));
                textFieldPhone.setText(rs.getString("phoneNumber"));
                textFieldEmail.setText(rs.getString("email"));
                cbGender.setValue(rs.getString("gender"));

                String provinceName = rs.getString("province");
                String districtName = rs.getString("district");
                String wardName = rs.getString("ward");

                for (Province province : cbProvince.getItems()) {
                    if (provinceName != null && province.getName().equalsIgnoreCase(provinceName)) {
                        cbProvince.setValue(province);
                        break;
                    }
                }
                for (District district : cbDistrict.getItems()) {
                    if (AddressService.isTwoLevelDistrict(district)) {
                        cbDistrict.setValue(district);
                        continue;
                    }
                    if (districtName != null && district.getName().equalsIgnoreCase(districtName)) {
                        cbDistrict.setValue(district);
                        break;
                    }
                }
                for (Ward ward : cbWard.getItems()) {
                    if (wardName != null && ward.getName().equalsIgnoreCase(wardName)) {
                        cbWard.setValue(ward);
                        break;
                    }
                }
                textAreaAddress.setText(rs.getString("address"));
            }
        } catch (SQLException e) {
            AppLogger.error(LOGGER, "Không thể tải thông tin tài khoản", e);
        }
    }

    @FXML
    public void reload() {
        textFieldName.clear();
        textFieldPhone.clear();
        textFieldEmail.clear();
        cbGender.setValue(null);
        textAreaAddress.clear();
        cbProvince.getSelectionModel().clearSelection();
        cbDistrict.getSelectionModel().clearSelection();
        cbWard.getSelectionModel().clearSelection();
        cbDistrict.setDisable(true);
        cbWard.setDisable(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbGender.setItems(optionsGender);
        cbDistrict.setDisable(true);
        cbWard.setDisable(true);

        addressService.loadProvinces(provinces -> {
            cbProvince.setItems(FXCollections.observableArrayList(provinces));
            displayInformation();
        });

        cbProvince.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            cbDistrict.getItems().clear();
            cbWard.getItems().clear();
            cbDistrict.getSelectionModel().clearSelection();
            cbWard.getSelectionModel().clearSelection();
            cbWard.setDisable(true);

            if (newVal == null || newVal.getDistricts() == null || newVal.getDistricts().isEmpty()) {
                cbDistrict.setDisable(true);
                return;
            }
            cbDistrict.setItems(FXCollections.observableArrayList(newVal.getDistricts()));
            cbDistrict.setDisable(false);
            if (newVal.getDistricts().size() == 1 && AddressService.isTwoLevelDistrict(newVal.getDistricts().get(0))) {
                cbDistrict.getSelectionModel().select(0);
            }
        });

        cbDistrict.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            cbWard.getItems().clear();
            cbWard.getSelectionModel().clearSelection();
            if (newVal == null || newVal.getWards() == null || newVal.getWards().isEmpty()) {
                cbWard.setDisable(true);
                return;
            }
            cbWard.setItems(FXCollections.observableArrayList(newVal.getWards()));
            cbWard.setDisable(false);
        });
    }
}
