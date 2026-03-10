package mssu.in.admin_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * Request payload from Admin UI when creating a product.
 * This is used both for manual posting and for AI-assisted posting.
 */
public class ProductRequest {

    /**
     * Id of the admin creating the product (from current session user).
     */
    @NotNull(message = "Admin ID is required")
    private Long adminId;

    @NotBlank(message = "Product type is required")
    private String productType;

    @NotBlank(message = "Model name is required")
    private String modelName;

    @NotBlank(message = "Main color is required")
    private String mainColor;

    /**
     * Optional list of other color options.
     */
    private List<String> otherColors;

    @NotNull(message = "Max price is required")
    @Positive(message = "Max price must be positive")
    private Double maxPrice;

    /**
     * Description strings for up to 5 pricing options (e.g. credit card EMIs, bank offers etc).
     */
    private List<String> otherPrices;

    /**
     * For this assignment we expect a direct image URL.
     */
    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    /**
     * Optional free text overview written by admin.
     */
    private String overview;

    // Getters and setters

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

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }
}

