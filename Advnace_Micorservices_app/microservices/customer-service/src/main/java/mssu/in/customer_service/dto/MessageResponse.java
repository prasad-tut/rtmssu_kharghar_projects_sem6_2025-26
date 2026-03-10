package mssu.in.customer_service.dto;

import mssu.in.customer_service.entity.Message;
import mssu.in.customer_service.entity.MessageType;
import mssu.in.customer_service.entity.SenderType;
import java.time.LocalDateTime;

public class MessageResponse {

    private Long id;
    private Long ticketId;
    private Long senderId;
    private String senderName;
    private String content;
    private String attachment;
    private SenderType senderType;
    private MessageType messageType;
    private LocalDateTime createdAt;
    private boolean isRead;

    public MessageResponse() {
    }

    public MessageResponse(Message message) {
        this.id = message.getId();
        this.ticketId = message.getTicketId();
        this.senderId = message.getSenderId();
        this.content = message.getContent();
        this.attachment = message.getAttachment();
        this.senderType = message.getSenderType();
        this.messageType = message.getMessageType();
        this.createdAt = message.getCreatedAt();
        this.isRead = message.isRead();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public SenderType getSenderType() {
        return senderType;
    }

    public void setSenderType(SenderType senderType) {
        this.senderType = senderType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}
