package application.messaging;


import application.*;
import application.model.*;
import application.repository.*;
import application.service.*;
import application.messaging.*;
import application.util.*;
import application.config.*;

public class UserSessionManager {
    private RabbitMQManager rabbitMQManager = RabbitMQManager.getInstance();

    public UserSessionManager() {
        this.rabbitMQManager = RabbitMQManager.getInstance();
    }

    // Khi user login, tạo (hoặc xác nhận tồn tại) queue dựa trên userName
    public void login(String userName) {
        rabbitMQManager.createUserQueue(userName);
    }

    // Khi logout, không xóa queue để giữ lại phiên chat
    public void logout(String userName) {
    	rabbitMQManager.deleteUserQueue(userName);
    }

    public RabbitMQManager getRabbitMQManager() {
        if (rabbitMQManager == null || rabbitMQManager.getConnection() == null || !rabbitMQManager.getConnection().isOpen()) {
            rabbitMQManager = RabbitMQManager.getInstance();
        }
        return rabbitMQManager;
    }
}
