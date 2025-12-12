package application;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import com.rabbitmq.client.Channel;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;

import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainLayoutAdmin implements Initializable {

	@FXML
	private TextField nameTF;
	
	@FXML
	private TextField searchTF;

	@FXML
	private ComboBox<String> statusCb;

	@FXML
	private BorderPane mainLayoutAdmin;

	@FXML
	private VBox adminChat;

	@FXML
	private VBox listUser;

	@FXML
	private ListView<String> lvUserList;

	@FXML
	private AnchorPane productManager;

	@FXML
	private ScrollPane scrollPane;

	@FXML
	private TextArea txtMessage;

	@FXML
	private VBox vBoxMessageContainer;

	@FXML
	private AnchorPane anchorPaneSupport;

	private String selectedUserName = null;

	@FXML
	private Label currentUserName;

	@FXML
	private TableView<Product> productTable;

	@FXML
	private TableColumn<Product, Integer> productID;

	@FXML
	private TableColumn<Product, String> productName;

	@FXML
	private TableColumn<Product, String> category;

	@FXML
	private TableColumn<Product, Double> price;

	@FXML
	private TableColumn<Product, String> description;

	@FXML
	private TableColumn<Product, String> typeProduct;

	@FXML
	private TableColumn<Product, String> status;

	@FXML
	private TableColumn<Product, String> productCode;

	@FXML
	private TableColumn<Product, Date> date;

	@FXML
	private TableColumn<Product, ImageView> thumbnail;

	@FXML
	private TableColumn<Product, Void> buttonColumn;

	@FXML
	private Button btnAddProduct;

	@FXML
	private Label nameWelcome;

	@FXML
	private Label lbCustomer;

	@FXML
	private Label lbMoneyDay;

	@FXML
	private Label lbRevenue;

	@FXML
	private Label lbDay;

	@FXML
	private Label lbSoldPorduct;

	@FXML
	private PieChart pieChart;

	@FXML
	private AreaChart<String, Number> areaChart;

	@FXML
	LineChart<String, Number> lineChart;

	@FXML
	private BarChart<String, Number> barChart;

	@FXML
	private ComboBox<Integer> yearcombobox;

	@FXML
	private ComboBox<Integer> monthcombobox;

	@FXML
	private ComboBox<Integer> monthcomboboxProduct;

	@FXML
	private ComboBox<Integer> yearcomboboxProduct;

	@FXML
	private VBox overView;

	@FXML
	private VBox statistical;

	@FXML
	private AnchorPane order;

	@FXML
	private Button btnSupport;

	@FXML
	private Button btnOverView;

	@FXML
	private Button btnProductManagement;

	@FXML
	private Button btnStatistical;

	@FXML
	private Button btnLogout;

	@FXML
	private Button btnOrder;

	@FXML
	private TableView<Order> orderTable;

	@FXML
	private TableColumn<Order, String> orderID;

	@FXML
	private TableColumn<Order, String> customerName;

	@FXML
	private TableColumn<Order, String> customerEmail;

	@FXML
	private TableColumn<Order, String> customerAddress;

	@FXML
	private TableColumn<Order, String> customerPhone;

	@FXML
	private TableColumn<Order, Double> orderTotal;

	@FXML
	private TableColumn<Order, LocalDateTime> dateOrder;

	@FXML
	private TableColumn<Order, Void> buttonDetailOrder;

	@FXML
	private DatePicker orderDatePicker;
	
	public static final DecimalFormat PRICE_FORMATTER = new DecimalFormat("#,###");

	private ObservableList<Product> productList = FXCollections.observableArrayList();

	public LoginController loginController;

	private ObservableList<String> userList = FXCollections.observableArrayList();

	private ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

	@FXML
	public void switchSupport() {
		btnSupport.setStyle("-fx-background-color: #c7a087;-fx-border-color:  #532b12; -fx-text-fill: white");

		btnOverView.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");
		btnProductManagement.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");
		btnStatistical.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");
		btnLogout.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");
		btnOrder.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");

		statistical.setVisible(false);
		overView.setVisible(false);
		productManager.setVisible(false);
		anchorPaneSupport.setVisible(true);
		listUser.setVisible(true);
		adminChat.setVisible(false);
		order.setVisible(false);

		try {
			Connection connect = Database.connect();
			String sql = "SELECT DISTINCT sender FROM chat_messages WHERE receiver = ? AND sender <> ?";
			PreparedStatement pstmt = connect.prepareStatement(sql);
			pstmt.setString(1, "Admin");
			pstmt.setString(2, "Admin");
			ResultSet rs = pstmt.executeQuery();
			userList.clear();
			while (rs.next()) {
				String userName = rs.getString("sender");
				userList.add(userName);
			}
			rs.close();
			pstmt.close();
			connect.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		lvUserList.setItems(userList);

	}

	@FXML
	public void switchOverview() {
		btnOverView.setStyle("-fx-background-color: #c7a087; -fx-border-color:  #532b12; -fx-text-fill: white");

		btnSupport.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");
		btnProductManagement.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");
		btnStatistical.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");
		btnLogout.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");
		btnOrder.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");

		statistical.setVisible(false);
		overView.setVisible(true);
		productManager.setVisible(false);
		anchorPaneSupport.setVisible(false);
		order.setVisible(false);
	}

	@FXML
	public void switchProductManager() {
		btnProductManagement
				.setStyle("-fx-background-color: #c7a087; -fx-border-color:  #532b12; -fx-text-fill: white");

		btnOverView.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");
		btnSupport.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");
		btnStatistical.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");
		btnLogout.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");
		btnOrder.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");

		statistical.setVisible(false);
		overView.setVisible(false);
		productManager.setVisible(true);
		anchorPaneSupport.setVisible(false);
		order.setVisible(false);
	}

	@FXML
	public void switchStatistical() {
		btnStatistical.setStyle("-fx-background-color: #c7a087; -fx-border-color:  #532b12; -fx-text-fill: white");

		btnOverView.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");
		btnProductManagement.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");
		btnSupport.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");
		btnLogout.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");
		btnOrder.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");

		statistical.setVisible(true);
		overView.setVisible(false);
		productManager.setVisible(false);
		anchorPaneSupport.setVisible(false);
		order.setVisible(false);
	}

	@FXML
	public void switchOrder() {
		btnOrder.setStyle("-fx-background-color: #c7a087; -fx-border-color:  #532b12; -fx-text-fill: white");

		btnOverView.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");
		btnProductManagement.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");
		btnSupport.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");
		btnStatistical.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");
		btnLogout.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");

		order.setVisible(true);
		statistical.setVisible(false);
		overView.setVisible(false);
		productManager.setVisible(false);
		anchorPaneSupport.setVisible(false);
	}

	private Map<String, Boolean> unreadMessage = new HashMap<>();

	private final String ADMIN_QUEUE = "support.admin";
	private RabbitMQManager rabbitMQManager = RabbitMQManager.getInstance();
	private Channel adminChannel;
	private String consumerTag;

	private ChatHistoryDAO chatHistoryDAO = new ChatHistoryDAO();
	private List<ChatMessage> messageList; // lịch sử tin nhắn
	// Tập lưu các "signature" của tin nhắn đã hiển thị
	private Set<String> displayedSignatures = new HashSet<>();

	// Hàm hiển thị tin nhắn lên UI và lưu signature vào tập displayedSignatures
	private void displayChatMessage(ChatMessage msg) {
		Timestamp timeStamp = msg.getTimestamp();
		String formattedTime = timeStamp.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));

		HBox outerHbox = new HBox();
		Label lbTimeMessage = new Label(formattedTime);
		VBox innerVbox = new VBox();
		innerVbox.setPadding(new Insets(0, 5, 0, 0));
		HBox hboxMessage = new HBox();
		hboxMessage.setPadding(new Insets(0, 5, 0, 5));
		hboxMessage.setSpacing(5);
		VBox vBoxMessage = new VBox();
		vBoxMessage.setMaxWidth(400);

		vBoxMessage.setAlignment(Pos.CENTER);

		Label contentMessage = new Label(msg.getContent());
		contentMessage.setWrapText(true);
		contentMessage.setFont(Font.font(18));
		contentMessage.setPadding(new Insets(0, 0, 0, 5));

		// Tạo Tooltip hiển thị ngày giờ gửi tin nhắn
		DateTimeFormatter tooltipFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		String tooltipText = timeStamp.toLocalDateTime().format(tooltipFormatter);
		Tooltip tooltip = new Tooltip(tooltipText);
		Tooltip.install(contentMessage, tooltip);

		tooltip.setShowDelay(Duration.millis(100));
		// Nếu muốn Tooltip ẩn nhanh hơn khi rời chuột, bạn có thể đặt hideDelay
		tooltip.setHideDelay(Duration.millis(100));

		vBoxMessage.getChildren().add(contentMessage);
//		VBox.setMargin(contentMessage, new Insets(20, 0, 0, 0));

		hboxMessage.getChildren().add(vBoxMessage);
		innerVbox.getChildren().addAll(hboxMessage, lbTimeMessage);
		lbTimeMessage.setPadding(new Insets(0, 5, 0, 5));
		HBox.setMargin(innerVbox, new Insets(0, 0, 0, 10));

		if (msg.getSender().equalsIgnoreCase("Admin")) {
			outerHbox.setAlignment(Pos.TOP_RIGHT);
			vBoxMessage.setStyle("-fx-background-color: #dcdcdc; -fx-background-radius: 5;");
		} else {
			outerHbox.setAlignment(Pos.TOP_LEFT);
			vBoxMessage.setStyle("-fx-background-color: #24ADFD; -fx-background-radius: 5;");
		}
		outerHbox.getChildren().add(innerVbox);

		Platform.runLater(() -> {
			vBoxMessageContainer.getChildren().add(outerHbox);
			Timeline timeline = new Timeline(new KeyFrame(Duration.millis(50), e -> scrollPane.setVvalue(1.0)));
			timeline.play();
		});
	}

	// Phương thức gửi tin nhắn từ Admin đến user
	@FXML
	void sendReply() {
		String timeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
		String message = txtMessage.getText().trim();
		if (message.isEmpty())
			return;
		// Tạo tin nhắn hiển thị của Admin
		ChatMessage chatMsg = new ChatMessage("Admin", selectedUserName, message,
				new Timestamp(System.currentTimeMillis()));
		displayChatMessage(chatMsg);
		// Gửi tin nhắn qua RabbitMQ
		String userRoutingKey = "user." + selectedUserName;
		rabbitMQManager.sendMessage(userRoutingKey, message);

		txtMessage.clear();

		try {
			chatHistoryDAO.saveMessage(chatMsg);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void logOut() {
		btnLogout.setStyle("-fx-background-color: #c7a087; -fx-border-color:  #532b12; -fx-text-fill: white");

		btnOverView.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");
		btnProductManagement.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");
		btnStatistical.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");
		btnSupport.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");
		btnOrder.setStyle("-fx-background-color: white; -fx-border-color:  #532b12;");
		
		RabbitMQManager rabbitMQManager = new RabbitMQManager();
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Xác nhận đăng xuất");
		alert.setHeaderText("Bạn có chắc chắn muốn đăng xuất?");
		alert.setContentText("Xác nhận đăng xuất khỏi tài khoản?");

		ButtonType buttonTypeYes = new ButtonType("YES", ButtonBar.ButtonData.YES);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == buttonTypeYes) {
			AddminSessionManager addminSessionManager = new AddminSessionManager();
			addminSessionManager.logout();
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
				Parent parent = loader.load();
				Stage homeStage = new Stage();
//				homeStage.setTitle("null");
				homeStage.setScene(new Scene(parent));
				homeStage.show();

				Stage stage = (Stage) mainLayoutAdmin.getScene().getWindow();
				stage.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void loadChatHistory() {
		if (selectedUserName == null)
			return;
		try {
			messageList = chatHistoryDAO.getMessages(selectedUserName, "Admin");
			for (ChatMessage msg : messageList) {
				displayChatMessage(msg);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void switchAdd() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("AddAndUpdateProduct.fxml"));
			Parent parent = loader.load();
			AddAndUpdateProductController addController = loader.getController();
			addController.setProductManagementController(this);
			addController.lbAddAndUpdate().setText("Thêm sản phẩm");
			Stage addStage = new Stage();
//			addStage.setTitle(null);
			addStage.setScene(new Scene(parent));
			addStage.initModality(Modality.APPLICATION_MODAL);
			addStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void switchDetails(Product product) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ProductDetails.fxml"));
			Parent parent = loader.load();
			ProductDetailsController detailsController = loader.getController();
			detailsController.setproductManagementController(this);
			detailsController.setDetailsData(product);

			Stage detailsStage = new Stage();
			detailsStage.setTitle("Product Details");
			detailsStage.setScene(new Scene(parent));
			detailsStage.initModality(Modality.APPLICATION_MODAL);
			detailsStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reloadProductList() {
		ProductDAO productDAO = new ProductDAO();
		// Lấy danh sách sản phẩm mới từ CSDL
		List<Product> products = productDAO.getAllProducts();
		productList.clear();
		// Thêm dữ liệu mới vào ObservableList
		productList.addAll(products);
		productTable.setItems(productList);

		productTable.refresh();
	}

	@FXML
	public void reloadProductListBtn() {
		statusCb.getSelectionModel().clearSelection();
		nameTF.clear();
		reloadProductList();
	}

	public String getSelectedProductCode() {
		Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
		return selectedProduct != null ? selectedProduct.getProductCode() : null;
	}

	public void displayDashBoard() {
		int customerQuantity = 0;
		double moneyOfDay = 0;
		double moneyTotal = 0;
		int productTotal = 0;

		String customerSql = "SELECT COUNT(DISTINCT userID) AS total_customer FROM orders";

		String moneyOfDaySql = "SELECT DATE(orderDate) AS Day, SUM(totalPrice) AS daily_total "
				+ "FROM orders WHERE DATE(orderDate) = CURDATE()";

		String moneyTotalSql = "SELECT SUM(totalPrice) AS total_price FROM orders";

		String productTotalSql = "SELECT SUM(quantity) AS total_product FROM order_items";

		// Mở kết nối một lần và dùng cho cả 2 truy vấn
		try (Connection connect = Database.connect()) {
			// Truy vấn số lượng khách hàng đã mua hàng
			try (PreparedStatement psCustomer = connect.prepareStatement(customerSql);
					ResultSet rsCustomer = psCustomer.executeQuery()) {
				if (rsCustomer.next()) {
					customerQuantity = rsCustomer.getInt("total_customer");
					lbCustomer.setText(String.valueOf(customerQuantity));
				}
			}
			// Truy vấn tổng tiền bán được theo ngày
			try (PreparedStatement psMoney = connect.prepareStatement(moneyOfDaySql);
					ResultSet rsMoney = psMoney.executeQuery()) {
				if (rsMoney.next()) {
					moneyOfDay = rsMoney.getDouble("daily_total");
					String dayMonthYear = rsMoney.getString("Day");
					
//					lbMoneyDay.setText(String.format("%.3f", moneyOfDay));
					lbMoneyDay.setText(PRICE_FORMATTER.format(moneyOfDay));
					lbDay.setText(dayMonthYear);
				}
			}

			try (PreparedStatement psMoneyTotal = connect.prepareStatement(moneyTotalSql);
					ResultSet rsMoneyTotal = psMoneyTotal.executeQuery()) {
				if (rsMoneyTotal.next()) {
					moneyTotal = rsMoneyTotal.getDouble("total_price");

					lbRevenue.setText(PRICE_FORMATTER.format(moneyTotal));
				}
			}

			try (PreparedStatement psProductTotal = connect.prepareStatement(productTotalSql);
					ResultSet rsProductTotal = psProductTotal.executeQuery()) {
				if (rsProductTotal.next()) {
					productTotal = rsProductTotal.getInt("total_product");

					lbSoldPorduct.setText(PRICE_FORMATTER.format(productTotal));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void displayPieChart() {
		pieChart.getData().clear();
		pieChartData.clear();

		String sqlTypeProduct = "SELECT p.typeProduct, SUM(o.quantity) AS total_quantity "
				+ "FROM order_items o JOIN products p ON p.productID = o.productID GROUP BY p.typeProduct";

		try (Connection connect = Database.connect();
				PreparedStatement psTypeProduct = connect.prepareStatement(sqlTypeProduct);
				ResultSet rsTypeProduct = psTypeProduct.executeQuery()) {
			while (rsTypeProduct.next()) {
				String type = rsTypeProduct.getString("typeProduct");
				int quantity = rsTypeProduct.getInt("total_quantity");
				pieChartData.add(new PieChart.Data(type, quantity));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		pieChart.getData().addAll(pieChartData);
	}

	public void displayYearlyRevenue() {
		Connection connect;
		try {
			connect = Database.connect();
			areaChart.getData().clear();
			XYChart.Series<String, Number> seriesArea = new XYChart.Series<>();
			seriesArea.setName("Doanh thu theo năm");
			String yearlyTotalSql = "SELECT YEAR(orderDate) AS order_year, SUM(totalPrice) AS yearly_total "
					+ "FROM orders GROUP BY YEAR(orderDate) ORDER BY order_year";
			PreparedStatement psYearlyTotal = connect.prepareStatement(yearlyTotalSql);
			ResultSet rsYearlyTotal = psYearlyTotal.executeQuery();
			while (rsYearlyTotal.next()) {
				String year = rsYearlyTotal.getString("order_year");
				double revenue = rsYearlyTotal.getDouble("yearly_total");

				seriesArea.getData().add(new XYChart.Data<>(year, revenue));
			}
			areaChart.getData().add(seriesArea);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void displayMonthlyRevenue(int selectedYear) {
		Connection connect;
		try {
			connect = Database.connect();
			areaChart.getData().clear();
			XYChart.Series<String, Number> seriesArea = new XYChart.Series<>();
			seriesArea.setName("Doanh thu theo tháng năm");
			String yearlyTotalSql = "SELECT DATE_FORMAT(orderDate, '%Y-%m') AS order_month, SUM(totalPrice) AS monthly_total "
					+ "FROM orders WHERE YEAR(orderDate) = ? GROUP BY DATE_FORMAT(orderDate, '%Y-%m') ORDER BY order_month";
			PreparedStatement psYearlyTotal = connect.prepareStatement(yearlyTotalSql);
			psYearlyTotal.setInt(1, selectedYear);
			ResultSet rsYearlyTotal = psYearlyTotal.executeQuery();
			while (rsYearlyTotal.next()) {
				String month = rsYearlyTotal.getString("order_month");
				double revenue = rsYearlyTotal.getDouble("monthly_total");

				seriesArea.getData().add(new XYChart.Data<>(month, revenue));
			}
			areaChart.getData().add(seriesArea);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void displayDailyRevenue(int selectedMonth, int selectedYear) {
		Connection connect;
		try {
			connect = Database.connect();
			areaChart.getData().clear();
			XYChart.Series<String, Number> seriesArea = new XYChart.Series<>();
			seriesArea.setName("Doanh thu theo ngày tháng");
			String dailyTotalSql = "SELECT DATE(orderDate) AS order_day, SUM(totalPrice) AS daily_total "
					+ "FROM orders WHERE DATE_FORMAT(orderDate, '%m') = ? AND YEAR(orderDate) = ? GROUP BY DATE(orderDate) ORDER BY order_day";
			PreparedStatement psDailyTotal = connect.prepareStatement(dailyTotalSql);
			psDailyTotal.setInt(1, selectedMonth);
			psDailyTotal.setInt(2, selectedYear);
			ResultSet rsDailyTotal = psDailyTotal.executeQuery();
			while (rsDailyTotal.next()) {
				String day = rsDailyTotal.getString("order_day");
				double revenue = rsDailyTotal.getDouble("daily_total");

				seriesArea.getData().add(new XYChart.Data<>(day, revenue));
			}
			areaChart.getData().add(seriesArea);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void lineChart() {
		Connection connect;
		try {
			connect = Database.connect();
			lineChart.getData().clear();
			XYChart.Series<String, Number> seriesLineChart = new XYChart.Series<>();
			seriesLineChart.setName("Doanh thu theo ngày");
			String dailyTotalSql = "SELECT DATE(orderDate) AS order_day, SUM(totalPrice) AS daily_total "
					+ "FROM orders GROUP BY DATE(orderDate) ORDER BY order_day";
			PreparedStatement psDailyTotal = connect.prepareStatement(dailyTotalSql);
			ResultSet rsDailyTotal = psDailyTotal.executeQuery();
			while (rsDailyTotal.next()) {
				String day = rsDailyTotal.getString("order_day");
				double revenue = rsDailyTotal.getDouble("daily_total");

				seriesLineChart.getData().add(new XYChart.Data<>(day, revenue));
			}
			lineChart.getData().add(seriesLineChart);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void barChartProductYearly() {
		try (Connection connect = Database.connect()) {
			// Truy vấn sản phẩm bán được theo ngày, sắp xếp theo day ASC
			String productSoldOfDay = "SELECT p.productName, SUM(o.quantity) AS total_quantity, YEAR(orderDate) AS year "
					+ "FROM order_items o " + "JOIN products p ON p.productID = o.productID "
					+ "JOIN orders od ON od.orderID = o.orderID " + "GROUP BY p.productName, YEAR(od.orderDate) "
					+ "ORDER BY year ASC";

			PreparedStatement prepare = connect.prepareStatement(productSoldOfDay);
			ResultSet rs = prepare.executeQuery();

			// Xóa dữ liệu cũ trên BarChart
			barChart.getData().clear();

			// Map để gom Series theo productName
			// (mỗi productName -> một Series)
			Map<String, XYChart.Series<String, Number>> seriesMap = new LinkedHashMap<>();

			// Dùng Set để thu thập tất cả các ngày, nhằm đặt thứ tự cho trục X
			Set<String> daySet = new LinkedHashSet<>();

			while (rs.next()) {
				String productName = rs.getString("productName");
				int totalQuantity = rs.getInt("total_quantity");
				String day = rs.getString("year"); // yyyy-MM-dd

				// Thêm ngày vào daySet để lát nữa setCategories cho trục X
				daySet.add(day);

				// Tìm series cho productName, nếu chưa có thì tạo mới
				XYChart.Series<String, Number> series = seriesMap.get(productName);
				if (series == null) {
					series = new XYChart.Series<>();
					series.setName(productName); // Tên series là tên sản phẩm
					seriesMap.put(productName, series);
				}
				// Thêm cột: trục X là day, trục Y là totalQuantity
				series.getData().add(new XYChart.Data<>(day, totalQuantity));
			}
			rs.close();
			prepare.close();

			// Đảm bảo BarChart có trục X là CategoryAxis
			CategoryAxis xAxis = (CategoryAxis) barChart.getXAxis();

			 xAxis.setAutoRanging(false);  
		     xAxis.getCategories().setAll(daySet);

			// Thêm tất cả series vào BarChart
			barChart.getData().addAll(seriesMap.values());

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void barChartProductMonthly(int selectedYear) {
	    try (Connection connect = Database.connect()) {
	        String sql =
	            "SELECT p.productName, " +
	            "       SUM(oi.quantity) AS total_quantity, " +
	            "       DATE_FORMAT(ord.orderDate, '%Y-%m') AS month_year " +
	            "FROM order_items oi " +
	            "JOIN products p ON p.productID = oi.productID " +
	            "JOIN orders ord ON ord.orderID = oi.orderID " +
	            "WHERE YEAR(ord.orderDate) = ? " +
	            "GROUP BY p.productName, month_year " +
	            "ORDER BY month_year ASC";

	        PreparedStatement ps = connect.prepareStatement(sql);
	        ps.setInt(1, selectedYear);
	        ResultSet rs = ps.executeQuery();

	        barChart.getData().clear();

	        Map<String, XYChart.Series<String, Number>> seriesMap = new LinkedHashMap<>();
	        // dùng LinkedHashSet để giữ thứ tự month_year ASC đúng như SQL trả về
	        Set<String> monthYearSet = new LinkedHashSet<>();

	        while (rs.next()) {
	            String productName = rs.getString("productName");
	            int totalQuantity  = rs.getInt("total_quantity");
	            String monthYear    = rs.getString("month_year");

	            monthYearSet.add(monthYear);

	            // Tạo hoặc tìm series phù hợp với productName
	            XYChart.Series<String, Number> series = seriesMap.get(productName);
	            if (series == null) {
	                series = new XYChart.Series<>();
	                series.setName(productName);
	                seriesMap.put(productName, series);
	            }
	            series.getData().add(new XYChart.Data<>(monthYear, totalQuantity));
	        }
	        rs.close();
	        CategoryAxis xAxis = (CategoryAxis) barChart.getXAxis();
	        xAxis.setAutoRanging(false);  
	        xAxis.getCategories().setAll(monthYearSet);
	         
	        barChart.getData().setAll(seriesMap.values());

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	public void barChartProductDaily(int selectedMonth, int selectedYear) {
		try (Connection connect = Database.connect()) {
			// Truy vấn sản phẩm bán được theo ngày, sắp xếp theo day ASC
			String productSoldOfDay = "SELECT p.productName, SUM(o.quantity) AS total_quantity, DATE(od.orderDate) AS dayMonthYear "
					+ "FROM order_items o " + "JOIN products p ON p.productID = o.productID "
					+ "JOIN orders od ON od.orderID = o.orderID  WHERE DATE_FORMAT(orderDate, '%m') = ? AND YEAR(orderDate) = ? "
					+ "GROUP BY p.productName, DATE(od.orderDate) " + "ORDER BY dayMonthYear ASC";

			PreparedStatement prepare = connect.prepareStatement(productSoldOfDay);
			prepare.setInt(1, selectedMonth);
			prepare.setInt(2, selectedYear);
			ResultSet rs = prepare.executeQuery();

			// Xóa dữ liệu cũ trên BarChart
			barChart.getData().clear();

			// Map để gom Series theo productName
			// (mỗi productName -> một Series)
			Map<String, XYChart.Series<String, Number>> seriesMap = new LinkedHashMap<>();

			// Dùng Set để thu thập tất cả các ngày, nhằm đặt thứ tự cho trục X
			Set<String> daySet = new LinkedHashSet<>();

			while (rs.next()) {
				String productName = rs.getString("productName");
				int totalQuantity = rs.getInt("total_quantity");
				String day = rs.getString("dayMonthYear"); // yyyy-MM-dd

				// Thêm ngày vào daySet để lát nữa setCategories cho trục X
				daySet.add(day);

				// Tìm series cho productName, nếu chưa có thì tạo mới
				XYChart.Series<String, Number> series = seriesMap.get(productName);
				if (series == null) {
					series = new XYChart.Series<>();
					series.setName(productName); // Tên series là tên sản phẩm
					seriesMap.put(productName, series);
				}
				// Thêm cột: trục X là day, trục Y là totalQuantity
				series.getData().add(new XYChart.Data<>(day, totalQuantity));
			}
			rs.close();
			prepare.close();

			// Đảm bảo BarChart có trục X là CategoryAxis
			CategoryAxis xAxis = (CategoryAxis) barChart.getXAxis();

			 xAxis.setAutoRanging(false);  
		     xAxis.getCategories().setAll(daySet);

			// Thêm tất cả series vào BarChart
			barChart.getData().addAll(seriesMap.values());

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void refreshRevenue() {
		yearcombobox.setValue(null);
		monthcombobox.setValue(null);
		displayYearlyRevenue();
	}

	@FXML
	public void refreshTypeProduct() {
		yearcomboboxProduct.setValue(null);
		monthcomboboxProduct.setValue(null);
		barChartProductYearly();
	}

	public void refreshAll() {
		displayPieChart();
		lineChart();
	}

	@FXML
	public void searchByProductName() {
		try {
			Connection connect = Database.connect();
			String sqlSelect = "SELECT * FROM products WHERE productName LIKE ?";
			PreparedStatement prepare = connect.prepareStatement(sqlSelect);
			if (nameTF.getText() == null || nameTF.getText().isEmpty()) {
				return;
			}
			String searchPattern = "%" + nameTF.getText().trim() + "%";
			prepare.setString(1, searchPattern);
			ResultSet rs = prepare.executeQuery();
			ObservableList<Product> searchResults = FXCollections.observableArrayList();
			while (rs.next()) {
				Product product = new Product();
				product.setProductID(rs.getInt("productID"));
				product.setThumbnail(rs.getBytes("thumbnail"));
				product.setProductName(rs.getString("productName"));
				product.setPrice(rs.getDouble("price"));
				product.setDescription(rs.getString("description"));
				product.setTypeProduct(rs.getString("typeProduct"));
				product.setStatus(rs.getString("status"));
				product.setProductCode(rs.getString("productCode"));
				product.setDate(rs.getDate("date")); // nếu trong DB là timestamp, bạn có thể chuyển đổi theo nhu cầu
				searchResults.add(product);
			}
			productTable.setItems(searchResults);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	ObservableList<String> optionsStatus = FXCollections.observableArrayList("Đang hoạt động", "Dừng hoạt động");

	@FXML
	public void searchByProductStatus() {
		try (Connection connect = Database.connect()) {
			String sqlSelect = "SELECT * FROM products WHERE status = ?";
			PreparedStatement prepare = connect.prepareStatement(sqlSelect);
			prepare.setString(1, statusCb.getValue());
			ResultSet rs = prepare.executeQuery();
			ObservableList<Product> searchResults = FXCollections.observableArrayList();

			while (rs.next()) {
				Product product = new Product();
				product.setProductID(rs.getInt("productID"));
				product.setThumbnail(rs.getBytes("thumbnail"));
				product.setProductName(rs.getString("productName"));
				product.setPrice(rs.getDouble("price"));
				product.setDescription(rs.getString("description"));
				product.setTypeProduct(rs.getString("typeProduct"));
				product.setStatus(rs.getString("status"));
				product.setProductCode(rs.getString("productCode"));
				product.setDate(rs.getDate("date"));
				searchResults.add(product);
			}

			productTable.setItems(searchResults);
			rs.close();
			prepare.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void backToListUser() {
		adminChat.setVisible(false);
		listUser.setVisible(true);
		lvUserList.getSelectionModel().clearSelection();
		selectedUserName = null;
	}

	ObservableList<Order> orderList = FXCollections.observableArrayList();

	public void displayOrder() {
		orderList.clear();
		try {
			Connection connect = Database.connect();
			String sql = "SELECT o.orderID, o.addressOrder, o.phoneOrder, o.totalPrice, o.orderDate, o.notes , u.fullName, "
					+ "u.Email FROM orders o JOIN " + "users u ON u.userID = o.userID "
							+ "ORDER BY orderID ASC";
			PreparedStatement prepare = connect.prepareStatement(sql);
			ResultSet rs = prepare.executeQuery();
			while (rs.next()) {
				int orderID = rs.getInt("orderID");
				String address = rs.getString("addressOrder");
				double totalPrice = rs.getDouble("totalPrice");
				Timestamp orderDate = rs.getTimestamp("orderDate");
				String fullName = rs.getString("fullName");
				String email = rs.getString("Email");
				String phoneNumber = rs.getString("phoneOrder");
				String note = rs.getString("notes");
				Order order = new Order(orderID, fullName, email, address, phoneNumber, totalPrice,
						orderDate.toLocalDateTime(), note);
				orderList.add(order);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		orderTable.setItems(orderList);
		orderTable.refresh();

	}

	@FXML
	public void reloadOrderList() {
		displayOrder();
		orderDatePicker.setValue(null);
		searchTF.clear();
	}

	@FXML
	public void switchDetailsOrder(Order order) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("DetailsOrder.fxml"));
			Parent viewDetailsOrder = loader.load();

			DetailsOrderController detailsOrderController = loader.getController();
			detailsOrderController.setDetailsOrder(order);

			Stage viewStage = new Stage();
			viewStage.setScene(new Scene(viewDetailsOrder));
			viewStage.initModality(Modality.APPLICATION_MODAL);
			viewStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		btnOverView.setStyle("-fx-background-color: #c7a087; -fx-border-color:  #532b12; -fx-text-fill: white");
		statusCb.setItems(optionsStatus);
		displayDashBoard();
		displayYearlyRevenue();
		barChartProductYearly();
		displayPieChart();
		lineChart();
		int currentYear = LocalDate.now().getYear();
		Integer currentMonth = LocalDate.now().getMonthValue();
		
		ObservableList<Integer> years = FXCollections.observableArrayList();
		ObservableList<Integer> months = FXCollections.observableArrayList();
		
		for (int y = 2000; y <= currentYear; y++) {
			years.add(y);
		}
		
		yearcombobox.setItems(years);
		
		yearcombobox.valueProperty().addListener((obs, oldYear, newYear) -> {
			if (newYear != null) {
				displayMonthlyRevenue(newYear);
			}
			monthcombobox.setValue(null);
		});

		for (int m = 1; m <= 12; m++) {
			months.add(m);
		}
		
		monthcombobox.setItems(months);
		
		monthcombobox.valueProperty().addListener((obs, oldMonth, newMonth) -> {
			if (newMonth != null && yearcombobox.getValue() != null) {
				int selectedYear = yearcombobox.getValue();
				displayDailyRevenue(newMonth, selectedYear);
			}
		});

		ObservableList<Integer> yearsProduct = FXCollections.observableArrayList();
		
		ObservableList<Integer> monthsProduct = FXCollections.observableArrayList();
		
		for (int y = 2000; y <= currentYear; y++) {
			yearsProduct.add(y);
		}
		
		yearcomboboxProduct.setItems(yearsProduct);
		
		yearcomboboxProduct.valueProperty().addListener((obs, oldYear, newYear) -> {
			if (newYear != null) {
				barChartProductMonthly(newYear);
			}
			monthcomboboxProduct.setValue(null);
		});

		for (int m = 1; m <= 12; m++) {
			monthsProduct.add(m);
		}
		
		monthcomboboxProduct.setItems(monthsProduct);
		
		monthcomboboxProduct.valueProperty().addListener((obs, oldMonth, newMonth) -> {
			if (newMonth != null && yearcomboboxProduct.getValue() != null) {
				int selectedYear = yearcomboboxProduct.getValue();
				barChartProductDaily(newMonth, selectedYear);
			}
		});

		Timeline refreshDashboard = new Timeline(new KeyFrame(Duration.seconds(1), event -> displayDashBoard()));
		refreshDashboard.setCycleCount(Timeline.INDEFINITE);
		refreshDashboard.play();

		lvUserList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				selectedUserName = newValue;

				unreadMessage.put(selectedUserName, false);
				lvUserList.refresh();

				listUser.setVisible(false);
				adminChat.setVisible(true);
				System.out.println("Selected user: " + selectedUserName);
				// Xóa toàn bộ tin nhắn cũ trước khi nạp lịch sử mới
				vBoxMessageContainer.getChildren().clear();
				loadChatHistory();
				currentUserName.setText(newValue);
			}
		});
		try {
			adminChannel = rabbitMQManager.createChannel();
			adminChannel.queueDeclare(ADMIN_QUEUE, true, false, false, null);
			adminChannel.queueBind(ADMIN_QUEUE, "support.direct", ADMIN_QUEUE);
			// Đăng ký consumer 1 lần khi khởi tạo
			consumerTag = adminChannel.basicConsume(ADMIN_QUEUE, false, (tag, delivery) -> {
				String messageStr = new String(delivery.getBody(), StandardCharsets.UTF_8);
				// Định dạng tin nhắn: "sender|chatContent"
				String[] parts = messageStr.split("\\|", 2);
				if (parts.length < 2) {
					System.out.println("Invalid message format: " + messageStr);
					adminChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
					return;
				}
				String sender = parts[0];
				String chatContent = parts[1];
				// Chỉ xử lý tin nhắn từ user đang được chọn
				if (adminChat.isVisible() && sender.equalsIgnoreCase(selectedUserName)) {
					ChatMessage incomingMsg = new ChatMessage(sender, "Admin", chatContent,
							new Timestamp(System.currentTimeMillis()));
					Platform.runLater(() -> {
						// Nếu signature chưa tồn tại, hiển thị tin nhắn
						displayChatMessage(incomingMsg);
					});
				} else {
					unreadMessage.put(sender, true);
					Platform.runLater(() -> lvUserList.refresh());
					System.out.println("Tin nhắn từ " + sender + " (không phải chat hiện tại)");
				}
				adminChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
			}, tag -> {
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		lvUserList.setCellFactory(lv -> new ListCell<String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setStyle(" -fx-padding: 10px; "); // Reset style nếu không có dữ liệu
					setOnMouseEntered(null);
					setOnMouseExited(null);
				} else {
					setText(item);

					// Base style cho mỗi cell
					final String baseStyle = "-fx-font-size: 20px; -fx-padding: 10px; "
							+ "-fx-background-color: #fff; -fx-border-color: #ddd; -fx-text-fill: black;";

					final boolean isUnread = unreadMessage.getOrDefault(item, false);
					// Nếu có tin chưa đọc, thêm in đậm và text màu đỏ
					final String defaultStyle = isUnread ? baseStyle + " -fx-font-weight: bold; -fx-text-fill: red;"
							: baseStyle;

					// Áp dụng style mặc định
					setStyle(defaultStyle);

					// Hover effect: khi di chuột vào, chỉ thay đổi nền và text-fill (dựa vào
					// defaultStyle)
					setOnMouseEntered(event -> setStyle(
							defaultStyle + " -fx-background-color: #b19585; -fx-text-fill: white; -fx-cursor: hand;"));
					setOnMouseExited(event -> setStyle(defaultStyle));
				}
			}
		});

		productID.setCellValueFactory(new PropertyValueFactory<>("productID"));
		// Giả sử Product có phương thức getThumbnailImageView() trả về ImageView hiển
		// thị hình ảnh
		thumbnail.setCellValueFactory(new PropertyValueFactory<>("thumbnailImageView"));
		productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
		price.setCellValueFactory(new PropertyValueFactory<>("price"));
		description.setCellValueFactory(new PropertyValueFactory<>("description"));
		typeProduct.setCellValueFactory(new PropertyValueFactory<>("typeProduct"));
		status.setCellValueFactory(new PropertyValueFactory<>("status"));
		productCode.setCellValueFactory(new PropertyValueFactory<>("productCode"));
		date.setCellValueFactory(new PropertyValueFactory<>("date"));

		productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		date.setCellFactory(column -> new TableCell<Product, java.util.Date>() {
			@Override
			protected void updateItem(java.util.Date item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				} else {
					// Chuyển java.sql.Date sang LocalDate
					LocalDate localDate = Instant.ofEpochMilli(item.getTime()).atZone(ZoneId.systemDefault())
							.toLocalDate();
					// Định dạng LocalDate theo "yyyy-M-d"
					setText(localDate.format(formatter));
				}
			}
		});

		price.setCellFactory(column -> new TableCell<Product, Double>() {
			@Override
			protected void updateItem(Double item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				} else {
					setText(PRICE_FORMATTER.format(item));
				}
			}
		});

		

		// Load dữ liệu sản phẩm ban đầu
		reloadProductList();

		// Tạo cell cho cột nút "Details"
		buttonColumn.setCellFactory(col -> new TableCell<>() {
			private final HBox buttonBox = new HBox();
			private final Button detailsButton = new Button("Chi tiết");

			{
				buttonBox.getChildren().add(detailsButton);
				buttonBox.setAlignment(Pos.CENTER);
				detailsButton.setStyle("-fx-border-color:  #532b12; -fx-background-color: white;");

				detailsButton.setOnMouseEntered(
						e -> detailsButton.setStyle("-fx-background-color:  #532b12; -fx-text-fill: white; "
								+ "-fx-border-color:  #532b12; -fx-cursor: hand ;"));
				detailsButton.setOnMouseExited(e -> detailsButton
						.setStyle("-fx-background-color: white; -fx-text-fill: #532b12; -fx-border-color:  #532b12;"));

				detailsButton.setOnAction(event -> {
					Product product = getTableView().getItems().get(getIndex());
					switchDetails(product);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					setGraphic(buttonBox);
				}
			}
		});

		description.setCellFactory(tc -> {
			TableCell<Product, String> cell = new TableCell<>() {
				private final Text text = new Text();

				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
						setGraphic(null);
					} else {
						text.setText(item);
						text.setWrappingWidth(200); // Đặt độ rộng tối đa để xuống dòng
						setGraphic(text);
					}
				}
			};
			return cell;
		});

		productName.setCellFactory(tc -> {
			TableCell<Product, String> cell = new TableCell<>() {
				private final Text text = new Text();

				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
						setGraphic(null);
					} else {
						text.setText(item);
						text.setWrappingWidth(120); // Đặt độ rộng tối đa để xuống dòng
						setGraphic(text);
					}
				}
			};
			return cell;
		});

		typeProduct.setCellFactory(tc -> {
			TableCell<Product, String> cell = new TableCell<>() {
				private final Text text = new Text();

				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
						setGraphic(null);
					} else {
						text.setText(item);
						text.setWrappingWidth(90); // Đặt độ rộng tối đa để xuống dòng
						setGraphic(text);
					}
				}
			};
			return cell;
		});
		
		displayOrder();
		orderID.setCellValueFactory(new PropertyValueFactory<>("orderID"));
		customerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
		customerEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		customerAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
		customerPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
		orderTotal.setCellValueFactory(new PropertyValueFactory<>("orderTotalPrice"));
		dateOrder.setCellValueFactory(new PropertyValueFactory<>("dateOrder"));
		
		buttonDetailOrder.setCellFactory(col -> new TableCell<>() {
			private final HBox buttonBox = new HBox();
			private final Button detailsButton = new Button("Chi tiết");

			{
				buttonBox.getChildren().add(detailsButton);
				buttonBox.setAlignment(Pos.CENTER);
				detailsButton.setStyle("-fx-border-color:  #532b12; -fx-background-color: white;");

				detailsButton.setOnMouseEntered(
						e -> detailsButton.setStyle("-fx-background-color:  #532b12; -fx-text-fill: white; "
								+ "-fx-border-color:  #532b12; -fx-cursor: hand ;"));
				detailsButton.setOnMouseExited(e -> detailsButton
						.setStyle("-fx-background-color: white; -fx-text-fill: #532b12; -fx-border-color:  #532b12;"));

				detailsButton.setOnAction(event -> {
					Order order = getTableView().getItems().get(getIndex());
					switchDetailsOrder(order);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					setGraphic(buttonBox);
				}
			}
		});


		FilteredList<Order> filteredData = new FilteredList<>(orderList, p -> true);
		
		searchTF.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(order -> {
                // Nếu ô tìm kiếm rỗng, trả về true cho tất cả
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                
                if (String.valueOf(order.getOrderID()).contains(lowerCaseFilter)) {
                    return true;
                } else if (order.getCustomerName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
//                    else if (order.getPhoneNumber().toLowerCase().contains(lowerCaseFilter)) {
//                    return true;
//                } 
                return false;
            });
        });

		
		orderDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(order -> {
				if (newValue == null)
					return true;
				LocalDate orderDate = order.getDateOrder().toLocalDate();
				return orderDate.equals(newValue);
			});
		});
		orderTable.setItems(filteredData);

		orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("MM/dd/yyyy");

		dateOrder.setCellFactory(column -> new TableCell<Order, LocalDateTime>() {
			@Override
			protected void updateItem(LocalDateTime item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				} else {
					// Lấy LocalDate từ LocalDateTime
					LocalDate localDate = item.toLocalDate();
					// Định dạng LocalDate theo "yyyy-MM-dd"
					setText(localDate.format(formatTime));
				}
			}
		});

		orderTotal.setCellFactory(column -> new TableCell<Order, Double>() {
			@Override
			protected void updateItem(Double item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				} else {
					setText(PRICE_FORMATTER.format(item));
				}
			}
		});

		customerName.setCellFactory(tc -> {
			TableCell<Order, String> cell = new TableCell<>() {
				private final Text text = new Text();

				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
						setGraphic(null);
					} else {
						text.setText(item);
						text.setWrappingWidth(140); // Đặt độ rộng tối đa để xuống dòng
						setGraphic(text);
					}
				}
			};
			return cell;
		});

		customerEmail.setCellFactory(tc -> {
			TableCell<Order, String> cell = new TableCell<>() {
				private final Text text = new Text();

				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
						setGraphic(null);
					} else {
						text.setText(item);
						text.setWrappingWidth(180); // Đặt độ rộng tối đa để xuống dòng
						setGraphic(text);
					}
				}
			};
			return cell;
		});

		customerAddress.setCellFactory(tc -> {
			TableCell<Order, String> cell = new TableCell<>() {
				private final Text text = new Text();

				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
						setGraphic(null);
					} else {
						text.setText(item);
						text.setWrappingWidth(250); // Đặt độ rộng tối đa để xuống dòng
						setGraphic(text);
					}
				}
			};
			return cell;
		});

	}
}
