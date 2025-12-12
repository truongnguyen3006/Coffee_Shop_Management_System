package application;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import application.AddressService.District;
import application.AddressService.Province;
import application.AddressService.Ward;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AddressSelectionController {

    @FXML
    private ComboBox<Province> cbProvince;
    @FXML
    private ComboBox<District> cbDistrict;
    @FXML
    private ComboBox<Ward> cbWard;
    @FXML
    private VBox selectedAddressBox;
    @FXML
    private TextArea tAreaAddress;


    private HttpClient httpClient = HttpClient.newHttpClient();
    private Gson gson = new Gson();

    // Danh sách các tỉnh đã lấy từ API
    private List<Province> provinceList;

    @FXML
    public void initialize() {
        loadProvinces();
        cbDistrict.setDisable(true);
        cbWard.setDisable(true);

        // Khi chọn tỉnh, cập nhật danh sách Quận/Huyện
        cbProvince.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cbDistrict.getItems().clear();
                cbDistrict.setDisable(false);
                cbWard.getItems().clear();
                cbWard.setDisable(true);
                cbDistrict.getItems().addAll(newVal.getDistricts());      
            }else {
            	cbDistrict.setDisable(true);
            	cbWard.setDisable(true);
            }
           
        });

        // Khi chọn Quận/Huyện, cập nhật danh sách Phường/Xã
        cbDistrict.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cbWard.getItems().clear();
                cbWard.setDisable(false);
                cbWard.getItems().addAll(newVal.getWards());
            }else {
            	cbWard.setDisable(true);
            }
        });
    }

    // Gọi API và parse dữ liệu JSON trả về danh sách các tỉnh (với depth=3)
    private void loadProvinces() {
        String url = "https://provinces.open-api.vn/api/?depth=3";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::handleApiResponse)
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    // Xử lý phản hồi từ API
    private void handleApiResponse(String responseBody) {
        Type provinceListType = new TypeToken<List<Province>>() {}.getType();
        provinceList = gson.fromJson(responseBody, provinceListType);

        Platform.runLater(() -> {
            cbProvince.getItems().clear();
            cbProvince.getItems().addAll(provinceList);
//            displayAddress() sau khi cbProvince đã có dữ liệu
            displayAddress();
            
            if(cbProvince.getValue() != null || cbDistrict.getValue() != null
            		|| cbWard.getValue() != null || tAreaAddress.getText() != null) {
            	cbProvince.setDisable(true);
            	cbDistrict.setDisable(true);
            	cbWard.setDisable(true);
            	tAreaAddress.setDisable(true);
            }
        });
    }
    
    private CartController cartController;
    
    public void setCartController(CartController cartController) {
    	this.cartController = cartController;
    }
    
    public String addressDetails = "";

    @FXML
    public void handleSelectAddress() {
    	String address = "";
    	String province = "";
        Province selectedProvince = cbProvince.getSelectionModel().getSelectedItem();
        District selectedDistrict = cbDistrict.getSelectionModel().getSelectedItem();
        Ward selectedWard = cbWard.getSelectionModel().getSelectedItem();
        if(selectedProvince == null || selectedDistrict == null || selectedWard == null || tAreaAddress.getText() == null) {
        	AlertMessage.showAlert(AlertType.ERROR, null, null, "Vui lòng điền đầy đủ thông tin");
        }else {
        	if(selectedProvince != null) {
            	province += selectedProvince.getName();
            }
        	
            String tArea = tAreaAddress.getText();
            
            if(addressDetails != null) {
            	if(selectedProvince != null) {
                	address += selectedProvince.getName();
                	cbDistrict.setDisable(false);    
                }

                if (selectedDistrict != null) {
                    address += ", " + selectedDistrict.getName();
                    cbWard.setDisable(false);        
                }
                if (selectedWard != null) {
                    address += ", " + selectedWard.getName();     
                }
                address += ", " + addressDetails;  
            }else {
            	if(selectedProvince != null) {
                	address += selectedProvince.getName();
                	cbDistrict.setDisable(false);
                }

                if (selectedDistrict != null) {
                    address += ", " + selectedDistrict.getName();
                    cbWard.setDisable(false);    
                }
                if (selectedWard != null) {
                    address += ", " + selectedWard.getName();
                }
                address += ", " + tArea; 
            }
                
            
            if(cartController != null) {
            	cartController.translateSelectAdress(address, province);
            }
            Stage stage = (Stage) cbProvince.getScene().getWindow();
            stage.close();
        }
       
    }
    
   
    public interface CartController {
        void translateSelectAdress(String adress, String province);
    }

    // Model cho Province, District, Ward (định dạng dựa trên dữ liệu trả về từ API)
    public static class Province {
        private int code;
        private String name;
        private List<District> districts;
        
        public int getCode() { return code; }
        public String getName() { return name; }
        public List<District> getDistricts() { return districts; }
        
        @Override
        public String toString() {
            return name;
        }
    }

    public static class District {
        private int code;
        private String name;
        private List<Ward> wards;
        
        public int getCode() { return code; }
        public String getName() { return name; }
        public List<Ward> getWards() { return wards; }
        
        @Override
        public String toString() {
            return name;
        }
    }

    public static class Ward {
        private int code;
        private String name;
        
        public int getCode() { return code; }
        public String getName() { return name; }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    public void displayAddress() {
		try {
			Connection connect = Database.connect();
			String sql = "SELECT * FROM users WHERE userID = ?";
			PreparedStatement prepare = connect.prepareStatement(sql);
			prepare.setInt(1, Session.getUserID());
			ResultSet rs = prepare.executeQuery();
			if (rs.next()) {
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
				
				addressDetails = rs.getString("address");
				tAreaAddress.setText(addressDetails);	
			}
			rs.close();
			prepare.close();
			connect.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
    
}
