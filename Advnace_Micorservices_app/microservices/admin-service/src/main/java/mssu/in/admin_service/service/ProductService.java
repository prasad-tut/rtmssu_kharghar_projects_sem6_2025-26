package mssu.in.admin_service.service;

import mssu.in.admin_service.dto.ProductRequest;
import mssu.in.admin_service.dto.ProductResponse;
import mssu.in.admin_service.dto.ProductSummaryResponse;
import mssu.in.admin_service.entity.Product;
import mssu.in.admin_service.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final GeminiClient geminiClient;

    public ProductService(ProductRepository productRepository, GeminiClient geminiClient) {
        this.productRepository = productRepository;
        this.geminiClient = geminiClient;
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = mapToEntity(request);

        // Even for manual entry, use AI to structure the overview properly
        // This ensures executives always have well-formatted, complete product
        // information
        if (request.getOverview() != null && !request.getOverview().isBlank()) {
            String prompt = buildProductOverviewPrompt(request);
            String aiJson = geminiClient.generateText(prompt);
            if (aiJson != null && !aiJson.isBlank()) {
                product.setAiOverviewJson(aiJson);
            } else {
                // Fallback if AI fails
                product.setAiOverviewJson(buildFallbackAiOverviewJson(request));
            }
        } else {
            // If no overview provided at all, use fallback
            product.setAiOverviewJson(buildFallbackAiOverviewJson(request));
        }

        product = productRepository.save(product);
        return new ProductResponse(product);
    }

    /**
     * Creates a product and also asks Gemini to generate a huge overview JSON,
     * which is stored in {@code aiOverviewJson}.
     */
    @Transactional
    public ProductResponse createProductWithAi(ProductRequest request) {
        Product product = mapToEntity(request);

        String prompt = buildProductOverviewPrompt(request);
        String aiJson = geminiClient.generateText(prompt);
        if (aiJson == null || aiJson.isBlank()) {
            // Fail-safe: if Gemini is unavailable (timeout / no network), still store a
            // valid JSON
            // so the executive overview can render without breaking.
            aiJson = buildFallbackAiOverviewJson(request);
        }
        product.setAiOverviewJson(aiJson);

        product = productRepository.save(product);
        return new ProductResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Lightweight product catalog for customer UI.
     * Uses optimized query that excludes aiOverviewJson and manualOverview
     * to reduce database transfer time by ~90%.
     */
    @Transactional(readOnly = true)
    public List<ProductSummaryResponse> getProductCatalog() {
        return productRepository.findAllForCatalog()
                .stream()
                .map(ProductSummaryResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsForAdmin(Long adminId) {
        return productRepository.findByAdminIdOrderByCreatedAtDesc(adminId)
                .stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        return productRepository.findById(id)
                .map(ProductResponse::new)
                .orElse(null);
    }

    private Product mapToEntity(ProductRequest request) {
        Product product = new Product();
        product.setAdminId(request.getAdminId());
        product.setProductType(request.getProductType());
        product.setModelName(request.getModelName());
        product.setMainColor(request.getMainColor());
        product.setOtherColors(joinList(request.getOtherColors()));
        product.setMaxPrice(request.getMaxPrice());
        product.setOtherPricesJson(joinList(request.getOtherPrices()));
        product.setImageUrl(request.getImageUrl());
        product.setManualOverview(request.getOverview());
        return product;
    }

    private String joinList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining("|"));
    }

    /**
     * Builds a very explicit prompt asking Gemini to return JSON only,
     * with all the sections you listed in the assignment.
     */
    private String buildProductOverviewPrompt(ProductRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(
                "You are a product data specialist creating a comprehensive product overview for customer support executives. ")
                .append("Your task is to generate COMPLETE, REALISTIC, and DETAILED information for every field. ")
                .append("Return STRICTLY valid JSON (no markdown, no comments, no backticks).\n\n")

                .append("CRITICAL REQUIREMENTS:\n")
                .append("- NEVER use 'N/A', 'Unknown', 'No', '0', 'None', 'Not applicable', or empty values\n")
                .append("- ALL fields must contain realistic, specific, believable data\n")
                .append("- Generate fake but consistent and professional information\n")
                .append("- Use actual brand names, realistic dates, specific measurements, and detailed descriptions\n")
                .append("- Make the data look like a real product listing from Amazon or similar e-commerce platform\n\n")

                .append("HANDLING ADMIN INPUT:\n")
                .append("- The 'Admin notes' field below may contain unstructured, messy, or minimal text\n")
                .append("- If admin provides copy-pasted text with no formatting: Extract key information and structure it properly\n")
                .append("- If admin provides just one word or brief note: Use it as context and generate complete realistic data\n")
                .append("- If admin provides detailed structured text: Use it as the foundation and enhance with additional realistic details\n")
                .append("- Parse any relevant information from admin notes (features, specs, warranty, etc.) and incorporate into appropriate sections\n")
                .append("- Fill ALL remaining fields with realistic, believable data even if admin notes are minimal\n\n")

                .append("Base product information:\n")
                .append("Type: ").append(request.getProductType()).append("\n")
                .append("Model name: ").append(request.getModelName()).append("\n")
                .append("Main color: ").append(request.getMainColor()).append("\n")
                .append("Other colors: ").append(joinList(request.getOtherColors())).append("\n")
                .append("Max price: $").append(request.getMaxPrice()).append("\n")
                .append("Other prices/options: ").append(joinList(request.getOtherPrices())).append("\n")
                .append("Admin notes: ").append(request.getOverview()).append("\n\n")

                .append("Return JSON with this EXACT structure (all fields REQUIRED with realistic values):\n")
                .append("{\n")
                .append("  \"basicInfo\": {\n")
                .append("    \"productTitle\": \"[Full product name with brand and model]\",\n")
                .append("    \"brandName\": \"[Actual brand name like Apple, Samsung, Dell, etc.]\",\n")
                .append("    \"productCategory\": \"[Main category]\",\n")
                .append("    \"subCategory\": \"[Specific subcategory]\",\n")
                .append("    \"productType\": \"[Type like Smartphone, Laptop, etc.]\",\n")
                .append("    \"modelNumber\": \"[Realistic model number like A2482, XPS-9520]\",\n")
                .append("    \"manufacturerName\": \"[Company name]\",\n")
                .append("    \"countryOfOrigin\": \"[Specific country like China, USA, Taiwan]\",\n")
                .append("    \"releaseDate\": \"[Realistic date like September 2023, Q2 2024]\",\n")
                .append("    \"itemCondition\": \"[New, Refurbished, etc.]\"\n")
                .append("  },\n")
                .append("  \"identifiers\": {\n")
                .append("    \"sku\": \"[Realistic SKU like SKU-APL-IPH15-BLK-128]\",\n")
                .append("    \"upcEanIsbn\": \"[12-digit UPC like 194253123456]\",\n")
                .append("    \"asin\": \"[10-character ASIN like B0CHX3TZ7R]\",\n")
                .append("    \"gtinExemption\": \"[No or specific reason]\",\n")
                .append("    \"partNumber\": \"[Manufacturer part number]\"\n")
                .append("  },\n")
                .append("  \"descriptionContent\": {\n")
                .append("    \"productDescription\": \"[2-3 sentence detailed description]\",\n")
                .append("    \"keyFeatures\": [\"[Feature 1]\", \"[Feature 2]\", \"[Feature 3]\", \"[Feature 4]\", \"[Feature 5]\"],\n")
                .append("    \"searchKeywords\": \"[Comma-separated relevant keywords]\",\n")
                .append("    \"materialComposition\": \"[Specific materials like Aluminum, Glass, Plastic]\",\n")
                .append("    \"color\": \"[Exact color name]\",\n")
                .append("    \"sizeDimensions\": \"[Specific dimensions like 6.1 x 2.8 x 0.3 inches]\",\n")
                .append("    \"weight\": \"[Specific weight like 6.1 oz or 174g]\"\n")
                .append("  },\n")
                .append("  \"pricingOffers\": {\n")
                .append("    \"mrp\": \"[Original price]\",\n")
                .append("    \"sellingPrice\": \"[Current selling price]\",\n")
                .append("    \"discountPercentage\": \"[Realistic discount like 15% or 0%]\",\n")
                .append("    \"taxCategoryGstSlab\": \"[Tax rate like 18% GST or Standard]\",\n")
                .append("    \"shippingFee\": \"[Specific amount like Free, $5.99, or ₹99]\"\n")
                .append("  },\n")
                .append("  \"inventoryFulfillment\": {\n")
                .append("    \"stockQuantity\": \"[Realistic number like 247, In Stock, Limited Stock]\",\n")
                .append("    \"fulfillmentMethod\": \"[FBA, FBM, or Seller Fulfilled]\",\n")
                .append("    \"warehouseLocation\": \"[City/State like California, Mumbai, etc.]\",\n")
                .append("    \"handlingTime\": \"[Specific time like Ships within 24 hours, 1-2 business days]\",\n")
                .append("    \"packageType\": \"[Retail Box, Bulk Packaging, etc.]\"\n")
                .append("  },\n")
                .append("  \"mediaMarketing\": {\n")
                .append("    \"mainProductImage\": \"[Use the provided image URL]\",\n")
                .append("    \"additionalImages\": [\"[URL 1]\", \"[URL 2]\"],\n")
                .append("    \"productVideo\": \"[YouTube URL or product demo link]\",\n")
                .append("    \"enhancedBrandContent\": \"[Yes with A+ Content or Standard Listing]\"\n")
                .append("  },\n")
                .append("  \"compliancePolicies\": {\n")
                .append("    \"returnPolicy\": \"[Specific policy like 30-day free returns, 14-day return window]\",\n")
                .append("    \"warrantyInformation\": \"[Specific warranty like 1-year manufacturer warranty, 2-year extended warranty available]\",\n")
                .append("    \"safetyComplianceCertificates\": \"[Specific certifications like FCC, CE, RoHS certified]\",\n")
                .append("    \"hazardousMaterialDeclaration\": \"[Contains lithium-ion battery or No hazardous materials]\"\n")
                .append("  },\n")
                .append("  \"productUnderstanding\": {\n")
                .append("    \"problemSolved\": \"[Specific problem this product solves]\",\n")
                .append("    \"bestSuitedFor\": \"[Target audience like professionals, students, gamers]\",\n")
                .append("    \"keyBenefits\": \"[Main benefits in one sentence]\",\n")
                .append("    \"howItWorks\": \"[Brief explanation of functionality]\",\n")
                .append("    \"includedInTheBox\": \"[Specific items like Device, USB-C cable, 20W adapter, documentation]\",\n")
                .append("    \"notIncluded\": \"[Items not included like Headphones sold separately, Case not included]\",\n")
                .append("    \"mainDifferentiator\": \"[What makes this unique]\",\n")
                .append("    \"beginnerFriendly\": \"[Yes - intuitive interface or Requires some technical knowledge]\",\n")
                .append("    \"requiresInstallation\": \"[Plug and play or Requires software installation]\",\n")
                .append("    \"setupTime\": \"[Specific time like 5-10 minutes, Under 30 minutes]\"\n")
                .append("  },\n")
                .append("  \"usageCompatibility\": {\n")
                .append("    \"howToUse\": \"[Step-by-step basic usage]\",\n")
                .append("    \"compatibleDevicesPlatforms\": \"[Specific compatibility like Works with iOS 15+, Android 11+, Windows 10/11]\",\n")
                .append("    \"unsupportedDevicesLimitations\": \"[Specific limitations or Fully compatible with all modern devices]\",\n")
                .append("    \"powerRequirementsBatteryDetails\": \"[Specific details like Built-in 3,279 mAh battery, Up to 20 hours video playback]\",\n")
                .append("    \"internetAppRequired\": \"[WiFi required for setup, Optional for enhanced features]\",\n")
                .append("    \"indoorOutdoorUsage\": \"[Indoor use recommended or Suitable for both]\",\n")
                .append("    \"recommendedUsageConditions\": \"[Specific conditions like Operating temp: 32° to 95° F]\",\n")
                .append("    \"commonMistakes\": \"[Specific mistakes users make]\",\n")
                .append("    \"maintenanceCleaningInstructions\": \"[Specific care instructions]\",\n")
                .append("    \"expectedProductLifespan\": \"[Realistic lifespan like 4-5 years with normal use]\"\n")
                .append("  },\n")
                .append("  \"deliveryPackaging\": {\n")
                .append("    \"deliveryTimeline\": \"[Specific timeline like 2-3 business days, Same-day delivery available]\",\n")
                .append("    \"packagingTypeSafety\": \"[Specific packaging like Eco-friendly retail packaging with protective foam]\",\n")
                .append("    \"giftWrap\": \"[Available for $3.99 or Complimentary gift wrapping]\",\n")
                .append("    \"trackingAvailability\": \"[Real-time tracking via SMS and email]\",\n")
                .append("    \"packageDelayedProcedure\": \"[Specific steps like Contact customer service with order number for immediate assistance]\",\n")
                .append("    \"packageDamagedProcedure\": \"[Specific steps like Take photos and request replacement within 48 hours]\",\n")
                .append("    \"missingItemsProcedure\": \"[Specific steps like Report within 24 hours for immediate resolution]\",\n")
                .append("    \"whoDelivers\": \"[Specific courier like FedEx, UPS, Amazon Logistics]\",\n")
                .append("    \"internationalShipping\": \"[Available to 50+ countries or Domestic only]\",\n")
                .append("    \"cashOnDelivery\": \"[Available in select regions or Prepaid only]\"\n")
                .append("  },\n")
                .append("  \"returnsRefundsWarranty\": {\n")
                .append("    \"returnEligibilityConditions\": \"[Specific conditions like Unopened with original packaging and all accessories]\",\n")
                .append("    \"returnWindowDays\": \"[Specific number like 30 days, 14 days]\",\n")
                .append("    \"refundTimeline\": \"[Specific timeline like 5-7 business days after return received]\",\n")
                .append("    \"replacementVsRefundRules\": \"[Specific policy like Free replacement for defects, refund for change of mind]\",\n")
                .append("    \"warrantyDuration\": \"[Specific duration like 12 months manufacturer warranty, Extended warranty available]\",\n")
                .append("    \"warrantyCovers\": \"[Specific coverage like Manufacturing defects, hardware failures]\",\n")
                .append("    \"warrantyDoesNotCover\": \"[Specific exclusions like Physical damage, water damage, unauthorized repairs]\",\n")
                .append("    \"howToClaimWarranty\": \"[Specific steps like Register product online, contact support with proof of purchase]\",\n")
                .append("    \"supportContactChannels\": \"[Specific channels like 24/7 phone support, live chat, email support within 24 hours]\"\n")
                .append("  }\n")
                .append("}\n\n")
                .append("REMEMBER: Generate realistic, complete, specific data for EVERY field. No placeholders, no N/A, no Unknown values!");

        return sb.toString();
    }

    /**
     * Assignment-safe fallback JSON, used when Gemini can't be reached.
     * Keeps the executive UI working and avoids errors.
     */
    private String buildFallbackAiOverviewJson(ProductRequest request) {
        String title = (request.getProductType() + " - " + request.getModelName()).trim();
        String brand = guessBrand(request.getModelName());

        String features = String.join("\", \"", List.of(
                "High performance for everyday use",
                "Modern design with reliable build quality",
                "Optimized for productivity and entertainment",
                "Energy efficient and portable",
                "Backed by standard warranty and support"));

        return "{"
                + "\"basicInfo\":{"
                + "\"productTitle\":\"" + jsonEscape(title) + "\","
                + "\"brandName\":\"" + jsonEscape(brand) + "\","
                + "\"productCategory\":\"" + jsonEscape(request.getProductType()) + "\","
                + "\"subCategory\":\"General\","
                + "\"productType\":\"" + jsonEscape(request.getProductType()) + "\","
                + "\"modelNumber\":\"" + jsonEscape(request.getModelName()) + "\","
                + "\"manufacturerName\":\"" + jsonEscape(brand) + "\","
                + "\"countryOfOrigin\":\"Unknown\","
                + "\"releaseDate\":\"Unknown\","
                + "\"itemCondition\":\"New\""
                + "},"
                + "\"identifiers\":{"
                + "\"sku\":\"SKU-" + System.currentTimeMillis() + "\","
                + "\"upcEanIsbn\":\"N/A\","
                + "\"asin\":\"ASIN-DEMO-" + System.currentTimeMillis() + "\","
                + "\"gtinExemption\":\"Yes\","
                + "\"partNumber\":\"N/A\""
                + "},"
                + "\"descriptionContent\":{"
                + "\"productDescription\":\"" + jsonEscape("Demo description for " + title + ". " +
                        "This content is generated for an academic assignment and may not reflect real specifications.")
                + "\","
                + "\"keyFeatures\":[\"" + features + "\"],"
                + "\"searchKeywords\":\""
                + jsonEscape(title + ", " + request.getMainColor() + ", support, troubleshooting") + "\","
                + "\"materialComposition\":\"Mixed\","
                + "\"color\":\"" + jsonEscape(request.getMainColor()) + "\","
                + "\"sizeDimensions\":\"Standard\","
                + "\"weight\":\"Standard\""
                + "},"
                + "\"pricingOffers\":{"
                + "\"mrp\":\"" + request.getMaxPrice() + "\","
                + "\"sellingPrice\":\"" + request.getMaxPrice() + "\","
                + "\"discountPercentage\":\"0\","
                + "\"taxCategoryGstSlab\":\"Standard\","
                + "\"shippingFee\":\"0\""
                + "},"
                + "\"inventoryFulfillment\":{"
                + "\"stockQuantity\":\"50\","
                + "\"fulfillmentMethod\":\"FBM\","
                + "\"warehouseLocation\":\"Local\","
                + "\"handlingTime\":\"1-2 days\","
                + "\"packageType\":\"Box\""
                + "},"
                + "\"mediaMarketing\":{"
                + "\"mainProductImage\":\"" + jsonEscape(request.getImageUrl()) + "\","
                + "\"additionalImages\":[],"
                + "\"productVideo\":\"\","
                + "\"enhancedBrandContent\":\"\""
                + "},"
                + "\"compliancePolicies\":{"
                + "\"returnPolicy\":\"7-day return (demo)\","
                + "\"warrantyInformation\":\"1-year limited warranty (demo)\","
                + "\"safetyComplianceCertificates\":\"N/A\","
                + "\"hazardousMaterialDeclaration\":\"No\""
                + "},"
                + "\"productUnderstanding\":{"
                + "\"problemSolved\":\"Helps customers accomplish day-to-day tasks reliably.\","
                + "\"bestSuitedFor\":\"Students, professionals, and general users.\","
                + "\"keyBenefits\":\"Fast, reliable, easy to use.\","
                + "\"howItWorks\":\"Works as expected for its category; follow standard usage.\","
                + "\"includedInTheBox\":\"Device, charger, documentation.\","
                + "\"notIncluded\":\"Accessories unless mentioned.\","
                + "\"mainDifferentiator\":\"Balanced performance and build quality.\","
                + "\"beginnerFriendly\":\"Yes\","
                + "\"requiresInstallation\":\"No\","
                + "\"setupTime\":\"10-20 minutes\""
                + "},"
                + "\"usageCompatibility\":{"
                + "\"howToUse\":\"Unbox, power on, complete setup, start using.\","
                + "\"compatibleDevicesPlatforms\":\"Depends on category.\","
                + "\"unsupportedDevicesLimitations\":\"N/A\","
                + "\"powerRequirementsBatteryDetails\":\"Standard for category.\","
                + "\"internetAppRequired\":\"Optional\","
                + "\"indoorOutdoorUsage\":\"Indoor\","
                + "\"recommendedUsageConditions\":\"Normal room temperature.\","
                + "\"commonMistakes\":\"Incorrect setup, ignoring updates.\","
                + "\"maintenanceCleaningInstructions\":\"Wipe with soft cloth.\","
                + "\"expectedProductLifespan\":\"3-5 years\""
                + "},"
                + "\"deliveryPackaging\":{"
                + "\"deliveryTimeline\":\"3-7 days\","
                + "\"packagingTypeSafety\":\"Secure box packaging\","
                + "\"giftWrap\":\"Optional\","
                + "\"trackingAvailability\":\"Yes\","
                + "\"packageDelayedProcedure\":\"Contact support with order ID\","
                + "\"packageDamagedProcedure\":\"Share images and request replacement\","
                + "\"missingItemsProcedure\":\"Report within 48 hours\","
                + "\"whoDelivers\":\"Seller\","
                + "\"internationalShipping\":\"No\","
                + "\"cashOnDelivery\":\"Depends\""
                + "},"
                + "\"returnsRefundsWarranty\":{"
                + "\"returnEligibilityConditions\":\"Unused, original packaging\","
                + "\"returnWindowDays\":\"7\","
                + "\"refundTimeline\":\"5-10 business days\","
                + "\"replacementVsRefundRules\":\"Replacement preferred\","
                + "\"warrantyDuration\":\"12 months\","
                + "\"warrantyCovers\":\"Manufacturing defects\","
                + "\"warrantyDoesNotCover\":\"Physical damage\","
                + "\"howToClaimWarranty\":\"Provide invoice and device serial\","
                + "\"supportContactChannels\":\"Chat/email/phone\""
                + "}"
                + "}";
    }

    private String guessBrand(String modelName) {
        if (modelName == null)
            return "Generic";
        String v = modelName.toLowerCase();
        if (v.contains("mac") || v.contains("iphone") || v.contains("ipad"))
            return "Apple";
        if (v.contains("samsung") || v.contains("galaxy"))
            return "Samsung";
        if (v.contains("dell"))
            return "Dell";
        if (v.contains("hp"))
            return "HP";
        if (v.contains("lenovo"))
            return "Lenovo";
        if (v.contains("sony"))
            return "Sony";
        return "Generic";
    }

    private String jsonEscape(String s) {
        if (s == null)
            return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
