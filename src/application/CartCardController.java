package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.ResourceBundle;

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
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CartCardController implements Initializable {

	@FXML
	private Label topping;

	@FXML
	private Label size;

	@FXML
	private ImageView image;

	@FXML
	private Label name;

	@FXML
	private Label price;

	@FXML
	private Button btnAdd;

	@FXML
	private Button btnSub;

	@FXML
	private Label quantity;

	@FXML
	private Label quantityLabel;

	private int currentQuantity;

	private int currentUserID;

	private int currentProductID;

	private String currentProductName;

	private String currentProductSize;

	private Image currentProductImage;

	private double currentPrice;

	private String currentCode;

	private String currentToppingHash;
	
	private String currentToppingDisplay;

	private Parent rootNode;

	private int currentCartID;
	
	public static final DecimalFormat PRICE_FORMATTER = new DecimalFormat("#,###");

	public void setUserID(int userID) {
		this.currentUserID = userID;
	}

	public void setRootNode(Parent root) {
		this.rootNode = root;
	}

	private CartController cartController;

	public void setCartController(CartController cartController) {
		this.cartController = cartController;
	}

	public void setData(String productSize, String productName, Image productImage, double productPrice, int productID,
			int quantity, String toppingHash, String toppingDisplay, String productCode, int currentCartID) {
		this.currentProductID = productID;
		this.currentQuantity = quantity;
		this.currentPrice = productPrice;
		this.currentCode = productCode;
		this.currentProductName = productName;
		this.currentProductImage = productImage;
		this.currentProductSize = productSize;
		this.currentToppingHash = toppingHash;
		this.currentToppingDisplay = toppingDisplay;
		this.currentCartID = currentCartID;

		size.setText(productSize);
		name.setText(productName);
		image.setImage(productImage);
//		price.setText(String.format("%.3f", currentPrice * currentQuantity));
		price.setText(PRICE_FORMATTER.format(currentPrice * currentQuantity));
		quantityLabel.setText(String.valueOf(currentQuantity));
		if (toppingDisplay != null && !toppingDisplay.isEmpty()) {
			topping.setText(toppingDisplay);
		} else {
			topping.setText("Không topping");
		}
	}

	public double getTotalPrice() {
		return currentPrice * currentQuantity;
	}
	
	public int getQuantity() {
		return currentQuantity;
	}
	
	public String getProductName() {
		return currentProductName;
	}
	
	public String getToppingName() {
		return currentToppingDisplay;
	}
	
	public String getSize() {
		return currentProductSize;
	}
	
	public String toppingName() {
		return currentToppingDisplay;
	}

	public void updateFromDetail(String newSize, int newQuantity, String newToppingNames, double newUnitPrice) {
		this.currentProductSize = newSize;
		this.currentQuantity = newQuantity;
		this.currentToppingDisplay = newToppingNames;
		this.currentPrice = newUnitPrice; // đã tính (bao gồm phụ thu size + topping)
		size.setText(newSize);
		quantityLabel.setText(String.valueOf(newQuantity));
		topping.setText(newToppingNames);
//		price.setText(String.format("%.3f", newUnitPrice * newQuantity));
		price.setText(PRICE_FORMATTER.format(newUnitPrice * newQuantity));
		if (cartController != null) {
			cartController.updateTotalPrice();
		}
	}

	@FXML
	public void increaseQuantity() {
		try {
			Connection connect = Database.connect();
			String sql = "UPDATE carts SET quantity = quantity + 1 WHERE userID = ? AND productID = ? AND size = ? AND toppingHash = ?";
			PreparedStatement prepare = connect.prepareStatement(sql);
			prepare.setInt(1, currentUserID);
			prepare.setInt(2, currentProductID);
			prepare.setString(3, currentProductSize);
			prepare.setString(4, currentToppingHash);
			int rows = prepare.executeUpdate();
			if (rows > 0) {
				currentQuantity++;
				quantityLabel.setText(String.valueOf(currentQuantity));
				// Cập nhật giá theo đơn giá (đã bao gồm phụ thu size và topping)
//				price.setText(String.format("%.3f", currentPrice * currentQuantity));
				price.setText(PRICE_FORMATTER.format(currentPrice * currentQuantity));
				if (cartController != null) {
					cartController.updateTotalPrice();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void decreaseQuantity() {
		try {
			if (currentQuantity > 1) {
				Connection connect = Database.connect();
				String sql = "UPDATE carts SET quantity = quantity - 1 WHERE userID = ? AND productID = ? and size = ? AND toppingHash = ?  ";
				PreparedStatement prepare = connect.prepareStatement(sql);
				prepare.setInt(1, currentUserID);
				prepare.setInt(2, currentProductID);
				prepare.setString(3, currentProductSize);
				prepare.setString(4, currentToppingHash);
				int rows = prepare.executeUpdate();
				if (rows > 0) {
					currentQuantity--;
					quantityLabel.setText(String.valueOf(currentQuantity));
//					price.setText(String.format("%.3f", currentPrice * currentQuantity));
					price.setText(PRICE_FORMATTER.format(currentPrice * currentQuantity));
					if (cartController != null) {
						cartController.updateTotalPrice();
					}
				}
			} else if (currentQuantity == 1) {
				deleteProductItem();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	public void updateProductItem() {
		try {
			Parent currentRoot = btnAdd.getScene().getRoot();
			BoxBlur blur = new BoxBlur(5, 5, 3);
			currentRoot.setEffect(blur);
			
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("DetailUpdateProduct.fxml"));
			Parent updateRoot = loader.load();

			DetailUpdateProductController detailUpdateProductController = loader.getController();
			detailUpdateProductController.setCartItemController(this);

			detailUpdateProductController.setDataCartItemUpdate(currentProductName, currentProductImage, currentCode,
					currentPrice, currentQuantity, currentProductSize, currentToppingHash, currentToppingDisplay, currentCartID);
			
			Stage updateStage = new Stage();
			updateStage.setScene(new Scene(updateRoot));
			updateStage.initOwner(btnAdd.getScene().getWindow());
//			updateStage.setTitle("Cập nhật sản phẩm");
			updateStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
			// Khi cửa sổ update đóng lại, gỡ hiệu ứng blur
			updateStage.setOnHiding(event -> currentRoot.setEffect(null));

			updateStage.showAndWait();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	public void deleteProductItem() {
		try {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Xác nhận xóa");
			alert.setTitle("Lỗi");
			alert.setContentText("Bạn có chắc muốn xóa sản phẩm");
			ButtonType buttonYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
			ButtonType buttonCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
			alert.getButtonTypes().setAll(buttonYes, buttonCancel);
			Optional<ButtonType> result = alert.showAndWait();

			if (result.isPresent() && result.get() == buttonYes) {
				Connection connect = Database.connect();
				String sql = "DELETE FROM carts WHERE userID = ? AND productID = ? AND size = ? AND toppingHash = ? ";
				PreparedStatement prepare = connect.prepareStatement(sql);
				prepare.setInt(1, currentUserID);
				prepare.setInt(2, currentProductID);
				prepare.setString(3, currentProductSize);
				prepare.setString(4, currentToppingHash);
				int affectRow =  prepare.executeUpdate();
				((VBox) rootNode.getParent()).getChildren().remove(rootNode);
//				price.setText(String.format("%.3f", currentPrice * currentQuantity));
				price.setText(PRICE_FORMATTER.format(currentPrice * currentQuantity));
				if (cartController != null) {
					cartController.updateTotalPrice();
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getCartID() {
		return currentCartID;
	}

	public int getProductID() {
		return currentProductID;
	}
	
	public double getUnitPrice() {
		return currentPrice;
	}

	public CartController getCartController() {
		return cartController;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
	}

}
