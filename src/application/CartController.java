package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.io.ByteArrayInputStream;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.BoxBlur;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CartController implements Initializable {

	@FXML
	private Button btnPay;

	@FXML
	private Label date;

	@FXML
	private VBox layoutDelivery;

	@FXML
	private Label lbAdress;

	@FXML
	private Label lbNote;

	@FXML
	private Label lbProvince;

	@FXML
	private VBox listCard;

	@FXML
	private Label name;

	@FXML
	private ToggleGroup pay;

	@FXML
	private Label phone;

	@FXML
	private Label quantityLabel;

	@FXML
	private Label shippingFee;

	@FXML
	private Label time;

	@FXML
	private Label lbTimeExpired;

	@FXML
	private Label totalPrice;

	@FXML
	private Label totalPriceVAT;

	@FXML
	private Button btnTimeDelivery;

	@FXML
	private HBox HboxTimeDelivery;

	private int currentUserID;

	private AlertMessage alert;

	public static final DecimalFormat PRICE_FORMATTER = new DecimalFormat("#,###");

	public void setUserID(int userID) {
		this.currentUserID = userID;
		try {
			displayCard();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void switchVoucher() {
		Parent currentRoot = btnPay.getScene().getRoot();
		BoxBlur blur = new BoxBlur(5, 5, 3);
		currentRoot.setEffect(blur);
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EnterVoucher.fxml"));
			Parent viewVoucher = loader.load();

			Stage selectStage = new Stage();
			selectStage.setScene(new Scene(viewVoucher));
			selectStage.initModality(Modality.APPLICATION_MODAL);
			selectStage.show();
			selectStage.setOnHiding(event -> currentRoot.setEffect(null));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void translateSelectTime(LocalDate date, String time) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String formattedDate = date.format(formatter);
		this.date.setText(formattedDate);
		this.time.setText(time);
	}

	@FXML
	public void switchTime() {
		Parent currentRoot = btnPay.getScene().getRoot();
		BoxBlur blur = new BoxBlur(5, 5, 3);
		currentRoot.setEffect(blur);
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("TimeDelivery.fxml"));
			Parent viewTime = loader.load();

			// Lấy controller của cửa sổ chọn địa chỉ
			TimeDeliveryController timeDeliveryController = loader.getController();

			// Đăng ký callback để nhận địa chỉ được chọn
			timeDeliveryController.setCartController(this::translateSelectTime);
			timeDeliveryController.setTime(time.getText());

			Stage selectStage = new Stage();
//		selectStage.setTitle("Select Adress");
			selectStage.setScene(new Scene(viewTime));
			selectStage.initModality(Modality.APPLICATION_MODAL);
			selectStage.show();
			selectStage.setOnHiding(event -> currentRoot.setEffect(null));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void translateSelectAdress(String address, String province) {

		if (!province.isEmpty() && address != null) {
			lbProvince.setText(province);
			lbAdress.setText(address);
		}

	}

	@FXML
	public void selectAdress() {
		Parent currentRoot = btnPay.getScene().getRoot();
		BoxBlur blur = new BoxBlur(5, 5, 3);
		currentRoot.setEffect(blur);
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("SelectAddress.fxml"));
			Parent viewSelect = loader.load();

			// Lấy controller của cửa sổ chọn địa chỉ
			AddressSelectionController addressController = loader.getController();

			// Đăng ký callback để nhận địa chỉ được chọn
			addressController.setCartController(this::translateSelectAdress);
			Stage selectStage = new Stage();
//			selectStage.setTitle("Select Adress");
			selectStage.setScene(new Scene(viewSelect));
			selectStage.initModality(Modality.APPLICATION_MODAL);
			selectStage.show();
			selectStage.setOnHiding(event -> currentRoot.setEffect(null));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void translateSelectInformatinUser(String userName, String phoneNumber) {
		name.setText(userName);
		phone.setText(phoneNumber);
	}

	@FXML
	public void selectNameAndPhone() {
		Parent currentRoot = btnPay.getScene().getRoot();
		BoxBlur blur = new BoxBlur(5, 5, 3);
		currentRoot.setEffect(blur);
//		int userID = Session.getUserID();
		String fullName = "";
		String phone = "";
		try {
			Connection connect = Database.connect();
			String sql = "SELECT fullName, phoneNumber FROM users WHERE userName = ?";
			PreparedStatement prepare = connect.prepareStatement(sql);
			prepare.setString(1, Session.getUserName());
			ResultSet rs = prepare.executeQuery();
			if (rs.next()) {
				fullName = rs.getString("fullName");
				phone = rs.getString("phoneNumber"); // Nếu giá trị trong DB là null, biến phone sẽ nhận giá trị null
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("InformationUser.fxml"));
			Parent viewInformationUser = loader.load();
			InformationUserController informationUserController = loader.getController();

			// Đăng ký callback: truyền reference về phương thức translateSelect... của
			// CartController
			informationUserController.setInformationController(this::translateSelectInformatinUser);
			informationUserController.setInformation(fullName, phone);
			Stage selectStage = new Stage();
//			selectStage.setTitle("Select Adress");
			selectStage.setScene(new Scene(viewInformationUser));
			selectStage.initModality(Modality.APPLICATION_MODAL);
			selectStage.show();
			selectStage.setOnHiding(event -> currentRoot.setEffect(null));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void translateSelectNotes(String notes) {
		lbNote.setText(notes);
	}

	@FXML
	public void switchNote() {
		Parent currentRoot = btnPay.getScene().getRoot();
		BoxBlur blur = new BoxBlur(5, 5, 3);
		currentRoot.setEffect(blur);
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("NoteForStore.fxml"));
			Parent noteView = loader.load();
			NoteController noteController = loader.getController();
			noteController.setCartController(this::translateSelectNotes);
			noteController.setNoteText(lbNote.getText());
//	        noteController.noteTextField.clear();

			Stage selectStage = new Stage();
//			selectStage.setTitle("Note");
			selectStage.setScene(new Scene(noteView));
			selectStage.initModality(Modality.APPLICATION_MODAL);
			selectStage.show();
			selectStage.setOnHiding(event -> currentRoot.setEffect(null));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void requestListCardLayout() {
		listCard.requestLayout();
	}

	public void displayCard() throws IOException {
		try {
			Connection connect = Database.connect();
			String sql = "SELECT c.cartID, c.size, p.productName, p.thumbnail, p.price, p.productID, p.productCode, c.quantity, c.toppingHash, "
					+ "       (SELECT GROUP_CONCAT(t.toppingName SEPARATOR ', ') FROM cart_toppings ct "
					+ "        JOIN toppings t ON ct.toppingID = t.toppingID "
					+ "        WHERE ct.cartID = c.cartID) AS toppingNames, "
					+ "       (SELECT IFNULL(SUM(t.toppingPrice), 0) FROM cart_toppings ct "
					+ "        JOIN toppings t ON ct.toppingID = t.toppingID "
					+ "        WHERE ct.cartID = c.cartID) AS toppingTotal " + "FROM products p "
					+ "JOIN carts c ON c.productID = p.productID " + "JOIN users u ON c.userID = u.userID "
					+ "WHERE u.userID = ?;";

			PreparedStatement prepare = connect.prepareStatement(sql);
			prepare.setInt(1, currentUserID);
			ResultSet rs = prepare.executeQuery();
			listCard.getChildren().clear();
			while (rs.next()) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("CartCard.fxml"));
				Parent cartItem = loader.load();

				CartCardController cartCardController = loader.getController();
				cartCardController.setUserID(currentUserID);
				cartCardController.setRootNode(cartItem);
				cartCardController.setCartController(this);

				int cartID = rs.getInt("cartID");
				String size = rs.getString("size");
				String productName = rs.getString("productName");
				String productCode = rs.getString("productCode");
				double price = rs.getDouble("price");
				int productID = rs.getInt("productID");
				int quantity = rs.getInt("quantity");
				byte[] thumbnailBytes = rs.getBytes("thumbnail");
				Image thumbnail = null;
				if (thumbnailBytes != null) {
					thumbnail = new Image(new ByteArrayInputStream(thumbnailBytes));
				}

				String toppingNames = rs.getString("toppingNames");
				if (toppingNames == null || toppingNames.trim().isEmpty()) {
					toppingNames = "Không topping";
				}

				String toppingHash = rs.getString("toppingHash");

				double toppingPrice = rs.getDouble("toppingTotal");

				double addditional = 0;
				if ("M".equalsIgnoreCase(size)) {
					addditional = 6000;
				} else if ("L".equalsIgnoreCase(size)) {
					addditional = 10000;
				}

				double finalPrice = (price + addditional + toppingPrice);

				// truyền finalPrice đã tính vào để hiển thị đơn giá đã bao gồm phụ thu
				cartCardController.setData(size, productName, thumbnail, finalPrice, productID, quantity, toppingHash,
						toppingNames, productCode, cartID);
				// Lưu controller vào userData để có thể lấy sau này khi tính tổng tiền
				cartItem.setUserData(cartCardController);
				listCard.getChildren().add(cartItem);
			}
			updateTotalPrice();
//			listCard.requestLayout();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	double totalVAT = 0;

	// Thay đổi updateTotalPrice để duyệt qua các thẻ sản phẩm trong listCard
	public void updateTotalPrice() {
		double total = 0;
		int quantity = 0;
		for (javafx.scene.Node node : listCard.getChildren()) {
//			gọi node.getUserData() để lấy đúng CartCardController đã lưu trước đó bằng setUserData(...).
			Object data = node.getUserData();
			if (data instanceof CartCardController) {
//				Kiểm tra instanceof CartCardController để chắc chắn rằng userData đúng là controller của card.
				CartCardController card = (CartCardController) data;
				total += card.getTotalPrice();
				quantity += card.getQuantity();
			}

		}
		totalVAT = total * 1.1; // Giả sử VAT 10%

//		totalPrice.setText(String.format("%.3f", total));
		totalPrice.setText(PRICE_FORMATTER.format(total));

		if (quantity > 0) {
			shippingFee.setText("25,000");
//			totalPriceVAT.setText(String.format("%.3f", totalVAT + 25000));
			totalPriceVAT.setText(PRICE_FORMATTER.format(totalVAT + 25000));
		} else {
			shippingFee.setText("0");
//			totalPriceVAT.setText(String.format("%.3f", totalVAT));
			totalPriceVAT.setText(PRICE_FORMATTER.format(totalVAT));
		}
		quantityLabel.setText(String.valueOf(quantity));
	}

	@FXML
	public void confirmPayment() {
		if (!lbAdress.getText().trim().isEmpty() || !name.getText().trim().isEmpty()
				|| !phone.getText().trim().isEmpty() || !quantityLabel.getText().trim().equals("0")
				|| !date.getText().trim().isEmpty() || !time.getText().trim().isEmpty()) {
			Connection connect = null;
			try {
				connect = Database.connect();
				connect.setAutoCommit(false);
				String sql = "INSERT INTO orders(userID, addressOrder, phoneOrder, totalPrice, notes) VALUES(?, ?, ?, ?, ?)";
				PreparedStatement prepare = connect.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
				prepare.setInt(1, Session.getUserID());
				prepare.setString(2, lbAdress.getText());
				prepare.setString(3, phone.getText());
				prepare.setDouble(4, totalVAT + 25000);
				if (lbNote.getText().isEmpty()) {
					prepare.setString(5, "Không có ghi chú");
				} else {
					prepare.setString(5, lbNote.getText());
				}
				int rows = prepare.executeUpdate();
				ResultSet rs = prepare.getGeneratedKeys();
				int orderID = 0;
				if (rows > 0 && rs.next()) {
					orderID = rs.getInt(1);

					AlertMessage.showAlert(AlertType.INFORMATION, "Thông báo", "Thanh toán thành công",
							"Thanh toán đã được thực hiện thành công.");

					ButtonType buttonYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
					ButtonType buttonCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.getButtonTypes().clear();
					alert.getButtonTypes().addAll(buttonYes, buttonCancel);
					alert.setContentText("Bạn có muốn quay về trang chủ?");
					Optional<ButtonType> result = alert.showAndWait();

					if (result.get() == buttonYes && result.isPresent()) {
						try {
							FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
							Parent homeView = loader.load();
							HomeController homeCardController = loader.getController();
							BorderPane mainLayout = (BorderPane) btnPay.getScene().getRoot();
							mainLayout.setCenter(homeView);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				}

				String insertOrderItems = "INSERT INTO order_items(orderID, productID, quantity, size, toppingName, unitPrice)"
						+ "VALUES(?,?,?,?,?,?)";
				PreparedStatement prepareInsert = connect.prepareStatement(insertOrderItems);
				for (javafx.scene.Node node : listCard.getChildren()) {
					Object data = node.getUserData();
					if (data instanceof CartCardController) {
						CartCardController card = (CartCardController) data;
						prepareInsert.setInt(1, orderID);
						prepareInsert.setInt(2, card.getProductID());
						prepareInsert.setInt(3, card.getQuantity());
						prepareInsert.setString(4, card.getSize());
						prepareInsert.setString(5, card.getToppingName());
						prepareInsert.setDouble(6, card.getUnitPrice());
						prepareInsert.addBatch();
					}
				}
				prepareInsert.executeBatch();

				String deleteCart = "DELETE FROM carts WHERE userID = ?";
				PreparedStatement prepareDelete = connect.prepareStatement(deleteCart);
				prepareDelete.setInt(1, Session.getUserID());
				prepareDelete.executeUpdate();

				connect.commit();

				// Sau khi commit() và có được orderID, lấy thông tin đơn hàng + sản phẩm
				String orderSql = "SELECT u.email, u.fullName, o.addressOrder, o.orderDate, o.totalPrice, "
						+ "       p.productName, i.quantity, i.size, i.toppingName, i.unitPrice " + "FROM users u "
						+ "JOIN orders o ON o.userID = u.userID " + "JOIN order_items i ON i.orderID = o.orderID "
						+ "JOIN products p ON p.productID = i.productID " + "WHERE u.userID = ? AND o.orderID = ?;";

				PreparedStatement prepareOrder = connect.prepareStatement(orderSql);
				prepareOrder.setInt(1, Session.getUserID());
				prepareOrder.setInt(2, orderID);
				ResultSet rsOrder = prepareOrder.executeQuery();

				/*
				 * Để gom nhiều sản phẩm vào cùng 1 email, - Chỉ lấy các thông tin chung (email,
				 * fullName, addressOrder, orderDate, totalPrice) ở lần đầu. - Dùng
				 * StringBuilder để nối thông tin từng sản phẩm (tên, size, quantity,
				 * topping...) cho toàn bộ dòng trong ResultSet.
				 */
				String email = "";
				String fullName = "";
				String addressOrder = "";
				Timestamp orderDate = null;
				double totalPrice = 0;

				// Dùng StringBuilder để lưu danh sách sản phẩm
				StringBuilder productDetails = new StringBuilder();

				// Biến cờ để biết đã gán các thông tin chung hay chưa
				boolean firstRow = true;

				while (rsOrder.next()) {
					// Chỉ set các thông tin chung ở dòng đầu tiên
					if (firstRow) {
						email = rsOrder.getString("email");
						fullName = rsOrder.getString("fullName");
						addressOrder = rsOrder.getString("addressOrder");
						orderDate = rsOrder.getTimestamp("orderDate");
						totalPrice = rsOrder.getDouble("totalPrice");
						firstRow = false;
					}

					// Lấy thông tin sản phẩm cho từng dòng
					String productName = rsOrder.getString("productName");
					int quantity = rsOrder.getInt("quantity");
					String size = rsOrder.getString("size");
					String toppingName = rsOrder.getString("toppingName");
					double unitPrice = rsOrder.getDouble("unitPrice");

					double price = unitPrice * quantity;
					// Thêm vào StringBuilder
					productDetails.append("- ").append(productName).append(" (").append(size).append(")").append(" x ")
							.append(quantity).append(": ").append(PRICE_FORMATTER.format(price)).append("đ");

					// Nếu toppingName không phải "Không topping" thì hiển thị
					if (toppingName != null && !toppingName.trim().isEmpty()
							&& !toppingName.equalsIgnoreCase("Không topping")) {
						productDetails.append(" (").append(toppingName).append(")");
					}

					productDetails.append("\n");
				}
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY HH:mm:ss");
				String formattedDate = sdf.format(orderDate);

				System.out.println("Email lấy được: " + email);

				String note = lbNote.getText().isEmpty() ? "Ghi chú: Không có ghi chú" : "Ghi chú: " + lbNote.getText();
				String formatPrice = PRICE_FORMATTER.format(totalPrice);
				// Xây dựng nội dung email
				String orderDetails = "Chào " + fullName + ",\n\n" + "Cảm ơn bạn đã đặt nước tại CoffeeShop!\n\n"
						+ "Mã đơn hàng: #" + orderID + "\n" + "Thời gian đặt: " + " Ngày " + formattedDate + "\n\n"
						+ "Sản phẩm:\n" + productDetails.toString() + "\n" + note + "\n" + "Phí vận chuyển: 25,000đ"
						+ "\n" + "Tổng tiền: " + formatPrice + "đ" + " (Bao gồm VAT 10%)" + "\n"
						+ "Thanh toán: Đã thanh toán \n\n" + "Địa chỉ giao: " + addressOrder + "\n\n"
						+ "Thời gian giao dự kiến: " + time.getText() + " ngày hôm nay." + "\n"
						+ "Mọi thắc mắc vui lòng liên hệ: 1800 123 456\n\n" + "CoffeeShop xin cảm ơn và hẹn gặp lại!";

				try {
					// Chỉ gửi nếu email không rỗng
					if (email != null && !email.trim().isEmpty()) {
						EmailSender.sendOrderConfirmation(email, orderDetails);
					} else {
						System.out.println("Email trống hoặc null, không thể gửi.");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				try {
					displayCard();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					if (connect != null) {
						connect.rollback();
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} finally {
				if (connect != null) {
					try {
						connect.setAutoCommit(true);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						connect.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} else {
			AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Thiếu thông tin", "Vui lòng điền đầy đủ thông tin.");
		}

	}

	boolean isValid = false;

	private void updateButtonState() {
		isValid = !lbAdress.getText().trim().isEmpty() && !name.getText().trim().isEmpty()
				&& !phone.getText().trim().isEmpty() && !quantityLabel.getText().trim().equals("0")
				&& !date.getText().trim().isEmpty() && !time.getText().trim().isEmpty();

		if (isValid) {
			btnPay.setDisable(false);
			btnPay.setStyle("-fx-background-color: green; -fx-text-fill: white;");
		} else {
			btnPay.setDisable(true);
			btnPay.setStyle("-fx-background-color: grey; -fx-text-fill: white;");
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Kiểm tra điều kiện ban đầu
		updateButtonState();

		// Thêm listener để tự động cập nhật nút btnPay khi có thay đổi
		lbAdress.textProperty().addListener((obs, oldVal, newVal) -> updateButtonState());
		name.textProperty().addListener((obs, oldVal, newVal) -> updateButtonState());
		phone.textProperty().addListener((obs, oldVal, newVal) -> updateButtonState());
		quantityLabel.textProperty().addListener((obs, oldVal, newVal) -> updateButtonState());
		date.textProperty().addListener((obs, oldVal, newVal) -> updateButtonState());
		time.textProperty().addListener((obs, oldVal, newVal) -> updateButtonState());
		pay.selectedToggleProperty().addListener((obs, oldVal, newVal) -> updateButtonState());

		if (!pay.getToggles().isEmpty()) {
			pay.selectToggle(pay.getToggles().get(0));
		}

		LocalTime now = LocalTime.now();

		// Kiểm tra nếu sau 21h thì ẩn và vô hiệu hóa nút
		if (now.isAfter(LocalTime.of(21, 0))) {
			btnTimeDelivery.setVisible(false);
			btnTimeDelivery.setDisable(true);
			HboxTimeDelivery.setVisible(false);
			lbTimeExpired.setVisible(true);
		} else {
			btnTimeDelivery.setVisible(true);
			btnTimeDelivery.setDisable(false);
			HboxTimeDelivery.setVisible(true);
			lbTimeExpired.setVisible(false);
		}
	}
}
