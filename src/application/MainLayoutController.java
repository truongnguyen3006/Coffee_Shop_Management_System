package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainLayoutController implements Initializable {
	
  	@FXML
    private Button btnHome;

    @FXML
    private ImageView imageView1;

    @FXML
    private ImageView imageView2;

    @FXML
    private Button leftBtn;

    @FXML
    private Button logOut;

    @FXML
    public BorderPane mainLayout;

    @FXML
    private Button rightBtn;
    
    @FXML
    private ScrollPane menuLoggedin;
    
    @FXML
    private Label welcomeName;
  
    public void disPlayFullName() {
    	String fullName = "";
    	try {
			Connection connect = Database.connect();
			String sql = "SELECT fullName FROM users WHERE userID = ?";
			PreparedStatement prepare = connect.prepareStatement(sql);
			prepare.setInt(1, Session.getUserID());
			ResultSet rs = prepare.executeQuery();
			if(rs.next()) {
				fullName = rs.getString("fullName");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	welcomeName.setText(fullName);
    }
    
    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
    	loadFXML("Home.fxml");
    	disPlayFullName();
	}

    @FXML
    private void switchHome() {
    	loadFXML("Home.fxml");
    }
    
    @FXML
    private void switchMenu() {
    	loadFXML("Menu.fxml");
    }
    
    @FXML
    private void switchHistoryOrder() {
    	loadFXML("OrderHistory.fxml");
    }
    
    @FXML
    private void switchAccount() {
    	try {
    		Parent currentRoot = mainLayout.getScene().getRoot();
    		BoxBlur blur = new BoxBlur(5, 5, 3);
    		currentRoot.setEffect(blur);	
			FXMLLoader loader = new FXMLLoader(getClass().getResource("AccountDetails.fxml"));
			Parent view = loader.load();
			AccountDetails accountDetails = loader.getController();
			accountDetails.setMainLayoutController(this);
			Stage accountStage = new Stage();
			accountStage.setScene(new Scene(view));
			accountStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
			accountStage.show();
			accountStage.setOnHiding(event -> currentRoot.setEffect(null));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    @FXML
    private void switchCart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Cart.fxml"));
            Parent view = loader.load();
            // Lấy controller của trang Cart
            CartController cartController = loader.getController();
            cartController.setUserID(Session.getUserID());            
            mainLayout.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }  
    
	public void loadFXML(String FXML) {
		try {
			Parent view = FXMLLoader.load(getClass().getResource(FXML));
			mainLayout.setCenter(view);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private UserChatController currentUserChatController; // Instance variable

	@FXML 
	private void switchSupport() {
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("UserChat.fxml"));
	        Parent root = loader.load();
	        // Get the controller instance
	        currentUserChatController = loader.getController();
	        mainLayout.setCenter(root);
	    } catch(IOException e) {
	        e.printStackTrace();
	    }
	}

	
	@FXML
	public void logOut() {
	    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
	    alert.setTitle("Xác nhận đăng xuất");
	    alert.setHeaderText( "Bạn có chắc chắn muốn đăng xuất?");
	    alert.setContentText( "Xác nhận đăng xuất khỏi tài khoản?");
	    Optional<ButtonType> result = alert.showAndWait();

	    if(result.isPresent() && result.get() == ButtonType.OK) {
	    	String userName = Session.getUserName();
	    	UserSessionManager userSessionManager = new UserSessionManager();
	    	userSessionManager.logout(userName);
	        
	        try {
	            // Load the Login screen
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
	            Parent loginRoot = loader.load();
	            Stage loginStage = new Stage();
	            loginStage.setScene(new Scene(loginRoot));
	            loginStage.show();

	            // Close the current window
	            Stage currentStage = (Stage) mainLayout.getScene().getWindow();
	            currentStage.close();
	        } catch(IOException e) {
	            e.printStackTrace();
	        }
	    }
	}


}
