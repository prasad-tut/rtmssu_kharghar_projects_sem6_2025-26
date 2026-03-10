package mssu.in.executive_service.service;

import mssu.in.executive_service.client.CustomerServiceClient;
import mssu.in.executive_service.dto.MessageResponse;
import mssu.in.executive_service.dto.SendMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExecutiveMessageService {

    private static final Logger logger = LoggerFactory.getLogger(ExecutiveMessageService.class);

    private final CustomerServiceClient customerServiceClient;

    public ExecutiveMessageService(CustomerServiceClient customerServiceClient) {
        this.customerServiceClient = customerServiceClient;
    }

    public List<MessageResponse> getMessagesForTicket(Long ticketId) {
        logger.info("Getting messages for ticket: {}", ticketId);
        return customerServiceClient.getMessagesForTicket(ticketId);
    }

    public MessageResponse sendMessageToCustomer(Long ticketId, Long executiveId, String content, String messageType) {
        logger.info("Executive {} sending message to ticket {}", executiveId, ticketId);

        SendMessageRequest request = new SendMessageRequest();
        request.setTicketId(ticketId);
        request.setSenderId(executiveId);
        request.setSenderType("EXECUTIVE");
        request.setContent(content);
        request.setMessageType(messageType);

        return customerServiceClient.sendMessage(request);
    }

    public long getUnreadMessageCount(Long ticketId) {
        return customerServiceClient.getUnreadCountForTicket(ticketId);
    }

    public void markMessagesAsRead(Long ticketId, Long executiveId) {
        logger.info("Marking messages as read for ticket {} by executive {}", ticketId, executiveId);
        customerServiceClient.markMessagesAsRead(ticketId, executiveId);
    }
}
