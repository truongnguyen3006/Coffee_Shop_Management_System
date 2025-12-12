package application;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertMessage {
	public static void showAlert(AlertType alertType, String tittle,String header,String content){
		Alert alert = new Alert(alertType);
		alert.setTitle(tittle);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
	
	public static void showAlertOTP(AlertType alertType, String tittle,String header,String content){
		Alert alert = new Alert(alertType);
		alert.setTitle(tittle);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.show();
	}
}
