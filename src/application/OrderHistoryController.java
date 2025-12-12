package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class OrderHistoryController implements Initializable {

	@FXML
	private TableColumn<Order, Void> buttonDetailOrder;

	@FXML
	private TableColumn<Order, LocalDateTime> dateOrder;

	@FXML
	private TableView<Order> historyTable;

	@FXML
	private TableColumn<Order, Integer> orderID;

	@FXML
	private TableColumn<Order, Double> orderTotal;
	
	public static final DecimalFormat PRICE_FORMATTER = new DecimalFormat("#,###");
	
	@FXML
	public void switchDetailsOrderHistory(Order order) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("DetailsOrderHistory.fxml"));
			Parent viewDetailsOrder = loader.load();

			DetailsOrderHistoryController detailsOrderHistoryController = loader.getController();
			detailsOrderHistoryController.setDetailsOrderHistory(order);

			Stage viewStage = new Stage();
			viewStage.setScene(new Scene(viewDetailsOrder));
			viewStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	ObservableList<Order> orderList = FXCollections.observableArrayList();
	
	public void displayOrder() {
		orderList.clear();
		try {
			Connection connect = Database.connect();
			String sql = "SELECT o.orderID, o.totalPrice, o.orderDate "
					+ "FROM orders o "
					+ "JOIN users u ON u.userID = o.userID "
					+ "Where u.userID = ?;";
			PreparedStatement prepare = connect.prepareStatement(sql);
			prepare.setInt(1, Session.getUserID());
			ResultSet rs = prepare.executeQuery();
			while (rs.next()) {
				int orderID = rs.getInt("orderID");
				double totalPrice = rs.getDouble("totalPrice");
				Timestamp orderDate = rs.getTimestamp("orderDate");
				Order order = new Order(orderID, totalPrice, orderDate.toLocalDateTime());
				orderList.add(order);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		historyTable.setItems(orderList);
		historyTable.refresh();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		displayOrder();
		orderID.setCellValueFactory(new PropertyValueFactory<>("orderID"));
		orderTotal.setCellValueFactory(new PropertyValueFactory<>("orderTotalPrice"));
		dateOrder.setCellValueFactory(new PropertyValueFactory<>("dateOrder"));
		
		historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
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
					switchDetailsOrderHistory(order);
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
		
		DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

		dateOrder.setCellFactory(column -> new TableCell<Order, LocalDateTime>() {
			@Override
			protected void updateItem(LocalDateTime item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				} else {
					setText(item.format(formatTime));
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
	}

}
