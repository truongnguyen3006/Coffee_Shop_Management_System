package application;

import java.sql.Timestamp;

import com.google.gson.Gson;

public class ChatMessage {
    private int id;
    private String sender;
    private String receiver;
    private String content;
    private Timestamp timestamp;

    public ChatMessage(String sender, String receiver, String content, Timestamp timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = timestamp;
    }
    

   
	public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }
    public String getContent() { return content; }
    public Timestamp getTimestamp() { return timestamp; }
}
