package application;

import java.io.IOException;
import java.lang.classfile.TypeAnnotation.FormalParameterTarget;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.io.ByteArrayInputStream;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.TilePane;
import javafx.scene.shape.Rectangle;

public class ProductDetailsCardController implements Initializable {

	@FXML
	private FlowPane listHintProduct;

	@FXML
	private ImageView productDetailImage;

	@FXML
	private Label productDetailName;

	@FXML
	private Label productDetailPrice;

	@FXML
	private Label productDetailDescription;

	@FXML
	private Label productDetailsCode;

	@FXML
	private Button btnAdd;

	@FXML
	private Button btnAddToCart;

	@FXML
	private Button btnSub;

	@FXML
	private Label quantity;

	@FXML
	private Button btnL;

	@FXML
	private Button btnM;

	@FXML
	private Button btnS;

	@FXML
	private Label price0;

	@FXML
	private Label price10;

	@FXML
	private Label price6;

	@FXML
	private Button btntopping1;

	@FXML
	private Button btntopping10;

	@FXML
	private Button btntopping2;

	@FXML
	private Button btntopping3;

	@FXML
	private Button btntopping4;

	@FXML
	private Button btntopping5;

	@FXML
	private Button btntopping6;

	@FXML
	private Button btntopping7;

	@FXML
	private Button btntopping8;

	@FXML
	private Button btntopping9;

	private Product currentProduct;

	private int currentQuantity;

	private int currentUserID;

	public void setCurrentUserID(int userID) {
		this.currentUserID = userID;
	}
	
	public static final DecimalFormat PRICE_FORMATTER = new DecimalFormat("#,###");

	private String currentButtonSize = "S";
	
	@FXML
	public void backMenu() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Menu.fxml"));
			Parent menuView = loader.load();
			BorderPane mainLayout = (BorderPane) productDetailImage.getScene().getRoot();
			mainLayout.setCenter(menuView);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Sử dụng một Set để lưu toppingID đã chọn trong cửa sổ update
	private Set<Integer> chosenToppings = new HashSet<>();

	// Map để chuyển toppingID sang tên và giá của topping
	private static final Map<Integer, String> toppingNameMap = new HashMap<>();
	private static final Map<Integer, Integer> toppingPriceMap = new HashMap<>();
	static {
		toppingNameMap.put(1, "Thạch sương sáo");
		toppingNameMap.put(2, "Thạch Kim Quất");
		toppingNameMap.put(3, "Thạch Cà Phê");
		toppingNameMap.put(4, "Foam Phô Mai");
		toppingNameMap.put(5, "Shot Espresso");
		toppingNameMap.put(6, "Sốt Caramel");
		toppingNameMap.put(7, "Trân Châu Trắng");
		toppingNameMap.put(8, "Đào Miếng");
		toppingNameMap.put(9, "Hạt Sen");
		toppingNameMap.put(10, "Trái Vải");

		toppingPriceMap.put(1, 10000);
		toppingPriceMap.put(2, 10000);
		toppingPriceMap.put(3, 10000);
		toppingPriceMap.put(4, 10000);
		toppingPriceMap.put(5, 10000);
		toppingPriceMap.put(6, 10000);
		toppingPriceMap.put(7, 10000);
		toppingPriceMap.put(8, 10000);
		toppingPriceMap.put(9, 10000);
		toppingPriceMap.put(10, 10000);
	}

	private String computeToppingHash(Set<Integer> chosenToppings) {
		// Ví dụ đơn giản: sắp xếp các toppingID và nối chúng thành chuỗi
		List<Integer> list = new ArrayList<>(chosenToppings);
		Collections.sort(list);
		StringBuilder sb = new StringBuilder();
		for (Integer id : list) {
			sb.append(id).append("-");
		}
		return sb.toString();
	}


