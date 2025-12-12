package application;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import application.AddressSelectionController.CartController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TimeDeliveryController implements Initializable {

    @FXML
    private DatePicker Date;  // Chỉ cho phép chọn ngày hôm nay

    @FXML
    private ComboBox<String> cbTime;  // ComboBox hiển thị các mốc thời gian

    private CartController cartController;

    public void setCartController(CartController cartController) {
        this.cartController = cartController;
    }

    // Danh sách mốc thời gian mặc định trong ngày
    private ObservableList<String> optionTime = FXCollections.observableArrayList(
            "10:45", "11:00", "11:15", "11:30", "11:45", "12:00", "12:15", "12:30", "12:45", "13:00",
            "13:15", "13:30", "13:45", "14:00", "14:15", "14:30", "14:45", "15:00", "15:15", "15:30",
            "15:45", "16:00", "16:45", "17:00", "17:15", "17:30", "17:45", "18:00", "18:15", "18:30",
            "18:45", "19:00", "19:15", "19:30", "19:45", "20:00", "20:15", "20:30", "20:45", "21:00");

    // Cập nhật danh sách thời gian dựa trên thời gian hiện tại
    private void updateTimeOptions() {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
        ObservableList<String> filteredTimes = optionTime.stream()
                .filter(timeStr -> {
                    LocalTime slot = LocalTime.parse(timeStr, formatter);
                    // Chỉ hiển thị các mốc giờ chưa qua (hoặc đúng bằng giờ hiện tại)
                    return !slot.isBefore(now);
                })
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        cbTime.setItems(filteredTimes);
        // Nếu có ít nhất 1 mốc giờ hợp lệ, đặt giá trị mặc định là mốc đầu tiên
        if (!filteredTimes.isEmpty()) {
            cbTime.setValue(filteredTimes.get(0));
        } else {
            cbTime.setValue(null);
        }
    }

    // Xác nhận thời gian đã chọn và truyền qua callback
    @FXML
    void confirmTime() {
    	if(Date.getValue() == null || cbTime.getValue() == null || cbTime.getValue().isEmpty()) {
    		AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Thiếu thông tin", "Vui lòng điền đầy đủ thông tin.");
    	}else {
    		if (cartController != null) {
                cartController.translateSelectTime(Date.getValue(), cbTime.getValue());
            }
            Stage stage = (Stage) cbTime.getScene().getWindow();
            stage.close();
    	}  
    }

    public interface CartController {
        void translateSelectTime(LocalDate localDate, String time);
    }
    
    public void setTime(String time) {
		cbTime.setValue(time);
	}

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Cài đặt cho DatePicker: chỉ cho phép chọn ngày hôm nay
        Date.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                // Nếu không phải ngày hôm nay thì disable ô đó
                if (!item.equals(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #EEEEEE;");
                }
            }
        });
        Date.setValue(LocalDate.now());

        // Cập nhật danh sách thời gian dựa trên thời gian hiện tại
        updateTimeOptions();

        // Nếu người dùng ở lại trang quá lâu, bạn có thể tự động cập nhật danh sách thời gian mỗi phút
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(60), event -> updateTimeOptions()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
