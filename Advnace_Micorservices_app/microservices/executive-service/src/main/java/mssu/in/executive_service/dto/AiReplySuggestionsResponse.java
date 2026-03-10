package mssu.in.executive_service.dto;

import java.util.List;

public class AiReplySuggestionsResponse {
    private List<String> suggestions;

    public AiReplySuggestionsResponse() {}

    public AiReplySuggestionsResponse(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }
}

