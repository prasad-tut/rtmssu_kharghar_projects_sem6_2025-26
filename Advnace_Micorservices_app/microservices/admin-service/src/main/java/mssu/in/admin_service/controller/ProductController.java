package mssu.in.admin_service.controller;

import jakarta.validation.Valid;
import mssu.in.admin_service.dto.ProductRequest;
import mssu.in.admin_service.dto.ProductResponse;
import mssu.in.admin_service.dto.ProductSummaryResponse;
import mssu.in.admin_service.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * List all products (latest first). Used by admin listing and customer catalog.
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        logger.info("Getting all products");
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * Lightweight product catalog for customers (excludes AI overview and manual
     * overview).
     * This endpoint is optimized for fast loading on customer-facing pages.
     */
    @GetMapping("/catalog")
    public ResponseEntity<List<ProductSummaryResponse>> getProductCatalog() {
        logger.info("Getting product catalog for customers");
        return ResponseEntity.ok(productService.getProductCatalog());
    }

    /**
     * List products created by a specific admin (owner view).
     */
    @GetMapping("/by-admin/{adminId}")
    public ResponseEntity<List<ProductResponse>> getProductsForAdmin(@PathVariable Long adminId) {
        logger.info("Getting products for admin {}", adminId);
        return ResponseEntity.ok(productService.getProductsForAdmin(adminId));
    }

    /**
     * Get full product including AI overview for executive product overview modal.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        logger.info("Getting product {}", id);
        ProductResponse product = productService.getProduct(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    /**
     * Create product with only admin-provided info.
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        logger.info("Creating product (manual) for admin {}", request.getAdminId());
        ProductResponse product = productService.createProduct(request);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    /**
     * Create product and also generate detailed overview via Gemini.
     */
    @PostMapping("/ai")
    public ResponseEntity<ProductResponse> createProductWithAi(@Valid @RequestBody ProductRequest request) {
        logger.info("Creating product with AI for admin {}", request.getAdminId());
        ProductResponse product = productService.createProductWithAi(request);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }
}
