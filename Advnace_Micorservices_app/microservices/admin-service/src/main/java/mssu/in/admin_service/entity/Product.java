package mssu.in.admin_service.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_products_admin_id", columnList = "adminId"),
        @Index(name = "idx_products_created_at", columnList = "createdAt")
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Owner/admin who created the product (maps to auth-user-service user id).
     */
    private Long adminId;

    /**
     * High level type like Laptop, Mobile, Sofa etc.
     */
    @Column(nullable = false, length = 100)
    private String productType;

    /**
     * Human readable model / product name (e.g. MacBook Pro 16").
     */
    @Column(nullable = false, length = 200)
    private String modelName;

    @Column(nullable = false, length = 50)
    private String mainColor;

    /**
     * Comma separated color options entered by admin.
     */
    @Column(length = 500)
    private String otherColors;

    @Column(nullable = false)
    private Double maxPrice;

    /**
     * JSON or text describing up to 5 pricing options (card offers, EMI etc).
     */
    @Column(length = 2000)
    private String otherPricesJson;

    /**
     * For this assignment we only store image URL (UI asks for URL instead of raw
     * upload).
     */
    @Column(length = 1000)
    private String imageUrl;

    /**
     * Free text overview written by admin.
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String manualOverview;

    /**
     * Raw JSON string returned from Gemini containing the very detailed product
     * information
     * (all the sections you listed in the assignment).
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String aiOverviewJson;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Default constructor for JPA
    public Product() {
    }

    /**
     * Lightweight constructor for catalog projections (used by findAllForCatalog
     * query).
     * Only initializes essential fields, excluding heavy LOB fields.
     */
    public Product(Long id, String productType, String modelName, String mainColor,
            String otherColors, Double maxPrice, String imageUrl) {
        this.id = id;
        this.productType = productType;
        this.modelName = modelName;
        this.mainColor = mainColor;
        this.otherColors = otherColors;
        this.maxPrice = maxPrice;
        this.imageUrl = imageUrl;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public String getOtherColors() {
        return otherColors;
    }

    public void setOtherColors(String otherColors) {
        this.otherColors = otherColors;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getOtherPricesJson() {
        return otherPricesJson;
    }

    public void setOtherPricesJson(String otherPricesJson) {
        this.otherPricesJson = otherPricesJson;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
