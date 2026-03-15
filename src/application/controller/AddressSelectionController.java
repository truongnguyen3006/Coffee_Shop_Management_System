package application.controller;

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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class AddressSelectionController implements Initializable {
    private static final Logger LOGGER = AppLogger.getLogger(AddressSelectionController.class);

    @FXML
    private ComboBox<Province> cbProvince;
    @FXML
    private ComboBox<District> cbDistrict;
    @FXML
    private ComboBox<Ward> cbWard;
    @FXML
    private TextArea tAreaAddress;

    private final AddressService addressService = new AddressService();
    private CartController cartController;
    private String addressDetails = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbDistrict.setDisable(true);
        cbWard.setDisable(true);

        cbProvince.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> onProvinceChanged(newVal));
        cbDistrict.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> onDistrictChanged(newVal));

        addressService.loadProvinces(provinces -> {
            cbProvince.getItems().setAll(provinces);
            if (provinces == null || provinces.isEmpty()) {
                AlertMessage.showAlert(AlertType.ERROR, "Lỗi hệ thống", "Không tải được dữ liệu địa chỉ",
                        "Không thể lấy danh sách tỉnh/thành. Vui lòng thử lại sau.");
                return;
            }
            displayAddress();
        });
    }

    public void setCartController(CartController cartController) {
        this.cartController = cartController;
    }

    private void onProvinceChanged(Province province) {
        cbDistrict.getItems().clear();
        cbWard.getItems().clear();
        cbDistrict.getSelectionModel().clearSelection();
        cbWard.getSelectionModel().clearSelection();
        cbWard.setDisable(true);

        if (province == null || province.getDistricts() == null || province.getDistricts().isEmpty()) {
            cbDistrict.setDisable(true);
            return;
        }

        cbDistrict.getItems().addAll(province.getDistricts());
        cbDistrict.setDisable(false);
        if (province.getDistricts().size() == 1 && AddressService.isTwoLevelDistrict(province.getDistricts().get(0))) {
            cbDistrict.getSelectionModel().select(0);
        }
    }

    private void onDistrictChanged(District district) {
        cbWard.getItems().clear();
        cbWard.getSelectionModel().clearSelection();
        if (district == null || district.getWards() == null || district.getWards().isEmpty()) {
            cbWard.setDisable(true);
            return;
        }
        cbWard.getItems().addAll(district.getWards());
        cbWard.setDisable(false);
    }

    @FXML
    public void handleSelectAddress() {
        Province selectedProvince = cbProvince.getSelectionModel().getSelectedItem();
        District selectedDistrict = cbDistrict.getSelectionModel().getSelectedItem();
        Ward selectedWard = cbWard.getSelectionModel().getSelectedItem();
        String detail = tAreaAddress.getText() == null ? "" : tAreaAddress.getText().trim();

        if (selectedProvince == null || selectedDistrict == null || selectedWard == null || detail.isEmpty()) {
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Thiếu thông tin", "Vui lòng điền đầy đủ thông tin địa chỉ.");
            return;
        }

        StringBuilder address = new StringBuilder();
        address.append(selectedProvince.getName());
        if (!AddressService.isTwoLevelDistrict(selectedDistrict)) {
            address.append(", ").append(selectedDistrict.getName());
        }
        address.append(", ").append(selectedWard.getName());
        address.append(", ").append(detail);

        if (cartController != null) {
            cartController.translateSelectAdress(address.toString(), selectedProvince.getName());
        }
        Stage stage = (Stage) cbProvince.getScene().getWindow();
        stage.close();
    }

    public interface CartController {
        void translateSelectAdress(String adress, String province);
    }

    public void displayAddress() {
        try (Connection connect = Database.connect();
                PreparedStatement prepare = connect.prepareStatement("SELECT province, district, ward, address FROM users WHERE userID = ?")) {
            prepare.setInt(1, Session.getUserID());
            try (ResultSet rs = prepare.executeQuery()) {
                if (!rs.next()) {
                    return;
                }
                String provinceName = rs.getString("province");
                String districtName = rs.getString("district");
                String wardName = rs.getString("ward");
                addressDetails = rs.getString("address");
                tAreaAddress.setText(addressDetails == null ? "" : addressDetails);

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
            }
        } catch (SQLException e) {
            AppLogger.error(LOGGER, "Không thể tải địa chỉ người dùng", e);
        }
    }
}
