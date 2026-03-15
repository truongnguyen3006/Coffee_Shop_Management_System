package application.controller;


import application.*;
import application.model.*;
import application.repository.*;
import application.service.*;
import application.messaging.*;
import application.util.*;
import application.config.*;

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

import java.util.logging.Logger;

import application.config.AdminSessionManager;
import application.model.DashboardSummary;
import application.repository.DashboardRepository.ProductChartData;
import application.service.DashboardService;
import application.service.OrderService;
import application.service.ProductService;
import application.service.RevenueService;
import application.util.AppLogger;
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
	private static final Logger LOGGER = AppLogger.getLogger(MainLayoutAdmin.class);

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

	private final ProductService productService = new ProductService();
	private final DashboardService dashboardService = new DashboardService();
	private final RevenueService revenueService = new RevenueService();
	private final OrderService orderService = new OrderService();

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

		try (Connection connect = Database.connect();
				PreparedStatement pstmt = connect
						.prepareStatement("SELECT DISTINCT sender FROM chat_messages WHERE receiver = ? AND sender <> ?")) {
			pstmt.setString(1, "Admin");
			pstmt.setString(2, "Admin");
			try (ResultSet rs = pstmt.executeQuery()) {
				userList.clear();
				while (rs.next()) {
					userList.add(rs.getString("sender"));
				}
			}
		} catch (SQLException e) {
			AppLogger.error(LOGGER, "Không thể tải danh sách người dùng chat support", e);
			AlertMessage.showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không tải được danh sách chat",
					"Đã xảy ra lỗi khi tải danh sách người dùng cần hỗ trợ.");
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
		String message = txtMessage.getText().trim();
		if (message.isEmpty())
			return;
		ChatMessage chatMsg = new ChatMessage("Admin", selectedUserName, message,
				new Timestamp(System.currentTimeMillis()));
		displayChatMessage(chatMsg);
		String userRoutingKey = "user." + selectedUserName;
		rabbitMQManager.sendMessage(userRoutingKey, message);
		txtMessage.clear();

		try {
			chatHistoryDAO.saveMessage(chatMsg);
		} catch (SQLException e) {
			AppLogger.error(LOGGER, "Không thể lưu lịch sử chat gửi tới user=" + selectedUserName, e);
			AlertMessage.showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không lưu được tin nhắn",
					"Tin nhắn đã gửi nhưng không thể lưu lịch sử hội thoại.");
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

		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Xác nhận đăng xuất");
		alert.setHeaderText("Bạn có chắc chắn muốn đăng xuất?");
		alert.setContentText("Xác nhận đăng xuất khỏi tài khoản?");

		ButtonType buttonTypeYes = new ButtonType("YES", ButtonBar.ButtonData.YES);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == buttonTypeYes) {
			try {
				new AdminSessionManager().logout();
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Login.fxml"));
				Parent parent = loader.load();
				Stage homeStage = new Stage();
				homeStage.setScene(new Scene(parent));
				homeStage.show();

				Stage stage = (Stage) mainLayoutAdmin.getScene().getWindow();
				stage.close();
			} catch (Exception e) {
				AppLogger.error(LOGGER, "Không thể đăng xuất admin", e);
				AlertMessage.showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Đăng xuất thất bại",
						"Không thể quay về màn hình đăng nhập. Vui lòng thử lại.");
			}
		}
	}

	private void loadChatHistory() {
		if (selectedUserName == null)
			return;
		try {
			messageList = chatHistoryDAO.getMessages(selectedUserName, "Admin");
			vBoxMessageContainer.getChildren().clear();
			for (ChatMessage msg : messageList) {
				displayChatMessage(msg);
			}
		} catch (SQLException e) {
			AppLogger.error(LOGGER, "Không thể tải lịch sử chat với user=" + selectedUserName, e);
			AlertMessage.showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không tải được lịch sử chat",
					"Đã xảy ra lỗi khi tải lịch sử trò chuyện.");
		}
	}

	@FXML
	public void switchAdd() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/AddAndUpdateProduct.fxml"));
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
			AppLogger.error(LOGGER, "Không thể mở màn hình thêm sản phẩm", e);
			AlertMessage.showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không mở được màn hình thêm sản phẩm",
					"Đã xảy ra lỗi khi tải màn hình thêm sản phẩm.");
		}
	}

	@FXML
	public void switchDetails(Product product) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/ProductDetails.fxml"));
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
			AppLogger.error(LOGGER, "Không thể mở chi tiết sản phẩm", e);
			AlertMessage.showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không mở được chi tiết sản phẩm",
					"Đã xảy ra lỗi khi mở chi tiết sản phẩm.");
		}
	}

	public void reloadProductList() {
		try {
			productList.setAll(productService.getAllProducts());
			productTable.setItems(productList);
			productTable.refresh();
		} catch (SQLException e) {
			AppLogger.error(LOGGER, "Không thể tải danh sách sản phẩm", e);
			AlertMessage.showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không tải được sản phẩm",
					"Đã xảy ra lỗi khi tải danh sách sản phẩm. Vui lòng thử lại.");
		}
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
		try {
			DashboardSummary summary = dashboardService.getDashboardSummary();
			lbCustomer.setText(String.valueOf(summary.getCustomerCount()));
			lbMoneyDay.setText(PRICE_FORMATTER.format(summary.getRevenueToday()));
			lbDay.setText(summary.getRevenueDate() == null ? "" : summary.getRevenueDate());
			lbRevenue.setText(PRICE_FORMATTER.format(summary.getTotalRevenue()));
			lbSoldPorduct.setText(PRICE_FORMATTER.format(summary.getTotalProductsSold()));
		} catch (SQLException e) {
			AppLogger.error(LOGGER, "Không thể tải dashboard tổng quan", e);
		}
	}

	public void displayPieChart() {
		try {
			pieChart.getData().setAll(dashboardService.getPieChartData());
		} catch (SQLException e) {
			AppLogger.error(LOGGER, "Không thể tải biểu đồ cơ cấu sản phẩm", e);
		}
	}

	public void displayYearlyRevenue() {
		try {
			renderAreaChart("Doanh thu theo năm", revenueService.getYearlyRevenue());
		} catch (SQLException e) {
			AppLogger.error(LOGGER, "Không thể tải doanh thu theo năm", e);
		}
	}

	public void displayMonthlyRevenue(int selectedYear) {
		try {
			renderAreaChart("Doanh thu theo tháng năm", revenueService.getMonthlyRevenue(selectedYear));
		} catch (SQLException e) {
			AppLogger.error(LOGGER, "Không thể tải doanh thu theo tháng cho năm=" + selectedYear, e);
		}
	}

	public void displayDailyRevenue(int selectedMonth, int selectedYear) {
		try {
			renderAreaChart("Doanh thu theo ngày tháng", revenueService.getDailyRevenue(selectedMonth, selectedYear));
		} catch (SQLException e) {
			AppLogger.error(LOGGER, "Không thể tải doanh thu theo ngày cho tháng=" + selectedMonth + ", năm=" + selectedYear, e);
		}
	}

	public void lineChart() {
		try {
			renderLineChart("Doanh thu theo ngày", revenueService.getRevenueLineChart());
		} catch (SQLException e) {
			AppLogger.error(LOGGER, "Không thể tải line chart doanh thu", e);
		}
	}

	public void barChartProductYearly() {
		try {
			renderBarChart(dashboardService.getYearlyProductSales());
		} catch (SQLException e) {
			AppLogger.error(LOGGER, "Không thể tải biểu đồ sản phẩm theo năm", e);
		}
	}

	public void barChartProductMonthly(int selectedYear) {
		try {
			renderBarChart(dashboardService.getMonthlyProductSales(selectedYear));
		} catch (SQLException e) {
			AppLogger.error(LOGGER, "Không thể tải biểu đồ sản phẩm theo tháng cho năm=" + selectedYear, e);
		}
	}


	public void barChartProductDaily(int selectedMonth, int selectedYear) {
		try {
			renderBarChart(dashboardService.getDailyProductSales(selectedMonth, selectedYear));
		} catch (SQLException e) {
			AppLogger.error(LOGGER, "Không thể tải biểu đồ sản phẩm theo ngày cho tháng=" + selectedMonth + ", năm=" + selectedYear, e);
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
		String keyword = nameTF.getText();
		if (keyword == null || keyword.trim().isEmpty()) {
			reloadProductList();
			return;
		}
		try {
			productTable.setItems(FXCollections.observableArrayList(productService.searchByName(keyword)));
		} catch (SQLException e) {
			AppLogger.error(LOGGER, "Không thể tìm sản phẩm theo tên: " + keyword, e);
			AlertMessage.showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Tìm kiếm thất bại",
					"Không thể tìm sản phẩm theo tên lúc này.");
		}
	}

	ObservableList<String> optionsStatus = FXCollections.observableArrayList("Đang hoạt động", "Dừng hoạt động");

	@FXML
	public void searchByProductStatus() {
		String selectedStatus = statusCb.getValue();
		if (selectedStatus == null || selectedStatus.trim().isEmpty()) {
			reloadProductList();
			return;
		}
		try {
			productTable.setItems(FXCollections.observableArrayList(productService.searchByStatus(selectedStatus)));
		} catch (SQLException e) {
			AppLogger.error(LOGGER, "Không thể lọc sản phẩm theo trạng thái: " + selectedStatus, e);
			AlertMessage.showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Lọc sản phẩm thất bại",
					"Không thể lọc sản phẩm theo trạng thái lúc này.");
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
		try {
			orderList.setAll(orderService.getAllOrders());
			orderTable.refresh();
		} catch (SQLException e) {
			AppLogger.error(LOGGER, "Không thể tải danh sách đơn hàng", e);
			AlertMessage.showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không tải được đơn hàng",
					"Đã xảy ra lỗi khi tải danh sách đơn hàng. Vui lòng thử lại.");
		}
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
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/DetailsOrder.fxml"));
			Parent viewDetailsOrder = loader.load();

			DetailsOrderController detailsOrderController = loader.getController();
			detailsOrderController.setDetailsOrder(order);

			Stage viewStage = new Stage();
			viewStage.setScene(new Scene(viewDetailsOrder));
			viewStage.initModality(Modality.APPLICATION_MODAL);
			viewStage.show();
		} catch (IOException e) {
			AppLogger.error(LOGGER, "Không thể mở chi tiết đơn hàng", e);
			AlertMessage.showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không mở được chi tiết đơn hàng",
					"Đã xảy ra lỗi khi mở chi tiết đơn hàng.");
		}
	}

	private void renderAreaChart(String seriesName, Map<String, Number> values) {
		areaChart.getData().clear();
		XYChart.Series<String, Number> seriesArea = new XYChart.Series<>();
		seriesArea.setName(seriesName);
		values.forEach((timeKey, revenue) -> seriesArea.getData().add(new XYChart.Data<>(timeKey, revenue)));
		areaChart.getData().add(seriesArea);
	}

	private void renderLineChart(String seriesName, Map<String, Number> values) {
		lineChart.getData().clear();
		XYChart.Series<String, Number> seriesLineChart = new XYChart.Series<>();
		seriesLineChart.setName(seriesName);
		values.forEach((timeKey, revenue) -> seriesLineChart.getData().add(new XYChart.Data<>(timeKey, revenue)));
		lineChart.getData().add(seriesLineChart);
	}

	private void renderBarChart(ProductChartData productChartData) {
		barChart.getData().clear();
		CategoryAxis xAxis = (CategoryAxis) barChart.getXAxis();
		xAxis.setAutoRanging(false);
		xAxis.getCategories().setAll(productChartData.getCategories());
		for (Map.Entry<String, Map<String, Number>> entry : productChartData.getSeriesMap().entrySet()) {
			XYChart.Series<String, Number> series = new XYChart.Series<>();
			series.setName(entry.getKey());
			entry.getValue().forEach((category, value) -> series.getData().add(new XYChart.Data<>(category, value)));
			barChart.getData().add(series);
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
			AppLogger.error(LOGGER, "Không thể khởi tạo RabbitMQ admin consumer", e);
			AlertMessage.showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không kết nối được hỗ trợ trực tuyến",
					"Tính năng chat hỗ trợ hiện chưa sẵn sàng.");
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
