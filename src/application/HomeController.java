package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.rmi.server.LoaderHandler;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class HomeController implements Initializable {
	
	@FXML
    public ImageView imageView1;
	
	@FXML
    public ImageView imageView2;

    @FXML
    private Button btnHome;

    @FXML
    private Button btnMenu;

    @FXML
    private Button leftBtn;
    
    @FXML
    private Button rightBtn;
    
    @FXML
    private FlowPane productBestSellerListContainer;   
    
    
    public LoginController loginController;
    
    private List<Image> images;
    private int currentImageIndex = 0;
    private boolean showingFirst = true;
    private boolean isTransitionRunning = false;
    private Timeline autoSlideTimeline;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageBanner();
        startAutoSlide();
        displayProductBestSeller();
    }
    
	public void displayProductBestSeller() {
		ProductDAO productDAO = new ProductDAO();
		List<Product> productList = productDAO.getAllProductsBestSeller();
		productBestSellerListContainer.getChildren().clear();
		for (Product product : productList) {
			try {
				// Load FXML của từng card sản phẩm
				FXMLLoader loader = new FXMLLoader(getClass().getResource("ProductCard.fxml"));
				Node card = loader.load();

				// Lấy controller của card để set dữ liệu
				ProductCardController controller = loader.getController();
				controller.setData(product);

				// Thêm card vào container
				productBestSellerListContainer.getChildren().add(card);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

    

    public void imageBanner() {
    	images = List.of(
    		    new Image("/Banner/z6476239459381_4e5fcf032f68db1742b2608e5b5c9c4e.jpg"),
    		    new Image("/Banner/z6460518110998_5cf949f40bb93cd0fa7050e694a2a4a6.jpg"),
    		    new Image("/Banner/snapedit_1742634524189_0.jpeg")
    		);
        // Hiển thị ảnh đầu tiên cho imageView1, imageView2 ẩn đi
        imageView1.setImage(images.get(currentImageIndex));
        imageView1.setOpacity(1.0);
        imageView2.setOpacity(0.0);
        
        leftBtn.setOnAction(e -> {
            if (!isTransitionRunning) {
                autoSlideTimeline.pause();
                currentImageIndex = (currentImageIndex - 1 + images.size()) % images.size();
                changeImageSlide(false);
            }
        });

        rightBtn.setOnAction(e -> {
            if (!isTransitionRunning) {
                autoSlideTimeline.pause();
                currentImageIndex = (currentImageIndex + 1) % images.size();
                changeImageSlide(true);
            }
        });
    }
    
    private void changeImageSlide(boolean isNext) {
        isTransitionRunning = true;

        Image newImage = images.get(currentImageIndex);
        ImageView slideOutView, slideInView;

        if (showingFirst) {
            slideOutView = imageView1;
            slideInView = imageView2;
        } else {
            slideOutView = imageView2;
            slideInView = imageView1;
        }

        // Đặt ảnh mới vào ImageView cần trượt vào
        slideInView.setImage(newImage);

        double imageWidth = slideOutView.getFitWidth(); // Lấy kích thước ảnh hiện tại
        double direction = isNext ? 1 : -1; // Xác định hướng trượt

        // Đặt vị trí ban đầu của ảnh mới
        slideInView.setTranslateX(imageWidth * direction);
        slideInView.setOpacity(1.0);

        // Tạo hiệu ứng trượt ảnh cũ ra ngoài màn hình
        TranslateTransition slideOut = new TranslateTransition(Duration.seconds(0.5), slideOutView);
        slideOut.setToX(-imageWidth * direction);

        // Tạo hiệu ứng trượt ảnh mới vào vị trí hiển thị
        TranslateTransition slideIn = new TranslateTransition(Duration.seconds(0.5), slideInView);
        slideIn.setToX(0);

        // Chạy song song hai hiệu ứng
        ParallelTransition slideTransition = new ParallelTransition(slideOut, slideIn);
        slideTransition.setOnFinished(event -> {
            slideOutView.setOpacity(0.0); // Ẩn ảnh cũ
            slideOutView.setTranslateX(0); // Reset vị trí của ảnh cũ
            showingFirst = !showingFirst;
            isTransitionRunning = false;
            autoSlideTimeline.playFromStart(); // Khởi động lại auto-slide nếu cần
        });

        slideTransition.play();
    }
    
    private void startAutoSlide() {
        autoSlideTimeline = new Timeline(
            new KeyFrame(Duration.seconds(3), event -> {
                if (!isTransitionRunning) {
                    currentImageIndex = (currentImageIndex + 1) % images.size();
                    changeImageSlide(true);
                }
            })
        );
        autoSlideTimeline.setCycleCount(Timeline.INDEFINITE);
        autoSlideTimeline.play();
    }
    
    public void btnLoadFXMLMenu() {
    	 FXMLLoader loader = new FXMLLoader(getClass().getResource("Menu.fxml"));
    	try {
			Parent viewMenu = loader.load();
			BorderPane mainLayout = (BorderPane) imageView1.getScene().getRoot();
			mainLayout.setCenter(viewMenu);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void btnTraXanh() {
    	btnLoadFXMLMenu();
    }
    
    public void SwitchMenu() {
    	btnLoadFXMLMenu();
    }
    
    public void BannerToMenu() {
    	btnLoadFXMLMenu();
    }
}
