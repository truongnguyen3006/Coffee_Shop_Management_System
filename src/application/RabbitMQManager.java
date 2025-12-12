package application;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class RabbitMQManager {
    private Connection connection;
    private static RabbitMQManager instance;

    private final String EXCHANGE_NAME = "support.direct";
    private final String EXCHANGE_TYPE = "direct";
    private final String ADMIN_QUEUE = "support.admin";

    public RabbitMQManager() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            connection = factory.newConnection();
            // Khai báo exchange và admin queue khi khởi tạo
            Channel initChannel = createChannel();
            if (initChannel != null) {
                initChannel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE, true);
                initChannel.queueDeclare(ADMIN_QUEUE, true, false, false, null);
                initChannel.queueBind(ADMIN_QUEUE, EXCHANGE_NAME, ADMIN_QUEUE);
                initChannel.close();
            }
        } catch(IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public static synchronized RabbitMQManager getInstance() {
        if(instance == null || instance.connection == null || !instance.connection.isOpen()){
            instance = new RabbitMQManager();
        }
        return instance;
    }

    public Channel createChannel() {
        try {
            Channel channel = connection.createChannel();
            // exchangeDeclare is idempotent so it is safe to call it here.
            channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE, true);
            return channel;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendMessage(String routingKey, String message) {
        Channel channel = createChannel();
        if(channel == null) return;
        try {
            channel.basicPublish(EXCHANGE_NAME, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN,
                    message.getBytes(StandardCharsets.UTF_8));
            System.out.println("Sent message to exchange " + EXCHANGE_NAME + " with routing key " + routingKey + ": " + message);
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                channel.close();
            } catch(IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    public void createUserQueue(String userName) {
        Channel channel = createChannel();
        if(channel == null) return;
        try {
            String queueName = "user." + userName;
            // Đảm bảo queue được tạo với durable = true và không tự động xóa
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, EXCHANGE_NAME, queueName);
            System.out.println("User queue created and bound: " + queueName);
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try { channel.close(); } catch(IOException | TimeoutException e) { e.printStackTrace(); }
        }
    }


    public void deleteUserQueue(String userName) {
        Channel channel = createChannel();
        if(channel == null) return;
        try {
            String queueName = "user." + userName;
            channel.queueDelete(queueName);
            System.out.println("User queue deleted: " + queueName);
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try { channel.close(); } catch(IOException | TimeoutException e) { e.printStackTrace(); }
        }
    }
    
    public void deleteAdminQueue(String userName) {
        Channel channel = createChannel();
        if(channel == null) return;
        try {
//            String queueName = "user." + userName;
            channel.queueDelete(ADMIN_QUEUE);
//            System.out.println("User queue deleted: " + queueName);
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try { channel.close(); } catch(IOException | TimeoutException e) { e.printStackTrace(); }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() throws TimeoutException {
        try {
            if(connection != null && connection.isOpen()) connection.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