	@FXML
	public void addToCart() {
	    try {
	        Alert alert = new Alert(AlertType.CONFIRMATION);
	        alert.setHeaderText("Bạn có chắc muốn thêm sản phẩm vào giỏ hàng?");
	        alert.setTitle("Xác nhận thêm");
	        alert.setContentText("Xác nhận thêm sản phẩm vào giỏ hàng?");
	        ButtonType buttonYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
	        ButtonType buttonCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
	        alert.getButtonTypes().setAll(buttonYes, buttonCancel);
	        Optional<ButtonType> result = alert.showAndWait();

	        if (result.isPresent() && result.get() == buttonYes) {
	            Connection connect = Database.connect();
	            Set<Integer> chosenToppingsCopy = new HashSet<>(chosenToppings);
	            String toppingHash = computeToppingHash(chosenToppingsCopy);
	            // CHANGED: Nếu không có topping nào được chọn, đặt toppingHash thành "No topping"
	            if (toppingHash.isEmpty()) {
	                toppingHash = "Không topping";
	            }

	            // CHANGED: Kiểm tra xem đã có sản phẩm trùng với cùng userID, productID, size và toppingHash hay chưa
	            String checkSql = "SELECT cartID, quantity FROM carts WHERE userID = ? AND productID = ? AND size = ? AND toppingHash = ?";
	            PreparedStatement checkStmt = connect.prepareStatement(checkSql);
	            checkStmt.setInt(1, currentUserID);
	            checkStmt.setInt(2, currentProduct.getProductID());
	            checkStmt.setString(3, currentButtonSize);
	            checkStmt.setString(4, toppingHash);
	            ResultSet rsCheck = checkStmt.executeQuery();

	            if (rsCheck.next()) {
	                // Nếu tìm thấy, cập nhật số lượng (cộng thêm số lượng hiện tại)
	                int cartID = rsCheck.getInt("cartID");
	                int existingQuantity = rsCheck.getInt("quantity");
	                String updateSql = "UPDATE carts SET quantity = ? WHERE cartID = ?";
	                PreparedStatement updateStmt = connect.prepareStatement(updateSql);
	                updateStmt.setInt(1, existingQuantity + currentQuantity);
	                updateStmt.setInt(2, cartID);
	                updateStmt.executeUpdate();
	            } else {
	                // Nếu không tìm thấy, insert dòng mới
	                String sql = "INSERT INTO carts(userID, productID, quantity, size, toppingHash) VALUES(?,?,?,?,?)";
	                PreparedStatement prepare = connect.prepareStatement(sql);
	                prepare.setInt(1, currentUserID);
	                prepare.setInt(2, currentProduct.getProductID());
	                prepare.setInt(3, currentQuantity);
	                prepare.setString(4, currentButtonSize);
	                prepare.setString(5, toppingHash);
	                prepare.executeUpdate();
	            }

	            // Tiếp tục lấy cartID và chèn topping vào bảng cart_toppings như cũ
	            String findCartID = "SELECT cartID FROM carts WHERE userID = ? and productID = ? and size = ? and toppingHash = ?";
	            PreparedStatement findCardIDPrepare = connect.prepareStatement(findCartID);
	            findCardIDPrepare.setInt(1, currentUserID);
	            findCardIDPrepare.setInt(2, currentProduct.getProductID());
	            findCardIDPrepare.setString(3, currentButtonSize);
	            findCardIDPrepare.setString(4, toppingHash);
	            ResultSet rs = findCardIDPrepare.executeQuery();
	            int cartID = 0;
	            if (rs.next()) {
	                cartID = rs.getInt("cartID");
	            }

	            String insertTopping = "INSERT IGNORE INTO cart_toppings (cartID, toppingID) VALUES (?,?)";
	            PreparedStatement insertToppingPrepare = connect.prepareStatement(insertTopping);
	            for (Integer toppingID : chosenToppings) {
	                insertToppingPrepare.setInt(1, cartID);
	                insertToppingPrepare.setInt(2, toppingID);
	                insertToppingPrepare.executeUpdate();
	            }

	            AlertMessage.showAlert(AlertType.INFORMATION, "Thông báo", "Thêm sản phẩm thành công", "Sản phẩm đã được thêm vào giỏ hàng thành công!");

	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	private void toggleTopping(int toppingID, Button toppingButton) {
		if (chosenToppings.contains(toppingID)) {
			chosenToppings.remove(toppingID);
			// Đổi style nút về trạng thái chưa chọn
			toppingButton.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-text-fill: black;");
		} else {
			chosenToppings.add(toppingID);
			// Đổi style nút báo đã chọn
			toppingButton.setStyle("-fx-background-color: #532b12; -fx-text-fill: white;");
		}
	}

	@FXML
	private void toggleTopping1() {
		toggleTopping(1, btntopping1);
		productDetailPrice.setText(PRICE_FORMATTER.format(computeUnitPrice(currentButtonSize, computeToppingNames(chosenToppings)) * currentQuantity));
//		productDetailPrice.setText(String.format("%.3f",
//				);
	}

	@FXML
	private void toggleTopping2() {
		toggleTopping(2, btntopping2);
		productDetailPrice.setText(PRICE_FORMATTER.format(computeUnitPrice(currentButtonSize, computeToppingNames(chosenToppings)) * currentQuantity));
//		productDetailPrice.setText(String.format("%.3f",
//				computeUnitPrice(currentButtonSize, computeToppingNames(chosenToppings)) * currentQuantity));
	}

	@FXML
	private void toggleTopping3() {
		toggleTopping(3, btntopping3);
		productDetailPrice.setText(PRICE_FORMATTER.format(computeUnitPrice(currentButtonSize, computeToppingNames(chosenToppings)) * currentQuantity));
//		productDetailPrice.setText(String.format("%.3f",
//				computeUnitPrice(currentButtonSize, computeToppingNames(chosenToppings)) * currentQuantity));
	}

	@FXML
	private void toggleTopping4() {
		toggleTopping(4, btntopping4);
		productDetailPrice.setText(PRICE_FORMATTER.format(computeUnitPrice(currentButtonSize, computeToppingNames(chosenToppings)) * currentQuantity));
//		productDetailPrice.setText(String.format("%.3f",
//				computeUnitPrice(currentButtonSize, computeToppingNames(chosenToppings)) * currentQuantity));
	}

	@FXML
	private void toggleTopping5() {
		toggleTopping(5, btntopping5);
		productDetailPrice.setText(PRICE_FORMATTER.format(computeUnitPrice(currentButtonSize, computeToppingNames(chosenToppings)) * currentQuantity));
//		productDetailPrice.setText(String.format("%.3f",
//				computeUnitPrice(currentButtonSize, computeToppingNames(chosenToppings)) * currentQuantity));
	}

	@FXML
	private void toggleTopping6() {
		toggleTopping(6, btntopping6);
		productDetailPrice.setText(PRICE_FORMATTER.format(computeUnitPrice(currentButtonSize, computeToppingNames(chosenToppings)) * currentQuantity));
//		productDetailPrice.setText(String.format("%.3f",
//				computeUnitPrice(currentButtonSize, computeToppingNames(chosenToppings)) * currentQuantity));
	}

	@FXML
	private void toggleTopping7() {
		toggleTopping(7, btntopping7);
		productDetailPrice.setText(PRICE_FORMATTER.format(computeUnitPrice(currentButtonSize, computeToppingNames(chosenToppings)) * currentQuantity));
//		productDetailPrice.setText(String.format("%.3f",
//				computeUnitPrice(currentButtonSize, computeToppingNames(chosenToppings)) * currentQuantity));
	}

	@FXML
	private void toggleTopping8() {
		toggleTopping(8, btntopping8);
		productDetailPrice.setText(PRICE_FORMATTER.format(computeUnitPrice(currentButtonSize, computeToppingNames(chosenToppings)) * currentQuantity));
//		productDetailPrice.setText(String.format("%.3f",
//				computeUnitPrice(currentButtonSize, computeToppingNames(chosenToppings)) * currentQuantity));
	}

	@FXML
	private void toggleTopping9() {
		toggleTopping(9, btntopping9);
		productDetailPrice.setText(PRICE_FORMATTER.format(computeUnitPrice(currentButtonSize, computeToppingNames(chosenToppings)) * currentQuantity));
//		productDetailPrice.setText(String.format("%.3f",
//				computeUnitPrice(currentButtonSize, computeToppingNames(chosenToppings)) * currentQuantity));
	}

	@FXML
	private void toggleTopping10() {
		toggleTopping(10, btntopping10);
		productDetailPrice.setText(PRICE_FORMATTER.format(computeUnitPrice(currentButtonSize, computeToppingNames(chosenToppings)) * currentQuantity));
//		productDetailPrice.setText(String.format("%.3f",
//				computeUnitPrice(currentButtonSize, computeToppingNames(chosenToppings)) * currentQuantity));
	}

	public void setProductData(Product product) {
		currentQuantity = 1;
		this.currentProduct = product;

		// Hiển thị
		productDetailName.setText(product.getProductName());

//		productDetailPrice.setText(String.format("%.3f", product.getPrice()));
		productDetailPrice.setText(PRICE_FORMATTER.format(product.getPrice()));

		productDetailDescription.setText(product.getDescription());
		productDetailsCode.setText(product.getProductCode());
		// Nếu có ảnh, chuyển đổi byte[] sang Image và hiển thị
		if (product.getThumbnail() != null) {
			Image image = new Image(new ByteArrayInputStream(product.getThumbnail()));
			productDetailImage.setImage(image);
		}

		quantity.setText(String.valueOf(currentQuantity));
		// goi ham hien thi sp goi y o day, vi goi o init thi currentProduct chua co gia
		// tri
		try {
			displayHintProduct();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String computeToppingNames(Set<Integer> chosenToppings) {
		List<Integer> list = new ArrayList<>(chosenToppings);
		Collections.sort(list);
		StringBuilder sb = new StringBuilder();
		for (Integer id : list) {
			String toppingName = toppingNameMap.get(id);
			if (toppingName != null) {
				if (sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(toppingName);
			}
		}
		return sb.toString();
	}

	private double computeUnitPrice(String size, String toppingNames) {
		// Tính phụ thu và tổng topping ban đầu dựa trên dữ liệu hiện có
		double additional = 0;
		if ("M".equalsIgnoreCase(size)) {
			additional = 6000;
		} else if ("L".equalsIgnoreCase(size)) {
			additional = 10000;
		}
		double toppingTotal = 0;
		if (toppingNames != null && !toppingNames.equalsIgnoreCase("Không topping")) {
			toppingTotal = computeToppingTotal(toppingNames);
		}

		return currentProduct.getPrice() + additional + toppingTotal;
	}

	// Hàm helper tính tổng giá của các topping dựa trên chuỗi toppingNames
	private double computeToppingTotal(String toppingNames) {
		double total = 0;
		if (toppingNames == null || toppingNames.isEmpty()) {
			return total;
		}
		String[] toppingsArray = toppingNames.split(",\\s*");
		for (String topping : toppingsArray) {
			for (Map.Entry<Integer, String> entry : toppingNameMap.entrySet()) {
				if (entry.getValue().equalsIgnoreCase(topping.trim())) {
					total += toppingPriceMap.getOrDefault(entry.getKey(), 0);
					break;
				}
			}
		}
		return total;
	}

	public void inreaseQuantity() {
		currentQuantity++;
		quantity.setText(String.valueOf(currentQuantity));
		productDetailPrice.setText(PRICE_FORMATTER.format(computeUnitPrice(currentButtonSize, computeToppingNames(chosenToppings)) * currentQuantity));
	}

	public void decreaseQuantity() {
		if (currentQuantity > 1) {
			currentQuantity--;
			productDetailPrice.setText(PRICE_FORMATTER.format(computeUnitPrice(currentButtonSize, computeToppingNames(chosenToppings)) * currentQuantity));
			quantity.setText(String.valueOf(currentQuantity));
		}
	}

	public void btnS() {
		btnS.setStyle(
				"-fx-background-color: #532b12; -fx-border-radius: 15;  -fx-background-radius: 15; -fx-text-fill: white;");
		btnM.setStyle(
				"-fx-background-color: white;  -fx-border-radius: 15; -fx-background-radius: 15; -fx-text-fill: black;");
		btnL.setStyle(
				"-fx-background-color: white; -fx-border-radius: 15; -fx-background-radius: 15; -fx-text-fill: black;");

		currentButtonSize = "S";

		price0.setStyle(" -fx-text-fill: #532b12;");
		price6.setStyle("-fx-text-fill: black;");
		price10.setStyle(" -fx-text-fill: black;");

		productDetailPrice.setText(PRICE_FORMATTER.format(computeUnitPrice(currentButtonSize, computeToppingNames(chosenToppings)) * currentQuantity));

	}

	public void btnM() {
		btnM.setStyle(
				"-fx-background-color: #532b12; -fx-border-radius: 15; -fx-background-radius: 15; -fx-text-fill: white;");
		btnL.setStyle(
				"-fx-background-color: white; -fx-border-radius: 15; -fx-background-radius: 15; -fx-text-fill: black;");

		btnS.setStyle(
				"-fx-background-color: white; -fx-border-radius: 15; -fx-background-radius: 15; -fx-text-fill: black;");
		currentButtonSize = "M";

		price6.setStyle(" -fx-text-fill: #532b12;");
		price0.setStyle(" -fx-text-fill: black;");
		price10.setStyle("-fx-text-fill: black;");

		productDetailPrice.setText(PRICE_FORMATTER.format(computeUnitPrice(currentButtonSize, computeToppingNames(chosenToppings)) * currentQuantity));
	}

	public void btnL() {
		btnL.setStyle(
				"-fx-background-color: #532b12; -fx-border-radius: 15; -fx-background-radius: 15; -fx-text-fill: white;");
		btnM.setStyle(
				"-fx-background-color: white; -fx-border-radius: 15; -fx-background-radius: 15; -fx-text-fill: black;");
		btnS.setStyle(
				"-fx-background-color: white; -fx-border-radius: 15; -fx-background-radius: 15; -fx-text-fill: black;");

		currentButtonSize = "L";

		price10.setStyle(" -fx-text-fill: #532b12;");
		price6.setStyle("-fx-text-fill: black;");
		price0.setStyle(" -fx-text-fill: black;");

		productDetailPrice.setText(PRICE_FORMATTER.format(computeUnitPrice(currentButtonSize, computeToppingNames(chosenToppings)) * currentQuantity));
	}

	public void displayHintProduct() throws IOException {
		try {
			Connection connect = Database.connect();
			String sql = "SELECT productID, productCode, description, productName, price, typeProduct, thumbnail FROM products WHERE typeProduct = ?";
			PreparedStatement prepare = connect.prepareStatement(sql);
			prepare.setString(1, currentProduct.getTypeProduct());
			ResultSet rs = prepare.executeQuery();
			listHintProduct.getChildren().clear();
			while (rs.next()) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("HintCard.fxml"));
				Parent hintItem = loader.load();

				HintCardController hintCardController = loader.getController();

				int productID = rs.getInt("productID");
				String productCode = rs.getString("productCode");
				String description = rs.getString("description");
				String productName = rs.getString("productName");
				double productPrice = rs.getDouble("price");
				byte[] thumbnailBytes = rs.getBytes("thumbnail");
				String productType = rs.getString("typeProduct");
				Image thumbnail = null;
				if (thumbnailBytes != null) {
					thumbnail = new Image(new ByteArrayInputStream(thumbnailBytes));
				}
				Product product = new Product(productID, productCode, thumbnailBytes, productName, productPrice,
						description, productType);

				hintCardController.setData(product);
				listHintProduct.getChildren().add(hintItem);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		Rectangle clip = new Rectangle(productDetailImage.getFitWidth(), productDetailImage.getFitHeight());
		clip.setArcWidth(20);
		clip.setArcHeight(20);
		productDetailImage.setClip(clip);
		btnS.setStyle(
				"-fx-background-color: #532b12;  -fx-border-radius: 15; -fx-background-radius: 15; -fx-text-fill: white;");
		price0.setStyle("-fx-text-fill: #532b12;");

	}

}
