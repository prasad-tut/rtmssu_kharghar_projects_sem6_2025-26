package mssu.in.executive_service.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Minimal product DTO as returned by admin-service. Used for AI prompt context.
 */
public class ProductResponse {
    private Long id;
    private Long adminId;
    private String productType;
    private String modelName;
    private String mainColor;
    private List<String> otherColors;
    private Double maxPrice;
    private List<String> otherPrices;
    private String imageUrl;
    private String manualOverview;
    private String aiOverviewJson;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }
    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    public String getMainColor() { return mainColor; }
    public void setMainColor(String mainColor) { this.mainColor = mainColor; }
    public List<String> getOtherColors() { return otherColors; }
    public void setOtherColors(List<String> otherColors) { this.otherColors = otherColors; }
    public Double getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Double maxPrice) { this.maxPrice = maxPrice; }
    public List<String> getOtherPrices() { return otherPrices; }
    public void setOtherPrices(List<String> otherPrices) { this.otherPrices = otherPrices; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getManualOverview() { return manualOverview; }
    public void setManualOverview(String manualOverview) { this.manualOverview = manualOverview; }
    public String getAiOverviewJson() { return aiOverviewJson; }
    public void setAiOverviewJson(String aiOverviewJson) { this.aiOverviewJson = aiOverviewJson; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

