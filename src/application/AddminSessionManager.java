package application;

public class AddminSessionManager {
    private RabbitMQManager rabbitMQManager = RabbitMQManager.getInstance();
    private final String ADMIN_QUEUE = "support.admin";

    public void initAdminQueue() {
        try {
            rabbitMQManager.createChannel().queueDeclare(ADMIN_QUEUE, true, false, false, null);
            System.out.println("Queue cho admin (" + ADMIN_QUEUE + ") đã được tạo.");
        } catch (Exception e) {
            e.printStackTrace();
        }   
    }
    
    public void logout() {
    	rabbitMQManager.deleteAdminQueue(ADMIN_QUEUE);
    }
    
    public void sendMessageToAdmin(String message) {
        rabbitMQManager.sendMessage(ADMIN_QUEUE, message);
    }
}
