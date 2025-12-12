package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.io.ByteArrayInputStream;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class AddAndUpdateProductController implements Initializable {

    @FXML
    private TextField addCode;

    @FXML
    private TextArea addDescription;

    @FXML
    private ImageView addImage;

    @FXML
    private TextField addName;

    @FXML
    private TextField addPrice;

    @FXML
    private ComboBox<String> addStatus;

    @FXML
    private ComboBox<String> addTypeProduct;

    @FXML
    private Button btnAddProduct;

    @FXML
    private Button btnCancelProduct;

    @FXML
    private Button chooseImage;

    @FXML
    private Label lbAddAndUpdate;
    
	ObservableList<String> optionsStatus = FXCollections.observableArrayList("Đang hoạt động", "Dừng hoạt động");
	ObservableList<String> optionsTypeProduct = FXCollections.observableArrayList(
			"Cà phê", 
			"Americano",
			"Trái cây xay",
			"Trà trái cây",
			"Trà sữa",
			"Trà xanh - Chocolate",
			"Thức uống đá xay");

	private File selectedFile; // Lưu file được chọn

	private MainLayoutAdmin mainLayoutAdmin;

	public void setProductManagementController(MainLayoutAdmin mainLayoutAdmin) {
		this.mainLayoutAdmin = mainLayoutAdmin;
	}

	private static AddAndUpdateProductController instance;
	
	public static final DecimalFormat PRICE_FORMATTER = new DecimalFormat("#,###");

	public AddAndUpdateProductController() {
		instance = this;
	}

	public static AddAndUpdateProductController getInstance() {
		return instance;
	}
	
	private Product updatedProduct; // Lưu lại sản phẩm sau khi thêm hoặc cập nhật

	public Product getUpdatedProduct() {
		return updatedProduct;
	}

	public Button btnAdd() {
		return btnAddProduct;
	}
	
	public Label lbAddAndUpdate() {
		return lbAddAndUpdate;
	}

	@FXML
	private void chooseImage() {
		FileChooser fileChooser = new FileChooser();
//		fileChooser.setTitle("Chọn ảnh sản phẩm");
		fileChooser.getExtensionFilters()
				.addAll(new FileChooser.ExtensionFilter("Hình ảnh", "*.png", "*.jpg", "*.jpeg"));

		   Window ownerWindow = addImage.getScene().getWindow();

		    // Hiển thị hộp thoại chọn ảnh (modal)
		    selectedFile = fileChooser.showOpenDialog(ownerWindow);

		if (selectedFile != null) {
			// Hiển thị ảnh trên ImageView
			Image image = new Image(selectedFile.toURI().toString());
			addImage.setImage(image);
		}
	}

	// biến cập nhật sản phẩm
	private boolean isUpdate = false;
	private Product productToUpdate;

	// Hàm setProduct sẽ được gọi từ màn hình details khi chuyển sang chế độ cập
	// nhật
	public void setProduct(Product product) {
		this.productToUpdate = product;
		isUpdate = true;
		addName.setText(product.getProductName());
		addPrice.setText(String.valueOf((int) product.getPrice())); // hiển thị 55000
		addDescription.setText(product.getDescription());
		addStatus.setValue(product.getStatus());
		addTypeProduct.setValue(product.getTypeProduct());
		
		addCode.setText(product.getProductCode());
		addCode.setDisable(true);
		if (product.getThumbnail() != null) {
			Image image = new Image(new ByteArrayInputStream(product.getThumbnail()));
			addImage.setImage(image);
		}
	}

	@FXML
	void handleSaveProduct() {
		try {
			// Nếu người dùng chọn ảnh mới thì đọc file, nếu không thì giữ lại ảnh cũ (nếu
				// đang cập nhật)
			byte[] thumbnail = null;
			if (selectedFile != null) {
				thumbnail = java.nio.file.Files.readAllBytes(selectedFile.toPath());
			} else if (isUpdate && productToUpdate.getThumbnail() != null) {
				thumbnail = productToUpdate.getThumbnail();
			}
			
			String codeProduct = addCode.getText();
			String nameProduct = addName.getText();
			String typeProduct = addTypeProduct.getValue();
			double price = Double.parseDouble(addPrice.getText());
			String description = addDescription.getText();
			String status = addStatus.getValue();
			Date date = new Date();
			java.sql.Date sqlDate = new java.sql.Date(date.getTime());

			// Tạo đối tượng Product mới (hoặc cập nhật)
			Product product = new Product(codeProduct, thumbnail, nameProduct, typeProduct, price, description, status, sqlDate);

			ProductDAO productDAO = new ProductDAO();
			if (isUpdate) {
				
				if (productToUpdate != null) {	
					product.setProductID(productToUpdate.getProductID());
					productDAO.updateProduct(product);
					mainLayoutAdmin.reloadProductList();
					this.updatedProduct = product;
					
					AlertMessage.showAlert(AlertType.CONFIRMATION, "Xác nhận thay đổi", "Bạn có chắc chắn muốn thay đổi thông tin?", "Xác nhận thay đổi thông tin?");
					
					AlertMessage.showAlert(AlertType.INFORMATION, "Thông báo", "Thay đổi thông tin thành công", "Thông tin đã được thay đổi thành công.");

					Stage stage = (Stage) btnAddProduct.getScene().getWindow();
					stage.close();
				}
			} else {

				ProductDAO list = new ProductDAO();
				ObservableList<Product> listProduct = list.getAllProducts();
				String selectedCode = mainLayoutAdmin.getSelectedProductCode();

				for (Product productList : listProduct) {
					if ((selectedCode != null && selectedCode.equals(addCode.getText()))) {
						AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Sản phẩm đã tồn tại", "Sản phẩm này đã tồn tại. Vui lòng kiểm tra lại.");
						return; // Dừng xử lý nếu đã tồn tại
					}
				}
				productDAO.addProduct(product);
				mainLayoutAdmin.reloadProductList();
				this.updatedProduct = product;
				AlertMessage.showAlert(AlertType.CONFIRMATION, "Xác nhận thêm mới", "Bạn có chắc chắn muốn thêm sản phẩm mới?", "Xác nhận thêm sản phẩm mới?");
				Platform.runLater(() -> {
				    Stage stage = (Stage) btnAddProduct.getScene().getWindow();
				    stage.close();
				});

			}

			// Cập nhật lại danh sách sản phẩm và đóng cửa sổ
			mainLayoutAdmin.reloadProductList();
			Stage stage = (Stage) btnAddProduct.getScene().getWindow();
			stage.close();

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void handleCancelProduct() {
		AlertMessage.showAlert(AlertType.CONFIRMATION, "Xác nhận hủy", "Bạn có chắc chắn muốn hủy?", "Xác nhận hủy hành động?");
		Stage stage = (Stage) btnAddProduct.getScene().getWindow();
		stage.close();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		addStatus.setItems(optionsStatus);
		addTypeProduct.setItems(optionsTypeProduct);
	}

}
