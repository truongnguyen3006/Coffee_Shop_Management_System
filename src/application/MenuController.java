package application;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

public class MenuController implements Initializable {

	@FXML
	private FlowPane productListContainer;

	@FXML
	private Button btnAll;

	@FXML
	private Button btnAme;

	@FXML
	private Button btnCoffee;

	@FXML
	private Button btnMilkTea;

	@FXML
	private Button btnThucUong;

	@FXML
	private Button btnTraXanh;

	@FXML
	private Button btnTraiXay;

	@FXML
	private Label labelType;

	@FXML
	ImageView productImage;

	@FXML
	private Label productName;

	@FXML
	private Label productPrice;

	@FXML
	private TextField textField;

	@FXML
	private ScrollPane scrollPane;

	@FXML
	void enterSearch() {
		String keyWord = textField.getText().trim();
		if (keyWord.isEmpty()) {
			displayAll();
		}

		ProductDAO productDAO = new ProductDAO();
		List<Product> productList = productDAO.searchProduct(keyWord);
		productListContainer.getChildren().clear();
		if (!productList.isEmpty()) {
			labelType.setVisible(true);
			productListContainer.setAlignment(Pos.TOP_LEFT);
			for (Product product : productList) {
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("ProductCard.fxml"));
					Parent productCard = loader.load();

					ProductCardController productCardController = loader.getController();
					productCardController.setData(product);

					productListContainer.getChildren().add(productCard);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			Label notFind = new Label("Sản phẩm " + '"' + keyWord + '"' + " không tìm thấy");
			notFind.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
			productListContainer.setAlignment(Pos.TOP_CENTER);
			productListContainer.getChildren().add(notFind);
			labelType.setVisible(false);
		}

	}

	@FXML
	public void displayAllProduct() {
		ProductDAO productDAO = new ProductDAO();
		List<Product> productList = productDAO.getAllProductsSatus();
		productListContainer.getChildren().clear();
		for (Product product : productList) {
			try {
				// Load FXML của từng card sản phẩm
				FXMLLoader loader = new FXMLLoader(getClass().getResource("ProductCard.fxml"));
				Node card = loader.load();

				// Lấy controller của card để set dữ liệu
				ProductCardController controller = loader.getController();
				controller.setData(product);

				// Thêm card vào container
				productListContainer.getChildren().add(card);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String displayProducts(String btnType) {
		String type = "";
		ProductDAO productDAO = new ProductDAO();
		List<Product> productList = productDAO.getAllProductByType(btnType);
		productListContainer.getChildren().clear();
		for (Product product : productList) {
	        // Giả sử model Product có getter getStatus() trả về "Đang hoạt động" hoặc "Đã dừng"
	        if (!"Đang hoạt động".equalsIgnoreCase(product.getStatus())) {
	            // nếu status khác "Đang hoạt động", bỏ qua không tạo card
	            continue;
	        }

			try {
				type = product.getTypeProduct();
				// Load FXML của từng card sản phẩm
				FXMLLoader loader = new FXMLLoader(getClass().getResource("ProductCard.fxml"));
				Node card = loader.load();

				// Lấy controller của card để set dữ liệu
				ProductCardController cardController = loader.getController();
				cardController.setData(product);

				// Thêm card vào container
				productListContainer.getChildren().add(card);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Platform.runLater(() -> {
		    productListContainer.applyCss();
		    productListContainer.layout();
		    scrollPane.setVvalue(0);
		});

		return type;
		
	}

	@FXML
	public void displayCoffee() {
		String type = displayProducts(btnCoffee.getText());
		labelType.setText(type);
	}

	@FXML
	public void displayMilkTea() {
		String type = displayProducts(btnMilkTea.getText());
		labelType.setText(type);
	}

	@FXML
	public void displayAmericano() {
		String type = displayProducts(btnAme.getText());
		labelType.setText(type);
	}

	@FXML
	public void displayTraiXay() {
		String type = displayProducts(btnTraiXay.getText());
		labelType.setText(type);
	}

	@FXML
	public void displayTraXanh() {
		String type = displayProducts(btnTraXanh.getText());
		labelType.setText(type);
	}

	@FXML
	public void displayThucUong() {
		String type = displayProducts(btnThucUong.getText());
		labelType.setText(type);
	}

	@FXML
	public void displayAll() {
		displayAllProduct();
		String type = btnAll.getText();
		labelType.setText(type);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		displayAll();
	}

}
