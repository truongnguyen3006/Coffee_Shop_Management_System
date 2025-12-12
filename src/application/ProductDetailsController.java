package application;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProductDetailsController implements Initializable {
	
    @FXML
    private Button btnDeleteProduct;

    @FXML
    private Label dateProductM;

    @FXML
    private Label descriptionProductM;

    @FXML
    private Label nameProductM;

    @FXML
    private Label priceProductM;

    @FXML
    private Label statusProductM;

    @FXML
    private Label codeProductM;
    
    @FXML
    private Label typeProductM;
    
    @FXML
    private ImageView imageProductM;
    
    private Product product;
    
    private MainLayoutAdmin mainLayoutAdmin;
  
    public void setproductManagementController(MainLayoutAdmin mainLayoutAdmin) {
    	this.mainLayoutAdmin = mainLayoutAdmin;
    }
    
    public static final DecimalFormat PRICE_FORMATTER = new DecimalFormat("#,###");
    
    public void setDetailsData(Product product) {
    	
    	this.product = product;
    	
    	nameProductM.setText(product.getProductName()); 
    	priceProductM.setText(PRICE_FORMATTER.format(product.getPrice()));   
    	typeProductM.setText(product.getTypeProduct());
    	
    	descriptionProductM.setText(product.getDescription());
    	codeProductM.setText(product.getProductCode());
    	statusProductM.setText(product.getStatus());
    	
    	if (product.getDate() != null) {
    	    Date utilDate = product.getDate(); 
    	    Instant instant = Instant.ofEpochMilli(utilDate.getTime());
    	    LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
    	    String formattedDate = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    	    dateProductM.setText(formattedDate);
    	}


        if (product.getThumbnail() != null) {
            Image image = new Image(new ByteArrayInputStream(product.getThumbnail()));
            imageProductM.setImage(image);
        }
    }
    
    @FXML
    public void switchUpdate() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddAndUpdateProduct.fxml"));
            Parent parent = loader.load();
            
            AddAndUpdateProductController controller = loader.getController();
            // Truyền tham chiếu productManagementController sang form add/update
            controller.setProductManagementController(this.mainLayoutAdmin);
            // Truyền thông tin sản phẩm hiện tại để hiển thị trong form update
            controller.setProduct(product);;
            controller.lbAddAndUpdate().setText("Cập nhật thông tin sản phẩm");
            Stage updateStage = new Stage();
            updateStage.setScene(new Scene(parent));
            updateStage.initModality(Modality.APPLICATION_MODAL);
            updateStage.showAndWait();
            
            // Sau khi cửa sổ update đóng, lấy đối tượng sản phẩm cập nhật từ controller
            Product updatedProduct = controller.getUpdatedProduct();
            if (updatedProduct != null) {
                // Cập nhật lại thông 	tin trên trang chi tiết
                setDetailsData(updatedProduct);
                // Cập nhật biến product cho các lần update sau
                this.product = updatedProduct;
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    public void handleDeleteProduct() {
        try (Connection connect = Database.connect()) {
            // Truy vấn trạng thái của sản phẩm dựa vào productID
            String sqlStatus = "SELECT status FROM products WHERE productID = ?";
            PreparedStatement prepare = connect.prepareStatement(sqlStatus);
            prepare.setInt(1, product.getProductID());
            ResultSet rs = prepare.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");
                if ("Dừng hoạt động".equals(status)) {
                    btnDeleteProduct.setDisable(true);
                    AlertMessage.showAlert(AlertType.INFORMATION, "Thông báo", "Sản phẩm không thể xóa", "Sản phẩm này đang ở trạng thái 'Dừng hoạt động' và không thể xóa.");
                    return;
                }
            }
            rs.close();
            prepare.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Nếu sản phẩm không ở trạng thái "Dừng hoạt động", tiến hành xóa
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText( "Bạn có chắc muốn xóa?");
        alert.setContentText("Xác nhận xóa đối tượng này?");
        alert.initModality(Modality.APPLICATION_MODAL);
        ButtonType buttonTypeYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeCancel);
        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent() && result.get() == buttonTypeYes) {
            int productID = product.getProductID();
            ProductDAO productDAO = new ProductDAO();
            boolean isDelete = productDAO.deleteProduct(productID);
            if(isDelete) {
            	AlertMessage.showAlert(AlertType.INFORMATION, "Thông báo", "Xóa sản phẩm thành công", "Sản phẩm đã dừng hoạt động.");
                Stage stage = (Stage) btnDeleteProduct.getScene().getWindow();
                stage.close();
                mainLayoutAdmin.reloadProductList();
            } else {
            	AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Xóa sản phẩm thất bại", "Không thể xóa sản phẩm. Vui lòng thử lại!");
            }
        }
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		
	}
	
}
