package application;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import com.rabbitmq.client.Channel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class UserChatController implements Initializable {

	@FXML
	private VBox vBoxMessageContainer;

	@FXML
	private TextArea txtMessage;

	@FXML
	private ScrollPane scrollPane;

	private String userName = Session.getUserName();
	private RabbitMQManager rabbitMQManager = RabbitMQManager.getInstance();
	private Channel userChannel;
	private final String ADMIN_ROUTING_KEY = "support.admin";
	private String consumerTag;

	private ChatHistoryDAO chatHistoryDAO = new ChatHistoryDAO();
	private List<ChatMessage> messageList = FXCollections.observableArrayList();

	String timeNow = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
	UIChat uiChat = new UIChat();

	@FXML
	void sendMessage() {
		String message = txtMessage.getText().trim();
		if(!message.isEmpty()) {
			// Tạo timestamp tại thời điểm gửi tin nhắn
	        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
	        String formattedTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
			// Tin nhắn của người dùng (user)
			uiChat.addMessageToContainer(vBoxMessageContainer, txtMessage.getText().trim(), timeNow, true, currentTimestamp);
			
			// Tin nhắn của admin

			// Tự động cuộn xuống cuối sau khi thêm tin nhắn mới
			Platform.runLater(() -> {
				scrollPane.setVvalue(1.0);
			});

			String fullMessage = userName + "|" + message;
			rabbitMQManager.sendMessage(ADMIN_ROUTING_KEY, fullMessage);

			txtMessage.clear();
			
			// Lưu tin nhắn của user gửi vào DB
			ChatMessage chatMsg = new ChatMessage(userName, "Admin", message, currentTimestamp);
			try {
				chatHistoryDAO.saveMessage(chatMsg);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}	
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			messageList.clear();
			messageList = chatHistoryDAO.getMessages(userName, "Admin");
			for (ChatMessage msg : messageList) {

				   Timestamp timeHistory = msg.getTimestamp();
		            LocalDateTime localDateTime = timeHistory.toLocalDateTime();
		            String formattedTime = localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));

				
				if (msg.getSender().equals("Admin")) {
					uiChat.addMessageToContainer(vBoxMessageContainer, msg.getContent(), formattedTime, false, timeHistory);
				} else {
					uiChat.addMessageToContainer(vBoxMessageContainer, msg.getContent(), formattedTime, true, timeHistory);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			userChannel = rabbitMQManager.createChannel();
			String userQueue = "user." + userName;
			userChannel.queueDeclare(userQueue, true, false, false, null);
			userChannel.queueBind(userQueue, "support.direct", userQueue);
			
			Timestamp currentTime = new Timestamp(System.currentTimeMillis());
			consumerTag = userChannel.basicConsume(userQueue, false, (tag, delivery) -> {
				String reply = new String(delivery.getBody(), StandardCharsets.UTF_8);
				
				Platform.runLater(() -> {
					uiChat.addMessageToContainer(vBoxMessageContainer, reply, timeNow, false, currentTime);
					Platform.runLater(() -> {
						scrollPane.setVvalue(1.0);
					});
				});
				try {
					userChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}, tag -> {
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
