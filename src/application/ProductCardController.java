package application;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;



public class ProductCardController implements Initializable {
	public static final DecimalFormat PRICE_FORMATTER = new DecimalFormat("#,###");
	@FXML
	ImageView productImage;

	@FXML
	private Label productName;

	@FXML
	private Label productPrice;

	@FXML
	private Button btnAddToCart;

	private Product currentProduct;

	public void setData(Product product) {
		// Lưu sản phẩm hiện tại để truyền sang trang chi tiết khi click nút Detail
		this.currentProduct = product;
		productName.setText(product.getProductName()); // Đặt tên sản phẩm
//		productPrice.setText(String.format("%.3f", product.getPrice()));
		productPrice.setText(PRICE_FORMATTER.format(product.getPrice()));

		// Nếu có ảnh thì chuyển đổi byte[] sang Image và hiển thị
		if (product.getThumbnail() != null) {
			Image image = new Image(new ByteArrayInputStream(product.getThumbnail()));
			productImage.setImage(image);
		}
	}

	@FXML
	public void switchDetailProduct() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ProductDetailsCard.fxml"));
			Parent detailView = loader.load();
			// Lấy controller của trang chi tiết
			ProductDetailsCardController detailsCardController = loader.getController();
			// Truyền dữ liệu sản phẩm hiện tại sang trang chi tiết
			detailsCardController.setProductData(currentProduct);
			// Lấy userID từ Session và truyền cho controller trang chi tiết
			detailsCardController.setCurrentUserID(Session.getUserID());

			BorderPane mainLayout = (BorderPane) productName.getScene().getRoot();
			mainLayout.setCenter(detailView);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// cat anh thanh hinh tron
//		productImageH.setFitWidth(100);
//		productImageH.setFitHeight(100);
//		    
//		    // Đảm bảo ảnh không bị méo
//		productImageH.setPreserveRatio(false);
//
//		    // Xác định bán kính hình tròn
//		    double radius = 50; // Vì ảnh là 100x100, bán kính là 50
//
//		    // Đặt clip hình tròn
//		    Circle clip = new Circle(50, 50, radius);
//		    productImageH.setClip(clip);
		Rectangle clip = new Rectangle(productImage.getFitWidth(), productImage.getFitHeight());
		clip.setArcWidth(20);
		clip.setArcHeight(20);
		productImage.setClip(clip);

	}

}
