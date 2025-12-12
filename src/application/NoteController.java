package application;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class NoteController implements Initializable {

	@FXML
	public TextArea noteTextField;

	CartController cartController;

	public void setCartController(CartController cartController) {
		this.cartController = cartController;
	}
	
	public void setNoteText(String notes) {
		noteTextField.setText(notes);
	}

	@FXML
	void conFirmInformation() {
		if (cartController != null) {
			cartController.translateSelectNotes(noteTextField.getText());
		}
		Stage currentStage = (Stage) noteTextField.getScene().getWindow();
		currentStage.close();
	}

	public interface CartController {
		void translateSelectNotes(String note);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}

}
