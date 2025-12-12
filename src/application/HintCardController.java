package application;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

public class HintCardController implements Initializable {

	@FXML
	private ImageView productImage;

	@FXML
	private Label productName;

	@FXML
	private Label productPrice;

	private Product curentProduct;
	
	public static final DecimalFormat PRICE_FORMATTER = new DecimalFormat("#,###");

	public void setData(Product product) {
		this.curentProduct = product;
		productName.setText(product.getProductName());
//		productPrice.setText(String.format("%.3f", product.getPrice()));
		productPrice.setText(PRICE_FORMATTER.format(product.getPrice()));
		productImage.setImage(product.getThumbnailImage());
	}

	@FXML
	void switchProductDetailsCard(MouseEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ProductDetailsCard.fxml"));
			Parent view = loader.load();

			ProductDetailsCardController productDetailsCardController = loader.getController();

			productDetailsCardController.setProductData(curentProduct);

			// loi o cho nay do khong truyen userID qua ben productdetailscard khi chuyen
			// trang nen khong them duoc
			productDetailsCardController.setCurrentUserID(Session.getUserID());

			BorderPane mainLayout = (BorderPane) productName.getScene().getRoot();
			mainLayout.setCenter(view);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}

}
