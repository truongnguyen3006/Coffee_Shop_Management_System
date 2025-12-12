package application;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatHistoryDAO {

    // Lưu tin nhắn vào DB
    public void saveMessage(ChatMessage message) throws SQLException {
        Connection connect = Database.connect();
        String sql = "INSERT INTO chat_messages(sender, receiver, content, timestamp) VALUES(?, ?, ?, ?)";
        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            pstmt.setString(1, message.getSender());
            pstmt.setString(2, message.getReceiver());
            pstmt.setString(3, message.getContent());
            pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            pstmt.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            connect.close();
        }
    }

    // Lấy lịch sử chat giữa 2 bên (ví dụ: user và Admin) theo thứ tự thời gian tăng dần
    public List<ChatMessage> getMessages(String userA, String userB) throws SQLException {
        Connection connect = Database.connect();
        List<ChatMessage> messages = new ArrayList<>();
        String sql = "SELECT * FROM chat_messages WHERE (sender=? AND receiver=?) OR (sender=? AND receiver=?) ORDER BY timestamp";
        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            pstmt.setString(1, userA);
            pstmt.setString(2, userB);
            pstmt.setString(3, userB);
            pstmt.setString(4, userA);
            ResultSet rs = pstmt.executeQuery();
          
            while(rs.next()){
            	String sender = rs.getString("sender");
            	String receiver = rs.getString("receiver");
            	String content = rs.getString("content");
            	Timestamp formattedTime = rs.getTimestamp("timestamp");     	
            	ChatMessage chatMessage = new ChatMessage(sender, receiver, content, formattedTime);
            	messages.add(chatMessage);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            connect.close();
        }
        return messages;
    }

}
