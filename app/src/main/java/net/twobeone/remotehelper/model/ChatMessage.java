package net.twobeone.remotehelper.model;

public class ChatMessage {

    private ChatUser sender;
    private String message;
    private long createdAt;

    public ChatMessage(ChatUser sender, String message) {
        this.sender = sender;
        this.message = message;
        createdAt = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

    public ChatUser getSender() {
        return sender;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
