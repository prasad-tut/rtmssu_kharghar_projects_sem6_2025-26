package mssu.in.customer_service.service;

import mssu.in.customer_service.client.UserServiceClient;
import mssu.in.customer_service.dto.MessageResponse;
import mssu.in.customer_service.dto.SendMessageRequest;
import mssu.in.customer_service.entity.Message;
import mssu.in.customer_service.entity.MessageType;
import mssu.in.customer_service.entity.SenderType;
import mssu.in.customer_service.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserServiceClient userServiceClient;

    public MessageService(MessageRepository messageRepository, UserServiceClient userServiceClient) {
        this.messageRepository = messageRepository;
        this.userServiceClient = userServiceClient;
    }

    public Message saveMessage(SendMessageRequest request) {
        Message message = new Message();
        message.setTicketId(request.getTicketId());
        message.setSenderId(request.getSenderId());
        message.setContent(request.getContent());
        message.setAttachment(request.getAttachment());
        message.setSenderType(SenderType.valueOf(request.getSenderType().toUpperCase()));

        // Handle messageType
        if (request.getMessageType() != null && !request.getMessageType().isEmpty()) {
            message.setMessageType(MessageType.valueOf(request.getMessageType().toUpperCase()));
        } else {
            message.setMessageType(MessageType.REGULAR);
        }

        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getMessagesByTicketId(Long ticketId) {
        List<Message> messages = messageRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);
        return messages.stream()
                .map(this::toMessageResponse)
                .collect(Collectors.toList());
    }

    public void markMessagesAsRead(Long ticketId, Long userId) {
        messageRepository.markMessagesAsRead(ticketId, userId);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long ticketId, Long userId) {
        return messageRepository.countUnreadMessages(ticketId, userId);
    }

    public void deleteMessagesByTicketId(Long ticketId) {
        messageRepository.deleteByTicketId(ticketId);
    }

    private MessageResponse toMessageResponse(Message message) {
        MessageResponse response = new MessageResponse(message);
        response.setSenderName(userServiceClient.getUserName(message.getSenderId()));
        return response;
    }
}
