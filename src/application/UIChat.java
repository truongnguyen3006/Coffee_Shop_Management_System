package application;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class UIChat {
	public static void addMessageToContainer(VBox container, String messageContent, String formattedTime,
			boolean isUserMessage, Timestamp timeStamp) {
		// Tạo label hiển thị thời gian
		Label lbTimeMessage = new Label(formattedTime);

		// HBox ngoài cùng
		HBox outerHbox = new HBox();
		outerHbox.setAlignment(isUserMessage ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
		outerHbox.setLayoutX(10);
		outerHbox.setLayoutY(20);

		// VBox chứa nội dung tin nhắn và thời gian
		VBox innerVbox = new VBox();
		innerVbox.setPadding(new Insets(0, 5, 0, 0));

		// HBox chứa phần tin nhắn
		HBox hboxMessage = new HBox();
		hboxMessage.setPadding(new Insets(0, 5, 0, 5));
		hboxMessage.setSpacing(5);
		

		// VBox chứa tin nhắn (định dạng background, giới hạn chiều rộng)
		VBox vBoxMessage = new VBox();
		// Với tin nhắn của user có thể căn lề trái để hiển thị nội dung đẹp
		// Với admin, bạn có thể căn giữa (hoặc tùy chỉnh theo ý muốn)
		vBoxMessage.setAlignment(isUserMessage ? Pos.CENTER_LEFT : Pos.CENTER);
		vBoxMessage.setStyle(isUserMessage ? "-fx-background-color: #24ADFD; -fx-background-radius: 5;" : "-fx-background-color: #dcdcdc; -fx-background-radius: 5;");
		vBoxMessage.setMaxWidth(300);

		// Label chứa nội dung tin nhắn
		Label contentMessage = new Label(messageContent);
		contentMessage.setWrapText(true);
		contentMessage.setFont(Font.font(18));
		contentMessage.setPadding(new Insets(0, 0, 0, 5));
		
		 // Tạo Tooltip hiển thị ngày giờ gửi tin nhắn
	    DateTimeFormatter tooltipFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	    String tooltipText = timeStamp.toLocalDateTime().format(tooltipFormatter);
	    Tooltip tooltip = new Tooltip(tooltipText);
	    Tooltip.install(contentMessage, tooltip);
	    
	    tooltip.setShowDelay(Duration.millis(100));
	 // Nếu muốn Tooltip ẩn nhanh hơn khi rời chuột, bạn có thể đặt hideDelay
	    tooltip.setHideDelay(Duration.millis(100));

		// Lắp ráp các thành phần
		vBoxMessage.getChildren().add(contentMessage);
		hboxMessage.getChildren().add(vBoxMessage);
		innerVbox.getChildren().addAll(hboxMessage, lbTimeMessage);
		lbTimeMessage.setPadding(new Insets(0,5,0,5));

		// Với tin nhắn của user, có thể cần căn lề bên trong VBox
		if (isUserMessage) {
			innerVbox.setAlignment(Pos.TOP_RIGHT);
			HBox.setMargin(innerVbox, new Insets(0, 0, 0, 10));
		}

		outerHbox.getChildren().add(innerVbox);
		container.getChildren().add(outerHbox);
	}
}
