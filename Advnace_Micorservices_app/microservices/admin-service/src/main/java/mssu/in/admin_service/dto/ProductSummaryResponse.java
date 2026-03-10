package mssu.in.admin_service.dto;

import mssu.in.admin_service.entity.Product;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Lightweight product response for customer catalog.
 * Excludes heavy fields like aiOverviewJson and manualOverview.
 */
public class ProductSummaryResponse {

    private Long id;
    private String productType;
    private String modelName;
    private String mainColor;
    private List<String> otherColors;
    private Double maxPrice;
    private String imageUrl;

    public ProductSummaryResponse() {
    }

    public ProductSummaryResponse(Product product) {
        if (product != null) {
            this.id = product.getId();
            this.productType = product.getProductType();
            this.modelName = product.getModelName();
            this.mainColor = product.getMainColor();
            this.otherColors = splitList(product.getOtherColors());
            this.maxPrice = product.getMaxPrice();
            this.imageUrl = product.getImageUrl();
        }
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
