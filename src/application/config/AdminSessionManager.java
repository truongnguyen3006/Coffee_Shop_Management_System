package application.config;


import application.*;
import application.model.*;
import application.repository.*;
import application.service.*;
import application.messaging.*;
import application.util.*;
import application.config.*;

import java.util.logging.Logger;

import application.messaging.RabbitMQManager;
import application.util.AppLogger;

public class AdminSessionManager {
    private static final Logger LOGGER = AppLogger.getLogger(AdminSessionManager.class);
    private static final String ADMIN_QUEUE = "support.admin";
    private final RabbitMQManager rabbitMQManager = RabbitMQManager.getInstance();

    public void initAdminQueue() {
        try {
            rabbitMQManager.createChannel().queueDeclare(ADMIN_QUEUE, true, false, false, null);
        } catch (Exception e) {
            AppLogger.error(LOGGER, "Không thể khởi tạo queue cho admin", e);
        }
    }

    public void logout() {
        rabbitMQManager.deleteAdminQueue(ADMIN_QUEUE);
    }

    public void sendMessageToAdmin(String message) {
        rabbitMQManager.sendMessage(ADMIN_QUEUE, message);
    }
}
