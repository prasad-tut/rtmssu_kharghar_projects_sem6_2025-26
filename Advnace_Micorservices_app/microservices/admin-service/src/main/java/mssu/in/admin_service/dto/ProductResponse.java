package mssu.in.admin_service.dto;

import mssu.in.admin_service.entity.Product;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Product payload returned to UI (admin, customer, executive).
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

    public ProductResponse() {
    }

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.adminId = product.getAdminId();
        this.productType = product.getProductType();
        this.modelName = product.getModelName();
        this.mainColor = product.getMainColor();
        this.otherColors = splitList(product.getOtherColors());
        this.maxPrice = product.getMaxPrice();
        this.otherPrices = splitList(product.getOtherPricesJson());
        this.imageUrl = product.getImageUrl();
        this.manualOverview = product.getManualOverview();
        this.aiOverviewJson = product.getAiOverviewJson();
        this.createdAt = product.getCreatedAt();
    }

    private List<String> splitList(String value) {
        if (value == null || value.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(value.split("\\|"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getMainColor() {
        return mainColor;
    }

    public void setMainColor(String mainColor) {
        this.mainColor = mainColor;
    }

    public List<String> getOtherColors() {
        return otherColors;
    }

    public void setOtherColors(List<String> otherColors) {
        this.otherColors = otherColors;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public List<String> getOtherPrices() {
        return otherPrices;
    }

    public void setOtherPrices(List<String> otherPrices) {
        this.otherPrices = otherPrices;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getManualOverview() {
        return manualOverview;
    }

    public void setManualOverview(String manualOverview) {
        this.manualOverview = manualOverview;
    }

    public String getAiOverviewJson() {
        return aiOverviewJson;
    }

    public void setAiOverviewJson(String aiOverviewJson) {
        this.aiOverviewJson = aiOverviewJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

