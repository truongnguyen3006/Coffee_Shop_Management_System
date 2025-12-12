package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

public class DetailsOrderController implements Initializable {

	@FXML
	private Label customerAddress;

	@FXML
	private Label customerDate;

	@FXML
	private Label customerName;

	@FXML
	private Label customerEmail;

	@FXML
	private Label customerOrderID;

	@FXML
	private Label orderToTal;

	@FXML
	private Label orderToTalVAT;

	@FXML
	private Label customerNote;

	@FXML
	private Label customerPhone;

	@FXML
	private TableColumn<OrderItems, String> productName;

	@FXML
	private TableColumn<OrderItems, Double> productPrice;

	@FXML
	private TableColumn<OrderItems, Integer> productQuantity;

	@FXML
	private TableColumn<OrderItems, String> productSize;

	@FXML
	private TableColumn<OrderItems, String> productTopping;

	@FXML
	private TableColumn<OrderItems, Double> productTotalUnit;

	@FXML
	private TableView<OrderItems> tableOrderDetailList;

	public static final DecimalFormat PRICE_FORMATTER = new DecimalFormat("#,###");

	private int orderID;

	public void setDetailsOrder(Order order) {

//		System.out.println("Order Note: " + order.getNote());

		this.orderID = order.getOrderID();
		// TODO Auto-generated method stub
		customerName.setText(order.getCustomerName());
		customerAddress.setText(order.getAddress());
		customerEmail.setText(order.getEmail());
		customerOrderID.setText(String.valueOf(order.getOrderID()));
		customerNote.setText(order.getNote());
		customerPhone.setText(order.getPhoneNumber());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		customerDate.setText(order.getDateOrder().format(formatter));

		displayOrderDetails();
	}

	ObservableList<OrderItems> orderDetailsList = FXCollections.observableArrayList();

	public void displayOrderDetails() {
		orderDetailsList.clear();
		double totalPriceVAT = 0;
		try {
			Connection connect = Database.connect();
			String sql = "SELECT p.productName, o.unitPrice, o.quantity, o.size, o.toppingName, od.totalPrice "
					+ "FROM order_items o JOIN products p on p.productID = o.productID "
					+ "JOIN orders od on od.orderID = o.orderID " + "where o.orderID = ?;";
			PreparedStatement prepare = connect.prepareStatement(sql);
			prepare.setInt(1, orderID);
			ResultSet rs = prepare.executeQuery();
			while (rs.next()) {
				String productName = rs.getString("productName");
				double price = rs.getDouble("unitPrice");
				int quantity = rs.getInt("quantity");
				String size = rs.getString("size");
				String topping = rs.getString("toppingName");
				double totalPriceUnit = quantity * price;
				totalPriceVAT = rs.getDouble("totalPrice");
				OrderItems orderItems = new OrderItems(productName, price, quantity, size, topping, totalPriceUnit);
				orderDetailsList.add(orderItems);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tableOrderDetailList.setItems(orderDetailsList);
		tableOrderDetailList.refresh();

		orderToTal.setText((PRICE_FORMATTER.format(sumPrice())));
		orderToTalVAT.setText((PRICE_FORMATTER.format(totalPriceVAT)));
	}

	public double sumPrice() {
		double sum = 0;
		for (OrderItems i : orderDetailsList) {
			sum += i.getProductPrice() * i.getProductQuantity();
		}
		return sum;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
		productPrice.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
		productQuantity.setCellValueFactory(new PropertyValueFactory<>("productQuantity"));
		productSize.setCellValueFactory(new PropertyValueFactory<>("productSize"));
		productTopping.setCellValueFactory(new PropertyValueFactory<>("topping"));
		productTotalUnit.setCellValueFactory(new PropertyValueFactory<>("sum"));

		tableOrderDetailList.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		productPrice.setCellFactory(column -> new TableCell<OrderItems, Double>() {
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

		productTotalUnit.setCellFactory(column -> new TableCell<OrderItems, Double>() {
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

		productTopping.setCellFactory(tc -> {
			TableCell<OrderItems, String> cell = new TableCell<>() {
				private final Text text = new Text();

				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
						setGraphic(null);
					} else {
						text.setText(item);
						text.setWrappingWidth(100); // Đặt độ rộng tối đa để xuống dòng
						setGraphic(text);
					}
				}
			};
			return cell;
		});

		productName.setCellFactory(tc -> {
			TableCell<OrderItems, String> cell = new TableCell<>() {
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
	}

}
