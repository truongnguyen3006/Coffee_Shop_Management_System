package application;
import java.io.IOException;
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
import java.util.ResourceBundle;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class DetailUpdateProductController implements Initializable {
    
	@FXML private Label productDetailName;
	@FXML private Label productDetailsCode;
	@FXML private Label productDetailPrice;
	@FXML private Label quantityLabel;
	@FXML private ImageView productDetailImage;

	@FXML private Button btnIncreaseQuantity;
	@FXML private Button btnDecreaseQuantity;

	@FXML private Button btnS;
	@FXML private Button btnM;
	@FXML private Button btnL;

	@FXML private Button btntopping1;
	@FXML private Button btntopping2;
	@FXML private Button btntopping3;
	@FXML private Button btntopping4;
	@FXML private Button btntopping5;
	@FXML private Button btntopping6;
	@FXML private Button btntopping7;
	@FXML private Button btntopping8;
	@FXML private Button btntopping9;
	@FXML private Button btntopping10;

	@FXML private Button btnUpdate;
	@FXML private Button btnCancel;

	// Các biến lưu dữ liệu hiện tại (được truyền từ CartCardController)
	private String currentProductName;
	private String currentCode;
	private double basePrice;      // Giá cơ bản của sản phẩm (không bao gồm phụ thu)
	private int currentQuantity;
	private String currentProductSize; 
	private String currentToppingNames; // Chuỗi topping ban đầu ("No topping")
	private String currentToppingHash;
	private Image currentProductImage;
	private int currentCartID;

	// Các biến lưu trạng thái cập nhật mới
	private String updatedSize;
	private int updatedQuantity;
	
	
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
	
	public static final DecimalFormat PRICE_FORMATTER = new DecimalFormat("#,###");
	// Tham chiếu đến CartCardController (được truyền từ CartCardController)
	private CartCardController cartItemController;
	public void setCartItemController(CartCardController controller) {
	    this.cartItemController = controller;
	}

	// Phương thức truyền dữ liệu từ CartCardController sang cửa sổ update.
	// Giá truyền vào là basePrice (chưa cộng phụ thu size và topping)
	public void setDataCartItemUpdate(String name, Image image, String code, double basePrice, int quantity, String size, String toppingHash, String toppingNames, int cartID) {
	    this.currentProductName = name;
	    this.currentProductImage = image;
	    this.currentCode = code;
	    this.basePrice = basePrice;
	    this.currentQuantity = quantity;
	    this.currentProductSize = size;
	    
	    // Nếu toppingNames null hoặc rỗng, gán mặc định "No topping"
	    if(toppingNames == null || toppingNames.trim().isEmpty()){
	        toppingNames = "Không topping";
	    }
	    
	    this.currentToppingNames = toppingNames;
	    this.currentToppingHash = toppingHash;
	    this.currentCartID = cartID;
	    
	    productDetailName.setText(name);
	    productDetailsCode.setText(code);
	    
//	    productDetailPrice.setText(String.format("%.3f",basePrice * quantity));
	    productDetailPrice.setText(PRICE_FORMATTER.format(basePrice * quantity));
	    
	    if(image != null) {
	        productDetailImage.setImage(image);
	    }
	    quantityLabel.setText(String.valueOf(quantity));
	    
	    highlightSizeButton(size);
	    highlightToppingButtons(toppingNames);
	    
	    // Khởi tạo trạng thái cập nhật
	    updatedSize = size;
	    updatedQuantity = quantity;
	    // Nếu toppingNames không rỗng, parse thành toppingIDs
	    chosenToppings.clear();
	    if(toppingNames != null && !toppingNames.trim().isEmpty() && !toppingNames.equalsIgnoreCase("Không topping")){
	        String[] arr = toppingNames.split(",\\s*");
	        for(String toppingStr : arr){
	            for(Map.Entry<Integer, String> entry : toppingNameMap.entrySet()){
	                if(entry.getValue().equalsIgnoreCase(toppingStr.trim())){
	                    chosenToppings.add(entry.getKey());
	                    break;
	                }
	            }
	        }
	    }
	    highlightToppingButtons(toppingNames); 
//	    System.out.println("toppingNames " + toppingNames);
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

	private void highlightSizeButton(String size) {
	    btnS.setStyle("-fx-background-color: white; -fx-text-fill: black;");
	    btnM.setStyle("-fx-background-color: white; -fx-text-fill: black;");
	    btnL.setStyle("-fx-background-color: white; -fx-text-fill: black;");
	    if("S".equalsIgnoreCase(size)) {
	        btnS.setStyle("-fx-background-color: #532b12; -fx-text-fill: white;");
	    } else if("M".equalsIgnoreCase(size)) {
	        btnM.setStyle("-fx-background-color: #532b12; -fx-text-fill: white;");
	    } else if("L".equalsIgnoreCase(size)) {
	        btnL.setStyle("-fx-background-color: #532b12; -fx-text-fill: white;");
	    }
	}

	private void highlightToppingButtons(String toppingNames) {
	    // Reset style cho 10 nút topping
	    btntopping1.setStyle("-fx-background-color: white; -fx-text-fill: black;");
	    btntopping2.setStyle("-fx-background-color: white; -fx-text-fill: black;");
	    btntopping3.setStyle("-fx-background-color: white; -fx-text-fill: black;");
	    btntopping4.setStyle("-fx-background-color: white; -fx-text-fill: black;");
	    btntopping5.setStyle("-fx-background-color: white; -fx-text-fill: black;");
	    btntopping6.setStyle("-fx-background-color: white; -fx-text-fill: black;");
	    btntopping7.setStyle("-fx-background-color: white; -fx-text-fill: black;");
	    btntopping8.setStyle("-fx-background-color: white; -fx-text-fill: black;");
	    btntopping9.setStyle("-fx-background-color: white; -fx-text-fill: black;");
	    btntopping10.setStyle("-fx-background-color: white; -fx-text-fill: black;");
	    
	    if(toppingNames != null && !toppingNames.isEmpty()){
	        if(toppingNames.contains("Thạch sương sáo")) {
	            btntopping1.setStyle("-fx-background-color: #532b12; -fx-border-color: black; -fx-text-fill: white;");
	        }
	        if(toppingNames.contains("Thạch Kim Quất")) {
	            btntopping2.setStyle("-fx-background-color: #532b12; -fx-border-color: black; -fx-text-fill: white;");
	        }
	        if(toppingNames.contains("Thạch Cà Phê")) {
	            btntopping3.setStyle("-fx-background-color: #532b12; -fx-border-color: black; -fx-text-fill: white;");
	        }
	        if(toppingNames.contains("Foam Phô Mai")) {
	            btntopping4.setStyle("-fx-background-color: #532b12; -fx-border-color: black; -fx-text-fill: white;");
	        }
	        if(toppingNames.contains("Shot Espresso")) {
	            btntopping5.setStyle("-fx-background-color: #532b12; -fx-border-color: black; -fx-text-fill: white;");
	        }
	        if(toppingNames.contains("Sốt Caramel")) {
	            btntopping6.setStyle("-fx-background-color: #532b12; -fx-border-color: black; -fx-text-fill: white;");
	        }
	        if(toppingNames.contains("Trân Châu Trắng")) {
	            btntopping7.setStyle("-fx-background-color: #532b12; -fx-border-color: black; -fx-text-fill: white;");
	        }
	        if(toppingNames.contains("Đào Miếng")) {
	            btntopping8.setStyle("-fx-background-color: #532b12; -fx-border-color: black; -fx-text-fill: white;");
	        }
	        if(toppingNames.contains("Hạt Sen")) {
	            btntopping9.setStyle("-fx-background-color: #532b12; -fx-border-color: black; -fx-text-fill: white;");
	        }
	        if(toppingNames.contains("Trái Vải")) {
	            btntopping10.setStyle("-fx-background-color: #532b12; -fx-border-color: black; -fx-text-fill: white;");
	        }
	    }
	}

	// Các event handlers cho nút tăng, giảm số lượng
	@FXML
	public void increaseQuantity() {
	    updatedQuantity++;
	    quantityLabel.setText(String.valueOf(updatedQuantity));
//	    productDetailPrice.setText(String.format("%.3f", computeUnitPrice(updatedSize, computeToppingNames(chosenToppings)) * updatedQuantity));
	    productDetailPrice.setText(PRICE_FORMATTER.format(computeUnitPrice(updatedSize, computeToppingNames(chosenToppings)) * updatedQuantity));
	}

	@FXML
	public void decreaseQuantity() {
	    if(updatedQuantity > 1) {
	        updatedQuantity--;
	        quantityLabel.setText(String.valueOf(updatedQuantity));
	        productDetailPrice.setText(PRICE_FORMATTER.format(computeUnitPrice(updatedSize, computeToppingNames(chosenToppings)) * updatedQuantity));
	    }
	}

	// Các event handlers cho nút chọn size
	@FXML
	private void selectSizeS() {
	    updatedSize = "S";
	    highlightSizeButton("S");
	    productDetailPrice.setText(PRICE_FORMATTER.format(computeUnitPrice(updatedSize, computeToppingNames(chosenToppings)) * updatedQuantity));
	}
	@FXML
	private void selectSizeM() {
	    updatedSize = "M";
	    highlightSizeButton("M");
//	    productDetailPrice.setText(String.format("%.3f", computeUnitPrice(updatedSize, computeToppingNames(chosenToppings)) * updatedQuantity));
	    productDetailPrice.setText(PRICE_FORMATTER.format(computeUnitPrice(updatedSize, computeToppingNames(chosenToppings)) * updatedQuantity));
	}
	@FXML
	private void selectSizeL() {
	    updatedSize = "L";
	    highlightSizeButton("L");
	    productDetailPrice.setText(PRICE_FORMATTER.format(computeUnitPrice(updatedSize, computeToppingNames(chosenToppings)) * updatedQuantity));
	}

	// Các event handlers cho nút topping (toggle)
	private void toggleTopping(int toppingID, Button toppingButton) {
	    if(chosenToppings.contains(toppingID)) {
	        chosenToppings.remove(toppingID);
	        toppingButton.setStyle("-fx-background-color: white; -fx-text-fill: black;");
	    } else {
	        chosenToppings.add(toppingID);
	        toppingButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
	    }
	    // Cập nhật lại giá khi topping thay đổi
	    productDetailPrice.setText(PRICE_FORMATTER.format(computeUnitPrice(updatedSize, computeToppingNames(chosenToppings)) * updatedQuantity));
	    highlightToppingButtons(computeToppingNames(chosenToppings));
	}

	@FXML
	private void toggleTopping1() { toggleTopping(1, btntopping1); }
	@FXML
	private void toggleTopping2() { toggleTopping(2, btntopping2); }
	@FXML
	private void toggleTopping3() { toggleTopping(3, btntopping3); }
	@FXML
	private void toggleTopping4() { toggleTopping(4, btntopping4); }
	@FXML
	private void toggleTopping5() { toggleTopping(5, btntopping5); }
	@FXML
	private void toggleTopping6() { toggleTopping(6, btntopping6); }
	@FXML
	private void toggleTopping7() { toggleTopping(7, btntopping7); }
	@FXML
	private void toggleTopping8() { toggleTopping(8, btntopping8); }
	@FXML
	private void toggleTopping9() { toggleTopping(9, btntopping9); }
	@FXML
	private void toggleTopping10() { toggleTopping(10, btntopping10); }

	// Hàm helper để chuyển tập chosenToppings thành chuỗi tên topping (ngăn cách bởi dấu phẩy)
	private String computeToppingNames(Set<Integer> chosenToppings) {
	    List<Integer> list = new ArrayList<>(chosenToppings);
	    Collections.sort(list);
	    StringBuilder sb = new StringBuilder();
	    for (Integer id : list) {
	        String toppingName = toppingNameMap.get(id);
	        if(toppingName != null) {
	            if(sb.length() > 0) {
	                sb.append(", ");
	            }
	            sb.append(toppingName);
	        }
	    }
	    return sb.toString();
	}

	// Hàm helper để tính đơn giá cuối cùng của sản phẩm (basePrice + phụ thu theo size + tổng giá topping)
	private double computeUnitPrice(String updatedSize, String newToppingNames) {
	    // Tính phụ thu và tổng topping ban đầu dựa trên dữ liệu hiện có
	    double originalAdditional = 0;
	    if ("M".equalsIgnoreCase(currentProductSize)) {
	        originalAdditional = 6000;
	    } else if ("L".equalsIgnoreCase(currentProductSize)) {
	        originalAdditional = 10000;
	    }
	    double originalToppingTotal = 0;
	    if(currentToppingNames != null && !currentToppingNames.equalsIgnoreCase("Không topping")) {
	        originalToppingTotal = computeToppingTotal(currentToppingNames);
	    }
	    // Giá gốc của sản phẩm không bao gồm phụ thu và topping
	    double baseWithoutExtras = basePrice - (originalAdditional + originalToppingTotal);
	    
	    // Tính phụ thu theo kích cỡ mới
	    double newAdditional = 0;
	    if ("M".equalsIgnoreCase(updatedSize)) {
	        newAdditional = 6000;
	    } else if ("L".equalsIgnoreCase(updatedSize)) {
	        newAdditional = 10000;
	    }
	    // Tính tổng giá topping mới
	    double newToppingTotal = 0;
	    if(newToppingNames != null && !newToppingNames.trim().isEmpty() && !newToppingNames.equalsIgnoreCase("Không topping")) {
	        newToppingTotal = computeToppingTotal(newToppingNames);
	    }
	    
	    return baseWithoutExtras + newAdditional + newToppingTotal;
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

	// Khi người dùng nhấn nút Update, cập nhật thông tin vào CSDL và cập nhật giao diện CartCardController
	@FXML
	public void updateProductItem() {
	    Connection connect = null;
	    try {
	        // Nếu updatedSize chưa được gán, giữ nguyên giá trị ban đầu
	        if (updatedSize == null) {
	            updatedSize = currentProductSize;
	        }
	        
	        // CHANGED: Tính updatedToppingHash luôn từ tập chosenToppings; nếu rỗng thì gán là "No topping"
	        String updatedToppingHash = computeToppingHash(chosenToppings);
	        if(updatedToppingHash.isEmpty()){
	            updatedToppingHash = "Không topping";
	        }
	        
	        connect = Database.connect();
	        connect.setAutoCommit(false);
	        
	        // Kiểm tra xem sản phẩm (theo userID, productID, size, toppingHash) đã tồn tại nào khác chưa (ngoại trừ dòng đang update)
	        String checkDuplicateSql = "SELECT cartID, quantity FROM carts " +
	                                   "WHERE userID = ? AND productID = ? AND size = ? AND toppingHash = ? " +
	                                   "AND cartID <> ?";
	        PreparedStatement checkStmt = connect.prepareStatement(checkDuplicateSql);
	        checkStmt.setInt(1, Session.getUserID());
	        checkStmt.setInt(2, cartItemController.getProductID());
	        checkStmt.setString(3, updatedSize);
	        checkStmt.setString(4, updatedToppingHash);
	        checkStmt.setInt(5, cartItemController.getCartID());
	        
	        ResultSet rs = checkStmt.executeQuery();
	        if(rs.next()){
	            // Nếu tìm thấy bản ghi trùng, lấy số lượng của bản ghi đó
	            int duplicateCartID = rs.getInt("cartID");
	            int duplicateQuantity = rs.getInt("quantity");
	            
	            // Cập nhật số lượng cho bản ghi trùng: cộng thêm số lượng của dòng hiện tại
	            String updateDuplicateSql = "UPDATE carts SET quantity = ? WHERE cartID = ?";
	            PreparedStatement updateDupStmt = connect.prepareStatement(updateDuplicateSql);
	            updateDupStmt.setInt(1, duplicateQuantity + updatedQuantity);
	            updateDupStmt.setInt(2, duplicateCartID);
	            updateDupStmt.executeUpdate();
	            
	            // Xóa dòng đang update (để tránh trùng lặp)
	            String deleteCurrentSql = "DELETE FROM carts WHERE cartID = ?";
	            PreparedStatement deleteStmt = connect.prepareStatement(deleteCurrentSql);
	            deleteStmt.setInt(1, cartItemController.getCartID());
	            deleteStmt.executeUpdate();
	            
	            // Xóa các topping của dòng hiện tại trong bảng cart_toppings
	            String deleteToppingsSql = "DELETE FROM cart_toppings WHERE cartID = ?";
	            PreparedStatement deleteToppingsStmt = connect.prepareStatement(deleteToppingsSql);
	            deleteToppingsStmt.setInt(1, cartItemController.getCartID());
	            deleteToppingsStmt.executeUpdate();
	            
	            connect.commit();
	            
	            // CHANGED: Cập nhật lại giao diện ngay sau commit trước khi đóng cửa sổ update.
	            javafx.application.Platform.runLater(() -> {
	                CartController cartController = cartItemController.getCartController();
	                try {
	                    cartController.displayCard();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	                cartController.updateTotalPrice();
	                cartController.requestListCardLayout(); // Gọi hàm yêu cầu layout lại (xem ở CartController)
	            });
	            
	            AlertMessage.showAlert(AlertType.INFORMATION, "Thông báo", "Cập nhật thành công", "Sản phẩm đã được cập nhật, số lượng đã được cộng dồn!");
	        } else {
	            // Nếu không có duplicate, cập nhật dòng hiện tại
	            String sql = "UPDATE carts SET quantity = ?, size = ?, toppingHash = ? WHERE cartID = ?";
	            PreparedStatement prepare = connect.prepareStatement(sql);
	            prepare.setInt(1, updatedQuantity);
	            prepare.setString(2, updatedSize);
	            prepare.setString(3, updatedToppingHash);
	            prepare.setInt(4, cartItemController.getCartID());
	            int rows = prepare.executeUpdate();
	            if(rows > 0) {
	                // Cập nhật bảng cart_toppings: xóa cũ và thêm mới
	                String deleteToppingsSql = "DELETE FROM cart_toppings WHERE cartID = ?";
	                PreparedStatement deleteStmt = connect.prepareStatement(deleteToppingsSql);
	                deleteStmt.setInt(1, cartItemController.getCartID());
	                deleteStmt.executeUpdate();
	                for (Integer toppingID : chosenToppings) {
	                    String insertToppingSql = "INSERT INTO cart_toppings (cartID, toppingID) VALUES (?, ?)";
	                    PreparedStatement insertStmt = connect.prepareStatement(insertToppingSql);
	                    insertStmt.setInt(1, cartItemController.getCartID());
	                    insertStmt.setInt(2, toppingID);
	                    insertStmt.executeUpdate();
	                }
	                // Tính đơn giá mới và cập nhật giao diện cho dòng hiện tại
	                String updatedToppingNames = computeToppingNames(chosenToppings);
	                if(updatedToppingNames.isEmpty()){
	                    updatedToppingNames = "Không topping";
	                }
	                double newUnitPrice = computeUnitPrice(updatedSize, updatedToppingNames);
	                cartItemController.updateFromDetail(updatedSize, updatedQuantity, updatedToppingNames, newUnitPrice);
	                
	                connect.commit();
	                
	                // CHANGED: Cập nhật lại giao diện ngay sau commit
	                javafx.application.Platform.runLater(() -> {
	                    CartController cartController = cartItemController.getCartController();
	                    try {
	                        cartController.displayCard();
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                    }
	                    cartController.updateTotalPrice();
	                    cartController.requestListCardLayout();
	                });
	                
	                AlertMessage.showAlert(AlertType.INFORMATION, "Thông báo", "Cập nhật thành công", "Sản phẩm đã được cập nhật thành công!");

	            }
	        }
	    } catch (SQLException e) {
	        if(connect != null) {
	            try {
	                connect.rollback();
	            } catch (SQLException ex) {
	                ex.printStackTrace();
	            }
	        }
	        e.printStackTrace();
	    } finally {
	        if(connect != null) {
	            try {
	                connect.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	        // CHANGED: Đóng cửa sổ update sau khi các thao tác UI đã được yêu cầu cập nhật
	        Stage stage = (Stage) btnCancel.getScene().getWindow();
	        stage.close();
	    }
	}



	@FXML
	public void cancelUpdate() {
	    Stage stage = (Stage) btnCancel.getScene().getWindow();
	    stage.close();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	    // Khởi tạo nếu cần.
	}

}
