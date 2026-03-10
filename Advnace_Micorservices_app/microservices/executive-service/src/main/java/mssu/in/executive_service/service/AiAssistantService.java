package mssu.in.executive_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mssu.in.executive_service.client.AdminProductClient;
import mssu.in.executive_service.client.CustomerServiceClient;
import mssu.in.executive_service.client.TicketServiceClient;
import mssu.in.executive_service.dto.MessageResponse;
import mssu.in.executive_service.dto.ProductResponse;
import mssu.in.executive_service.dto.TicketResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AiAssistantService {

    private static final Logger logger = LoggerFactory.getLogger(AiAssistantService.class);

    private final TicketServiceClient ticketServiceClient;
    private final CustomerServiceClient customerServiceClient;
    private final AdminProductClient adminProductClient;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

    public AiAssistantService(TicketServiceClient ticketServiceClient,
            CustomerServiceClient customerServiceClient,
            AdminProductClient adminProductClient,
            GeminiClient geminiClient,
            ObjectMapper objectMapper) {
        this.ticketServiceClient = ticketServiceClient;
        this.customerServiceClient = customerServiceClient;
        this.adminProductClient = adminProductClient;
        this.geminiClient = geminiClient;
        this.objectMapper = objectMapper;
    }

    public List<String> generateReplySuggestions(Long ticketId) {
        TicketResponse ticket = ticketServiceClient.getTicketById(ticketId);
        if (ticket == null) {
            throw new RuntimeException("Ticket not found");
        }

        Long productId = ticket.getProductId();
        if (productId == null) {
            throw new RuntimeException("No product associated with this ticket");
        }

        ProductResponse product = adminProductClient.getProductById(productId);
        if (product == null) {
            throw new RuntimeException("Product details not available");
        }

        // Get FULL conversation history for context-aware suggestions
        List<MessageResponse> messages = customerServiceClient.getMessagesForTicket(ticketId);

        // Build conversation history string
        StringBuilder conversationHistory = new StringBuilder();
        for (MessageResponse msg : messages) {
            String sender = "CUSTOMER".equalsIgnoreCase(msg.getSenderType()) ? "Customer" : "Support";
            conversationHistory.append(sender).append(": ").append(msg.getContent()).append("\n");
        }

        // Get last customer message for quick reference
        String lastCustomerMessage = messages.stream()
                .filter(m -> m.getSenderType() != null && m.getSenderType().equalsIgnoreCase("CUSTOMER"))
                .max(Comparator.comparing(MessageResponse::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(MessageResponse::getContent)
                .orElse(ticket.getDescription());

        String prompt = buildReplyPrompt(ticket, product, conversationHistory.toString(), lastCustomerMessage);
        String aiText = geminiClient.generateText(prompt);
        List<String> suggestions = parseSuggestions(aiText);

        // Smart Fallbacks if API fails or quota is exceeded
        if (suggestions.isEmpty()) {
            String prodName = product.getModelName() != null ? product.getModelName() : "the product";
            String lowerMsg = lastCustomerMessage.toLowerCase();

            // Professional, Context-Aware Fallbacks
            if (lowerMsg.contains("color") || lowerMsg.contains("option") || lowerMsg.contains("look")) {
                String colors = (product.getOtherColors() != null && !product.getOtherColors().isEmpty())
                        ? String.join(", ", product.getOtherColors())
                        : "our premium standard selection";
                suggestions.add("Thank you for asking about the aesthetics of " + prodName + "! In addition to the "
                        + product.getMainColor() + " model, we also offer it in " + colors
                        + ". Which of these would best suit your style?");
            }

            if (lowerMsg.contains("price") || lowerMsg.contains("cost") || lowerMsg.contains("expensive")
                    || lowerMsg.contains("deal")) {
                suggestions.add("I would be delighted to discuss the pricing for " + prodName
                        + ". Currently, it is listed at $" + product.getMaxPrice()
                        + ". We also have various offer bundles available depending on your specific requirements. Shall I provide more details on those?");
            }

            suggestions.add("It's a pleasure to assist you with your inquiry regarding " + prodName
                    + ". Based on your request, I am carefully reviewing our latest technical specifications to ensure I provide you with the most accurate resolution. I appreciate your patience!");

            if (suggestions.size() < 3) {
                suggestions.add("Thank you for reaching out to TicketHub Support. I have prioritized your Ticket #"
                        + ticket.getId()
                        + " and am currently investigating the best possible solution for you. Is there anything else you'd like to add while I look into this?");
            }
        }

        // Return max 3
        return suggestions.stream().limit(3).toList();
    }

    private String buildReplyPrompt(TicketResponse ticket, ProductResponse product, String conversationHistory,
            String lastCustomerMessage) {
        StringBuilder productContext = new StringBuilder();
        if (product.getAiOverviewJson() != null && !product.getAiOverviewJson().isBlank()) {
            productContext.append("- Technical Overview: ").append(product.getAiOverviewJson()).append("\n");
        }
        if (product.getManualOverview() != null && !product.getManualOverview().isBlank()) {
            productContext.append("- Feature Details: ").append(product.getManualOverview()).append("\n");
        }
        if (product.getOtherColors() != null && !product.getOtherColors().isEmpty()) {
            productContext.append("- Available Variants/Colors: ").append(String.join(", ", product.getOtherColors()))
                    .append("\n");
        }
        if (product.getOtherPrices() != null && !product.getOtherPrices().isEmpty()) {
            productContext.append("- Alternative Pricing/Bundles: ").append(String.join("; ", product.getOtherPrices()))
                    .append("\n");
        }

        return "### ROLE\n"
                + "You are a Senior Customer Relations Executive at 'TicketHub Premium Support'. You are known for your sophisticated, professional, and exceptionally helpful communication style.\n\n"

                + "### OBJECTIVE\n"
                + "Analyze the customer's LATEST MESSAGE and provide 3 unique, high-quality reply suggestions. Each suggestion should be grounded in the provided PRODUCT KNOWLEDGE.\n\n"

                + "### CONTEXT\n"
                + "TICKET ID: #" + ticket.getId() + "\n"
                + "PRIORITY: " + safe(ticket.getPriority()) + "\n"
                + "PRODUCT: " + safe(product.getModelName()) + " (" + safe(product.getProductType()) + ")\n"
                + "MAIN COLOR: " + safe(product.getMainColor()) + "\n"
                + "BASE PRICE: $" + product.getMaxPrice() + "\n"
                + "CONVERSATION HISTORY:\n" + conversationHistory + "\n\n"

                + "### PRODUCT KNOWLEDGE (STRICTLY ADHERE TO THIS):\n"
                + productContext + "\n"

                + "### LATEST CUSTOMER QUERY:\n"
                + "\"" + safe(lastCustomerMessage) + "\"\n\n"

                + "### GUIDELINES\n"
                + "1. TONE: Eloquent, reassuring, and dedicated. Use phrases like 'I am fully committed to...', 'Rest assured...', 'It is my absolute priority to...'.\n"
                + "2. ACCURACY: Never hallucinate features. If info is missing, say: 'I will consult our specialized technical team to get that specific answer for you immediately.'\n"
                + "3. VARIETY: \n"
                + "   - Suggestion 1: Direct & Informative (Answer the question precisely).\n"
                + "   - Suggestion 2: Proactive & Solution-Oriented (Offer a next step or an alternative variant).\n"
                + "   - Suggestion 3: Empathic & Building Trust (Acknowledge their needs and offer continuous support).\n\n"

                + "### OUTPUT FORMAT\n"
                + "Return ONLY a JSON object:\n"
                + "{ \"suggestions\": [\"...\", \"...\", \"...\"] }";
    }

    private List<String> parseSuggestions(String aiText) {
        List<String> out = new ArrayList<>();
        if (aiText == null || aiText.isBlank())
            return out;
        try {
            // Clean up Gemini's markdown JSON wrapper if present
            String jsonClean = aiText.trim();
            if (jsonClean.startsWith("```")) {
                int firstNewline = jsonClean.indexOf('\n');
                int lastBackticks = jsonClean.lastIndexOf("```");
                if (firstNewline != -1 && lastBackticks > firstNewline) {
                    jsonClean = jsonClean.substring(firstNewline, lastBackticks).trim();
                }
            }

            JsonNode root = objectMapper.readTree(jsonClean);
            JsonNode arr = root.path("suggestions");
            if (arr.isArray()) {
                for (JsonNode n : arr) {
                    if (n.isTextual()) {
                        String s = n.asText().trim();
                        if (!s.isEmpty())
                            out.add(s);
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Could not parse Gemini suggestions as JSON. Returning empty suggestions.");
        }
        return out;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
